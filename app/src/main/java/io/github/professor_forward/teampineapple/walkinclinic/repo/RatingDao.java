package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface RatingDao extends AbstractDao<Rating> {
    @Query("DELETE FROM ratings WHERE patientId IN (:id)")
    Completable deleteByPatientId(List<String> id);

    @Query("SELECT * FROM ratings WHERE id=:id")
    Observable<Rating> getById(String id);

    @Query("SELECT * FROM ratings WHERE clinicId=:id")
    Observable<List<Rating>> getByClinicId(String id);

    @Query("SELECT * FROM ratings WHERE patientId=:id")
    Observable<List<Rating>> getByPatientId(String id);

    @Query("SELECT * FROM ratings WHERE patientId=:patientId AND clinicId=:clinicId")
    Observable<List<Rating>> getAssociated(String patientId, String clinicId);

    @Query("SELECT * FROM ratings WHERE patientId IN (:ids)")
    Single<List<Rating>> getByPatientIdsOnce(List<String> ids);
}

