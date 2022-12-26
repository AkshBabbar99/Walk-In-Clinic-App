package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RatingRepo extends AbstractRepo<Rating> {
    private final LocalDb db;
    private final Repository repo;

    RatingRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<Rating> dao() {
        return db.ratings();
    }


    public Completable create(@NonNull Clinic clinic, @NonNull PatientRole patient, @NonNull String comment, @NonNull int rating) {
        return db.ratings().insert(new Rating(clinic, patient, comment, rating))
                .andThen(Completable.defer(() -> db.clinics().updateRating(clinic.id)))
                .subscribeOn(Schedulers.io())
                ;
    }

    public Observable<List<Rating>> getByClinicId(@NonNull String clinicId) {
        return db.ratings().getByClinicId(clinicId)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<List<Rating>> getByPatientId(@NonNull String patientId) {
        return db.ratings().getByPatientId(patientId)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<Optional<Rating>> getAssociated(@NonNull String patientId, @NonNull String clinicId) {
        return db.ratings().getAssociated(patientId, clinicId)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                .map(x -> x.size() == 0 ? Optional.absent() : Optional.of(x.get(0)))
                ;
    }

    Completable deleteByPatientId(@NonNull List<String> ids) {
        return db.ratings().getByPatientIdsOnce(ids)
                .flatMapCompletable(ratings -> {
                    Set<String> clinicIds = new HashSet<>();
                    for (Rating rating : ratings) {
                        clinicIds.add(rating.clinicId);
                    }
                    List<Completable> completables = new ArrayList<>();
                    for (String id : clinicIds) {
                        completables.add(Completable.defer(() -> db.clinics().updateRating(id)));
                    }
                    return db.ratings().deleteByPatientId(ids)
                            .andThen(Completable.merge(completables));
                })
                .subscribeOn(Schedulers.io())
                ;
    }

    private void onLoad(List<Rating> ratings) {
        for (Rating rating : ratings) {
            onLoad(rating);
        }
    }

    private void onLoad(Rating rating) {
        rating.setClinic(repo.clinics().getById(rating.clinicId).filter(Optional::isPresent).map(Optional::get));
        rating.setPatient(repo.patientRoles().getById(rating.patientId));
    }
}
