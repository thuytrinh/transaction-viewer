package com.thuytrinh.transactionviewer.api;

import com.google.gson.Gson;
import com.thuytrinh.transactionviewer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MockRatesFetcherTest {
  @Test public void shouldHaveRates() {
    final MockRatesFetcher fetcher = new MockRatesFetcher(
        RuntimeEnvironment.application.getAssets(),
        new Gson()
    );
    final TestSubscriber<List<Rate>> subscriber = new TestSubscriber<>();
    fetcher.fetchRatesAsync().subscribe(subscriber);

    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    final List<Rate> rates = subscriber.getOnNextEvents().get(0);
    assertThat(rates).isNotEmpty();
  }
}
