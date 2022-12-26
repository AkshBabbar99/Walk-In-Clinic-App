package io.github.professor_forward.teampineapple.walkinclinic.admin;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Observable;

public class AdminClinicServicesViewModel extends ViewModel {
    public final MutableLiveData<String> searchText = new MutableLiveData<>("");
    public final Observable<PagedList<ClinicService>> serviceList;

    public AdminClinicServicesViewModel() {
        serviceList = ReactiveUtil.liveDataToObservable(searchText)
                .flatMap(search -> MyApplication.getInstance().getRepo().clinicServices().listByName(search))
                ;
    }

    public void addService(View vm){
        Navigation.findNavController(vm).navigate(AdminClinicServicesDirections.add());
    }
}
