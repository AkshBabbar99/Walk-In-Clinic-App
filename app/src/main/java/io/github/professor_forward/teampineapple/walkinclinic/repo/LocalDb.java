package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.UUID;

@Database(
        entities = {
                Clinic.class,
                ClinicHours.class,
                ClinicService.class,
                ClinicServiceJoin.class,
                Booking.class,
                EmployeeRole.class,
                PatientRole.class,
                Rating.class,
                User.class
        },
        version = 2
)
abstract class LocalDb extends RoomDatabase {

    static LocalDb create(Context context) {
        return Room.databaseBuilder(context, LocalDb.class, "local.db")
                .fallbackToDestructiveMigration()
                .addCallback(new Callback() {
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        Cursor exists = db.query("SELECT COUNT(*) FROM users WHERE email=\"admin@a4word.com\"");
                        if (exists.moveToFirst() && exists.getInt(0) < 1) {
                            ContentValues user = new ContentValues();
                            user.put("id", UUID.randomUUID().toString());
                            user.put("username", "admin");
                            user.put("email", UserRepo.ADMIN_EMAIL);
                            user.put("hashedPassword", User.digestPassword("5T5ptQ"));
                            user.put("roleId", AdminRole.ROLE_KEY);
                            db.insert("users", OnConflictStrategy.ABORT, user);
                        }
                    }
                })
                .build();
    }

    abstract BookingDao bookings();
    abstract ClinicDao clinics();
    abstract ClinicHoursDao clinicHours();
    abstract ClinicServiceDao clinicServices();
    abstract ClinicServiceJoinDao clinicServiceJoins();
    abstract EmployeeRoleDao employeeRoles();
    abstract PatientRoleDao patientRoles();
    abstract RatingDao ratings();
    abstract UserDao users();
}
