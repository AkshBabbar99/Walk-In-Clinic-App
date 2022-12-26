package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

import io.reactivex.Observable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "patient_roles")
public class PatientRole extends UserRole {
    public static final String ROLE_KEY = "patient";

    @PrimaryKey
    @NonNull
    // FIXME: This CASCADE doesn't work
    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "id", onDelete = CASCADE)
    public final String id;

    @Ignore
    private Observable<List<Rating>> ratings;

    @Ignore
    private Observable<List<Booking>> bookings;

    PatientRole(@NonNull String id) {
        this.id = id;
    }

    public Observable<List<Booking>> getBookings() {
        return bookings;
    }

    void setBookings(Observable<List<Booking>> bookings) {
        if (this.bookings != null) throw new IllegalStateException("Already set bookings");
        this.bookings = bookings;
    }

    public Observable<List<Rating>> getRatings() {
        return ratings;
    }

    void setRatings(Observable<List<Rating>> ratings) {
        if (this.ratings != null) throw new IllegalStateException("Already set ratings");
        this.ratings = ratings;
    }

    @Override
    public String getRoleId() {
        return ROLE_KEY;
    }
}
