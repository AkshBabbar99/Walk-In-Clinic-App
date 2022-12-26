package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import io.github.professor_forward.teampineapple.walkinclinic.databinding.UserBinding;
import io.github.professor_forward.teampineapple.walkinclinic.util.ViewHolderFactory;
import io.reactivex.Observable;

/**
 * A User, as stored in the database. This class is immutable. Updatable via UserDao.
 */
@Entity(tableName = "users", indices = {@Index(value = "email", unique = true)})
public class User {
    @NonNull
    @PrimaryKey
    public final String id;

    @NonNull
    public final String username;

    @NonNull
    public final String email;

    @NonNull
    public final String hashedPassword;

    @NonNull
    public final String roleId;

    @Ignore
    private Observable<? extends UserRole> role;

    /**
     * Create a new user.
     *
     * @param username display name
     * @param email
     * @param hashedPassword the user's password MUST be hashed before constructing to prevent saving plaintext passwords
     * @param roleId user role, one of {admin, employee, patient}
     */
    @Ignore
    User(@NonNull String username, @NonNull String email, @NonNull String hashedPassword, @NonNull String roleId) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.roleId = roleId;
    }

    @Ignore
    User(@NonNull String username, @NonNull String email, @NonNull String hashedPassword, @NonNull UserRole role) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = Observable.just(role);
        this.roleId = role.getRoleId();
    }

    /**
     * Create a new user object which updates an old one with an existing ID.
     *
     * This constructor is also called by Room when inflating rows from the database. This is why
     * the constructor cannot hash passwords--it would end up double hashing passwords when loading
     * the hashed password from the database.
     *
     * @param id user's ID
     * @param username display name
     * @param email
     * @param hashedPassword the user's password MUST be hashed before constructing to prevent saving plaintext passwords
     * @param roleId user role, one of {admin, employee, patient}
     */
    User(@NonNull String id, @NonNull String username, @NonNull String email, @NonNull String hashedPassword, @NonNull String roleId) {
        this.id = !id.equals("") ? id : UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.roleId = roleId;
    }

    void setRole(Observable<? extends UserRole> role) {
        if (this.role != null) throw new IllegalStateException("Already set role");
        this.role = role;
    }

    public Observable<? extends UserRole> getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                username.equals(user.username) &&
                email.equals(user.email) &&
                hashedPassword.equals(user.hashedPassword) &&
                roleId.equals(user.roleId);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, hashedPassword, roleId);
    }

    /**
     * Hashes a string using the SHA-256 algorithm, no salt added.
     *
     * @param password the string to hash
     * @return a SHA-256 hash digest
     */
    static String digestPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(password.getBytes());
            return Base64.encodeToString(digest, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Impossible. Android supports SHA-256 from API level 1.");
        }
    }

    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<User>() {
                @Override
                public boolean areItemsTheSame(@NonNull User old, @NonNull User newUser) {
                    return old.id.equals(newUser.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull User old, @NonNull User newUser) {
                    return old.equals(newUser);
                }
            };

    public static final ViewHolderFactory VIEW_HOLDER_FACTORY =
            (inflater, parent) -> UserBinding.inflate(inflater, parent, false);
}
