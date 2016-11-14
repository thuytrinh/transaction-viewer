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
}
