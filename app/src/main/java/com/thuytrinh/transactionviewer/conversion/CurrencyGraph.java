package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.Rate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CurrencyGraph {
  public static final NumberFormat GBP_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
  private static final String GBP = "GBP";
  private final Map<String, Map<String, BigDecimal>> graph;
  private final Map<String, Observable<BigDecimal>> rateCache = new ConcurrentHashMap<>();

  public CurrencyGraph(List<Rate> rates) {
    graph = createGraph(rates);
  }

  @DebugLog
  private static Path findConversion(
      String currency,
      Map<String, Map<String, BigDecimal>> graph) {
    final Set<String> visits = new LinkedHashSet<>();
    final Queue<Path> queue = new LinkedList<>();

    queue.add(ImmutablePath.of(null, currency, BigDecimal.ONE));
    while (!queue.isEmpty()) {
      final Path path = queue.poll();
      visits.add(path.currency());
      final Map<String, BigDecimal> neighbors = graph.get(path.currency());
      final Set<String> keys = neighbors.keySet();
      for (String key : keys) {
        if (!visits.contains(key)) {
          final Path newPath = ImmutablePath.of(path, key, neighbors.get(key));
          queue.add(newPath);
          if (GBP.equals(key)) {
            return newPath;
          }
        }
      }
    }
    throw new UnsupportedOperationException("Unknown conversion for " + currency);
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

  public Observable<ConversionResult> asGbpAsync(String currency, BigDecimal amount) {
    if (GBP.equals(currency)) {
      return Observable.fromCallable(() -> asConversionResult(
          currency,
          amount,
          amount
      ));
    } else if (!graph.containsKey(currency)) {
      return Observable.error(new UnsupportedOperationException(
          "Unknown currency: " + currency
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
          .fromCallable(() -> findConversion(currency, graph))
          .map(this::asRate)
          .cache()
          .subscribeOn(Schedulers.computation());
      rateCache.put(currency, task);
    }
    return task;
  }

  private BigDecimal asRate(Path path) {
    BigDecimal v = BigDecimal.ONE;
    while (path != null) {
      v = v.multiply(path.rate());
      path = path.parent();
    }
    return v;
  }

  private ConversionResult asConversionResult(
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

  static {
    GBP_FORMATTER.setCurrency(Currency.getInstance(GBP));
  }
}
