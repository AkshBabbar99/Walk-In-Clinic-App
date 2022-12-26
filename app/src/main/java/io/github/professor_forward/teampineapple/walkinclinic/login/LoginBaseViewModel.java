package io.github.professor_forward.teampineapple.walkinclinic.login;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.util.ActionBaseViewModel;
import io.github.professor_forward.teampineapple.walkinclinic.util.LiveDataObservableSandwich;

abstract class LoginBaseViewModel extends ActionBaseViewModel {
    public final LiveDataObservableSandwich<String> email = new LiveDataObservableSandwich<>(
            // Default Value
            "",
            // Input: Username from EditText
            // Output: Username validation error message
            x -> x.map(this::validateEmail)
    );
    public final LiveDataObservableSandwich<String> password = new LiveDataObservableSandwich<>(
            // Default value
            "",
            // Input: Password from EditText
            // Output: Password validation error message
            x -> x.map(this::validatePassword)
    );

    private Optional<String> validateEmail(String email) {
        if (!email.equals("admin") && email.indexOf("@") < 1) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_email));
        }
        return Optional.absent();
    }

    private Optional<String> validatePassword(String password) {
        if (password.length() <= 5) {
            return Optional.of(MyApplication.getInstance().getString(R.string.invalid_password));
        }
        // TODO
        return Optional.absent();
    }
}
