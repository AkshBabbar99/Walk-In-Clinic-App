package io.github.professor_forward.teampineapple.walkinclinic.repo;

public enum ClinicEmployeeRole {
    STAFF(0),
    NURSE(1),
    DOCTOR(2);

    public final int code;

    ClinicEmployeeRole(int code) {
        this.code = code;
    }

    public static ClinicEmployeeRole fromCode(int code) {
        for (ClinicEmployeeRole role : values()) {
            if (role.code == code) return role;
        }
        throw new IllegalArgumentException("Unknown clinic employee role id: " + code);
    }
}
