package com.thuytrinh.transactionviewer.products;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thuytrinh.transactionviewer.BR;
import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.app.App;
import com.thuytrinh.transactionviewer.databinding.ProductsBinding;

import javax.inject.Inject;

import me.tatarka.bindingcollectionadapter.ItemView;

public class ProductsFragment extends Fragment {
  @Inject ProductsViewModel viewModel;

  public static ItemView productView() {
    return ItemView.of(BR.viewModel, R.layout.product);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    App.component().productsComponent().inject(this);
    viewModel.loadTransactions();
  }

  @Nullable @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.products, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    ProductsBinding.bind(view).setViewModel(viewModel);
  }
}
