package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import java.sql.Time;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.EmployeeClinicHoursDialogBinding;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicHours;
import io.github.professor_forward.teampineapple.walkinclinic.repo.DayOfWeek;

public class EmployeeClinicHoursDialog extends DialogFragment {
    private final DayOfWeek dow;
    private final ClinicHours initialHours;

    private static Time midnight() {
        return Time.valueOf("0:0:0");
    }

    private EmployeeClinicHoursDialogBinding binding;

    EmployeeClinicHoursDialog(DayOfWeek dow, ClinicHours hours) {
        this.dow = dow;
        this.initialHours = hours;
    }

    private static String getTitle(Fragment f, boolean end) {
        return f.getString(end ? R.string.end_time : R.string.start_time);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.employee_clinic_hours_dialog, container, false);
        binding.pager.setAdapter(new Adapter());
        new TabLayoutMediator(binding.tabs, binding.pager, (tab, position) -> {
            String title;
            switch (position) {
                case 0:
                    title = getTitle(this, false);
                    break;
                case 1:
                    title = getTitle(this, true);
                    break;
                default:
                    throw new AssertionError();
            }
            tab.setText(title);
        }).attach();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EmployeeClinicHoursDialogViewModel vm =
                ViewModelProviders.of(this).get(EmployeeClinicHoursDialogViewModel.class);
        binding.setVm(vm);
        binding.setLifecycleOwner(this);
        vm.dow = dow;
        vm.isOpen.setValue(initialHours != null);

        binding.cancel.setOnClickListener(v -> this.dismiss());
        binding.ok.setOnClickListener(v -> {
            vm.save();
            this.dismiss();
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    class Adapter extends FragmentStateAdapter {
        Adapter() {
            super(EmployeeClinicHoursDialog.this);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new EmployeeTimePickerFragment(
                            initialHours == null ? midnight() : initialHours.startTime,
                            binding.getVm().start
                    );
                case 1:
                    return new EmployeeTimePickerFragment(
                            initialHours == null ? midnight() : initialHours.endTime,
                            binding.getVm().end
                    );
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
