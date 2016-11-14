package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.RatesFetcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton
public class RateRepository {
  private final Observable<CurrencyGraph> getCurrencyGraphAsync;

  @Inject RateRepository(
      Lazy<RatesFetcher> ratesFetcherLazy,
      Lazy<ConversionFinder> conversionFinderLazy,
      Lazy<RateCache> rateCacheLazy) {
    getCurrencyGraphAsync = Observable
        .defer(() -> ratesFetcherLazy.get().fetchRatesAsync())
        .map((rates) -> new CurrencyGraph(
            rates,
            conversionFinderLazy.get(),
            rateCacheLazy.get()
        ))
        .cache();
  }

  public Observable<CurrencyGraph> getCurrencyGraphAsync() {
    return getCurrencyGraphAsync;
  }
}
