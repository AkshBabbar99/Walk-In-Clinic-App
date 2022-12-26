package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.TypeConverter;

import java.sql.Time;

class TypeConverterUtil {
    @TypeConverter
    public static BookingState toBookingState(int code) {
        return BookingState.fromCode(code);
    }

    @TypeConverter
    public static int toInteger(BookingState state) {
        return state.code;
    }

    @TypeConverter
    public static DayOfWeek toDayOfWeek(int day) {
        return DayOfWeek.of(day);
    }

    @TypeConverter
    public static int toInteger(DayOfWeek day) {
        return day.code;
    }

    @TypeConverter
    public static ClinicEmployeeRole toRole(int role) {
        return ClinicEmployeeRole.fromCode(role);
    }

    @TypeConverter
    public static int toInteger(ClinicEmployeeRole role) {
        return role.code;
    }

    @TypeConverter
    public static Time toTime(String str) {
        String[] strs = str.split(":");
        return new Time(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
    }

    @TypeConverter
    public static String toString(Time time) {
        return time.toString();
    }
}
