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

public class MockRatesFetcher implements RatesFetcher {
  private final Type ratesType = new TypeToken<List<Rate>>() {}.getType();
  private final AssetManager assetManager;
  private final Gson gson;

  public MockRatesFetcher(AssetManager assetManager, Gson gson) {
    this.assetManager = assetManager;
    this.gson = gson;
  }

  @Override public Observable<List<Rate>> fetchRatesAsync() {
    return Observable
        .fromCallable(new Callable<List<Rate>>() {
          @Override public List<Rate> call() throws Exception {
            InputStreamReader streamReader = null;
            try {
              final InputStream stream = assetManager.open("rates.json");
              streamReader = new InputStreamReader(stream);
              final JsonReader jsonReader = gson.newJsonReader(streamReader);
              return gson.fromJson(jsonReader, ratesType);
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
