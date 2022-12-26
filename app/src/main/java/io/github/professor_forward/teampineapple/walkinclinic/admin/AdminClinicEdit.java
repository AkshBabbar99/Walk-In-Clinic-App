package io.github.professor_forward.teampineapple.walkinclinic.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.databinding.AdminClinicEditBinding;

public class AdminClinicEdit extends Fragment {
    private AdminClinicEditBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AdminClinicEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle icicle) {
        super.onActivityCreated(icicle);
        AdminClinicEditViewModel vm = ViewModelProviders.of(this).get(AdminClinicEditViewModel.class);
        binding.setVm(vm);
        binding.setLifecycleOwner(this);

        Bundle raw = getArguments();
        assert raw != null;
        if (raw.getString("id") != null) {
            AdminClinicEditArgs args = AdminClinicEditArgs.fromBundle(raw);
            String id = args.getId();
            if (id != null) {
                vm.setId(id);
                vm.name.input.setValue(args.getName());
                vm.role.input.setValue(args.getRole());
            }
        }
    }
}
