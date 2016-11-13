package com.thuytrinh.transactionviewer.products;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.thuytrinh.transactionviewer.R;

import javax.inject.Inject;

import rx.subjects.PublishSubject;

public class ProductViewModel implements Comparable<ProductViewModel> {
  private final Resources resources;
  private String sku;
  private String transactionCount;
  private PublishSubject<String> onSelected;

  @Inject ProductViewModel(Resources resources) {
    this.resources = resources;
  }

  public String sku() {
    return sku;
  }

  public void select() {
    onSelected.onNext(sku);
  }

  public String transactionCount() {
    return transactionCount;
  }

  @Override public int compareTo(@NonNull ProductViewModel viewModel) {
    return sku.compareTo(viewModel.sku);
  }

  public void setProduct(Product product) {
    sku = product.sku();
    final int size = product.transactions().size();
    transactionCount = resources.getQuantityString(R.plurals.transactions, size, size);
  }

  void setOnSelected(PublishSubject<String> onSelected) {
    this.onSelected = onSelected;
  }
}
