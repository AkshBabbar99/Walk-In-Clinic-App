package io.github.professor_forward.teampineapple.walkinclinic.employee;

import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.github.professor_forward.teampineapple.walkinclinic.util.NewListAdapter;

class EmployeeClinicServicesAdapter extends NewListAdapter<ClinicService> {
    EmployeeClinicServicesAdapter() {
        super(
                ClinicService.DIFF_CALLBACK,
                ClinicService.VIEW_HOLDER_FACTORY,
                null
        );
    }
}
