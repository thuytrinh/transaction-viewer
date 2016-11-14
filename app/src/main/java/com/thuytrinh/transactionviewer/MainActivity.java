package com.thuytrinh.transactionviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thuytrinh.transactionviewer.products.ProductsFragment;

public class MainActivity extends AppCompatActivity {
  public boolean hasTransactionsLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    hasTransactionsLayout = findViewById(R.id.transactionsLayout) != null;

    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.productsLayout, new ProductsFragment())
          .commit();
    }
  }
}
