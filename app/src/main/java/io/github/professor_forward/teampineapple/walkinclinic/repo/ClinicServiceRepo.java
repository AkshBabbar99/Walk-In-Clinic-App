package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ClinicServiceRepo extends AbstractRepo<ClinicService> {
    private final LocalDb db;
    private final Repository repo;

    ClinicServiceRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<ClinicService> dao() {
        return db.clinicServices();
    }

    public Completable create(@NonNull String name, @NonNull ClinicEmployeeRole role) {
        return create(new ClinicService(name, role))
                .onErrorResumeNext(e -> {
                    Log.e("Repository", e.toString());
                    return Completable.error(e instanceof SQLiteConstraintException ? new ClinicServiceWithSameNameExistsException() : e);
                })
                ;
    }

    public Completable update(@NonNull String id, @NonNull String name, @NonNull ClinicEmployeeRole role) {
        return update(new ClinicService(id, name, role))
                .onErrorResumeNext(e -> {
                    Log.e("Repository", e.toString());
                    return Completable.error(e instanceof SQLiteConstraintException ? new ClinicServiceWithSameNameExistsException() : e);
                })
                ;
    }

    public Completable delete(@NonNull Iterable<ClinicService> clinicServices) {
        return super.delete(clinicServices)
                .andThen(Completable.defer(() -> {
                    List<String> ids = new ArrayList<>();
                    for (ClinicService clinicService : clinicServices) {
                        ids.add(clinicService.id);
                    }

                    return db.clinicServiceJoins().deleteByServiceIds(ids);
                }))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Flowable<List<ClinicService>> getByClinicId(String id) {
        return db.clinicServices().getByClinicId(id)
                .doOnNext(this::onLoad)
                .subscribeOn(Schedulers.io())
                ;
    }

    public Observable<PagedList<ClinicService>> listByName() {
        return new RxPagedListBuilder<>(db.clinicServices().listByName(), Repository.PAGE_SIZE)
                .buildObservable()
                .doOnNext(this::onLoad)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Observable<PagedList<ClinicService>> listByName(String name) {
        return Single.fromCallable(() -> getByNameDataSource(name))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapObservable(ds -> new RxPagedListBuilder<>(getByNameDataSource(name), Repository.PAGE_SIZE).buildObservable())
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<PagedList<ClinicService>> listByClinicId(String id) {
        return Observable.defer(() -> new RxPagedListBuilder<>(db.clinicServices().listByClinicId(id), Repository.PAGE_SIZE).buildObservable())
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<PagedList<ClinicService>> searchNotProvidedByClinic(@NonNull String id, @NonNull String name) {
        return Single.fromCallable(() -> db.clinicServices().searchNotProvidedByClinic(id, "%" + name + "%"))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapObservable(ds -> new RxPagedListBuilder<>(ds, Repository.PAGE_SIZE).buildObservable())
                .doOnNext(this::onLoad)
                ;
    }

    private DataSource.Factory<Integer, ClinicService> getByNameDataSource(String name) {
        if (name.isEmpty()) return db.clinicServices().listByName();

        return db.clinicServices().listByName("%" + name + "%");
    }

    private void onLoad(List<ClinicService> clinicServices) {
        for (ClinicService clinicService : clinicServices) {
            onLoad(clinicService);
        }
    }

    private void onLoad(PagedList<ClinicService> clinicServices) {
        for (ClinicService clinicService : clinicServices) {
            onLoad(clinicService);
        }
    }

    private void onLoad(ClinicService clinicService) {
        clinicService.setClinics(repo.clinics().getByService(clinicService));
    }
}
