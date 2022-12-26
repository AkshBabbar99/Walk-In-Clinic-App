package io.github.professor_forward.teampineapple.walkinclinic.admin;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicEmployeeRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicServiceRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicServiceWithSameNameExistsException;
import io.github.professor_forward.teampineapple.walkinclinic.util.ActionBaseViewModel;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableSandwich;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class AdminClinicEditViewModel extends ActionBaseViewModel {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public final LiveDataObservableSandwich<ClinicEmployeeRole> role = new LiveDataObservableSandwich<>(
            ClinicEmployeeRole.STAFF,
            x -> x.map(this::validateRole)
    );

    public final LiveDataObservableSandwich<String> name = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateName)
    );

    {
        Observable.combineLatest(
                name.outputInternal,
                role.outputInternal,
                (w, x) -> !w.or(x).isPresent()
        ).subscribe(validInternal);
    }

    @Nullable
    @Override
    public Optional<NavDirections> getDestination(Object data) {
        if(data == Boolean.TRUE){
            return Optional.absent();
        }
        //noinspection OptionalAssignedToNull
        return null;
    }

    @Override
    public Single<Boolean> getAction() {
        ClinicServiceRepo repo = MyApplication.getInstance().getRepo().clinicServices();
        Completable completable = id == null ?
                repo.create(name.input.getValue(), role.input.getValue())
                        .doOnComplete(() -> MyApplication.getInstance().toast(R.string.added_service))
                :
                repo.update(id, name.input.getValue(), role.input.getValue())
                        .doOnComplete(() -> MyApplication.getInstance().toast(R.string.service_edited))
                ;
        return completable
                    .doOnError(e -> {
                        if (e instanceof ClinicServiceWithSameNameExistsException) {
                            MyApplication.getInstance().toast(R.string.edit_service_failed_same_name);
                        } else {
                            MyApplication.getInstance().toast(R.string.edit_service_failed);
                        }
                    })
                    .toSingleDefault(true)
                    .onErrorReturnItem(false)
                    ;
    }

    private Optional<String> validateRole(ClinicEmployeeRole role) {
        if (role == null || !(role.equals(ClinicEmployeeRole.STAFF) || role.equals(ClinicEmployeeRole.NURSE) || role.equals(ClinicEmployeeRole.DOCTOR))) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_role));
        }
        return Optional.absent();
    }

    private Optional<String> validateName(String name) {
        if (name == null || name.length() == 0) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_name));
        }
        return Optional.absent();
    }

    public void setRole(ClinicEmployeeRole role) {
        this.role.input.setValue(role);
    }

    public @StringRes int getButtonString(){
        return id == null ? R.string.action_add : R.string.action_edit;
    }
}

