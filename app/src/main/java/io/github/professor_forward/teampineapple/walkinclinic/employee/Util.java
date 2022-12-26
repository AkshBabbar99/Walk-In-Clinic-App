package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.util.Log;

import androidx.paging.PagedList;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.github.professor_forward.teampineapple.walkinclinic.repo.EmployeeRole;
import io.reactivex.Observable;

class Util {
    private static final Observable<EmployeeRole> role = MyApplication.getInstance().getSessionRepo().getCurrentRole()
            .filter(x -> x instanceof EmployeeRole)
            .map(x -> (EmployeeRole) x)
            .doOnNext(x -> {
                Log.i("Util", "EmployeeRole: " + x);
            })
            ;

    public static Observable<EmployeeRole> getEmployee() {
        return role;
    }

    public static Observable<Optional<String>> getClinicId() {
        return role.map(x -> Optional.fromNullable(x.clinicId));
    }

    public static Observable<Optional<Clinic>> getClinic() {
        return role.flatMap(EmployeeRole::getClinic);
    }

    public static Observable<PagedList<ClinicService>> getServices() {
        return getClinicId().flatMap(x -> MyApplication.getInstance().getRepo().clinicServices().listByClinicId(x.or("")));
    }
}
