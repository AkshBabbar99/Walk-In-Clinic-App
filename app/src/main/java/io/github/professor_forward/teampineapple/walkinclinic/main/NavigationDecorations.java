package io.github.professor_forward.teampineapple.walkinclinic.main;

import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;

import io.github.professor_forward.teampineapple.walkinclinic.R;

enum NavigationDecorations {
    NONE(0, 0, 0),
    LOGIN_OR_REGISTER(R.menu.drawer_login_or_register, 0, 0),
    ADMIN_DASHBOARD(0, R.menu.options_logout, R.menu.drawer_admin_dashboard),
    EMPLOYEE_DASHBOARD(0, R.menu.options_logout, R.menu.bottomnav_employee_dashboard),
    USER_DASHBOARD(R.menu.drawer_empty, R.menu.options_logout, 0),
    PATIENT_DASHBOARD(0, R.menu.options_logout, R.menu.bottomnav_patient_dashboard)
    ;

    // All of the navigation destinations in this menu must be top level
    @MenuRes
    final int drawerMenu;

    @MenuRes
    final int optionsMenu;

    @MenuRes
    final int bottomMenu;

    NavigationDecorations(@MenuRes int drawerMenu, @MenuRes int optionsMenu,
                          @MenuRes int bottomMenu) {
        this.drawerMenu = drawerMenu;
        this.optionsMenu = optionsMenu;
        this.bottomMenu = bottomMenu;
    }

    static NavigationDecorations forDestination(@IdRes int navId) {
        switch (navId) {
            case R.id.adminDashboard:
            case R.id.adminUsers:
            case R.id.adminServices:
                return ADMIN_DASHBOARD;
            case R.id.employeeDashboard:
            case R.id.employeeClinicEdit:
            case R.id.employeeClinicServices:
            case R.id.employeeSchedule:
                return EMPLOYEE_DASHBOARD;
            case R.id.patientDashboard:
            case R.id.patientClinicSearch:
                return PATIENT_DASHBOARD;
            case R.id.loginScreen:
            case R.id.registerScreen:
                return LOGIN_OR_REGISTER;
            default:
                return NONE;
        }
    }
}
