package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class PatientRoleRepo extends AbstractRepo<PatientRole> {
    private final LocalDb db;
    private final Repository repo;

    PatientRoleRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<PatientRole> dao() {
        return db.patientRoles();
    }

    Completable create(@NonNull User user) {
        return create(new PatientRole(user.id));
    }

    @Override
    public Completable delete(@NonNull Iterable<PatientRole> items) {
        return super.delete(items)
                .andThen(Completable.defer(() -> {
                    List<String> ids = new ArrayList<>();
                    for (PatientRole role : items) {
                        ids.add(role.id);
                    }
                    return
                            Completable.concatArray(
                                    repo.ratings().deleteByPatientId(ids),
                                    repo.bookings().deleteByPatientId(ids)
                            );
                }))
                ;
    }

    public Completable delete(List<String> ids) {
        return Completable.defer(() -> db.patientRoles().delete(ids))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .andThen(Completable.defer(() -> db.ratings().deleteByPatientId(ids)))
                ;
    }

    public Observable<PatientRole> getById(@NonNull String id) {
        return Observable.defer(() -> db.patientRoles().getById(id))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    private void onLoad(PatientRole role) {
        role.setBookings(repo.bookings().getByPatientId(role.id));
        role.setRatings(repo.ratings().getByPatientId(role.id));
    }
}
