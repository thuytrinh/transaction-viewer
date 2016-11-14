package com.thuytrinh.transactionviewer.products;

import com.thuytrinh.transactionviewer.api.Transaction;
import com.thuytrinh.transactionviewer.api.TransactionsFetcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton
public class ProductRepository {
  private final Observable<Product> getProductsAsync;

  @Inject ProductRepository(Lazy<TransactionsFetcher> transactionsFetcherLazy) {
    getProductsAsync = Observable
        .defer(() -> transactionsFetcherLazy.get().fetchTransactionsAsync())
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

  public Observable<Product> getProductBySkuAsync(String sku) {
    return getProductsAsync
        .filter(x -> x.sku().equals(sku));
  }

  Observable<Product> getProductsAsync() {
    return getProductsAsync;
  }
}
