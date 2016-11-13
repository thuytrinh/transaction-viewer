package com.thuytrinh.transactionviewer.transactions;

import com.thuytrinh.transactionviewer.util.ActivityScope;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent
public interface TransactionsComponent {
  void inject(TransactionsFragment fragment);
}
