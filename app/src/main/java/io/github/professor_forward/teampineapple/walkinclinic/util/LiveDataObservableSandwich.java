package io.github.professor_forward.teampineapple.walkinclinic.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.base.Optional;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public final class LiveDataObservableSandwich<T> {
    public final MutableLiveData<T> input;
    public final Observable<T> inputInternal;
    public final Observable<Optional<String>> outputInternal;
    public final LiveData<String> output;

    public LiveDataObservableSandwich(
            T initialValue,
            Function<Observable<T>, Observable<Optional<String>>> transformer
    ) {
        input = new MutableLiveData<>(initialValue);
        inputInternal = ReactiveUtil.liveDataToObservable(input);

        try {
            output = ReactiveUtil.observableToNullableLiveData(
                    outputInternal = transformer.apply(inputInternal)
            );
        } catch (Exception e) {
            throw new RuntimeException("LiveDataObservableSandwich transformer threw exception", e);
        }
    }
}
