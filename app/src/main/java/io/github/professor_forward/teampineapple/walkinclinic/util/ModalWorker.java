package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavDirections;

import com.google.common.base.Optional;

import io.reactivex.Single;

public interface ModalWorker<T> {
    /**
     * Gets the action to start running when the activity is created.
     * <br/>
     * This method will be called on the main thread but
     * the action will be executed in a background thread.
     * <br/>
     * The Single must succeed and its result will be passed to {@link #getDestination(Object)}.
     * It must also be idempotent.
     *
     * @return The action
     */
    Single<T> getAction();

    /**
     * Gets the destination to go to after the action, or null to ask the user to try again.
     * <br/>
     * Non-UI work MUST NOT be done here. This will not be called if the fragment is destroyed.
     * Absent means back. Null means stay. Present means go.
     */
    @Nullable
    Optional<NavDirections> getDestination(T data);

    /**
     * Gets a lifecycle owner bound by the lifecycle of the currently active screen
     * (e.g. LoginScreen).
     *
     * @param view A view inside the screen
     * @return An appropriate lifecycle owner
     */
    @NonNull
    static LifecycleOwner findLifecycleOwner(@NonNull View view) {
        Context context = view.getContext();
        while (true) {
            if (context instanceof LifecycleOwner) {
                //noinspection ConstantConditions
                return ((AppCompatActivity) context)
                        .getSupportFragmentManager()
                        .getPrimaryNavigationFragment()
                        .getChildFragmentManager()
                        .getFragments()
                        .get(0)
                        ;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
    }
}
