package com.thuytrinh.transactionviewer.products;

import com.thuytrinh.transactionviewer.api.Transaction;
import com.thuytrinh.transactionviewer.api.TransactionsFetcher;

import javax.inject.Inject;

import rx.Observable;

public class ProductRepository {
  private final TransactionsFetcher transactionsFetcher;

  @Inject ProductRepository(TransactionsFetcher transactionsFetcher) {
    this.transactionsFetcher = transactionsFetcher;
  }

  public Observable<Product> getProductsAsync() {
    return transactionsFetcher.fetchTransactionsAsync()
        .flatMap(Observable::from)
        .groupBy(Transaction::sku)
        .flatMap(x -> x
            .toList()
            .map(transactions -> ImmutableProduct.builder()
                .sku(x.getKey())
                .transactions(transactions)
                .build()
            ));
  }
}
