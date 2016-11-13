package com.thuytrinh.transactionviewer.app;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.thuytrinh.transactionviewer.api.MockTransactionsFetcher;
import com.thuytrinh.transactionviewer.api.TransactionsFetcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.functions.Action1;

@Module
class AppModule {
  private final Context context;

  AppModule(Context context) {
    this.context = context;
  }

  @Provides Resources resources() {
    return context.getResources();
  }

  @Provides TransactionsFetcher transactionsFetcher() {
    return new MockTransactionsFetcher(context.getAssets(), new Gson());
  }

  @Provides @Singleton Action1<Throwable> errorHandler() {
    return error -> Log.e(App.class.getSimpleName(), error.getMessage(), error);
  }
}
