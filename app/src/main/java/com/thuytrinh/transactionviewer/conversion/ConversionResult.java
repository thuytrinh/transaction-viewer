package com.thuytrinh.transactionviewer.conversion;

import org.immutables.value.Value;

@Value.Immutable
public interface ConversionResult {
  String from();
  String to();
}
