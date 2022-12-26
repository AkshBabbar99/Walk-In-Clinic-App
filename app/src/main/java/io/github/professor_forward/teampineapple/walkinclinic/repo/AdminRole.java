package io.github.professor_forward.teampineapple.walkinclinic.repo;

public class AdminRole extends UserRole {
    public static final String ROLE_KEY = "admin";

    @Override
    public String getRoleId() {
        return ROLE_KEY;
    }
}
