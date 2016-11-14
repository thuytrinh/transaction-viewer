package com.thuytrinh.transactionviewer.transactions;

import android.os.Bundle;

import com.thuytrinh.transactionviewer.BuildConfig;
import com.thuytrinh.transactionviewer.conversion.RateRepository;
import com.thuytrinh.transactionviewer.products.ProductRepository;

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

import rx.Observable;
import rx.functions.Action1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class TransactionsViewModelTest {
  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Mock TotalAmountFormatter totalAmountFormatter;
  @Mock RateRepository rateRepository;
  @Mock ProductRepository productRepository;
  @Mock Action1<Throwable> errorHandler;
  private TransactionsViewModel viewModel;

  @Before public void before() {
    viewModel = new TransactionsViewModel(
        RuntimeEnvironment.application.getResources(),
        () -> totalAmountFormatter,
        rateRepository,
        productRepository,
        errorHandler
    );
  }

  @Test public void shouldCreateArgsCorrectly() {
    final Bundle args = TransactionsViewModel.newArgs("ABC");
    assertThat(args.getString("sku")).isEqualTo("ABC");
  }

  @Test public void shouldReflectCorrectTitleForSku() {
    when(rateRepository.getCurrencyGraphAsync())
        .thenReturn(Observable.empty());

    final Bundle args = new Bundle();
    args.putString("sku", "ABC");
    viewModel.loadTransactions(args);
    assertThat(viewModel.title.get()).isEqualTo("Transactions for ABC");
  }
}
