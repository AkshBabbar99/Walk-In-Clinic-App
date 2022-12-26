package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "clinic_service_joins")
public class ClinicServiceJoin {
    @NonNull
    @PrimaryKey
    public final String id;

    @NonNull
    @ForeignKey(entity = Clinic.class, parentColumns = "id", childColumns = "clinicId", onDelete = CASCADE)
    public final String clinicId;

    @NonNull
    @ForeignKey(entity = ClinicService.class, parentColumns = "id", childColumns = "serviceId", onDelete = CASCADE)
    public final String serviceId;

    @Ignore
    public ClinicServiceJoin(@NonNull String clinicId, @NonNull String serviceId) {
        this.id = UUID.randomUUID().toString();
        this.clinicId = clinicId;
        this.serviceId = serviceId;
    }

    public ClinicServiceJoin(@NonNull String id, @NonNull String clinicId, @NonNull String serviceId) {
        this.id = id;
        this.clinicId = clinicId;
        this.serviceId = serviceId;
    }
}
