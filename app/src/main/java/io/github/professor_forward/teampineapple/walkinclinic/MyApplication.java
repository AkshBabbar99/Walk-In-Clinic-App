package io.github.professor_forward.teampineapple.walkinclinic;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.multidex.MultiDexApplication;

import io.github.professor_forward.teampineapple.walkinclinic.global.SessionRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.AdminRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicEmployeeRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.EmployeeRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.PatientRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Repository;
import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRepo;

import static io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil.runOnMainThread;

public class MyApplication extends MultiDexApplication {
    private static final String PREFERENCES_NAME =
            "io.github.professor_forward.teampineapple.walkinclinic_preferences";
    private static MyApplication INSTANCE;
    private SessionRepo sessionRepo;
    private Repository repo;

    public MyApplication() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Globals already created");
        }
        INSTANCE = this;
    }

    public UserRepo getUserRepo() {
        return repo.users();
    }

    public SessionRepo getSessionRepo() {
        return sessionRepo;
    }


    public void toast(String text) {
        runOnMainThread(() -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());
    }

    public void toast(@StringRes int resId) {
        runOnMainThread(() -> Toast.makeText(this, resId, Toast.LENGTH_SHORT).show());
    }

    public void toast(@StringRes int resId, Object ...formatArgs) {
        toast(getString(resId, formatArgs));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        repo = new Repository(this);
        sessionRepo = new SessionRepo(prefs, repo.users());
    }

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    public static @StringRes int getRoleString(String roleId) {
        switch (roleId) {
            case AdminRole.ROLE_KEY:
                return R.string.admin_role;
            case PatientRole.ROLE_KEY:
                return R.string.patient_role;
            case EmployeeRole.ROLE_KEY:
                return R.string.employee_role;
            default:
                throw new IllegalArgumentException("Invalid role id:" + roleId);
        }
    }

    public static @StringRes int getServiceRoleString(ClinicEmployeeRole roleId) {
        switch (roleId) {
            case STAFF:
                return R.string.staff_role;
            case NURSE:
                return R.string.nurse_role;
            case DOCTOR:
                return R.string.doctor_role;
            default:
                throw new IllegalArgumentException("Invalid role id:" + roleId);
        }
    }
    public Repository getRepo() {
        return repo;
    }
}
