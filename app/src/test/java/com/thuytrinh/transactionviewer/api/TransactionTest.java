package com.thuytrinh.transactionviewer.api;

import com.google.gson.Gson;
import com.thuytrinh.transactionviewer.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class TransactionTest {
  @Test public void canBeCreatedFromJson() {
    final String json = "{\n" +
        "  \"amount\": \"30.1\",\n" +
        "  \"sku\": \"X1893\",\n" +
        "  \"currency\": \"GBP\"\n" +
        "}";
    final Transaction actual = new Gson().fromJson(json, Transaction.class);
    assertThat(actual).isEqualTo(
        ImmutableTransaction.builder()
            .amount(new BigDecimal("30.1"))
            .sku("X1893")
            .currency("GBP")
            .build()
    );
  }
}
