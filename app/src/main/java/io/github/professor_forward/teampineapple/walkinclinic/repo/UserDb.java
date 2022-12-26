package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {User.class},
        version = 1,
        exportSchema = false
)
abstract class UserDb extends RoomDatabase {
    private static RoomDatabase.Builder<UserDb> createBuilder(Context context) {
        return Room
                .databaseBuilder(context, UserDb.class, "users.db")
                .fallbackToDestructiveMigration()
                ;
    }

    static UserDb create(Context context) {
        return createBuilder(context).build();
    }

    abstract UserDao users();
}
