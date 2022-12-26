package io.github.professor_forward.teampineapple.walkinclinic.repo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface ClinicHoursDao extends AbstractDao<ClinicHours> {
    @Delete
    Completable delete(ClinicHours clinicHours);

    @TypeConverters(TypeConverterUtil.class)
    @Query("DELETE FROM clinic_hours WHERE clinicId=:clinicId AND dayOfWeek=:dow")
    Completable delete(String clinicId, DayOfWeek dow);

    @Query("SELECT * FROM clinic_hours WHERE clinicId=:clinicId")
    Observable<List<ClinicHours>> getByClinicId(String clinicId);
}
