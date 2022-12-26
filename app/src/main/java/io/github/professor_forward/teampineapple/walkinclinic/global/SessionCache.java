package io.github.professor_forward.teampineapple.walkinclinic.global;

import android.content.SharedPreferences;

import com.google.common.base.Optional;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Saves the currently logged-in user so that app restarts don't log users out.
 */
class SessionCache {
    private static final String USER_TOKEN_PREFS_KEY = "currentUserToken";
    private static final String USER_NAME_PREFS_KEY = "currentUserName";
    private static final String USER_ROLE_PREFS_KEY = "currentUserRole";
    private final SharedPreferences prefs;
    private final BehaviorSubject<Optional<Session>> currentSession;
    private final Subject<Optional<Session>> updateSession = PublishSubject.create();

    private Optional<Session> load() {
        String currentUserToken = prefs.getString(USER_TOKEN_PREFS_KEY, null);
        if (currentUserToken == null) {
            return Optional.absent();
        }
        String username = prefs.getString(USER_NAME_PREFS_KEY, null);
        String role = prefs.getString(USER_ROLE_PREFS_KEY, null);
        Session session = new Session(currentUserToken, username, role);
        return Optional.of(session);
    }

    /**
     * Create a new SessionCache. Loads the current session state from SharedPreferences to maintain
     * session state across app restarts.
     *
     * @param prefs
     */
    SessionCache(SharedPreferences prefs) {
        this.prefs = prefs;
        currentSession = BehaviorSubject.createDefault(load());
        updateSession
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(x -> {
                    Session session = x.orNull();
                    if (session == null) {
                        prefs.edit()
                                .remove(USER_TOKEN_PREFS_KEY)
                                .remove(USER_NAME_PREFS_KEY)
                                .remove(USER_ROLE_PREFS_KEY)
                                .apply();
                    } else {
                        prefs.edit()
                                .putString(USER_TOKEN_PREFS_KEY, session.token)
                                .putString(USER_NAME_PREFS_KEY, session.username)
                                .putString(USER_ROLE_PREFS_KEY, session.roleId)
                                .apply();
                    }
                })
                .subscribe(currentSession)
        ;
    }

    /**
     * Get the Session representing the currently logged in user.
     *
     * @return a Session
     */
    Observable<Optional<Session>> getCurrentSession() {
        return currentSession;
    }

    /**
     * Update the cache with the newly logged-in user.
     *
     * @param session
     */
    void saveLogin(Session session) {
        updateSession.onNext(Optional.of(session));
    }

    /**
     * Clear the cache of session state when the user has logged out.
     */
    void saveLogout() {
        updateSession.onNext(Optional.absent());
    }
}
