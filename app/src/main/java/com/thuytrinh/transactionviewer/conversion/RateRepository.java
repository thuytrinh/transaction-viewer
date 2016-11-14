package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.RatesFetcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class RateRepository {
  private final Observable<CurrencyGraph> getCurrencyGraphAsync;

  @Inject RateRepository(
      RatesFetcher ratesFetcher,
      ConversionFinder conversionFinder) {
    getCurrencyGraphAsync = Observable.defer(ratesFetcher::fetchRatesAsync)
        .map((rates) -> new CurrencyGraph(rates, conversionFinder))
        .cache();
  }

  public Observable<CurrencyGraph> getCurrencyGraphAsync() {
    return getCurrencyGraphAsync;
  }
}
