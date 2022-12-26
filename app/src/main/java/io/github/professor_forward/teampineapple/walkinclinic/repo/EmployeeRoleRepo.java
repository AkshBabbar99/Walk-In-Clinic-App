package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class EmployeeRoleRepo extends AbstractRepo<EmployeeRole> {
    private final LocalDb db;
    private final Repository repo;

    EmployeeRoleRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<EmployeeRole> dao() {
        return db.employeeRoles();
    }

    Completable create(@NonNull String id, String clinicId) {
        return create(new EmployeeRole(id, clinicId));
    }

    public Completable update(@NonNull EmployeeRole role, String clinicId) {
        return update(new EmployeeRole(role.id, clinicId));
    }

    public Completable delete(List<String> ids) {
        return db.employeeRoles().delete(ids)
                .subscribeOn(Schedulers.io())
                ;
    }

    public Observable<EmployeeRole> getById(@NonNull String id) {
        return Observable.defer(() -> db.employeeRoles().getById(id))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    public Flowable<List<EmployeeRole>> getByClinicId(@NonNull String id) {
        return Flowable.defer(() -> db.employeeRoles().getByClinicId(id))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(list -> {
                    for (EmployeeRole role : list) {
                        onLoad(role);
                    }
                })
                ;
    }

    private void onLoad(EmployeeRole role) {
        role.setClinic(role.clinicId == null ? Observable.empty() : repo.clinics().getById(role.clinicId));
    }
}
