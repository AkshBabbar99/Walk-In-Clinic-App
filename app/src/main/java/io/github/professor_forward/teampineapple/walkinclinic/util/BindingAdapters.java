package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.text.DateFormat;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicEmployeeRole;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicHours;

public final class BindingAdapters {
    @BindingAdapter("error")
    public static void setError(EditText et, String error) {
        et.setError(error);
    }

    @BindingAdapter("android:text")
    public static void setText(TextView tv, ClinicHours h) {
        if (h == null) {
            tv.setText(R.string.closed);
            return;
        }
        String start = DateFormat.getTimeInstance().format(h.startTime);
        String end = DateFormat.getTimeInstance().format(h.endTime);
        tv.setText(start + "–" + end);
    }

    @BindingAdapter("android:text")
    public static void setText(TextView tv, ClinicEmployeeRole role) {
        tv.setText(MyApplication.getServiceRoleString(role));
    }

    @BindingAdapter("android:textUserRoleId")
    public static void setText(TextView tv, String roleId) {
        tv.setText(MyApplication.getRoleString(roleId));
    }

    private BindingAdapters() {}
}
