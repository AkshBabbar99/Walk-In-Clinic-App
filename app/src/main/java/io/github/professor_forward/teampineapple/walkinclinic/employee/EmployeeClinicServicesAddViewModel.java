package io.github.professor_forward.teampineapple.walkinclinic.employee;

import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.reactivex.Observable;

public class EmployeeClinicServicesAddViewModel extends ViewModel {
    public final Observable<PagedList<ClinicService>> serviceList;

    public EmployeeClinicServicesAddViewModel() {
        serviceList = Util.getClinicId().flatMap(x -> MyApplication.getInstance().getRepo().clinicServices().searchNotProvidedByClinic(x.or(""), ""));
    }
}
