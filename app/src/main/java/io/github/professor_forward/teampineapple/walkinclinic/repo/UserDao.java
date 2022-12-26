package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
interface UserDao extends AbstractDao<User> {
    @Query("SELECT COUNT(*) FROM users WHERE email=:email")
    Single<Boolean> exists(String email);

    @Query("SELECT * FROM users WHERE email=:email")
    Observable<User> getByEmail(String email);

    @Query("SELECT * FROM users WHERE id=:id")
    Observable<User> getById(String id);

    @Query("SELECT * FROM users WHERE username LIKE :string OR email LIKE :string ORDER BY LOWER(username)")
    DataSource.Factory<Integer, User> getSearchDataSource(String string);

    @Query("SELECT * FROM users ORDER BY LOWER(username)")
    DataSource.Factory<Integer, User> getSearchDataSource();

    @Query("SELECT * from users ORDER BY LOWER(username)")
    Observable<List<User>> getUsersByName();

    @Query("SELECT * FROM users WHERE email=:email AND hashedPassword=:hashedPassword")
    Maybe<User> login(String email, String hashedPassword);
}
