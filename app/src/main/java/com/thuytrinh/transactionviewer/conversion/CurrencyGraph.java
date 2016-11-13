package com.thuytrinh.transactionviewer.conversion;

import com.thuytrinh.transactionviewer.api.Rate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import rx.Observable;

public class CurrencyGraph {
  private static final String GBP = "GBP";
  private final Map<String, Map<String, BigDecimal>> graph;

  public CurrencyGraph(List<Rate> rates) {
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
    graph = g;
  }

  public Observable<ConversionResult> asGbpAsync(String currency, BigDecimal amount) {
    if (GBP.equals(currency)) {
      final String v = amount.toString();
      return Observable.just(
          ImmutableConversionResult.builder()
              .from(v)
              .to(v)
              .build()
      );
    } else if (!graph.containsKey(currency)) {
      return Observable.error(new UnsupportedOperationException(
          "Unknown currency: " + currency
      ));
    }
    return Observable
        .fromCallable(() -> {
          final Set<String> visits = new LinkedHashSet<>();
          final Queue<Node> queue = new LinkedList<>();

          queue.add(new Node(null, currency, amount));
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
        })
        .map(x -> {
          BigDecimal v = BigDecimal.ONE;
          while (x != null) {
            v = v.multiply(x.amount);
            x = x.parent;
          }
          return v;
        })
        .map(x -> ImmutableConversionResult.builder()
            .from(amount.toString())
            .to(x.toString())
            .build()
        );
  }

  private static class Node {
    final Node parent;
    final String currency;
    final BigDecimal amount;

    Node(Node parent, String currency, BigDecimal amount) {
      this.parent = parent;
      this.currency = currency;
      this.amount = amount;
    }
  }
}
