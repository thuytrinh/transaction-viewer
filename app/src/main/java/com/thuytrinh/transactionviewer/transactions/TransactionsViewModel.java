package com.thuytrinh.transactionviewer.transactions;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;

import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.conversion.ConversionResult;
import com.thuytrinh.transactionviewer.conversion.RateRepository;
import com.thuytrinh.transactionviewer.products.ProductRepository;
import com.thuytrinh.transactionviewer.util.DisposableViewModel;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.functions.Action1;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class TransactionsViewModel extends DisposableViewModel {
  private static final String KEY_SKU = "sku";
  public final ObservableList<ConversionResult> items = new ObservableArrayList<>();
  public final ObservableField<String> totalText = new ObservableField<>();
  public final ObservableField<String> title = new ObservableField<>();

  private final Resources resources;
  private final Lazy<TotalAmountFormatter> totalAmountFormatterLazy;
  private final RateRepository rateRepository;
  private final ProductRepository productRepository;
  private final Action1<Throwable> errorHandler;

  @Inject TransactionsViewModel(
      Resources resources,
      Lazy<TotalAmountFormatter> totalAmountFormatterLazy,
      RateRepository rateRepository,
      ProductRepository productRepository,
      Action1<Throwable> errorHandler) {
    this.resources = resources;
    this.totalAmountFormatterLazy = totalAmountFormatterLazy;
    this.rateRepository = rateRepository;
    this.productRepository = productRepository;
    this.errorHandler = errorHandler;
  }

  static Bundle newArgs(String sku) {
    final Bundle args = new Bundle();
    args.putString(KEY_SKU, sku);
    return args;
  }

  void loadTransactions(Bundle args) {
    final String sku = args.getString(KEY_SKU);
    title.set(resources.getString(R.string.transactions_for_x, sku));

    rateRepository.getCurrencyGraphAsync()
        .flatMap(g -> productRepository.getProductBySkuAsync(sku)
            .flatMap(x -> Observable.from(x.transactions()))
            // concatMap() to maintain the original order of transactions.
            .concatMap(x -> g.asGbpAsync(x.currency(), x.amount()))
        )
        .toList()
        .doOnNext(x -> totalText.set(totalAmountFormatterLazy.get().computeAndFormatTotal(x)))
        .takeUntil(onDispose())
        .observeOn(mainThread())
        .subscribe(x -> {
          items.clear();
          items.addAll(x);
        }, errorHandler);
  }
}
