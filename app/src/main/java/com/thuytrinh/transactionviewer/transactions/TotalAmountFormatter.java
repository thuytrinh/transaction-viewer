package com.thuytrinh.transactionviewer.transactions;

import android.content.res.Resources;

import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.conversion.ConversionResult;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import static com.thuytrinh.transactionviewer.conversion.CurrencyGraph.GBP_FORMATTER;

public class TotalAmountFormatter {
  private final Resources resources;

  @Inject TotalAmountFormatter(Resources resources) {
    this.resources = resources;
  }

  public String computeAndFormatTotal(List<ConversionResult> results) {
    BigDecimal total = BigDecimal.ZERO;
    for (ConversionResult result : results) {
      total = total.add(result.amountInGbp());
    }
    return resources.getString(R.string.total_x, GBP_FORMATTER.format(total));
  }
}
