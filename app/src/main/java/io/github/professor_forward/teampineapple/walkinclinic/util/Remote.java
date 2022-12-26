package io.github.professor_forward.teampineapple.walkinclinic.util;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class Remote<T> {
    public final Observable<T> data;

    private Remote(Observable<T> data) {
        this.data = data;
    }

    public static <T> Remote<T> fromLoaded(Observable<T> source) {
        return new Remote<>(
                source
        );
    }
}
