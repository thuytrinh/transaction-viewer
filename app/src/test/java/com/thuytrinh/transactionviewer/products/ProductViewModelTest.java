package com.thuytrinh.transactionviewer.products;

import android.content.res.Resources;

import com.google.common.collect.Lists;
import com.thuytrinh.transactionviewer.BuildConfig;
import com.thuytrinh.transactionviewer.api.Transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ProductViewModelTest {
  private ProductViewModel viewModel;
  private Resources resources;

  @Before public void before() {
    resources = RuntimeEnvironment.application.getResources();
    viewModel = new ProductViewModel(resources);
  }

  /**
   * To be able to be sorted by {@link Observable#toSortedList()}.
   */
  @Test public void shouldBeComparable() {
    assertThat(viewModel).isInstanceOf(Comparable.class);
  }

  @Test public void sortProductsByAZ() {
    final ImmutableProduct a = ImmutableProduct.builder()
        .sku("A")
        .transactions(Collections.emptyList())
        .build();
    final ProductViewModel lhs = new ProductViewModel(resources);
    lhs.setProduct(a);

    final ImmutableProduct b = ImmutableProduct.builder()
        .sku("B")
        .transactions(Collections.emptyList())
        .build();
    final ProductViewModel rhs = new ProductViewModel(resources);
    rhs.setProduct(b);
    assertThat(lhs.compareTo(rhs)).isNegative();
  }

  @Test public void shouldEmitSelectionEvent() {
    final PublishSubject<String> onSelected = PublishSubject.create();
    viewModel.setOnSelected(onSelected);
    viewModel.setProduct(
        ImmutableProduct.builder()
            .sku("A")
            .transactions(Collections.emptyList())
            .build()
    );

    final TestSubscriber<String> subscriber = new TestSubscriber<>();
    onSelected.subscribe(subscriber);
    viewModel.select();

    subscriber.assertValue("A");
  }

  @Test public void shouldReflectSku() {
    viewModel.setProduct(
        ImmutableProduct.builder()
            .sku("A")
            .transactions(Lists.newArrayList())
            .build()
    );
    assertThat(viewModel.sku()).isEqualTo("A");
  }

  @Test public void shouldReflectTransactionCountInSingular() {
    viewModel.setProduct(
        ImmutableProduct.builder()
            .sku("A")
            .transactions(Lists.newArrayList(
                Mockito.mock(Transaction.class)
            ))
            .build()
    );
    assertThat(viewModel.transactionCount()).isEqualTo("1 transaction");
  }

  @Test public void shouldReflectTransactionCountInPlural() {
    viewModel.setProduct(
        ImmutableProduct.builder()
            .sku("A")
            .transactions(Lists.newArrayList(
                Mockito.mock(Transaction.class),
                Mockito.mock(Transaction.class)
            ))
            .build()
    );
    assertThat(viewModel.transactionCount()).isEqualTo("2 transactions");
  }
}
