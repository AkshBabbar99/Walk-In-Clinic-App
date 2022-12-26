package io.github.professor_forward.teampineapple.walkinclinic.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.LoginBinding;

public class LoginScreen extends Fragment {
    private LoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.setVm(ViewModelProviders.of(this).get(LoginViewModel.class));
        binding.setLifecycleOwner(this);
    }
}
