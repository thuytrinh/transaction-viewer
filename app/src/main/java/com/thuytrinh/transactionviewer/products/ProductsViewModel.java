package com.thuytrinh.transactionviewer.products;

import android.content.res.Resources;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.util.DisposableViewModel;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class ProductsViewModel extends DisposableViewModel {
  public final ObservableList<ProductViewModel> products = new ObservableArrayList<>();
  public final ObservableField<String> error = new ObservableField<>();
  final PublishSubject<String> onProductSelected = PublishSubject.create();
  private final Resources resources;
  private final ProductRepository productRepository;
  private final Provider<ProductViewModel> productViewModelProvider;
  private final Action1<Throwable> errorHandler;

  @Inject ProductsViewModel(
      Resources resources,
      ProductRepository productRepository,
      Provider<ProductViewModel> productViewModelProvider,
      Action1<Throwable> errorHandler) {
    this.resources = resources;
    this.productViewModelProvider = productViewModelProvider;
    this.errorHandler = errorHandler;
    this.productRepository = productRepository;
  }

  Observable<String> onProductSelected() {
    return onProductSelected
        .asObservable()
        .takeUntil(onDispose());
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
        .doOnError(e -> error.set(resources.getString(R.string.error_loading_products)))
        .takeUntil(onDispose())
        .observeOn(mainThread())
        .subscribe(x -> {
          products.clear();
          products.addAll(x);
        }, errorHandler);
  }
}
