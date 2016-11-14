package com.thuytrinh.transactionviewer.products;

import com.google.common.collect.Lists;
import com.thuytrinh.transactionviewer.BuildConfig;
import com.thuytrinh.transactionviewer.api.Transaction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import dagger.internal.Factory;
import rx.Observable;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ProductsViewModelTest {
  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Mock ProductRepository productRepository;
  @Mock Factory<ProductViewModel> productViewModelFactory;
  @Mock Action1<Throwable> errorHandler;
  private ProductsViewModel viewModel;

  @Before public void before() {
    viewModel = new ProductsViewModel(
        RuntimeEnvironment.application.getResources(),
        productRepository,
        productViewModelFactory,
        errorHandler
    );
  }

  @Test public void initiallyNoError() {
    assertThat(viewModel.error.get()).isNull();
  }

  @Test public void shouldDisposeAnyObserversOnProductSelection() {
    final TestSubscriber<String> subscriber = new TestSubscriber<>();
    viewModel.onProductSelected().subscribe(subscriber);
    subscriber.assertNotCompleted();

    viewModel.dispose();
    subscriber.assertTerminalEvent();
  }

  @Test public void shouldSpecifyErrorHandlerWhenLoadingProducts() {
    when(productRepository.getProductsAsync())
        .thenReturn(Observable.error(new RuntimeException()));

    viewModel.loadProducts();
    verify(errorHandler).call(any(RuntimeException.class));
    assertThat(viewModel.error.get()).isEqualTo("Error loading products");
  }

  @Test public void terminateLoadingProductsBeforeDisposing() {
    final PublishSubject<Product> productSignal = PublishSubject.create();
    when(productRepository.getProductsAsync())
        .thenReturn(productSignal.asObservable());

    viewModel.loadProducts();
    assertThat(productSignal.hasObservers()).isTrue();

    viewModel.dispose();
    assertThat(productSignal.hasObservers()).isFalse();
  }

  @Test public void createViewModelsForEachProduct() {
    final ProductViewModel productVm0 = mock(ProductViewModel.class);
    final ProductViewModel productVm1 = mock(ProductViewModel.class);
    when(productViewModelFactory.get())
        .thenReturn(productVm0)
        .thenReturn(productVm1);

    final Product productB = ImmutableProduct.builder()
        .sku("B").addTransactions(mock(Transaction.class))
        .build();
    final Product productA = ImmutableProduct.builder()
        .sku("A").addTransactions(mock(Transaction.class))
        .build();
    when(productRepository.getProductsAsync())
        .thenReturn(Observable.from(Lists.newArrayList(
            productB,
            productA
        )));

    viewModel.loadProducts();
    verify(productVm0).setOnSelected(viewModel.onProductSelected);
    verify(productVm0).setProduct(same(productB));
    verify(productVm1).setOnSelected(viewModel.onProductSelected);
    verify(productVm1).setProduct(same(productA));
    assertThat(viewModel.products).containsOnly(productVm0, productVm1);
  }
}
