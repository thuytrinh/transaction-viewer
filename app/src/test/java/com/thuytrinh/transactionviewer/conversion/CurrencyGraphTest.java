package com.thuytrinh.transactionviewer.conversion;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.thuytrinh.transactionviewer.BuildConfig;
import com.thuytrinh.transactionviewer.api.ImmutableRate;
import com.thuytrinh.transactionviewer.api.Rate;

import org.assertj.core.data.MapEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class CurrencyGraphTest {
  @Test public void shouldCreateGraphCorrectly() {
    final ArrayList<Rate> rates = Lists.newArrayList(
        ImmutableRate.builder().from("USD").rate(BigDecimal.valueOf(0.77)).to("GBP").build(),
        ImmutableRate.builder().from("GBP").rate(BigDecimal.valueOf(1.3)).to("USD").build(),
        ImmutableRate.builder().from("USD").rate(BigDecimal.valueOf(1.09)).to("CAD").build(),
        ImmutableRate.builder().from("CAD").rate(BigDecimal.valueOf(0.92)).to("USD").build(),
        ImmutableRate.builder().from("GBP").rate(BigDecimal.valueOf(0.83)).to("AUD").build(),
        ImmutableRate.builder().from("AUD").rate(BigDecimal.valueOf(1.2)).to("GBP").build()
    );
    final Map<String, Map<String, BigDecimal>> graph = CurrencyGraph.createGraph(rates);
    assertThat(graph).containsOnly(
        MapEntry.entry("USD", ImmutableMap.of(
            "GBP", BigDecimal.valueOf(0.77),
            "CAD", BigDecimal.valueOf(1.09)
        )),
        MapEntry.entry("GBP", ImmutableMap.of(
            "USD", BigDecimal.valueOf(1.3),
            "AUD", BigDecimal.valueOf(0.83)
        )),
        MapEntry.entry("CAD", ImmutableMap.of(
            "USD", BigDecimal.valueOf(0.92)
        )),
        MapEntry.entry("AUD", ImmutableMap.of(
            "GBP", BigDecimal.valueOf(1.2)
        ))
    );
  }

  @Test public void shouldCalculateRateFromPathsCorrectly() {
    final Path a = ImmutablePath.of(null, "THB", BigDecimal.ONE);
    final Path b = ImmutablePath.of(a, "AUD", BigDecimal.valueOf(2));
    final Path c = ImmutablePath.of(b, "USD", BigDecimal.valueOf(3));
    final Path d = ImmutablePath.of(c, "GBP", BigDecimal.valueOf(4));
    final BigDecimal rate = CurrencyGraph.asRate(d);
    assertThat(rate).isEqualTo(BigDecimal.valueOf(24));
  }

  @Test public void shouldCreateConversionResultCorrectly() {
    final ConversionResult result = CurrencyGraph.asConversionResult(
        "USD",
        BigDecimal.valueOf(25.8),
        BigDecimal.valueOf(19.866)
    );
    assertThat(result).isEqualTo(
        ImmutableConversionResult.builder()
            .originalAmountText("$25.80")
            .amountInGbpText("GBP19.87") // Robolectric is unable to convert into `£`.
            .amountInGbp(BigDecimal.valueOf(19.866))
            .build()
    );
  }
}
