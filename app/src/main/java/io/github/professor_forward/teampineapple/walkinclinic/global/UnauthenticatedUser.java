package io.github.professor_forward.teampineapple.walkinclinic.global;

import io.github.professor_forward.teampineapple.walkinclinic.repo.UserRole;

class UnauthenticatedUser extends UserRole {
    @Override
    public String getRoleId() {
        return "unauthenticated";
    }
}
