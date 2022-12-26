package io.github.professor_forward.teampineapple.walkinclinic.worker;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.common.base.Optional;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.util.ModalWorker;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.schedulers.Schedulers;

abstract class WorkerFragment<T> extends Fragment implements ModalWorker<T> {
    WorkerFragment() {
        super(R.layout.loading);
    }

    WorkerFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ReactiveUtil.observeOnce(ReactiveUtil.observableToLiveData(
                getAction()
                        .subscribeOn(Schedulers.computation())
                        .toObservable()
        ), this, data ->
                NavHostFragment.findNavController(this).navigate(getDestination(data).get())
        );
    }

    /**
     * Gets the destination to go to after the action.
     * <br/>
     * Non-UI work MUST NOT be done here. This will not be called if the fragment is destroyed.
     * There must always be a destination.
     */
    @NonNull
    @Override
    public abstract Optional<NavDirections> getDestination(T data);
}
