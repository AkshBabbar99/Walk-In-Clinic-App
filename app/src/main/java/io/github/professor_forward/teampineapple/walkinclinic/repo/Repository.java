package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class Repository {
    public static final int PAGE_SIZE = 50;

    private BookingRepo bookings;
    private ClinicServiceRepo clinicServices;
    private ClinicRepo clinics;
    private EmployeeRoleRepo employeeRoles;
    private final LocalDb db;
    private PatientRoleRepo patientRoles;
    private RatingRepo ratings;
    private final UserDb userDb;
    private UserRepo users;

    public Repository(Context context) {
        db = LocalDb.create(context);
        userDb = UserDb.create(context);

        migrateUsers();
    }

    Repository(LocalDb db, UserDb userDb) {
        this.db = db;
        this.userDb = userDb;
        migrateUsers();
    }

    public BookingRepo bookings() {
        if (bookings == null) {
            bookings = new BookingRepo(this, db);
        }
        return bookings;
    }

    public ClinicServiceRepo clinicServices() {
        if (clinicServices == null) {
            clinicServices = new ClinicServiceRepo(this, db);
        }
        return clinicServices;
    }

    public UserRepo users() {
        if (users == null) {
            users = new UserRepo(this, db);
        }
        return users;
    }

    public EmployeeRoleRepo employeeRoles() {
        if (employeeRoles == null) {
            employeeRoles = new EmployeeRoleRepo(this, db);
        }
        return employeeRoles;
    }

    PatientRoleRepo patientRoles() {
        if (patientRoles == null) {
            patientRoles = new PatientRoleRepo(this, db);
        }
        return patientRoles;
    }

    public RatingRepo ratings() {
        if (ratings == null) {
            ratings = new RatingRepo(this, db);
        }
        return ratings;
    }

    public ClinicRepo clinics() {
        if (clinics == null) {
            clinics = new ClinicRepo(this, db);
        }
        return clinics;
    }

    private void migrateUsers() {
        // Migrate users to LocalDB
        // This fixes the weird design decision to have split dbs.
        // This migration code can be removed once all users have migrated to deliverable 3.
        List<User> users = userDb.users().getUsersByName().blockingFirst();
        for (User user : users) {
            Log.i("Repository", "Migrated user " + user.email);
            if (!db.users().exists(user.email).subscribeOn(Schedulers.io()).blockingGet()) {
                users().create(new User(user.username, user.email, user.hashedPassword, user.roleId)).blockingAwait();
            }
        }
        userDb.users().delete(users).subscribeOn(Schedulers.io()).blockingAwait();
    }
}
