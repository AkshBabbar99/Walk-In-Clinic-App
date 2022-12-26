package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
interface BookingDao extends AbstractDao<Booking> {
    @Query("SELECT * FROM bookings WHERE clinicId=:clinicId")
    Observable<List<Booking>> getByClinicId(String clinicId);

    @Query("SELECT * FROM bookings WHERE patientId=:patientId")
    Observable<List<Booking>> getByPatientId(String patientId);

    @Query("DELETE FROM bookings WHERE patientId IN (:patientId)")
    Completable deleteByPatientId(List<String> patientId);
}
