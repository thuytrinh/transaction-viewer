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

public class CurrencyGraph {
  public static final NumberFormat GBP_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
  private static final String GBP = "GBP";
  private final Map<String, Map<String, BigDecimal>> graph;
  private final Map<String, Observable<BigDecimal>> rateCache = new ConcurrentHashMap<>();

  public CurrencyGraph(List<Rate> rates) {
    graph = createGraph(rates);
  }

  @DebugLog
  private static Node findConversion(
      String currency,
      Map<String, Map<String, BigDecimal>> graph) {
    final Set<String> visits = new LinkedHashSet<>();
    final Queue<Node> queue = new LinkedList<>();

    queue.add(new Node(null, currency, BigDecimal.ONE));
    while (!queue.isEmpty()) {
      final Node node = queue.poll();
      visits.add(node.currency);
      final Map<String, BigDecimal> neighbors = graph.get(node.currency);
      final Set<String> keys = neighbors.keySet();
      for (String key : keys) {
        if (!visits.contains(key)) {
          final Node n = new Node(node, key, neighbors.get(key));
          queue.add(n);
          if (GBP.equals(key)) {
            return n;
          }
        }
      }
    }
    throw new UnsupportedOperationException("Unknown conversion for " + currency);
  }

  private static Map<String, Map<String, BigDecimal>> createGraph(List<Rate> rates) {
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
          .cache();
      rateCache.put(currency, task);
    }
    return task;
  }

  private BigDecimal asRate(Node gbpNode) {
    BigDecimal v = BigDecimal.ONE;
    while (gbpNode != null) {
      v = v.multiply(gbpNode.rate);
      gbpNode = gbpNode.parent;
    }
    return v;
  }

  private ConversionResult asConversionResult(
      String currency,
      BigDecimal amount,
      BigDecimal amountInGbp) {
    final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
    formatter.setCurrency(Currency.getInstance(currency));
    final String from = formatter.format(amount);

    final String to = GBP_FORMATTER.format(amountInGbp);
    return ImmutableConversionResult.builder()
        .from(from)
        .to(to)
        .amountInGbp(amountInGbp)
        .build();
  }

  private static class Node {
    final Node parent;
    final String currency;
    final BigDecimal rate;

    Node(Node parent, String currency, BigDecimal rate) {
      this.parent = parent;
      this.currency = currency;
      this.rate = rate;
    }
  }

  static {
    GBP_FORMATTER.setCurrency(Currency.getInstance(GBP));
  }
}
