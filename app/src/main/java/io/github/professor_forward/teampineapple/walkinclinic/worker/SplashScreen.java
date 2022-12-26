package io.github.professor_forward.teampineapple.walkinclinic.worker;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import java.util.concurrent.TimeUnit;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.global.Session;
import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.reactivex.Single;

public class SplashScreen extends WorkerFragment<Optional<Session>> {
    public SplashScreen() {
        super(R.layout.splash);
    }

    @Override
    public Single<Optional<Session>> getAction() {
        return MyApplication.getInstance().getSessionRepo().getCurrentSession()
                .firstOrError()
                .delay(1, TimeUnit.SECONDS);
    }

    @NonNull
    @Override
    public Optional<NavDirections> getDestination(Optional<Session> login) {
        return Optional.of(login.isPresent() ?
                SplashScreenDirections.startLoggedIn() :
                SplashScreenDirections.startLoggedOut());
    }
}
