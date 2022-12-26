package io.github.professor_forward.teampineapple.walkinclinic.commonui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.ClinicHoursBinding;

public class ClinicHoursFragment extends Fragment {

    private ClinicHoursViewModel mViewModel;
    private ClinicHoursBinding binding;
    private String clinicId;

    public static ClinicHoursFragment newInstance() {
        return new ClinicHoursFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.clinic_hours, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClinicHoursViewModel.class);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);


        Bundle bundle = getArguments();
        if (bundle != null) {
            setClinicId(bundle.getString("clinicId"));
        }
        if (clinicId == null) {
            Log.e("ClinicHoursFragment", "Got null clinicId");
            return;
        }
        else {
            setClinicId(clinicId);
        }
    }

    public void setClinicId(@NonNull String id) {
        this.clinicId = id;
        if (mViewModel != null) {
            mViewModel.setClinicId(id);
        }
    }
}
