package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.reactivex.Observable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "bookings")
class Booking {
    @PrimaryKey
    @NonNull
    public final String id;

    @NonNull
    @ForeignKey(entity = PatientRole.class, parentColumns = "id", childColumns = "patientId", onDelete = ForeignKey.CASCADE)
    public final String patientId;

    @NonNull
    @ForeignKey(entity = Clinic.class, parentColumns = "id", childColumns = "clinicId", onDelete = CASCADE)
    public final String clinicId;

    @NonNull
    @TypeConverters(TypeConverterUtil.class)
    public final BookingState state;

    @NonNull
    @TypeConverters(DateConverter.class)
    public final Date date;

    @NonNull
    @TypeConverters(DateTimeConverter.class)
    public final Date createdAt;

    @Ignore
    private
    Observable<Clinic> clinic;

    @Ignore
    private
    Observable<PatientRole> patient;

    @Ignore
    Booking(@NonNull String clinicId, @NonNull String patientId, @NonNull Date date) {
        this.id = UUID.randomUUID().toString();
        this.clinicId = clinicId;
        this.patientId = patientId;
        this.date = date;
        this.state = BookingState.PENDING;
        this.createdAt = Calendar.getInstance().getTime();
    }

    Booking(@NonNull String id, @NonNull String clinicId, @NonNull String patientId, @NonNull BookingState state, @NonNull Date date, @NonNull Date createdAt) {
        this.id = id;
        this.clinicId = clinicId;
        this.patientId = patientId;
        this.state = state;
        this.date = date;
        this.createdAt = createdAt;
    }

    void setClinic(Observable<Clinic> clinic) {
        this.clinic = clinic;
    }

    public Observable<Clinic> getClinic() {
        return clinic;
    }

    void setPatient(Observable<PatientRole> patient) {
        this.patient = patient;
    }

    public Observable<PatientRole> getPatient() {
        return patient;
    }
}
