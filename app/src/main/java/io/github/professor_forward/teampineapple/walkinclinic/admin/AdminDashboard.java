package io.github.professor_forward.teampineapple.walkinclinic.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.AdminDashboardBinding;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;

public class AdminDashboard extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AdminDashboardBinding binding = AdminDashboardBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        ReactiveUtil.observableToNullableLiveData(
                MyApplication.getInstance().getSessionRepo().getCurrentSession()
                        .map(x -> x.transform(y -> y.username))
        ).observe(this, binding::setVm);
        return binding.getRoot();
    }
}
