package io.github.professor_forward.teampineapple.walkinclinic.patient;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.common.base.Optional;

import java.util.Date;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.PatientClinicViewBinding;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class PatientClinicScreen extends Fragment {
    private Observable<Clinic> live;
    private Observable<Integer> waitTime;
    private PatientClinicViewBinding binding;
    private Disposable sub;
    private Disposable sub2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.patient_clinic_view, container, false);
        assert getArguments() != null;
        String id = getArguments().getString("id");
        assert id != null;
        live = MyApplication.getInstance().getRepo().clinics().getById(id)
                .filter(Optional::isPresent)
                .map(Optional::get);
        waitTime = MyApplication.getInstance().getRepo().clinics().getWaitTimeForDate(id, new Date());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        sub = live.subscribe(binding::setVm);
        sub2 = waitTime.subscribe(binding::setWaitTime);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (sub != null) {
            sub.dispose();
            sub = null;
        }
        if (sub2 != null) {
            sub2.dispose();
            sub2 = null;
        }
        super.onPause();
    }

    public static void book(String id, View v) {
        Date date = new Date();
        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                book(id, new Date(year - 1900, month, dayOfMonth));
            }
        }, date.getYear() + 1900, date.getMonth(), date.getDate()
        ).show();
    }

    private static void book(String id, Date date) {
        PatientUtil.getPatient().take(1).singleOrError().zipWith(
                MyApplication.getInstance().getRepo().clinics().getById(id).take(1).singleOrError(),
                (patient, clinic) -> {
                    MyApplication.getInstance().toast("Booking...");
                    return MyApplication.getInstance().getRepo().bookings().create(clinic.get(), patient, date);
                }
        )
                .flatMapCompletable(x -> x)
                .doOnComplete(() -> MyApplication.getInstance().toast("Booking done!"))
                .subscribe();
    }
}
