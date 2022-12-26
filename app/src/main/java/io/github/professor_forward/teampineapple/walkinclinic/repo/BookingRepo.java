package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.annotation.NonNull;

import com.google.common.base.Optional;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class BookingRepo extends AbstractRepo<Booking> {
    private final LocalDb db;
    private final Repository repo;

    BookingRepo(Repository repo, LocalDb db) {
        this.repo = repo;
        this.db = db;
    }

    @Override
    protected AbstractDao<Booking> dao() {
        return db.bookings();
    }

    public Completable create(@NonNull Clinic clinic, @NonNull PatientRole patient, Date date) {
        return create(new Booking(clinic.id, patient.id, date));
    }

    public Completable cancel(@NonNull Booking booking) {
        return update(new Booking(booking.id, booking.clinicId, booking.patientId, BookingState.CANCELED, booking.date, booking.createdAt));
    }

    public Completable complete(@NonNull Booking booking) {
        return update(new Booking(booking.id, booking.clinicId, booking.patientId, BookingState.COMPLETE, booking.date, booking.createdAt));
    }

    Completable deleteByPatientId(@NonNull List<String> patientId) {
        return db.bookings().deleteByPatientId(patientId)
                .subscribeOn(Schedulers.io())
                ;
    }

    public Observable<List<Booking>> getByClinicId(@NonNull String id) {
        return db.bookings().getByClinicId(id)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    public Observable<List<Booking>> getByPatientId(@NonNull String id) {
        return db.bookings().getByPatientId(id)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onLoad)
                ;
    }

    private void onLoad(List<Booking> bookings) {
        for (Booking booking : bookings) {
            onLoad(booking);
        }
    }

    private void onLoad(Booking booking) {
        booking.setClinic(repo.clinics().getById(booking.clinicId).filter(Optional::isPresent).map(Optional::get));
        booking.setPatient(repo.patientRoles().getById(booking.patientId));
    }
}
