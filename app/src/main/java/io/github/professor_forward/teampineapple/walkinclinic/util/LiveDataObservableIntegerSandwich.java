package io.github.professor_forward.teampineapple.walkinclinic.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public final class LiveDataObservableIntegerSandwich {
    public final MutableLiveData<String> input;
    public final Observable<String> inputInternal;
    public final Observable<Optional<String>> outputInternal;
    public final LiveData<String> output;

    public LiveDataObservableIntegerSandwich(
            Integer initialValue,
            Function<Integer, Optional<String>> validator
    ) {
        input = new MutableLiveData<>(Integer.toString(initialValue));
        inputInternal = ReactiveUtil.liveDataToObservable(input);

        try {
            output = ReactiveUtil.observableToNullableLiveData(
                    outputInternal = inputInternal.map(x -> {
                        Integer y;
                        try {
                            y = Integer.parseInt(x);
                        }
                        catch (NumberFormatException e) {
                            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_number));
                        }
                        return validator.apply(y);
                    })
            );
        } catch (Exception e) {
            throw new RuntimeException("LiveDataObservableSandwich transformer threw exception", e);
        }
    }

    public int getValue() {
        String val = input.getValue();
        return val == null ? -1 : Integer.parseInt(val);
    }

    public void setValue(int value) {
        input.setValue(Integer.toString(value));
    }
}

