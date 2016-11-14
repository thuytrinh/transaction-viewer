package com.thuytrinh.transactionviewer.conversion;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.inject.Inject;

/**
 * A BFS-based strategy to convert one unit of a currency into `GBP`.
 */
class ConversionFinder {
  static final String GBP = "GBP";

  @Inject ConversionFinder() {}

  Path call(
      String currency,
      Map<String, Map<String, BigDecimal>> graph) {
    if (!graph.containsKey(currency)) {
      throw new UnsupportedOperationException(
          "Unknown currency: " + currency
      );
    }
    final Set<String> visits = new LinkedHashSet<>();
    final Queue<Path> queue = new LinkedList<>();

    queue.add(ImmutablePath.of(null, currency, BigDecimal.ONE));
    while (!queue.isEmpty()) {
      final Path path = queue.poll();
      visits.add(path.currency());
      final Map<String, BigDecimal> neighbors = graph.get(path.currency());
      if (neighbors != null) {
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
    }
    throw new UnsupportedOperationException("Unknown conversion for " + currency);
  }
}
