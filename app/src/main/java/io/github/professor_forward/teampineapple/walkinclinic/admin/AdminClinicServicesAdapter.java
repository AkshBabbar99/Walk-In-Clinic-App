package io.github.professor_forward.teampineapple.walkinclinic.admin;

import androidx.navigation.Navigation;

import io.github.professor_forward.teampineapple.walkinclinic.repo.ClinicService;
import io.github.professor_forward.teampineapple.walkinclinic.util.NewListAdapter;

class AdminClinicServicesAdapter extends NewListAdapter<ClinicService> {
    AdminClinicServicesAdapter() {
        super(
                ClinicService.DIFF_CALLBACK,
                ClinicService.VIEW_HOLDER_FACTORY,
                (root, item) ->
                        Navigation.findNavController(root).navigate(
                                AdminClinicServicesDirections.edit(item.id, item.name, item.role)
                        )
        );
    }
}
