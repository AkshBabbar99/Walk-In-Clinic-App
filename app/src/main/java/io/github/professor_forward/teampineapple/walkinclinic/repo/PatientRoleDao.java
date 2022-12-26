package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface PatientRoleDao extends AbstractDao<PatientRole> {
    @Query("DELETE FROM patient_roles WHERE patient_roles.id IN (:id)")
    Completable delete(List<String> id);

    @Query("SELECT COUNT(*) FROM patient_roles WHERE id=:id")
    Single<Boolean> exists(String id);

    @Query("SELECT * FROM patient_roles WHERE id=:id")
    Observable<PatientRole> getById(String id);
}

