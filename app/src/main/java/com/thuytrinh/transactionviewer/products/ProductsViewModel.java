package com.thuytrinh.transactionviewer.products;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import javax.inject.Inject;

import rx.functions.Action1;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class ProductsViewModel {
  public final ObservableList<Product> products = new ObservableArrayList<>();
  private final ProductRepository productRepository;
  private final Action1<Throwable> errorHandler;

  @Inject ProductsViewModel(
      ProductRepository productRepository,
      Action1<Throwable> errorHandler) {
    this.errorHandler = errorHandler;
    this.productRepository = productRepository;
  }

  void loadTransactions() {
    productRepository.getProductsAsync()
        .observeOn(mainThread())
        .subscribe(x -> {
          products.clear();
          products.addAll(x);
        }, errorHandler);
  }
}
