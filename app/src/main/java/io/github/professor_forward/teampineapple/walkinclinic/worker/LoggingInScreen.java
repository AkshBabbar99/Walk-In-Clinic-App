package io.github.professor_forward.teampineapple.walkinclinic.worker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import java.util.concurrent.TimeUnit;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.global.Session;
import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.AdminRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.EmployeeRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.PatientRole;
import io.reactivex.Single;

public class LoggingInScreen extends WorkerFragment<Optional<Session>> {
    @Override
    public Single<Optional<Session>> getAction() {
        return MyApplication.getInstance().getSessionRepo().getCurrentSession()
                .firstOrError()
                .delay(1, TimeUnit.SECONDS);
    }

    @NonNull
    @Override
    public Optional<NavDirections> getDestination(Optional<Session> login) {
        return Optional.of(getDestinationInternal(login));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private NavDirections getDestinationInternal(Optional<Session> login) {
        if (!login.isPresent()) {
            Log.i("logging-in", "No session");
            MyApplication.getInstance().toast(R.string.session_expired);
            return LoggingInScreenDirections.doLogout();
        }

        String role = login.get().roleId;
        switch (role) {
            case AdminRole.ROLE_KEY:
                Log.i("logging-in", "Navigating to admin dashboard");
                return LoggingInScreenDirections.welcomeAdmin();
            case EmployeeRole.ROLE_KEY:
                Log.i("logging-in", "Navigating to employee dashboard");
                return LoggingInScreenDirections.welcomeEmployee();
            case PatientRole.ROLE_KEY:
                Log.i("logging-in", "Navigating to patient dashboard");
                return LoggingInScreenDirections.welcomePatient();
            default:
                Log.i("logging-in", "Invalid user role: " + role);
                MyApplication.getInstance().toast(R.string.session_expired);
                return LoggingOutScreenDirections.doLogout();
        }
    }
}
