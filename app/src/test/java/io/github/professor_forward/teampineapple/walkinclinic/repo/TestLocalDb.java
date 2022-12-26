package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                Clinic.class,
                ClinicHours.class,
                ClinicService.class,
                ClinicServiceJoin.class,
                EmployeeRole.class,
                User.class
        },
        version = 3
)
abstract class TestLocalDb extends LocalDb {
    protected static RoomDatabase.Builder<? extends LocalDb> createBuilder(Context context) {
        return Room.inMemoryDatabaseBuilder(context, TestLocalDb.class);
    }
}
