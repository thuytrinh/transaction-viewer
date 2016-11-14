package com.thuytrinh.transactionviewer.app;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.thuytrinh.transactionviewer.api.MockRatesFetcher;
import com.thuytrinh.transactionviewer.api.MockTransactionsFetcher;
import com.thuytrinh.transactionviewer.api.RatesFetcher;
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

  @Provides @Singleton Gson gson() {
    return new Gson();
  }

  @Provides TransactionsFetcher transactionsFetcher(Gson gson) {
    return new MockTransactionsFetcher(context.getAssets(), gson);
  }

  @Provides RatesFetcher ratesFetcher(Gson gson) {
    return new MockRatesFetcher(context.getAssets(), gson);
  }

  @Provides @Singleton Action1<Throwable> errorHandler() {
    // On production, we may consider uploading
    // the error to Fabric or sth like that.
    return error -> Log.e(App.class.getSimpleName(), error.getMessage(), error);
  }
}
