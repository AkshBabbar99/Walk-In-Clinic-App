package io.github.professor_forward.teampineapple.walkinclinic.patient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;

public class PatientViewModel extends ViewModel {
    public final LiveData<String> username = ReactiveUtil.observableToNullableLiveData(
            MyApplication.getInstance().getSessionRepo().getCurrentSession()
                    .map(x -> x.transform(y -> y.username))
    );
}
