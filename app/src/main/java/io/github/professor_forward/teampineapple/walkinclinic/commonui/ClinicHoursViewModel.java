package io.github.professor_forward.teampineapple.walkinclinic.commonui;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicHours;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ClinicHoursViewModel extends ViewModel {
    private final String closed = MyApplication.getInstance().getString(R.string.closed);
    public final MutableLiveData<String> sundayHours = new MutableLiveData<>(closed);
    public final MutableLiveData<String> mondayHours = new MutableLiveData<>(closed);
    public final MutableLiveData<String> tuesdayHours = new MutableLiveData<>(closed);
    public final MutableLiveData<String> wednesdayHours = new MutableLiveData<>(closed);
    public final MutableLiveData<String> thursdayHours = new MutableLiveData<>(closed);
    public final MutableLiveData<String> fridayHours = new MutableLiveData<>(closed);
    public final MutableLiveData<String> saturdayHours = new MutableLiveData<>(closed);

    private String clinicId;
    private Disposable watcher;

    public void setClinicId(@NonNull String clinicId) {
        if (clinicId.equals(this.clinicId)) return;

        this.clinicId = clinicId;
        if (watcher != null) {
            watcher.dispose();
        }

        watcher = MyApplication.getInstance().getRepo().clinics()
                .getById(clinicId)
                .flatMap(x -> x.isPresent() ? x.get().getHours() : Observable.empty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hours -> {
                    String[] strings = new String[8];
                    for (ClinicHours interval : hours) {
                        if (strings[interval.dayOfWeek.code] == null) {
                            strings[interval.dayOfWeek.code] = formatInterval(interval);
                        }
                        else {
                            strings[interval.dayOfWeek.code] += "\n" + formatInterval(interval);
                        }
                    }

                    for (int i = 1; i < strings.length; i++) {
                        if (strings[i] == null) {
                            strings[i] = closed;
                        }
                    }

                    sundayHours.setValue(strings[1]);
                    mondayHours.setValue(strings[2]);
                    tuesdayHours.setValue(strings[3]);
                    wednesdayHours.setValue(strings[4]);
                    thursdayHours.setValue(strings[5]);
                    fridayHours.setValue(strings[6]);
                    saturdayHours.setValue(strings[7]);
                })
                ;
    }

    private String formatInterval(ClinicHours interval) {
        return interval.startTime.getHours() + ":" + interval.startTime.getMinutes() + "-" + interval.endTime.getHours() + ":" + interval.endTime.getMinutes();
    }
}
