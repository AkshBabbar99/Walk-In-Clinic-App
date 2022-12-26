package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface EmployeeRoleDao extends AbstractDao<EmployeeRole> {
    @Query("DELETE FROM employee_roles WHERE employee_roles.id IN (:id)")
    Completable delete(List<String> id);

    @Query("SELECT COUNT(*) FROM employee_roles WHERE id=:id")
    Single<Boolean> exists(String id);

    @Query("SELECT * FROM employee_roles WHERE id=:id")
    Observable<EmployeeRole> getById(String id);

    @Query("SELECT * FROM employee_roles WHERE clinicId=:id")
    Flowable<List<EmployeeRole>> getByClinicId(String id);
}
