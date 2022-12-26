package io.github.professor_forward.teampineapple.walkinclinic.patient;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;
import io.github.professor_forward.teampineapple.walkinclinic.repo.PatientRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Rating;
import io.github.professor_forward.teampineapple.walkinclinic.repo.RatingRepo;
import io.github.professor_forward.teampineapple.walkinclinic.util.ActionBaseViewModel;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableSandwich;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ClinicRateViewModel extends ActionBaseViewModel {
    private Clinic clinic;
    private PatientRole patient;
    private Rating rate;

    public ClinicRateViewModel() {
        PatientUtil.getPatient()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
                    patient = x;
                    loadRating();
                });
    }

    public final LiveDataObservableSandwich<String> comments = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateNonEmpty)
    );

    public final MutableLiveData<Float> rating = new MutableLiveData<>((float) 3.0);

     {
            validInternal.onNext(false);
            comments.outputInternal.map(a -> !a.isPresent()).subscribe(validInternal);
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
        RatingRepo ratingRepo = MyApplication.getInstance().getRepo().ratings();
        Completable upsert = ratingRepo.create(clinic, patient, comments.input.getValue(), Math.round(rating.getValue()));
        if (rate != null) {
            upsert = ratingRepo.delete(rate)
                    .andThen(upsert)
                    ;
        }
        return upsert
                .doOnComplete(() -> MyApplication.getInstance().toast(R.string.updated_rating))
                .doOnError(e -> {
                })
                .toSingleDefault(true)
                .onErrorReturnItem(false)
                ;
    }

    public void setId(String id){
        MyApplication.getInstance().getRepo().clinics().getById(id)
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
                    if (x.isPresent()) {
                      clinic = x.get();
                      loadRating();
                    }
                });
    }


    private void loadRating () {
        if (clinic == null || patient == null) {
            return;
        }

        MyApplication.getInstance().getRepo().ratings().getAssociated(patient.id, clinic.id)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
                    rate = x;
                    rating.setValue((float) rate.value);
                    comments.input.setValue((rate.comment));
                });

    }

    private Optional<String> validateNonEmpty(String string) {
        if (string == null || string.length() == 0) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_string));
        }
        return Optional.absent();
    }
}
