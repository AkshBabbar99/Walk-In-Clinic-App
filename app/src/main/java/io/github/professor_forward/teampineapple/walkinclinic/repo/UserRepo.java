package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.professor_forward.teampineapple.walkinclinic.util.Remote;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class UserRepo extends AbstractRepo<User> {
    public static final String ADMIN_EMAIL = "admin@a4word.com";

    private final LocalDb db;
    private final Repository repo;

    UserRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<User> dao() {
        return db.users();
    }

    public Completable create(@NonNull String name, @NonNull String email, @NonNull String password, @NonNull String roleId) {
        return create(new User(UUID.randomUUID().toString(), name, email, User.digestPassword(password), roleId));
    }

    Completable create(@NonNull User user) {
        return super.create(user)
                .andThen(Completable.defer(() -> {
                    if (user.getRole() != null) return Completable.complete();
                    switch (user.roleId) {
                        case AdminRole.ROLE_KEY:
                            return Completable.complete();
                        case PatientRole.ROLE_KEY:
                            return repo.patientRoles().create(user);
                        case EmployeeRole.ROLE_KEY:
                            return repo.employeeRoles().create(user.id, null);
                    }
                    throw new IllegalStateException("Invalid role key");
                }))
                .onErrorResumeNext(e -> {
                    Log.e("UserRepo", e.toString());
                    return Completable.error(e instanceof SQLiteConstraintException ? UserWithEmailExistsException::new : UserCreateException::new);
                })
                ;
    }

    public Completable delete(@NonNull Iterable<User> users) {
        return super.delete(users)
                .andThen(Completable.defer(() -> {
                    List<String> ids = new ArrayList<>();
                    for (User user : users) {
                        ids.add(user.id);
                    }


                    return Completable.concatArray(
                            repo.employeeRoles().delete(ids),
                            repo.patientRoles().delete(ids)
                    );
                }))
                ;
    }

    public Remote<User> getByEmail(String email) {
        return Remote.fromLoaded(
                Observable.defer(() -> db.users().getByEmail(email))
                        .doOnNext(this::onLoad)
                        .subscribeOn(Schedulers.io())
        );
    }

    public Remote<User> getById(String id) {
        return Remote.fromLoaded(
                Observable.defer(() -> db.users().getById(id))
                        .doOnNext(this::onLoad)
                        .subscribeOn(Schedulers.io())
        );
    }

    public Observable<PagedList<User>> search(String string) {
        return Single.fromCallable(() -> getSearchDataSource(string))
                .flatMapObservable(ds -> new RxPagedListBuilder<>(ds, Repository.PAGE_SIZE).buildObservable())
                .doOnNext(list -> {
                    for (User user : list.snapshot()) {
                        if (user.getRole() == null) {
                            onLoad(user);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    private DataSource.Factory<Integer, User> getSearchDataSource(String string) {
        if (string.isEmpty()) return db.users().getSearchDataSource();

        return db.users().getSearchDataSource("%" + string + "%");
    }

    public Maybe<User> login(String email, String rawPassword) {
        // XXX: Support the requirement that there be a hardcoded admin account which one can log in
        // to using admin/5T5ptQ. We use email/password instead of username/password to allow support
        // for Firebase in the future.
        if (email.equals("admin")) {
            email = UserRepo.ADMIN_EMAIL;
        }

        return loginInternal(email, rawPassword);
    }

    private Maybe<User> loginInternal(String email, String rawPassword) {
        return Single.fromCallable(() -> User.digestPassword(rawPassword))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapMaybe(hashedPassword -> db.users().login(email, hashedPassword))
                .doOnSuccess(this::onLoad)
                ;
    }


    private void onLoad(User user) {
        if (user == null) return;
        Observable<? extends UserRole> role;

        switch (user.roleId) {
            case AdminRole.ROLE_KEY:
                role = Observable.just(new AdminRole());
                break;
            case EmployeeRole.ROLE_KEY:
                role = repo.employeeRoles().getById(user.id);
                break;
            case PatientRole.ROLE_KEY:
                role = repo.patientRoles().getById(user.id);
                break;
            default:
                throw new IllegalStateException("Bad patient role from db: " + user.roleId);
        }
        user.setRole(role);
    }
}
