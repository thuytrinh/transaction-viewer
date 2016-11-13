package com.thuytrinh.transactionviewer.products;

import com.thuytrinh.transactionviewer.api.Transaction;
import com.thuytrinh.transactionviewer.api.TransactionsFetcher;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.observables.GroupedObservable;

class ProductRepository {
  private final TransactionsFetcher transactionsFetcher;
  private final Provider<Product> productFactory;

  @Inject ProductRepository(
      TransactionsFetcher transactionsFetcher,
      Provider<Product> productFactory) {
    this.transactionsFetcher = transactionsFetcher;
    this.productFactory = productFactory;
  }

  Observable<List<Product>> getProductsAsync() {
    return transactionsFetcher.fetchTransactionsAsync()
        .flatMap(Observable::from)
        .groupBy(Transaction::sku)
        .flatMap(this::asProduct)
        .toList();
  }

  private Observable<Product> asProduct(GroupedObservable<String, Transaction> x) {
    return x
        .toList()
        .map(transactions -> {
          final Product product = productFactory.get();
          product.setTransactions(x.getKey(), transactions);
          return product;
        });
  }
}
