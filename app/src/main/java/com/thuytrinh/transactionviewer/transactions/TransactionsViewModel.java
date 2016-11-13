package com.thuytrinh.transactionviewer.transactions;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;

import com.thuytrinh.transactionviewer.api.RatesFetcher;
import com.thuytrinh.transactionviewer.conversion.ConversionResult;
import com.thuytrinh.transactionviewer.conversion.CurrencyGraph;
import com.thuytrinh.transactionviewer.products.ProductRepository;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class TransactionsViewModel {
  private static final String KEY_SKU = "sku";
  public final ObservableList<ConversionResult> items = new ObservableArrayList<>();
  private final RatesFetcher ratesFetcher;
  private final ProductRepository productRepository;
  private final Action1<Throwable> errorHandler;

  @Inject TransactionsViewModel(
      RatesFetcher ratesFetcher,
      ProductRepository productRepository,
      Action1<Throwable> errorHandler) {
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
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(x -> {
          items.clear();
          items.addAll(x);
        }, errorHandler);
  }
}
