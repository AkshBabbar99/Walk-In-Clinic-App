package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import java.sql.Time;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.EmployeeTimePickerBinding;

public class EmployeeTimePickerFragment extends Fragment {
    private final Time initialTime;
    private final MutableLiveData<Time> vm;

    EmployeeTimePickerFragment(Time initialTime, MutableLiveData<Time> vm) {
        this.initialTime = initialTime;
        this.vm = vm;
        vm.setValue(initialTime);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        EmployeeTimePickerBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.employee_time_picker, container, false);
        TimePicker tp = binding.picker;

        //noinspection deprecation
        int mm = initialTime.getMinutes();
        //noinspection deprecation
        int hh = initialTime.getHours();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.setHour(hh);
            tp.setMinute(mm);
        } else {
            tp.setCurrentHour(hh);
            tp.setCurrentMinute(mm);
        }

        vm.setValue(initialTime);
        tp.setOnTimeChangedListener(
                (view, hourOfDay, minute) ->
                        vm.setValue(Time.valueOf(hourOfDay + ":" + minute + ":0"))
        );

        return binding.getRoot();
    }

}
