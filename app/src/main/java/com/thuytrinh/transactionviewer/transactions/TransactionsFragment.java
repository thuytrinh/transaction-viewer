package com.thuytrinh.transactionviewer.transactions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thuytrinh.transactionviewer.BR;
import com.thuytrinh.transactionviewer.R;
import com.thuytrinh.transactionviewer.app.App;
import com.thuytrinh.transactionviewer.databinding.TransactionsBinding;

import javax.inject.Inject;

import me.tatarka.bindingcollectionadapter.ItemView;

public class TransactionsFragment extends Fragment {
  @Inject TransactionsViewModel viewModel;

  public static ItemView conversionResultView() {
    return ItemView.of(BR.viewModel, R.layout.conversion_result);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    App.component().transactionsComponent().inject(this);
    viewModel.loadTransactions(getArguments());
  }

  @Nullable @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.transactions, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    TransactionsBinding.bind(view).setViewModel(viewModel);
  }
}
