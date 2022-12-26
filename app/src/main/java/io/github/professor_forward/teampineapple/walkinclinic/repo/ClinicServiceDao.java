package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface ClinicServiceDao extends AbstractDao<ClinicService> {
    @Query("SELECT COUNT(*) FROM clinic_services WHERE id=:id")
    Single<Boolean> existsWithId(String id);

    @Query("SELECT COUNT(*) FROM clinic_services WHERE name=:name")
    Single<Boolean> existsWithName(String name);

    @Query("SELECT * FROM clinic_services WHERE id=:id")
    Observable<ClinicService> getById(String id);

    @Query("SELECT * FROM clinic_services WHERE name=:name")
    Observable<ClinicService> getByName(String name);

    @Query("SELECT clinic_services.* FROM clinic_services INNER JOIN clinic_service_joins ON clinic_service_joins.serviceId=clinic_services.id WHERE clinicId=:clinicId ORDER BY name ASC")
    Flowable<List<ClinicService>> getByClinicId(String clinicId);

    @Query("SELECT * FROM clinic_services ORDER BY name ASC")
    DataSource.Factory<Integer, ClinicService> listByName();

    @Query("SELECT * FROM clinic_services WHERE name LIKE :name ORDER BY name ASC")
    DataSource.Factory<Integer, ClinicService> listByName(String name);

    @Query("SELECT clinic_services.* FROM clinic_services INNER JOIN clinic_service_joins ON clinic_service_joins.serviceId=clinic_services.id WHERE clinicId=:clinicId ORDER BY name ASC")
    DataSource.Factory<Integer, ClinicService> listByClinicId(String clinicId);

    @Query("SELECT clinic_services.* FROM clinic_services " +
            "WHERE id NOT IN (SELECT clinic_services.id FROM clinic_services INNER JOIN clinic_service_joins ON clinic_service_joins.serviceId=clinic_services.id WHERE clinicId=:clinicId) " +
            "AND name LIKE :name " +
            "ORDER BY name ASC")
    DataSource.Factory<Integer, ClinicService> searchNotProvidedByClinic(String clinicId, String name);
}
