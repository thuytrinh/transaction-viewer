package com.thuytrinh.transactionviewer.api;

import java.util.List;

import rx.Observable;

public interface RatesFetcher {
  Observable<List<Rate>> fetchRatesAsync();
}
