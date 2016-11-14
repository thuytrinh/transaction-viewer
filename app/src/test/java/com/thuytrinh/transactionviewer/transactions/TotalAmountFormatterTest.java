package com.thuytrinh.transactionviewer.transactions;

import com.thuytrinh.transactionviewer.BuildConfig;
import com.thuytrinh.transactionviewer.conversion.ImmutableConversionResult;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class TotalAmountFormatterTest {
  private TotalAmountFormatter formatter;

  @Before public void before() {
    formatter = new TotalAmountFormatter(RuntimeEnvironment.application.getResources());
  }

  @Test public void shouldComputeAndFormatTotalAmount() {
    final String s = formatter.computeAndFormatTotal(Lists.newArrayList(
        ImmutableConversionResult.builder()
            .originalAmountText("Ignored")
            .amountInGbpText("Ignored")
            .amountInGbp(BigDecimal.valueOf(9609.17444))
            .build()
    ));

    // Robolectric is unable to convert into `£`.
    // So we temporarily stick with `GBP`.
    assertThat(s).isEqualTo("Total: GBP9,609.17");
  }
}
