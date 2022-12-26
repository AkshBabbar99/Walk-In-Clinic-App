package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.base.Optional;

import java.sql.Time;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.DayOfWeek;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Completable;
import io.reactivex.Observable;

public class EmployeeClinicHoursDialogViewModel extends ViewModel {
    public final MutableLiveData<Boolean> isOpen = new MutableLiveData<>(Boolean.FALSE);
    public final MutableLiveData<Time> start = new MutableLiveData<>(new Time(0));
    public final MutableLiveData<Time> end = new MutableLiveData<>(new Time(0));
    public final LiveData<Boolean> valid = ReactiveUtil.observableToLiveData(Observable.combineLatest(
            ReactiveUtil.liveDataToObservable(isOpen),
            ReactiveUtil.liveDataToObservable(start),
            ReactiveUtil.liveDataToObservable(end),
            (open, start, end) -> !open || start.compareTo(end) < 0
    ));
    DayOfWeek dow;

    @SuppressLint("CheckResult")
    void save() {
        //noinspection ConstantConditions
        boolean open = isOpen.getValue();
        Time start = this.start.getValue();
        Time end = this.end.getValue();
        if (valid.getValue() != Boolean.TRUE) {
            return;
        }
        assert start != null;
        if (open && start.compareTo(end) > 0) {
            // Error will we shown in UI, this as a delay
            Log.w("EmployeeClinicHours", "Attempted to save invalid data not filtered by UI");
            return;
        }
        DayOfWeek dow = this.dow;
        Util.getClinic()
                .take(1)
                .singleOrError()
                .map(Optional::get)
                .flatMapCompletable(clinic -> {
                    ClinicRepo clinics = MyApplication.getInstance().getRepo().clinics();
                    Completable delete = clinics.deleteHoursForDay(clinic.id, dow);
                    if (!open) {
                        return delete;
                    }
                    assert end != null;
                    return delete.andThen(clinics.addHours(clinic, dow, start, end));
                })
                .subscribe()
        ;
    }
}
