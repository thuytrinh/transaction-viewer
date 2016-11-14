package com.thuytrinh.transactionviewer.products;

import com.google.common.collect.Lists;
import com.thuytrinh.transactionviewer.BuildConfig;
import com.thuytrinh.transactionviewer.api.ImmutableTransaction;
import com.thuytrinh.transactionviewer.api.Transaction;
import com.thuytrinh.transactionviewer.api.TransactionsFetcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ProductRepositoryTest {
  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Mock TransactionsFetcher transactionsFetcher;
  private ProductRepository repository;

  @Before public void before() {
    repository = new ProductRepository(() -> transactionsFetcher);
  }

  @Test public void shouldFetchAndCacheProducts() {
    final Transaction a0 = ImmutableTransaction.builder()
        .sku("A")
        .amount(BigDecimal.valueOf(2))
        .currency("USD")
        .build();
    final Transaction a1 = ImmutableTransaction.builder()
        .sku("A")
        .amount(BigDecimal.valueOf(3))
        .currency("AUD")
        .build();
    when(transactionsFetcher.fetchTransactionsAsync())
        .thenReturn(Observable.just(Lists.newArrayList(a0, a1)));

    final TestSubscriber<Product> former = new TestSubscriber<>();
    repository.getProductsAsync().subscribe(former);

    final ImmutableProduct product = ImmutableProduct.builder()
        .sku("A")
        .transactions(Lists.newArrayList(a0, a1))
        .build();
    former.awaitTerminalEvent();
    former.assertNoErrors();
    former.assertValue(product);
    verify(transactionsFetcher).fetchTransactionsAsync();

    final TestSubscriber<Product> latter = new TestSubscriber<>();
    repository.getProductsAsync().subscribe(latter);
    latter.awaitTerminalEvent();
    latter.assertNoErrors();
    latter.assertValue(product);
    verifyNoMoreInteractions(transactionsFetcher);
  }

  @Test public void shouldFilterProductsMatchingBySku() {
    final Transaction a0 = ImmutableTransaction.builder()
        .sku("A")
        .amount(BigDecimal.valueOf(2))
        .currency("USD")
        .build();
    final Transaction a1 = ImmutableTransaction.builder()
        .sku("B")
        .amount(BigDecimal.valueOf(3))
        .currency("AUD")
        .build();
    when(transactionsFetcher.fetchTransactionsAsync())
        .thenReturn(Observable.just(Lists.newArrayList(a0, a1)));

    final TestSubscriber<Product> subscriber = new TestSubscriber<>();
    repository.getProductBySkuAsync("B").subscribe(subscriber);

    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValue(
        ImmutableProduct.builder()
            .sku("B")
            .transactions(Lists.newArrayList(a1))
            .build()
    );
  }
}
