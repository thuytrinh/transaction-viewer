package com.thuytrinh.transactionviewer.transactions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TransactionsActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, new TransactionsFragment())
          .commit();
    }
  }
}
