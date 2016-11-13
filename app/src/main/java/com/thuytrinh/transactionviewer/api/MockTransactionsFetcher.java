package com.thuytrinh.transactionviewer.api;

import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.schedulers.Schedulers;

public class MockTransactionsFetcher implements TransactionsFetcher {
  private final Type productsType = new TypeToken<List<Transaction>>() {}.getType();
  private final AssetManager assetManager;
  private final Gson gson;

  public MockTransactionsFetcher(AssetManager assetManager, Gson gson) {
    this.assetManager = assetManager;
    this.gson = gson;
  }

  @Override public Observable<List<Transaction>> fetchTransactionsAsync() {
    return Observable
        .fromCallable(new Callable<List<Transaction>>() {
          @Override public List<Transaction> call() throws Exception {
            InputStreamReader streamReader = null;
            try {
              final InputStream stream = assetManager.open("transactions.json");
              streamReader = new InputStreamReader(stream);
              final JsonReader jsonReader = gson.newJsonReader(streamReader);
              return gson.fromJson(jsonReader, productsType);
            } finally {
              if (streamReader != null) {
                streamReader.close();
              }
            }
          }
        })
        .subscribeOn(Schedulers.io());
  }
}
