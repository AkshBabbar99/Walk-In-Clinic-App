package io.github.professor_forward.teampineapple.walkinclinic.admin;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import io.github.professor_forward.teampineapple.walkinclinic.MyApplication;
import io.github.professor_forward.teampineapple.walkinclinic.repo.User;
import io.github.professor_forward.teampineapple.walkinclinic.util.ReactiveUtil;
import io.reactivex.Observable;

public class AdminUsersViewModel extends ViewModel {
    public final MutableLiveData<String> searchText = new MutableLiveData<>("");
}
