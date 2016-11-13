package com.thuytrinh.transactionviewer.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TransactionsActivity extends AppCompatActivity {
  public static Intent newIntent(Context context, String sku) {
    return new Intent(context, TransactionsActivity.class)
        .putExtras(TransactionsViewModel.newArgs(sku));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      final TransactionsFragment fragment = new TransactionsFragment();
      fragment.setArguments(getIntent().getExtras());

      getSupportFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, fragment)
          .commit();
    }
  }
}
