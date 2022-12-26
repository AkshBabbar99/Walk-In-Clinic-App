package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DateTimeConverter {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @TypeConverter
    public static Date toDate(String date) {
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            Log.e("TypeConverterUtil", "Error parsing date: " + date);
            return null;
        }
    }

    @TypeConverter
    public static String toString(Date date) {
        return formatter.format(date);
    }
}
