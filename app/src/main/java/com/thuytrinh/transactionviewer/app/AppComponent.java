package com.thuytrinh.transactionviewer.app;

import com.thuytrinh.transactionviewer.products.ProductsComponent;
import com.thuytrinh.transactionviewer.transactions.TransactionsComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
  ProductsComponent productsComponent();
  TransactionsComponent transactionsComponent();
}
