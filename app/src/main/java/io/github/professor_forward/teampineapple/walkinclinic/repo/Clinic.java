package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.Observable;

@Entity(tableName = "clinics", indices = {@Index(value = "name", unique = true)})
public class Clinic {
    @NonNull
    @PrimaryKey
    public final String id;

    @NonNull
    public final String name;

    @NonNull
    public final String address;

    @NonNull
    public final String phoneNumber;

    @NonNull
    public final String acceptedInsuranceTypes;

    @NonNull
    public final String acceptedPaymentTypes;

    public final int numStaff;

    public final int numNurses;

    public final int numDoctors;

    @ColumnInfo(defaultValue = "0")
    public final int rating;

    @Ignore
    private Observable<List<Booking>> bookings;

    @Ignore
    private Observable<List<ClinicHours>> hours;

    @Ignore
    private Flowable<List<ClinicService>> services;

    @Ignore
    private Flowable<List<EmployeeRole>> employees;

    @Ignore
    private Observable<List<Rating>> ratings;

    @Ignore
    Clinic(@NonNull String name, @NonNull String address, @NonNull String phoneNumber, @NonNull String acceptedInsuranceTypes, @NonNull String acceptedPaymentTypes, int numStaff, int numNurses, int numDoctors) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.acceptedInsuranceTypes = acceptedInsuranceTypes;
        this.acceptedPaymentTypes = acceptedPaymentTypes;
        this.numStaff = numStaff;
        this.numNurses = numNurses;
        this.numDoctors = numDoctors;
        this.rating = 0;
        validate();
    }

    Clinic(@NonNull String id, @NonNull String name, @NonNull String address, @NonNull String phoneNumber, @NonNull String acceptedInsuranceTypes, @NonNull String acceptedPaymentTypes, int numStaff, int numNurses, int numDoctors, int rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.acceptedInsuranceTypes = acceptedInsuranceTypes;
        this.acceptedPaymentTypes = acceptedPaymentTypes;
        this.numStaff = numStaff;
        this.numNurses = numNurses;
        this.numDoctors = numDoctors;
        this.rating = rating;
        validate();
    }

    public Flowable<List<EmployeeRole>> getEmployees() {
        return employees;
    }

    void setEmployees(Flowable<List<EmployeeRole>> employees) {
        if (this.employees != null) throw new IllegalStateException("employees already set");
        this.employees = employees;
    }

    public Flowable<List<ClinicService>> getServices() {
        return services;
    }

    void setServices(Flowable<List<ClinicService>> services) {
        if (this.services != null) throw new IllegalStateException("clinicService already set");
        this.services = services;
    }

    public Observable<List<ClinicHours>> getHours() {
        return hours;
    }

    void setHours(Observable<List<ClinicHours>> hours) {
        if (this.hours != null) throw new IllegalStateException("clinic hours already set");
        this.hours = hours;
    }

    public Observable<List<Rating>> getRatings() {
        return ratings;
    }

    void setRatings(Observable<List<Rating>> ratings) {
        if (this.ratings != null) throw new IllegalStateException("Ratings already set");
        this.ratings = ratings;
    }

    public Observable<List<Booking>> getBookings() {
        return bookings;
    }

    public void setBookings(Observable<List<Booking>> bookings) {
        if (this.bookings != null) throw new IllegalStateException("bookings already set");
        this.bookings = bookings;
    }

    private void validate() {
        if (numStaff < 0) throw new IllegalArgumentException("numStaff must be >= 0");
        if (numNurses < 0) throw new IllegalArgumentException("numNurses must be >= 0");
        if (numDoctors < 0) throw new IllegalArgumentException("numDoctors must be >= 0");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clinic clinic = (Clinic) o;
        return numStaff == clinic.numStaff &&
                numNurses == clinic.numNurses &&
                numDoctors == clinic.numDoctors &&
                rating == clinic.rating &&
                id.equals(clinic.id) &&
                name.equals(clinic.name) &&
                address.equals(clinic.address) &&
                phoneNumber.equals(clinic.phoneNumber) &&
                acceptedInsuranceTypes.equals(clinic.acceptedInsuranceTypes) &&
                acceptedPaymentTypes.equals(clinic.acceptedPaymentTypes);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final DiffUtil.ItemCallback<Clinic> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Clinic>() {
                @Override
                public boolean areItemsTheSame(@NonNull Clinic old, @NonNull Clinic newOne) {
                    return old.id.equals(newOne.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull Clinic old, @NonNull Clinic newOne) {
                    return old.equals(newOne);
                }
            };
}
