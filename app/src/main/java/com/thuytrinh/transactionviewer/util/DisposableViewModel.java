package com.thuytrinh.transactionviewer.util;

import rx.Observable;
import rx.subjects.PublishSubject;

public class DisposableViewModel {
  private final PublishSubject<Void> disposalSignal = PublishSubject.create();

  public void dispose() {
    disposalSignal.onNext(null);
  }

  public Observable<Void> onDispose() {
    return disposalSignal.asObservable();
  }
}
