package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

import io.reactivex.Observable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "ratings")
public class Rating {
    @PrimaryKey
    @NonNull
    public final String id;

    @NonNull
    @ForeignKey(entity = Clinic.class, parentColumns = "id", childColumns = "clinicId", onDelete = CASCADE)
    public final String clinicId;

    @NonNull
    @ForeignKey(entity = PatientRole.class, parentColumns = "id", childColumns = "patientId", onDelete = CASCADE)
    public final String patientId;

    @NonNull
    public final String comment;

    @NonNull
    public final int value;

    @Ignore
    private Observable<Clinic> clinic;

    @Ignore
    private Observable<PatientRole> patient;

    public Rating(@NonNull String id, @NonNull String clinicId, @NonNull String patientId, @NonNull String comment, int value) {
        this.id = id;
        this.clinicId = clinicId;
        this.patientId = patientId;
        this.comment = comment;
        this.value = value;
        validate();
    }

    @Ignore
    public Rating(@NonNull Clinic clinic, @NonNull PatientRole patientRole, @NonNull String comment, int value) {
        this.id = UUID.randomUUID().toString();
        this.clinicId = clinic.id;
        this.clinic = Observable.just(clinic);
        this.patientId = patientRole.id;
        this.patient = Observable.just(patientRole);
        this.comment = comment;
        this.value = value;
        validate();
    }

    public Observable<Clinic> getClinic() {
        return clinic;
    }

    public Observable<PatientRole> getPatient() {
        return patient;
    }

    void setClinic(Observable<Clinic> clinic) {
        if (this.clinic != null) throw new IllegalStateException("Already set clinic");
        this.clinic = clinic;
    }

    void setPatient(Observable<PatientRole> patient) {
        if (this.patient != null) throw new IllegalStateException("Already set patient");
        this.patient = patient;
    }

    private void validate() {
        if (value < 1 || value > 5) throw new IllegalArgumentException("Value must be within 1 and 5");
    }
}
