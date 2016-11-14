package com.thuytrinh.transactionviewer.conversion;

import com.google.common.collect.Maps;
import com.thuytrinh.transactionviewer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class RateCacheTest {
  private RateCache cache = new RateCache();

  @Test public void shouldCalculateRateFromPathsCorrectly() {
    final Path a = ImmutablePath.of(null, "THB", BigDecimal.ONE);
    final Path b = ImmutablePath.of(a, "AUD", BigDecimal.valueOf(2));
    final Path c = ImmutablePath.of(b, "USD", BigDecimal.valueOf(3));
    final Path d = ImmutablePath.of(c, "GBP", BigDecimal.valueOf(4));
    final BigDecimal rate = RateCache.asRate(d);
    assertThat(rate).isEqualTo(BigDecimal.valueOf(24));
  }

  @Test public void shouldComputeAndCacheRate() {
    final Path a = ImmutablePath.of(null, "THB", BigDecimal.ONE);
    final Path b = ImmutablePath.of(a, "AUD", BigDecimal.valueOf(2));
    final Path c = ImmutablePath.of(b, "USD", BigDecimal.valueOf(3));
    final Path path = ImmutablePath.of(c, "GBP", BigDecimal.valueOf(4));

    final HashMap<String, Map<String, BigDecimal>> graph = Maps.newHashMap();
    final ConversionFinder conversionFinder = mock(ConversionFinder.class);
    when(conversionFinder.call(anyString(), same(graph)))
        .thenReturn(path);

    final TestSubscriber<BigDecimal> former = new TestSubscriber<>();
    cache.getRateAsync("THB", conversionFinder, graph)
        .subscribe(former);

    former.awaitTerminalEvent();
    former.assertValue(BigDecimal.valueOf(24));
    verify(conversionFinder).call(anyString(), same(graph));

    final TestSubscriber<BigDecimal> latter = new TestSubscriber<>();
    cache.getRateAsync("THB", conversionFinder, graph)
        .subscribe(latter);

    latter.awaitTerminalEvent();
    latter.assertValue(BigDecimal.valueOf(24));
    verifyNoMoreInteractions(conversionFinder);
  }
}
