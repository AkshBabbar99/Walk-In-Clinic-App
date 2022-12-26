package io.github.professor_forward.teampineapple.walkinclinic.admin;

import io.github.professor_forward.teampineapple.walkinclinic.repo.User;
import io.github.professor_forward.teampineapple.walkinclinic.util.NewListAdapter;

class AdminUsersAdapter extends NewListAdapter<User> {
    AdminUsersAdapter() {
        super(
                User.DIFF_CALLBACK,
                User.VIEW_HOLDER_FACTORY,
                null
        );
    }
}
