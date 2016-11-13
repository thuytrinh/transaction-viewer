package com.thuytrinh.transactionviewer.products;

import com.thuytrinh.transactionviewer.api.Transaction;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface Product {
  String sku();
  List<Transaction> transactions();
}
