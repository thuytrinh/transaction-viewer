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
public class RateTest {
  @Test public void canBeCreatedFromJson() {
    final String json = "{\n" +
        "  \"from\": \"EUR\",\n" +
        "  \"rate\": \"0.5\",\n" +
        "  \"to\": \"GBP\"\n" +
        "}";
    final Rate actual = new Gson().fromJson(json, Rate.class);
    assertThat(actual).isEqualTo(
        ImmutableRate.builder()
            .from("EUR")
            .rate(new BigDecimal(0.5))
            .to("GBP")
            .build()
    );
  }
}
