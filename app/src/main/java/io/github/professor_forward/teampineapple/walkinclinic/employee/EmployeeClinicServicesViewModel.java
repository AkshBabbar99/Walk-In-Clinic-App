package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Observable;

public class EmployeeClinicServicesViewModel extends ViewModel {
    public final Observable<PagedList<ClinicService>> serviceList;
    public final LiveData<String> clinicID = ReactiveUtil.observableToNullableLiveData(Util.getClinicId());

    public EmployeeClinicServicesViewModel() {
        serviceList = Util.getClinicId().flatMap(x -> MyApplication.getInstance().getRepo().clinicServices().listByClinicId(x.or("")));
    }

    public void addService(View vm){
        Navigation.findNavController(vm).navigate(EmployeeClinicServicesDirections.add());
    }
}
