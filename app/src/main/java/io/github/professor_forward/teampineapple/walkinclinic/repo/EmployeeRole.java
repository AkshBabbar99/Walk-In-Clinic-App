package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.common.base.Optional;

import io.reactivex.Observable;

import static androidx.room.ForeignKey.CASCADE;


@Entity(tableName = "employee_roles")
public class EmployeeRole extends UserRole {
    public static final String ROLE_KEY = "employee";

    @PrimaryKey
    @NonNull
    // FIXME: This CASCADE doesn't work
    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "id", onDelete = CASCADE)
    public final String id;

    @ForeignKey(entity = Clinic.class, parentColumns = "id", childColumns = "clinicId")
    public final String clinicId;

    @Ignore
    private Observable<Optional<Clinic>> clinic;

    EmployeeRole(@NonNull String id, String clinicId) {
        this.id = id;
        this.clinicId = clinicId;
    }

    @Ignore
    EmployeeRole(@NonNull String id) {
        this.id = id;
        this.clinic = Observable.just(Optional.absent());
        clinicId = null;
    }

    @Ignore
    EmployeeRole(@NonNull String id, @NonNull Clinic clinic) {
        this.id = id;
        this.clinic = Observable.just(Optional.of(clinic));
        this.clinicId = clinic.id;
    }

    public Observable<Optional<Clinic>> getClinic() {
        return clinic;
    }

    void setClinic(Observable<Optional<Clinic>> clinic) {
        if (this.clinic != null) throw new IllegalStateException("Clinic already set");
        this.clinic = clinic;
    }

    @Override
    public String getRoleId() {
        return ROLE_KEY;
    }
}
