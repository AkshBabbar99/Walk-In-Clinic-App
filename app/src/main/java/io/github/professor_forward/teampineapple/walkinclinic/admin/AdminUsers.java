package io.github.professor_forward.teampineapple.walkinclinic.admin;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.databinding.AdminUserListBinding;
import io.github.professor_forward.teampineapple.walkinclinic.repo.User;
import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRepo;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AdminUsers extends Fragment {
    private final int DISPLAY_ALL_USERS_ON_DELETE_THRESHOLD = 5;

    private ActionMode actionMode;
    private AdminUsersViewModel model;
    private AdminUserListBinding binding;
    private final AdminUsersAdapter adapter = new AdminUsersAdapter();
    private RecyclerView list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.admin_user_list, container, false);
        View view = binding.getRoot();
        list = view.findViewById(R.id.adminUserList);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(this).get(AdminUsersViewModel.class);
        binding.setVm(model);
        binding.setLifecycleOwner(this);

        initAdapter();
    }

    @SuppressLint("CheckResult")
    private void initAdapter() {
        list.setAdapter(adapter);

        adapter.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

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
                    deleteUsers(adapter.getSelectedItems())
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
        ReactiveUtil.liveDataToObservable(model.searchText)
                .flatMap(search -> MyApplication.getInstance().getRepo().users().search(search))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> adapter.submitList(list));
    }

    private Single<Boolean> deleteUsers(Iterable<User> users) {
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            if (user.email.equals(UserRepo.ADMIN_EMAIL)) {
                MyApplication.getInstance().toast(R.string.error_cant_delete_admin_user);
                return Single.just(false);
            }

            usernames.add(user.username);
        }

        if (usernames.size() < 1) return Single.just(true);

        MyApplication myApplication = MyApplication.getInstance();
        Collections.sort(usernames);
        String message = usernames.size() <= DISPLAY_ALL_USERS_ON_DELETE_THRESHOLD
                ? myApplication.getString(R.string.prompt_confirm_delete_few_users, TextUtils.join(myApplication.getString(R.string.list_delimiter), usernames))
                : myApplication.getString(R.string.prompt_confirm_delete_many_users, usernames.size());

        return promptToDeleteUsers(message)
                .flatMap(response -> {
                    if (!response) return Single.just(false);

                    return myApplication.getUserRepo().delete(users)
                            .doOnComplete(() -> myApplication.toast(myApplication.getResources().getQuantityString(R.plurals.users_deleted, usernames.size(), usernames.size())))
                            .toSingleDefault(true)
                            ;
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
