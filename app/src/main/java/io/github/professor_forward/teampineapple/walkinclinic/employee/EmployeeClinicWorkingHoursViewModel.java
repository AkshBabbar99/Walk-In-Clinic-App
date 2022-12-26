package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.base.Optional;

import java.util.List;

import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicHours;
import io.github.professor_forward.teampineapple.walkinclinic.repo.DayOfWeek;
import io.github.professor_forward.teampineapple.walkinclinic.util.ModalWorker;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Observable;

public class EmployeeClinicWorkingHoursViewModel extends ViewModel {
    private final Observable<List<ClinicHours>> allHours =
            Util.getClinic().filter(Optional::isPresent).map(Optional::get).flatMap(Clinic::getHours);
    public final LiveData<ClinicHours> sunday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.SUNDAY)));
    public final LiveData<ClinicHours> monday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.MONDAY)));
    public final LiveData<ClinicHours> tuesday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.TUESDAY)));
    public final LiveData<ClinicHours> wednesday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.WEDNESDAY)));
    public final LiveData<ClinicHours> thursday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.THURSDAY)));
    public final LiveData<ClinicHours> friday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.FRIDAY)));
    public final LiveData<ClinicHours> saturday = ReactiveUtil.observableToNullableLiveData(
            allHours.map((List<ClinicHours> hours) -> getHoursForDayFromList(hours, DayOfWeek.SATURDAY)));

    private static Optional<ClinicHours> getHoursForDayFromList(List<ClinicHours> hours, DayOfWeek dow) {
        ClinicHours result = null;
        for (ClinicHours h : hours) {
            if (h.dayOfWeek == dow) {
                if (result != null) {
                    throw new UnsupportedOperationException(
                            "Multiple hours per day not supported yet");
                }
                result = h;
            }
        }
        return Optional.fromNullable(result);
    }

    public void edit(View view, DayOfWeek dow) {
        ClinicHours hours;
        switch (dow) {
            case SUNDAY:
                hours = sunday.getValue();
                break;
            case MONDAY:
                hours = monday.getValue();
                break;
            case TUESDAY:
                hours = tuesday.getValue();
                break;
            case WEDNESDAY:
                hours = wednesday.getValue();
                break;
            case THURSDAY:
                hours = thursday.getValue();
                break;
            case FRIDAY:
                hours = friday.getValue();
                break;
            case SATURDAY:
                hours = saturday.getValue();
                break;
            default:
                throw new AssertionError();
        }
        EmployeeClinicHoursDialog dialog = new EmployeeClinicHoursDialog(dow, hours);
        FragmentManager fm = ((Fragment) ModalWorker.findLifecycleOwner(view)).getFragmentManager();
        dialog.show(fm, "hours");
    }
}
