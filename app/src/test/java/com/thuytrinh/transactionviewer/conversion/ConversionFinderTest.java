package com.thuytrinh.transactionviewer.conversion;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.thuytrinh.transactionviewer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ConversionFinderTest {
  private ConversionFinder finder = new ConversionFinder();

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowErrorForUnknownCurrency() {
    finder.call("THB", Maps.newHashMap());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowErrorForUnknownConversion() {
    finder.call("USD", ImmutableMap.of(
        "USD", ImmutableMap.of("AUD", BigDecimal.ONE),
        "AUD", ImmutableMap.of("CAD", BigDecimal.ONE)
    ));
  }

  /**
   * I think the more conversion steps we take,
   * the more commission the bank will charge us.
   * So that's why we should take least steps.
   * For example, suppose there're 2 ways to convert AUD to GBP.
   * One is, AUD -> CAD -> USD -> GBP. The other is,
   * AUD -> CAD -> GBP. Then we'll go with the latter.
   */
  @Test public void shouldPickPathWithLeastConversionSteps() {
    final Path path = finder.call("AUD", ImmutableMap.of(
        "AUD", ImmutableMap.of("CAD", BigDecimal.valueOf(2)),
        "CAD", ImmutableMap.of(
            "USD", BigDecimal.valueOf(3),
            "GBP", BigDecimal.valueOf(4)
        ),
        "USD", ImmutableMap.of("GBP", BigDecimal.valueOf(5))
    ));
    assertThat(path).isEqualTo(ImmutablePath.of(
        ImmutablePath.of(
            ImmutablePath.of(null, "AUD", BigDecimal.ONE),
            "CAD", BigDecimal.valueOf(2)
        ),
        "GBP", BigDecimal.valueOf(4)
    ));
  }

  /**
   * Given there're 2 ways that takes the same number of steps.
   * For simplicity, we'll take the first match.
   */
  @Test public void shouldPickFirstMatchedPath() {
    final Path path = finder.call("AUD", ImmutableMap.of(
        "AUD", ImmutableMap.of(
            "CAD", BigDecimal.valueOf(2),
            "USD", BigDecimal.valueOf(3)
        ),
        "CAD", ImmutableMap.of("GBP", BigDecimal.valueOf(4)),
        "USD", ImmutableMap.of("GBP", BigDecimal.valueOf(5))
    ));
    assertThat(path).isEqualTo(ImmutablePath.of(
        ImmutablePath.of(
            ImmutablePath.of(null, "AUD", BigDecimal.ONE),
            "CAD", BigDecimal.valueOf(2)
        ),
        "GBP", BigDecimal.valueOf(4)
    ));
  }

  @Test public void shouldPickCorrectPathIn2WayConversions() {
    final Path path = finder.call("AUD", ImmutableMap.of(
        "AUD", ImmutableMap.of("USD", BigDecimal.valueOf(2)),
        "USD", ImmutableMap.of(
            "EUR", BigDecimal.valueOf(3),
            "AUD", BigDecimal.valueOf(4)
        ),
        "EUR", ImmutableMap.of(
            "GBP", BigDecimal.valueOf(5),
            "USD", BigDecimal.valueOf(6)
        ),
        "GBR", ImmutableMap.of("EUR", BigDecimal.valueOf(7))
    ));
    assertThat(path).isEqualTo(ImmutablePath.of(
        ImmutablePath.of(
            ImmutablePath.of(
                ImmutablePath.of(null, "AUD", BigDecimal.ONE),
                "USD", BigDecimal.valueOf(2)
            ),
            "EUR", BigDecimal.valueOf(3)
        ),
        "GBP", BigDecimal.valueOf(5)
    ));
  }
}
