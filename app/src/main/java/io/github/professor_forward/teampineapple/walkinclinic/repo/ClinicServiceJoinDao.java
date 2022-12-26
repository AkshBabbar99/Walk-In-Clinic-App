package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface ClinicServiceJoinDao extends AbstractDao<ClinicServiceJoin> {
    @Query("DELETE FROM clinic_service_joins WHERE clinicId=:clinicId")
    Completable deleteByClinicId(String clinicId);

    @Query("DELETE FROM clinic_service_joins WHERE serviceId IN (:ids)")
    Completable deleteByServiceIds(List<String> ids);

    @Query("DELETE FROM clinic_service_joins WHERE clinicId=:clinicId AND serviceId IN (:serviceIds)")
    Completable deleteServicesFromClinic(String clinicId, List<String> serviceIds);

    @Query("DELETE FROM clinic_service_joins WHERE clinicId=:clinicId AND serviceId=:serviceId")
    Completable deleteByBothIds(String clinicId, String serviceId);
}
