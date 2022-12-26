package io.github.professor_forward.teampineapple.walkinclinic.global;


import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRole;
import io.reactivex.Observable;

/**
 * Immutable storage for the currently logged in user.
 */
public class Session {
    public final String token;
    public final String username;
    public final String roleId;
    private Observable<? extends UserRole> role;

    public Session(String token, String username, String roleId) {
        this.token = token;
        this.username = username;
        this.roleId = roleId;
    }

    public Observable<? extends UserRole> getRole() {
        return role;
    }

    void setRole(Observable<? extends UserRole> role) {
        if (this.role != null) throw new IllegalStateException("Role already set");
        this.role = role;
    }

    @Override
    public String toString() {
        return "Session{" +
                "token='" + token + '\'' +
                "username='" + username + '\'' +
                "role='" + roleId + '\'' +
                '}';
    }
}
