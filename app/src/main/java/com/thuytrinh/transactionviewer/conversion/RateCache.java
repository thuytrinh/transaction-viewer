package com.thuytrinh.transactionviewer.conversion;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Asynchronously computes exchange rate between
 * an arbitrary currency and `GBP`, and cache it for later access.
 */
class RateCache {
  private final Map<String, Observable<BigDecimal>> cache = new ConcurrentHashMap<>();

  @Inject RateCache() {}

  static BigDecimal asRate(Path path) {
    BigDecimal v = BigDecimal.ONE;
    while (path != null) {
      v = v.multiply(path.rate());
      path = path.parent();
    }
    return v;
  }

  synchronized Observable<BigDecimal> getRateAsync(
      String currency,
      ConversionFinder conversionFinder,
      Map<String, Map<String, BigDecimal>> graph) {
    Observable<BigDecimal> task = cache.get(currency);
    if (task == null) {
      task = Observable
          .fromCallable(() -> conversionFinder.call(currency, graph))
          .map(RateCache::asRate)
          .cache()
          .subscribeOn(Schedulers.computation());
      cache.put(currency, task);
    }
    return task;
  }
}
