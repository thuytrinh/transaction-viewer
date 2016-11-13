package com.thuytrinh.transactionviewer.api;

import java.util.List;

import rx.Observable;

public interface TransactionsFetcher {
  Observable<List<Transaction>> fetchTransactionsAsync();
}
