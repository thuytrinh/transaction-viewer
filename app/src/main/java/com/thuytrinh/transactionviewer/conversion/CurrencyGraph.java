package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.Rate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.schedulers.Schedulers;

public class CurrencyGraph {
  public static final NumberFormat GBP_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
  private final Map<String, Map<String, BigDecimal>> graph;
  private final ConversionFinder conversionFinder;
  private final Map<String, Observable<BigDecimal>> rateCache = new ConcurrentHashMap<>();

  CurrencyGraph(List<Rate> rates, ConversionFinder conversionFinder) {
    graph = createGraph(rates);
    this.conversionFinder = conversionFinder;
  }

  static Map<String, Map<String, BigDecimal>> createGraph(List<Rate> rates) {
    final Map<String, Map<String, BigDecimal>> g = new HashMap<>();
    for (int i = 0, size = rates.size(); i < size; i++) {
      final Rate rate = rates.get(i);
      final String from = rate.from();
      Map<String, BigDecimal> neighbors = g.get(from);
      if (neighbors == null) {
        neighbors = new HashMap<>();
        g.put(from, neighbors);
      }
      neighbors.put(rate.to(), rate.rate());
    }
    return g;
  }

  static BigDecimal asRate(Path path) {
    BigDecimal v = BigDecimal.ONE;
    while (path != null) {
      v = v.multiply(path.rate());
      path = path.parent();
    }
    return v;
  }

  static ConversionResult asConversionResult(
      String currency,
      BigDecimal originalAmount,
      BigDecimal amountInGbp) {
    final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
    formatter.setCurrency(Currency.getInstance(currency));
    return ImmutableConversionResult.builder()
        .originalAmountText(formatter.format(originalAmount))
        .amountInGbpText(GBP_FORMATTER.format(amountInGbp))
        .amountInGbp(amountInGbp)
        .build();
  }

  public Observable<ConversionResult> asGbpAsync(String currency, BigDecimal amount) {
    if (ConversionFinder.GBP.equals(currency)) {
      return Observable.fromCallable(() -> asConversionResult(
          currency,
          amount,
          amount
      ));
    }
    return getRateAsync(currency)
        .map(amount::multiply)
        .map(amountInGbp -> asConversionResult(currency, amount, amountInGbp));
  }

  private synchronized Observable<BigDecimal> getRateAsync(String currency) {
    Observable<BigDecimal> task = rateCache.get(currency);
    if (task == null) {
      task = Observable
          .fromCallable(() -> conversionFinder.call(currency, graph))
          .map(CurrencyGraph::asRate)
          .cache()
          .subscribeOn(Schedulers.computation());
      rateCache.put(currency, task);
    }
    return task;
  }

  static {
    GBP_FORMATTER.setCurrency(Currency.getInstance(Locale.UK));
  }
}
