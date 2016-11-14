package com.thuytrinh.transactionviewer.conversion;

import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface ConversionResult {
  String originalAmountText();
  String amountInGbpText();
  BigDecimal amountInGbp();
}
