package io.github.professor_forward.teampineapple.walkinclinic.patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.databinding.ClinicRateBinding;

public class ClinicRate extends Fragment {
    private ClinicRateBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ClinicRateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ClinicRateViewModel vm = ViewModelProviders.of(this).get(ClinicRateViewModel.class);
        binding.setVm(vm);
        binding.setLifecycleOwner(this);
        Bundle bundle = getArguments();
        String clinicid = bundle.getString("id");
        if (clinicid != null) {
            vm.setId(clinicid);
        }
    }

}
