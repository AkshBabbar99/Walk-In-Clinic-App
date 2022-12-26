package io.github.professor_forward.teampineapple.walkinclinic.patient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.PatientClinicSearchListBinding;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class PatientClinicSearch extends Fragment {
    public final int DISPLAY_ALL_ON_DELETE_THRESHOLD = 5;

    private ActionMode actionMode;
    private PatientClinicSearchViewModel model;
    private PatientClinicSearchListBinding binding;
    private final PatientClinicSearchAdapter adapter = new PatientClinicSearchAdapter();
    private RecyclerView list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.patient_clinic_search_list, container, false);
        View view = binding.getRoot();
        list = view.findViewById(R.id.patientClinicServiceList);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(PatientClinicSearchViewModel.class);
        binding.setVm(model);
        binding.setLifecycleOwner(this);

        initAdapter();
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        list.setAdapter(adapter);
        //noinspection ResultOfMethodCallIgnored
        model.pagedListFlowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> adapter.submitList(list));
    }
}
