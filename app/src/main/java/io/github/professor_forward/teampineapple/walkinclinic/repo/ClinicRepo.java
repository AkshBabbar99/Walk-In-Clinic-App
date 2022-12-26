package io.github.professor_forward.teampineapple.walkinclinic.repo;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PagedList;
import androidx.paging.RxPagedListBuilder;

import com.google.common.base.Optional;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ClinicRepo extends AbstractRepo<Clinic> {
    private static final int TIME_PER_BOOKING = 15;
    private final LocalDb db;
    private final Repository repo;

    ClinicRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<Clinic> dao() {
        return db.clinics();
    }

    public Completable create(@NonNull EmployeeRole employee, @NonNull String name, @NonNull String address, @NonNull String phoneNumber, @NonNull String acceptedInsuranceTypes, @NonNull String acceptedPaymentTypes, int numStaff, int numNurses, int numDoctors) {
        Clinic clinic = new Clinic(name, address, phoneNumber, acceptedInsuranceTypes, acceptedPaymentTypes, numStaff, numNurses, numDoctors);
        return create(clinic)
                .onErrorResumeNext(e -> {
                    Log.e("Repository", e.toString());
                    return Completable.error(e instanceof SQLiteConstraintException ? RecordWithSameNameExistsException::new : RepositoryException::new);
                })
                .andThen(Completable.defer(() -> repo.employeeRoles().update(employee, clinic.id)))
                ;
    }

    public Completable update(@NonNull Clinic clinic, @NonNull String name, @NonNull String address, @NonNull String phoneNumber, @NonNull String acceptedInsuranceTypes, @NonNull String acceptedPaymentTypes, int numStaff, int numNurses, int numDoctors) {
        return update(new Clinic(clinic.id, name, address, phoneNumber, acceptedInsuranceTypes, acceptedPaymentTypes, numStaff, numNurses, numDoctors, clinic.rating))
                .onErrorResumeNext(e -> {
                    Log.e("Repository", e.toString());
                    return Completable.error(e instanceof SQLiteConstraintException ? RecordWithSameNameExistsException::new : RepositoryException::new);
                })
                ;
    }

    public Completable addService(@NonNull Clinic clinic, @NonNull ClinicService service) {
        return Single.fromCallable(() -> new ClinicServiceJoin(clinic.id, service.id))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapCompletable(join -> db.clinicServiceJoins().insert(join))
                ;
    }

    public Completable addServices(@NonNull Clinic clinic, @NonNull Iterable<ClinicService> services) {
        return Single.fromCallable(() -> {
            List<ClinicServiceJoin> joins = new ArrayList<>();
            for (ClinicService service : services) {
                joins.add(new ClinicServiceJoin(clinic.id, service.id));
            }
            return joins;
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapCompletable(joins -> db.clinicServiceJoins().insert(joins))
                ;
    }

    public Completable deleteService(@NonNull Clinic clinic, @NonNull ClinicService service) {
        return Completable.defer(() -> db.clinicServiceJoins().deleteByBothIds(clinic.id, service.id))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Completable deleteServices(@NonNull Clinic clinic, @NonNull Iterable<ClinicService> services) {
        return Single.fromCallable(() -> {
            List<String> serviceIds = new ArrayList<>();
            for (ClinicService service : services) {
                serviceIds.add(service.id);
            }
            return serviceIds;
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapCompletable(serviceIds -> db.clinicServiceJoins().deleteServicesFromClinic(clinic.id, serviceIds))
                ;
    }

    public Completable addHours(@NonNull Clinic clinic, @NonNull DayOfWeek dayOfWeek, @NonNull Time startTime, @NonNull Time endTime) {
        return Completable.defer(() -> db.clinicHours().insert(new ClinicHours(clinic.id, dayOfWeek, startTime, endTime)))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Completable updateHours(@NonNull ClinicHours clinicHours, @NonNull DayOfWeek dayOfWeek, @NonNull Time startTime, @NonNull Time endTime) {
        return Completable.defer(() -> db.clinicHours().update(new ClinicHours(clinicHours.id, clinicHours.clinicId, dayOfWeek, startTime, endTime)))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Completable deleteHours(@NonNull ClinicHours hours) {
        return Completable.defer(() -> db.clinicHours().delete(hours))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Completable deleteHoursForDay(@NonNull String clinicId, @NonNull DayOfWeek dow) {
        return Completable.defer(() -> db.clinicHours().delete(clinicId, dow))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                ;
    }

    public Observable<Optional<Clinic>> getById(@NonNull String id) {
        return Observable.defer(() -> db.clinics().getById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(x -> (Optional<Clinic>) (x.length == 0 ? Optional.absent() : Optional.of(x[0])))
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<List<Clinic>> getByService(@NonNull ClinicService service) {
        return db.clinics().getByServiceId(service.id)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<Integer> getWaitTimeForDate(@NonNull String id, @NonNull Date date) {
        return db.clinics().getBookingToStaffRatioForDate(id, date)
                .map(x -> TIME_PER_BOOKING * (int) Math.ceil(x))
                .subscribeOn(Schedulers.io())
                ;
    }

    public Flowable<PagedList<Clinic>> search(@NonNull String name, @NonNull String address, @NonNull String service) {
        return buildSearchFlowable(buildSearchDataSource(name, address, service));
    }

    public Flowable<PagedList<Clinic>> search(@NonNull String name, @NonNull String address, @NonNull String service, @NonNull Time time) {
        return buildSearchFlowable(buildSearchDataSource(name, address, service, time));
    }

    DataSource.Factory<Integer, Clinic> buildSearchDataSource(@NonNull String name, @NonNull String address, @NonNull String service) {
        return db.clinics().search("%" + name + "%", "%" + address + "%", "%" + service + "%");
    }

    DataSource.Factory<Integer, Clinic> buildSearchDataSource(@NonNull String name, @NonNull String address, @NonNull String service, @NonNull Time time) {
        return db.clinics().search("%" + name + "%", "%" + address + "%", "%" + service + "%", time);
    }

    private Flowable<PagedList<Clinic>> buildSearchFlowable(DataSource.Factory ds) {
        return new RxPagedListBuilder<>(ds, Repository.PAGE_SIZE)
                .setFetchScheduler(Schedulers.io())
                .buildFlowable(BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                ;
    }


    private void onLoad(List<Clinic> clinics) {
        for (Clinic clinic : clinics) {
            onLoad(clinic);
        }
    }

    private void onLoad(Optional<Clinic> clinic) {
        if (clinic.isPresent()) onLoad(clinic.get());
    }

    private void onLoad(Clinic clinic) {
        clinic.setBookings(repo.bookings().getByClinicId(clinic.id));
        clinic.setHours(db.clinicHours().getByClinicId(clinic.id)
                .subscribeOn(Schedulers.io())
                .doOnNext(hours -> {
                    for (ClinicHours hour : hours) {
                        hour.setClinic(Observable.just(clinic));
                    }
                })
        );
        clinic.setServices(repo.clinicServices().getByClinicId(clinic.id));
        clinic.setEmployees(repo.employeeRoles().getByClinicId(clinic.id));
        clinic.setRatings(repo.ratings().getByClinicId(clinic.id));
    }
}

