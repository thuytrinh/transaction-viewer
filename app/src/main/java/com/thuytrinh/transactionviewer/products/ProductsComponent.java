package com.thuytrinh.transactionviewer.products;

import com.thuytrinh.transactionviewer.util.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface ProductsComponent {
  void inject(ProductsFragment fragment);
}
