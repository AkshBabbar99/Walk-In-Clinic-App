package io.github.professor_forward.teampineapple.walkinclinic.login;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.global.Session;
import io.github.professor_forward.teampineapple.walkinclinic.repo.EmployeeRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.PatientRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.UserWithEmailExistsException;
import io.github.professor_forward.teampineapple.walkinclinic.util.ActionBaseViewModel;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableSandwich;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Observable;
import io.reactivex.Single;

public class RegisterViewModel extends ActionBaseViewModel {
    public final LiveDataObservableSandwich<String> role = new LiveDataObservableSandwich<>(
            PatientRole.ROLE_KEY,
            x -> x.map(this::validateRole)
    );

    public final LiveDataObservableSandwich<String> username = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateUsername)
    );

    public final LiveDataObservableSandwich<String> email = new LiveDataObservableSandwich<>(
            "",
            x -> x.map(this::validateEmail)
    );

    public final MutableLiveData<String> password = new MutableLiveData<>("");
    public final MutableLiveData<String> passwordConfirm = new MutableLiveData<>("");
    private final Observable<Optional<String>> passwordErrorInternal = Observable.combineLatest(
            ReactiveUtil.liveDataToObservable(password),
            ReactiveUtil.liveDataToObservable(passwordConfirm),
            (x, y) -> {
                if (x.length() <= 5) {
                    return R.string.invalid_password;
                }
                if (!x.equals(y)) {
                    return R.string.invalid_password2;
                }
                return 0;
            }
    ).map(x -> x == 0 ? Optional.absent() : Optional.of(MyApplication.getInstance().getString(x)));
    public final LiveData<String> passwordError =
            ReactiveUtil.observableToNullableLiveData(passwordErrorInternal);

    {
        validInternal.onNext(false);
        Observable.combineLatest(
                username.outputInternal,
                email.outputInternal,
                passwordErrorInternal,
                role.outputInternal,
                (w, x, y, z) -> !w.or(x).or(y).or(z).isPresent()
        ).subscribe(validInternal);
    }


    @Override
    public Single<Optional<Session>> getAction() {
        return MyApplication.getInstance().getSessionRepo()
                .register(username.input.getValue(), email.input.getValue(), password.getValue(), role.input.getValue())
                .onErrorComplete(e -> {
                    if (e instanceof UserWithEmailExistsException) {
                        MyApplication.getInstance().toast(R.string.registration_failed_same_email);
                    }
                    else {
                        MyApplication.getInstance().toast(R.string.registration_failed);
                    }
                    return true;
                })
                .map(Optional::of)
                .toSingle(Optional.absent())
                ;
    }

    @Nullable
    @Override
    public Optional<NavDirections> getDestination(Object value) {
        //noinspection unchecked
        if (!((Optional<Session>) value).isPresent()) {
            //noinspection OptionalAssignedToNull
            return null;
        }
        return Optional.of(RegisterScreenDirections.registerOk());
    }

    private Optional<String> validateEmail(String email) {
        if (email.indexOf("@") < 1) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_email));
        }
        return Optional.absent();
    }

    private Optional<String> validateRole(String role){
        if(role == null || !(role.equals(PatientRole.ROLE_KEY) || role.equals(EmployeeRole.ROLE_KEY))){
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_role));
        }
        return Optional.absent();
    }

    private Optional<String> validateUsername(String name){
        if(name == null || name.length() == 0){
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_username));
        }
        return Optional.absent();
    }

    public void setRole(String role){
        this.role.input.setValue(role);
    }
}
