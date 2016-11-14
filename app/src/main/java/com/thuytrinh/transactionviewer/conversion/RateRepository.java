package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.RatesFetcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class RateRepository {
  private final Observable<CurrencyGraph> getCurrencyGraphAsync;

  @Inject public RateRepository(RatesFetcher ratesFetcher) {
    getCurrencyGraphAsync = Observable.defer(ratesFetcher::fetchRatesAsync)
        .map(CurrencyGraph::new)
        .cache();
  }

  public Observable<CurrencyGraph> getCurrencyGraphAsync() {
    return getCurrencyGraphAsync;
  }
}
