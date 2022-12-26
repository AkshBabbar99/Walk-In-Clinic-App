package io.github.professor_forward.teampineapple.walkinclinic.global;

import android.content.SharedPreferences;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRole;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository responsible for the application's global session state. To log in/out, call SessionRepo
 * methods which interface with the UserRepo backend and then update the global session state.
 */
public class SessionRepo {
    private final SessionCache cache;
    private final SessionDataSource ds;

    /**
     * Create the application level SessionRepo
     *
     * @param prefs
     * @param userRepo the UserRepo singleton
     */
    public SessionRepo(SharedPreferences prefs, UserRepo userRepo) {
        cache = new SessionCache(prefs);
        ds = new SessionDataSource(userRepo);
    }

    /**
     * Get the current global login state.
     *
     * @return an Observable Session state
     */
    public Observable<Optional<Session>> getCurrentSession() {
        return cache.getCurrentSession()
                .doOnNext(session -> {
                    if (session.isPresent() && session.get().getRole() == null) {
                        session.get().setRole(ds.getRole(session.get().token));
                    }
                })
                .subscribeOn(Schedulers.io())
                ;
    }

    public Observable<UserRole> getCurrentRole() {
        return getCurrentSession()
                .flatMap(x -> x.isPresent() ? x.get().getRole() : Observable.just(new UnauthenticatedUser()))
                ;
    }

    /**
     * Attempt to log in using email and password. Updates the global login state on success or failure.
     *
     * @param email the user's email
     * @param rawPassword the user's plaintext password
     * @return the new login state, if successful
     */
    public Maybe<Session> login(String email, String rawPassword) {
        return ds.login(email, rawPassword)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(cache::saveLogin)
                .doOnComplete(cache::saveLogout)
                ;
    }

    public Maybe<Session> register(String name, String email, String password, String role) {
        return ds.register(name, email, password, role)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(cache::saveLogin)
                .doOnComplete(cache::saveLogout)
                ;
    }

    /**
     * Attempt to logout. Updates the global session state.
     *
     * @return a Completable with the current completedness of the logout procedure
     */
    public Completable logout() {
        return getCurrentSession()
                .firstOrError()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMapCompletable(ds::logout)
                .doFinally(cache::saveLogout)
                ;
    }
}
