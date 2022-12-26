package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface ClinicDao extends AbstractDao<Clinic> {
    @Query("SELECT COUNT(*) FROM clinics WHERE name=:name")
    Single<Boolean> existsByName(String name);

    @Query("SELECT * FROM clinics WHERE id=:id")
    Observable<Clinic[]> getById(String id);

    @Query("SELECT * from clinics WHERE name=:name")
    Observable<Clinic[]> getByName(String name);

    @Query("SELECT clinics.* FROM clinics INNER JOIN clinic_service_joins ON clinic_service_joins.clinicId = clinics.id WHERE clinic_service_joins.serviceId=:serviceId")
    Observable<List<Clinic>> getByServiceId(String serviceId);

    @TypeConverters(DateConverter.class)
    @Query("SELECT ((SELECT CAST(COUNT(*) AS REAL) FROM bookings WHERE clinicId=:clinicId AND `date` = :date) / CAST(MAX(numNurses + numDoctors, 1) AS REAL)) FROM clinics WHERE id=:clinicId")
    Observable<Double> getBookingToStaffRatioForDate(String clinicId, Date date);

    @Query("UPDATE clinics SET rating = COALESCE((SELECT avg(value) FROM ratings WHERE clinicId=:id),0) WHERE id=:id")
    Completable updateRating(String id);

    @Query("SELECT DISTINCT clinics.* FROM clinics " +
            "LEFT JOIN clinic_service_joins ON clinic_service_joins.clinicId=clinics.id " +
            "LEFT JOIN clinic_services ON clinic_service_joins.serviceId=clinic_services.id " +
            "WHERE clinics.name LIKE :name " +
            "AND clinics.address LIKE :address " +
            "AND (:service = \"%%\" OR clinic_services.name LIKE :service) " +
            "ORDER BY clinics.name ASC")
    DataSource.Factory<Integer, Clinic> search(String name, String address, String service);

    @TypeConverters(TypeConverterUtil.class)
    @Query("SELECT DISTINCT clinics.* FROM clinics " +
            "LEFT JOIN clinic_service_joins ON clinic_service_joins.clinicId=clinics.id " +
            "LEFT JOIN clinic_services ON clinic_service_joins.serviceId=clinic_services.id " +
            "LEFT JOIN clinic_hours ON clinic_hours.clinicId=clinics.id " +
            "WHERE clinics.name LIKE :name " +
            "AND clinics.address LIKE :address " +
            "AND (:service = \"%%\" OR clinic_services.name LIKE :service) " +
            "AND clinic_hours.startTime <= :time " +
            "AND clinic_hours.endTime >= :time " +
            "ORDER BY clinics.name ASC")
    DataSource.Factory<Integer, Clinic> search(String name, String address, String service, Time time);
}
