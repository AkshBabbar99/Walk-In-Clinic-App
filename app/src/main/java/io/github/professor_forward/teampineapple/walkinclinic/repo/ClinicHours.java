package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.sql.Time;
import java.util.UUID;

import io.reactivex.Observable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "clinic_hours")
public class ClinicHours {
    @PrimaryKey
    @NonNull
    public final String id;

    @NonNull
    @ForeignKey(entity = Clinic.class, parentColumns = "id", childColumns = "clinicId", onDelete = CASCADE)
    public final String clinicId;

    @NonNull
    @TypeConverters(TypeConverterUtil.class)
    public final DayOfWeek dayOfWeek;

    @NonNull
    @TypeConverters(TypeConverterUtil.class)
    public final Time startTime;

    @NonNull
    @TypeConverters(TypeConverterUtil.class)
    public final Time endTime;

    @Ignore
    private
    Observable<Clinic> clinic;

    @Ignore
    ClinicHours(@NonNull String clinicId, @NonNull DayOfWeek dayOfWeek, @NonNull Time startTime, @NonNull Time endTime) {
        this.id = UUID.randomUUID().toString();
        this.clinicId = clinicId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        validate();
    }

    ClinicHours(@NonNull String id, @NonNull String clinicId, @NonNull DayOfWeek dayOfWeek, @NonNull Time startTime, @NonNull Time endTime) {
        this.id = id;
        this.clinicId = clinicId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        validate();
    }

    void setClinic(Observable<Clinic> clinic) {
        this.clinic = clinic;
    }

    public Observable<Clinic> getClinic() {
        return clinic;
    }

    private void validate() {
        if (startTime.compareTo(endTime) > 0) {
            throw new IllegalArgumentException("start time may not be after end time");
        }
    }
}
