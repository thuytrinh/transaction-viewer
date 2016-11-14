package com.thuytrinh.transactionviewer.util;

import android.databinding.BindingAdapter;
import android.support.design.widget.Snackbar;
import android.view.View;

public final class Bindings {
  private Bindings() {}

  @BindingAdapter("error")
  public static void setError(View v, String error) {
    if (error != null) {
      Snackbar.make(v, error, Snackbar.LENGTH_SHORT).show();
    }
  }
}
