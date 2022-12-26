package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.view.View;

@FunctionalInterface
public interface OpenListener<T> {
    void open(View view, T item);
}
