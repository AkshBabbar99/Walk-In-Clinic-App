package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.commonui.ClinicHoursFragment;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.EmployeeDashboardBinding;

public class EmployeeDashboard extends Fragment {
    private EmployeeDashboardBinding binding;
    private ClinicHoursFragment clinicHoursFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.employee_dashboard, container, false);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        clinicHoursFragment = new ClinicHoursFragment();
        ft.replace(R.id.hours, clinicHoursFragment);
        ft.addToBackStack(null);
        ft.commit();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EmployeeViewModel vm = ViewModelProviders.of(this).get(EmployeeViewModel.class);
        vm.setClinicHoursFragment(clinicHoursFragment);
        binding.setVm(vm);
        binding.setLifecycleOwner(this);
    }
}
