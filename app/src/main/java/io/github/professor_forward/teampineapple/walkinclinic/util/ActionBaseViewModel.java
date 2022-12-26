package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.common.base.Optional;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public abstract class ActionBaseViewModel extends ViewModel implements ModalWorker {
    protected final BehaviorSubject<Boolean> validInternal = BehaviorSubject.createDefault(true);
    private final BehaviorSubject<Boolean> busyInternal = BehaviorSubject.createDefault(false);

    /**
     * Whether the main action is currently allowed.
     */
    public final LiveData<Boolean> valid = ReactiveUtil.observableToLiveData(validInternal);

    /**
     * Whether to show a LoadingBar and hide everything else.
     */
    public final LiveData<Boolean> busy = ReactiveUtil.observableToLiveData(busyInternal);

    @SuppressLint("CheckResult")
    @MainThread
    public final void onActionClick(@NonNull View view) {
        if (busyInternal.getValue() || !validInternal.getValue()) {
            return;
        }

        busyInternal.onNext(true);

        //noinspection unchecked
        ReactiveUtil.observeOnce(ReactiveUtil.observableToLiveData(
                getAction()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
        ),  ModalWorker.findLifecycleOwner(view), ((data) -> {
            //noinspection unchecked
            Optional<NavDirections> destination = getDestination(data);
            //noinspection OptionalAssignedToNull
            if (destination != null) {
                NavController nc = Navigation.findNavController(view);
                if (destination.isPresent()) {
                    nc.navigate(destination.get());
                } else {
                    if (!nc.popBackStack()) {
                        throw new RuntimeException("Failed to go back!");
                    }
                }
            } else {
                busyInternal.onNext(false);
            }
        }));
    }
}
