package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import io.reactivex.Completable;

public interface AbstractDao<T> {
    @Delete
    Completable delete(Iterable<T> items);

    @Insert
    Completable insert(T item);

    @Insert
    Completable insert(Iterable<T> items);

    @Update
    Completable update(T item);
}
