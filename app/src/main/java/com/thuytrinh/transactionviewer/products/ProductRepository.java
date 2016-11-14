package com.thuytrinh.transactionviewer.products;

import com.thuytrinh.transactionviewer.api.Transaction;
import com.thuytrinh.transactionviewer.api.TransactionsFetcher;

import javax.inject.Inject;

import rx.Observable;

public class ProductRepository {
  private Observable<Product> getProductsAsync;

  @Inject ProductRepository(TransactionsFetcher transactionsFetcher) {
    getProductsAsync = Observable
        .defer(transactionsFetcher::fetchTransactionsAsync)
        .flatMap(Observable::from)
        .groupBy(Transaction::sku)
        .flatMap(x -> x
            .toList()
            .map(transactions -> (Product) ImmutableProduct.builder()
                .sku(x.getKey())
                .transactions(transactions)
                .build()
            ))
        .cache();
  }

  public Observable<Product> getProductsAsync() {
    return getProductsAsync;
  }
}
