package com.thuytrinh.transactionviewer.products;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class ProductsViewModel {
  public final ObservableList<ProductViewModel> products = new ObservableArrayList<>();
  private final ProductRepository productRepository;
  private final Provider<ProductViewModel> productViewModelProvider;
  private final Action1<Throwable> errorHandler;
  private final PublishSubject<String> onProductSelected = PublishSubject.create();

  @Inject ProductsViewModel(
      ProductRepository productRepository,
      Provider<ProductViewModel> productViewModelProvider,
      Action1<Throwable> errorHandler) {
    this.productViewModelProvider = productViewModelProvider;
    this.errorHandler = errorHandler;
    this.productRepository = productRepository;
  }

  public Observable<String> onProductSelected() {
    return onProductSelected
        .asObservable();
  }

  void loadProducts() {
    productRepository.getProductsAsync()
        .map(x -> {
          final ProductViewModel viewModel = productViewModelProvider.get();
          viewModel.setProduct(x);
          viewModel.setOnSelected(onProductSelected);
          return viewModel;
        })
        .toSortedList()
        .observeOn(mainThread())
        .subscribe(x -> {
          products.clear();
          products.addAll(x);
        }, errorHandler);
  }
}
