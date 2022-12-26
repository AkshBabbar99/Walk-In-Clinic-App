package io.github.professor_forward.teampineapple.walkinclinic.employee;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.EmployeeClinicServiceListBinding;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicRepo;
import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EmployeeClinicServices extends Fragment {
    private final int DISPLAY_ALL_ON_DELETE_THRESHOLD = 5;

    private ActionMode actionMode;
    private EmployeeClinicServicesViewModel model;
    private EmployeeClinicServiceListBinding binding;
    private final EmployeeClinicServicesAdapter adapter = new EmployeeClinicServicesAdapter();
    private RecyclerView list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.employee_clinic_service_list, container, false);
        View view = binding.getRoot();
        list = view.findViewById(R.id.employeeClinicServiceList);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(EmployeeClinicServicesViewModel.class);
        binding.setVm(model);
        binding.setLifecycleOwner(this);

        initAdapter();
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        list.setAdapter(adapter);

        adapter.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Don't care
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                mode.getMenuInflater().inflate(R.menu.cab_admin_users, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.delete) {
                    deleteServices(adapter.getSelectedItems())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(deletedItems -> {
                                if (deletedItems) {
                                    adapter.setSelectableOff();
                                    mode.finish();
                                }
                            });
                    return true;
                }
                mode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        });

        //noinspection ResultOfMethodCallIgnored
        model.serviceList
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> adapter.submitList(list));
    }

    private Single<Boolean> deleteServices(Iterable<ClinicService> services) {
        List<String> names = new ArrayList<>();
        for (ClinicService clinicService : services) {
            names.add(clinicService.name);
        }

        if (names.size() < 1) return Single.just(true);

        MyApplication myApplication = MyApplication.getInstance();
        Collections.sort(names);
        String message = names.size() <= DISPLAY_ALL_ON_DELETE_THRESHOLD
                ? myApplication.getString(R.string.prompt_confirm_delete_few_clinic_services, TextUtils.join(myApplication.getString(R.string.list_delimiter), names))
                : myApplication.getString(R.string.prompt_confirm_delete_many_clinic_services, names.size());

        return promptToDeleteUsers(message)
                .flatMap(response -> {
                    if (!response) return Single.just(false);
                    ClinicRepo repo = MyApplication.getInstance().getRepo().clinics();
                    return Util.getClinic()
                            .map(Optional::get)
                            .take(1)
                            .flatMapCompletable(clinic -> repo.deleteServices(clinic, services))
                            .doOnComplete(() -> myApplication.toast(myApplication.getResources().getQuantityString(R.plurals.clinic_services_deleted, names.size(), names.size())))
                            .toSingleDefault(true);
                });
    }

    private Single<Boolean> promptToDeleteUsers(String message) {
        return Single.create(emitter -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.prompt_confirm);
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                emitter.onSuccess(true);
                dialog.dismiss();
            });
            builder.setNegativeButton(android.R.string.no, ((dialog, which) -> {
                emitter.onSuccess(false);
                dialog.dismiss();
            }));
            AlertDialog dialog = builder.create();
            emitter.setCancellable(dialog::dismiss);
            dialog.show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (actionMode != null)
            actionMode.finish();
    }

}
