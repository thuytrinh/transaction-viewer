package com.thuytrinh.transactionviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thuytrinh.transactionviewer.products.ProductsFragment;

public class MainActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.productsLayout, new ProductsFragment())
          .commit();
    }
  }
}
