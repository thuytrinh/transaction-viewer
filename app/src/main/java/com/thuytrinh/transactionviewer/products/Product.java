package com.thuytrinh.transactionviewer.products;

import android.content.res.Resources;

import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.api.Transaction;

import java.util.List;

import javax.inject.Inject;

public class Product {
  private final Resources resources;
  private String sku;
  private String transactionCount;

  @Inject Product(Resources resources) {
    this.resources = resources;
  }

  public String sku() {
    return sku;
  }

  public String transactionCount() {
    return transactionCount;
  }

  void setTransactions(String sku, List<Transaction> transactions) {
    this.sku = sku;
    final int size = transactions.size();
    transactionCount = resources.getQuantityString(R.plurals.transactions, size, size);
  }
}
