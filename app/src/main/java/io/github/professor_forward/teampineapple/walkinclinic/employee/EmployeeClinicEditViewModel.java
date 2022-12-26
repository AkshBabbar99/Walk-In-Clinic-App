package io.github.professor_forward.teampineapple.walkinclinic.employee;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.RecordWithSameNameExistsException;
import io.github.professor_forward.teampineapple.walkinclinic.util.ActionBaseViewModel;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableIntegerSandwich;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableSandwich;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EmployeeClinicEditViewModel extends ActionBaseViewModel {
    private Clinic clinic;

    public EmployeeClinicEditViewModel() {
        Util.getClinic()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
            if (x.isPresent()) {
                clinic = x.get();
                name.input.setValue(clinic.name);
                address.input.setValue(clinic.address);
                phoneNumber.input.setValue(clinic.phoneNumber);
                acceptedInsuranceTypes.input.setValue(clinic.acceptedInsuranceTypes);
                acceptedPaymentTypes.input.setValue(clinic.acceptedPaymentTypes);
                numStaff.setValue(clinic.numStaff);
                numNurses.setValue(clinic.numNurses);
                numDoctors.setValue(clinic.numDoctors);
            }
        });
    }

    public final LiveDataObservableSandwich<String> name = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateNonEmpty)
    );

    public final LiveDataObservableSandwich<String> address = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateNonEmpty)
    );

    public final LiveDataObservableSandwich<String> phoneNumber = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateNonEmpty)
    );

    public final LiveDataObservableSandwich<String> acceptedInsuranceTypes = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateNonEmpty)
    );

    public final LiveDataObservableSandwich<String> acceptedPaymentTypes = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateNonEmpty)
    );

    public final LiveDataObservableIntegerSandwich numStaff = new LiveDataObservableIntegerSandwich(
            0,
            this::validateNonPositive
    );

    public final LiveDataObservableIntegerSandwich numNurses = new LiveDataObservableIntegerSandwich(
            0,
            this::validateNonPositive
    );

    public final LiveDataObservableIntegerSandwich numDoctors = new LiveDataObservableIntegerSandwich(
            0,
            this::validateNonPositive
    );

    {
        validInternal.onNext(false);
        Observable.combineLatest(
                name.outputInternal,
                address.outputInternal,
                phoneNumber.outputInternal,
                acceptedInsuranceTypes.outputInternal,
                acceptedPaymentTypes.outputInternal,
                numStaff.outputInternal,
                numNurses.outputInternal,
                numDoctors.outputInternal,
                (a, b, c, d, e, f, g, h) -> !a.or(b).or(c).or(d).or(e).or(f).or(g).or(h).isPresent()
        ).subscribe(validInternal);
    }

    @Nullable
    @Override
    public Optional<NavDirections> getDestination(Object data) {
        if (data == Boolean.TRUE) {
            return Optional.absent();
        }
        //noinspection OptionalAssignedToNull
        return null;
    }

    @Override
    public Single<Boolean> getAction() {
        ClinicRepo clinicRepo = MyApplication.getInstance().getRepo().clinics();
        Completable upsert;
        if (clinic == null) {
            upsert = Util.getEmployee()
                    .take(1)
                    .flatMapCompletable(x -> clinicRepo.create(x,
                            name.input.getValue(),
                            address.input.getValue(),
                            phoneNumber.input.getValue(),
                            acceptedInsuranceTypes.input.getValue(),
                            acceptedPaymentTypes.input.getValue(),
                            numStaff.getValue(),
                            numNurses.getValue(),
                            numDoctors.getValue())
                    );
        } else {
            upsert = clinicRepo.update(clinic,
                    name.input.getValue(),
                    address.input.getValue(),
                    phoneNumber.input.getValue(),
                    acceptedInsuranceTypes.input.getValue(),
                    acceptedPaymentTypes.input.getValue(),
                    numStaff.getValue(),
                    numNurses.getValue(),
                    numDoctors.getValue()
            );
        }

        return upsert
                    .doOnComplete(() -> MyApplication.getInstance().toast(R.string.saved_clinic))
                    .doOnError(e -> {
                        if (e instanceof RecordWithSameNameExistsException) {
                            MyApplication.getInstance().toast(R.string.error_clinic_same_name);
                        } else {
                            Log.e("Edit", e.toString());
                            MyApplication.getInstance().toast(R.string.error_saving_clinic);
                        }
                    })
                    .toSingleDefault(true)
                    .onErrorReturnItem(false)
                    ;
    }

    private Optional<String> validateNonEmpty(String string) {
        if (string == null || string.length() == 0) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_string));
        }
        return Optional.absent();
    }

    private Optional<String> validateNonPositive(Integer integer) {
        if (integer < 0) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_non_positive));
        }
        return Optional.absent();
    }
}
