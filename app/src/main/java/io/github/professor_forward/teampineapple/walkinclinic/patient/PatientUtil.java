package io.github.professor_forward.teampineapple.walkinclinic.patient;

import android.util.Log;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.PatientRole;
import io.reactivex.Observable;

class PatientUtil {
    private static final Observable<PatientRole> role = MyApplication.getInstance().getSessionRepo().getCurrentRole()
            .filter(x -> x instanceof PatientRole)
            .map(x -> (PatientRole) x)
            .doOnNext(x -> {
                Log.i("Util", "PatientRole: " + x);
            })
            ;

    public static Observable<PatientRole> getPatient() {
        return role;
    }
}
