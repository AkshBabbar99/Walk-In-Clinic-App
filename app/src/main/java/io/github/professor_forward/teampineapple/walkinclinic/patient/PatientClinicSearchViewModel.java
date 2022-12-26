package io.github.professor_forward.teampineapple.walkinclinic.patient;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.google.common.base.Optional;

import java.sql.Time;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableSandwich;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;

public class PatientClinicSearchViewModel extends ViewModel {
    public final MutableLiveData<String> filterName = new MutableLiveData<>("");
    public final MutableLiveData<String> filterAddress = new MutableLiveData<>("");
    public final MutableLiveData<String> filterService = new MutableLiveData<>("");
    public final LiveDataObservableSandwich<String> filterHours = new LiveDataObservableSandwich<>("", x -> x.map(this::validateHours));
    public final Flowable<PagedList<Clinic>> pagedListFlowable = Flowable.switchOnNext(Observable.combineLatest(
            ReactiveUtil.liveDataToObservable(filterName),
            ReactiveUtil.liveDataToObservable(filterAddress),
            ReactiveUtil.liveDataToObservable(filterService),
            filterHours.inputInternal,
            (name, address, service, hours) -> {
                if (hours.equals("") || validateHours(hours).isPresent())
                    return MyApplication.getInstance().getRepo().clinics().search(name, address, service);
                return MyApplication.getInstance().getRepo().clinics().search(name, address, service, parseTime(hours));
            }
    ).toFlowable(BackpressureStrategy.BUFFER));

    private Optional<String> validateHours(String time) {
        if (time.equals("")) return Optional.absent();
        if(!time.matches("^\\d{1,2}(?::\\d{1,2})?$")) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_time));
        }

        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = parts.length == 1 ? 0 : Integer.parseInt(parts[1]);

        if (hours > 23 || minutes > 59) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_time));
        }
        return Optional.absent();
    }

    private Time parseTime(String hours) {
        hours = hours.trim();
        if (hours.matches("^\\d{1,2}:\\d{1,2}$")) return Time.valueOf(hours + ":0");
        if (hours.matches("^\\d{1,2}:$")) return Time.valueOf(hours + "0:0");
        return Time.valueOf(hours + ":0:0");
    }
}
