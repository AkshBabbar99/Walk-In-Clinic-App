package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public abstract class AbstractRepo<T> {
    protected abstract AbstractDao<T> dao();

    Completable create(@NonNull T item) {
        return dao().insert(item)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> Log.d("AbstractRepo", "created " + item))
                .doOnError(e -> Log.e("AbstractRepo", "Error creating: " + e.toString()))
                ;
    }

    public Completable delete(@NonNull T item) {
        ArrayList<T> arrayList = new ArrayList<>();
        arrayList.add(item);
        return delete(arrayList);
    }

    Completable delete(@NonNull Iterable<T> items) {
        return dao().delete(items)
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Log.e("AbstractRepo", "Error deleting: " + e.toString()))
                ;
    }

    Completable update(@NonNull T item) {
        return dao().update(item)
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Log.e("AbstractRepo", "Error updating: " + e.toString()))
                ;
    }
}
