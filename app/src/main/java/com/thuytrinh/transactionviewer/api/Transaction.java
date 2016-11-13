package com.thuytrinh.transactionviewer.api;

import com.google.gson.annotations.JsonAdapter;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@Gson.TypeAdapters
@JsonAdapter(GsonAdaptersTransaction.class)
public interface Transaction {
  BigDecimal amount();
  String sku();
  String currency();
}
