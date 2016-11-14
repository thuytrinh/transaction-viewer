package com.thuytrinh.transactionviewer.conversion;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable(builder = false)
interface Path {
  @Value.Parameter @Nullable Path parent();
  @Value.Parameter String currency();
  @Value.Parameter BigDecimal rate();
}
