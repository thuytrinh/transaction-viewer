package com.thuytrinh.transactionviewer.transactions;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;

import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.api.RatesFetcher;
import com.thuytrinh.transactionviewer.conversion.ConversionResult;
import com.thuytrinh.transactionviewer.conversion.CurrencyGraph;
import com.thuytrinh.transactionviewer.products.ProductRepository;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.thuytrinh.transactionviewer.conversion.CurrencyGraph.GBP_FORMATTER;

public class TransactionsViewModel {
  private static final String KEY_SKU = "sku";
  public final ObservableList<ConversionResult> items = new ObservableArrayList<>();
  public final ObservableField<String> totalText = new ObservableField<>();
  private final Resources resources;
  private final RatesFetcher ratesFetcher;
  private final ProductRepository productRepository;
  private final Action1<Throwable> errorHandler;

  @Inject TransactionsViewModel(
      Resources resources,
      RatesFetcher ratesFetcher,
      ProductRepository productRepository,
      Action1<Throwable> errorHandler) {
    this.resources = resources;
    this.ratesFetcher = ratesFetcher;
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
    ratesFetcher.fetchRatesAsync()
        .map(CurrencyGraph::new)
        .flatMap(g -> productRepository.getProductsAsync()
            .filter(x -> x.sku().equals(sku))
            .flatMap(x -> Observable.from(x.transactions()))
            .flatMap(x -> g.asGbpAsync(x.currency(), x.amount()))
        )
        .toList()
        .doOnNext(x -> {
          BigDecimal total = BigDecimal.ZERO;
          for (ConversionResult result : x) {
            total = total.add(result.amountInGbp());
          }
          totalText.set(resources.getString(R.string.total_x, GBP_FORMATTER.format(total)));
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(x -> {
          items.clear();
          items.addAll(x);
        }, errorHandler);
  }
}
