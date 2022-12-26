package io.github.professor_forward.teampineapple.walkinclinic.global;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.github.professor_forward.teampineapple.walkinclinic.repo.User;
import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRole;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * The data source for new Sessions, responsible for interacting with UserRepo to perform registration and login.
 */
class SessionDataSource {
    private final UserRepo userRepo;

    SessionDataSource(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Attempt to create a new Session using email and password.
     *
     * @param email the user's email
     * @param rawPassword the user's plaintext password
     * @return the new login state, if successful
     */
    Maybe<Session> login(String email, String rawPassword) {
        return userRepo.login(email, rawPassword)
                .delay(1, TimeUnit.SECONDS)
                .doOnSuccess(user -> Log.i("SessionDataSource", "Logged in user: " + user))
                .doOnComplete(() -> Log.i("SessionDataSource", "Login rejected"))
                .doOnError(x -> Log.w("SessionDataSource", "Login failed", x))
                .map(user -> new Session(user.id, user.username, user.roleId))
                ;
    }

    Maybe<Session> register(String name, String email, String password, String role){
        return userRepo.create(name, email, password, role)
                .andThen(userRepo.login(email, password))
                .doOnSuccess(user -> Log.i("SessionDataSource", "Registered user: " + user))
                .doOnComplete(() -> Log.i("SessionDataSource", "Registration rejected"))
                .doOnError(x -> Log.w("SessionDataSource", "Registration failed", x))
                .map(user -> new Session(user.id, user.username, user.roleId))
                ;
    }

    Observable<? extends UserRole> getRole(String id) {
        return userRepo.getById(id)
                .data
                .flatMap(User::getRole)
                ;
    }

    /**
     * Attempt to logout.
     *
     * @return a Completable with the current completedness of the logout procedure
     */
    Completable logout(Session session) {
        return Completable.fromAction(() -> {
            // TODO Logout from Firebase
        })
                .delay(1, TimeUnit.SECONDS)
                ;
    }
}
