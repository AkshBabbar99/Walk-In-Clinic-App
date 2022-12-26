package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.github.professor_forward.teampineapple.walkinclinic.databinding.ClinicServiceBinding;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.UserBinding;
import io.github.professor_forward.teampineapple.walkinclinic.util.ViewHolderFactory;
import io.reactivex.Observable;

@Entity(tableName = "clinic_services", indices = {@Index(value = "name", unique = true)})
public class ClinicService {
    @NonNull
    @PrimaryKey
    public final String id;

    @NonNull
    public final String name;

    @NonNull
    @TypeConverters(TypeConverterUtil.class)
    public final ClinicEmployeeRole role;

    @Ignore
    private
    Observable<List<Clinic>> clinics;

    @Ignore
    ClinicService(@NonNull String name, @NonNull ClinicEmployeeRole role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.role = role;
    }

    ClinicService(@NonNull String id, @NonNull String name, @NonNull ClinicEmployeeRole role) {
        this.id = !id.equals("") ? id : UUID.randomUUID().toString();
        this.name = name;
        this.role = role;
    }

    void setClinics(Observable<List<Clinic>> clinics) {
        this.clinics = clinics;
    }

    public Observable<List<Clinic>> getClinics() {
        return clinics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClinicService that = (ClinicService) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                role == that.role;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final DiffUtil.ItemCallback<ClinicService> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ClinicService>() {
                @Override
                public boolean areItemsTheSame(@NonNull ClinicService old, @NonNull ClinicService newOne) {
                    return old.id.equals(newOne.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull ClinicService old, @NonNull ClinicService newOne) {
                    return old.equals(newOne);
                }
            };

    public static final ViewHolderFactory VIEW_HOLDER_FACTORY =
            (inflater, parent) -> ClinicServiceBinding.inflate(inflater, parent, false);
}
