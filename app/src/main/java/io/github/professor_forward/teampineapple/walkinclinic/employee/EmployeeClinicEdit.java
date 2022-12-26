package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.databinding.EmployeeClinicEditBinding;

public class EmployeeClinicEdit extends Fragment {
    private EmployeeClinicEditBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = EmployeeClinicEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EmployeeClinicEditViewModel vm = ViewModelProviders.of(this).get(EmployeeClinicEditViewModel.class);
        binding.setVm(vm);
        binding.setLifecycleOwner(this);
    }
}
