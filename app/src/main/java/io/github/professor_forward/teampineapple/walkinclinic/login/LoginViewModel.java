package io.github.professor_forward.teampineapple.walkinclinic.login;

import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.global.Session;
import io.reactivex.Observable;
import io.reactivex.Single;

public class LoginViewModel extends LoginBaseViewModel {
    {
        validInternal.onNext(false);
        Observable.combineLatest(
                email.outputInternal,
                password.outputInternal,
                (x, y) -> !x.or(y).isPresent()
        ).subscribe(validInternal);
    }

    @Override
    public Single<Optional<Session>> getAction() {
        return MyApplication.getInstance().getSessionRepo()
                .login(email.input.getValue(), password.input.getValue())
                .map(Optional::of)
                .toSingle(Optional.absent())
                ;
    }

    @Nullable
    @Override
    public Optional<NavDirections> getDestination(Object value) {
        //noinspection unchecked
        if (!((Optional<Session>) value).isPresent()) {
            MyApplication.getInstance().toast(R.string.login_failed);
            //noinspection OptionalAssignedToNull
            return null;
        }
        return Optional.of(LoginScreenDirections.loginOk());
    }
}
