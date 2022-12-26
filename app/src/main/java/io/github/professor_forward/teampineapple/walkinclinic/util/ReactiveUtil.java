package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.common.base.Optional;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public final class ReactiveUtil {
    @SuppressLint("CheckResult")
    public static <T> LiveData<T> observableToLiveData(Observable<T> src) {
        MutableLiveData<T> result = new MutableLiveData<>();
        //noinspection ResultOfMethodCallIgnored
        src.subscribe(result::postValue);
        return result;
    }

    @SuppressLint("CheckResult")
    public static <T> LiveData<T> observableToNullableLiveData(Observable<Optional<T>> src) {
        MutableLiveData<T> result = new MutableLiveData<>();
        //noinspection ResultOfMethodCallIgnored
        src.subscribe(x -> result.postValue(x.orNull()));
        return result;
    }

    public static void runOnMainThread(Runnable task) {
        Completable.fromRunnable(task).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public static <T> void observeOnce(@NonNull LiveData<T> src, @NonNull LifecycleOwner owner,
                                       @NonNull Observer<T> observer) {
        src.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T data) {
                if (data != null) {
                    observer.onChanged(data);
                    src.removeObserver(this);
                }
            }
        });
    }

    public static <T> Observable<T> liveDataToObservable(@NonNull LiveData<T> src) {
        return Observable.create(emitter -> {
            Observer<T> observer = emitter::onNext;
            emitter.setCancellable(() -> src.removeObserver(observer));
            src.observeForever(observer);
        });
    }

    private ReactiveUtil() {}
}
