package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.Rate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Observable;

/**
 * A graph that converts amounts in arbitrary currencies into `GBP` asynchronously.
 */
public class CurrencyGraph {
  public static final NumberFormat GBP_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
  private final Map<String, Map<String, BigDecimal>> graph;
  private final ConversionFinder conversionFinder;
  private final RateCache rateCache;

  CurrencyGraph(
      List<Rate> rates,
      ConversionFinder conversionFinder,
      RateCache rateCache) {
    graph = createGraph(rates);
    this.conversionFinder = conversionFinder;
    this.rateCache = rateCache;
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
    return rateCache.getRateAsync(currency, conversionFinder, graph)
        .map(amount::multiply)
        .map(amountInGbp -> asConversionResult(currency, amount, amountInGbp));
  }

  static {
    GBP_FORMATTER.setCurrency(Currency.getInstance(Locale.UK));
  }
}
