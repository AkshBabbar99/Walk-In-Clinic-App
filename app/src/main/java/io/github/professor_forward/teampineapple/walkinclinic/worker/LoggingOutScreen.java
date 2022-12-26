package io.github.professor_forward.teampineapple.walkinclinic.worker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import java.util.concurrent.TimeUnit;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.reactivex.Single;

public class LoggingOutScreen extends WorkerFragment {
    @Override
    public Single<?> getAction() {
        return MyApplication.getInstance().getSessionRepo().logout()
                .doOnError(t ->
                        Log.w("LoggingOutScreen",
                                "SessionRepo rejected logout, retrying", t)
                )
                .delay(1, TimeUnit.SECONDS)
                .retry()
                .andThen(Single.just(true))
                ;
    }

    @NonNull
    @Override
    public Optional<NavDirections> getDestination(Object ignore) {
        MyApplication.getInstance().toast(R.string.logout_ok);
        return Optional.of(LoggingOutScreenDirections.logoutOk());
    }
}
