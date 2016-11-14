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

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.thuytrinh.transactionviewer.conversion.CurrencyGraph.GBP_FORMATTER;

public class TransactionsViewModel extends DisposableViewModel {
  private static final String KEY_SKU = "sku";
  public final ObservableList<ConversionResult> items = new ObservableArrayList<>();
  public final ObservableField<String> totalText = new ObservableField<>();
  private final Resources resources;
  private final RateRepository rateRepository;
  private final ProductRepository productRepository;
  private final Action1<Throwable> errorHandler;

  @Inject TransactionsViewModel(
      Resources resources,
      RateRepository rateRepository,
      ProductRepository productRepository,
      Action1<Throwable> errorHandler) {
    this.resources = resources;
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
    rateRepository.getCurrencyGraphAsync()
        .flatMap(g -> productRepository.getProductsAsync()
            .filter(x -> x.sku().equals(sku))
            .flatMap(x -> Observable.from(x.transactions()))
            .flatMap(x -> g.asGbpAsync(x.currency(), x.amount()))
        )
        .toList()
        .doOnNext(this::computeTotal)
        .takeUntil(onDispose())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(x -> {
          items.clear();
          items.addAll(x);
        }, errorHandler);
  }

  private void computeTotal(List<ConversionResult> x) {
    BigDecimal total = BigDecimal.ZERO;
    for (ConversionResult result : x) {
      total = total.add(result.amountInGbp());
    }
    totalText.set(resources.getString(R.string.total_x, GBP_FORMATTER.format(total)));
  }
}
