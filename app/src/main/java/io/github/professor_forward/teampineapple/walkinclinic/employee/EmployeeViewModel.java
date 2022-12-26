package io.github.professor_forward.teampineapple.walkinclinic.employee;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.base.Optional;

import java.util.List;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.commonui.ClinicHoursFragment;
import io.github.professor_forward.teampineapple.walkinclinic.global.Session;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EmployeeViewModel extends ViewModel {
    private Observable<Optional<Session>> session = MyApplication.getInstance().getSessionRepo().getCurrentSession();

    public final LiveData<String> username = ReactiveUtil.observableToNullableLiveData(
           session.map(x -> x.transform(y -> y.username))
    );

    public final LiveData<String> clinicName = ReactiveUtil.observableToNullableLiveData(
            Util.getClinic()
                .map(x -> x.transform(y -> y.name))
    );

    public final LiveData<Integer> serviceCount = ReactiveUtil.observableToLiveData(
            Util.getClinic()
                    .flatMap(clinicOptional -> clinicOptional.isPresent() ? clinicOptional.get().getServices().toObservable().map(List::size) : Observable.just(0))
                    .observeOn(AndroidSchedulers.mainThread())
    );

    public void setClinicHoursFragment(@NonNull ClinicHoursFragment fragment) {
        Util.getClinicId()
                .map(x -> x.isPresent() ? x.get() : "")
                .subscribe(x -> fragment.setClinicId(x))
                ;
    }
}
