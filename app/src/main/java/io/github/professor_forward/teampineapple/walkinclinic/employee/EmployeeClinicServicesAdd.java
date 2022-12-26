package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Optional;

import java.util.List;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.EmployeeClinicServiceListAddBinding;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EmployeeClinicServicesAdd extends Fragment {
    public final int DISPLAY_ALL_ON_DELETE_THRESHOLD = 5;

    private ActionMode actionMode;
    private EmployeeClinicServicesAddViewModel model;
    private EmployeeClinicServiceListAddBinding binding;
    private final EmployeeClinicServicesAddAdapter adapter = new EmployeeClinicServicesAddAdapter();
    private RecyclerView list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.employee_clinic_service_list_add, container, false);
        View view = binding.getRoot();
        list = view.findViewById(R.id.employeeClinicServiceList);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(EmployeeClinicServicesAddViewModel.class);
        binding.setVm(model);
        binding.setLifecycleOwner(this);

        initAdapter();
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        list.setAdapter(adapter);

        adapter.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Don't care
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                mode.getMenuInflater().inflate(R.menu.cab_employee_service_add, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.add) {
                    addServices(adapter.getSelectedItems())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                adapter.setSelectableOff();
                                mode.finish();
                            })
                    ;
                    return true;
                }
                mode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                Navigation.findNavController(list).navigate(EmployeeClinicServicesAddDirections.add());
            }
        });
        adapter.setSelectableOn(list);

        //noinspection ResultOfMethodCallIgnored
        model.serviceList
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> adapter.submitList(list));
    }

    private Completable addServices(List<ClinicService> services) {
        MyApplication myApplication = MyApplication.getInstance();
        ClinicRepo repo = MyApplication.getInstance().getRepo().clinics();
        return Util.getClinic()
                .map(Optional::get)
                .take(1)
                .flatMapCompletable(clinic -> repo.addServices(clinic, services))
                .doOnComplete(() -> myApplication.toast(myApplication.getResources().getQuantityString(R.plurals.clinic_services_added, services.size(), services.size())))
                ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (actionMode != null)
            actionMode.finish();
    }

}
