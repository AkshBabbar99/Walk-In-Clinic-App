package io.github.professor_forward.teampineapple.walkinclinic.patient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;

public class PatientClinicSearchViewHolder extends RecyclerView.ViewHolder {
    private final View view;
    private final TextView nameView;

    private Clinic clinic;

    public static PatientClinicSearchViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_clinic_list_item, parent, false);
        return new PatientClinicSearchViewHolder(view);
    }

    private PatientClinicSearchViewHolder(View view) {
        super(view);
        this.view = view;
        nameView = view.findViewById(R.id.name);
    }

    public void bind(RecyclerView.ViewHolder holder, Clinic clinic) {
        this.clinic = clinic;
        nameView.setText(clinic.name);
        view.setOnClickListener(view -> {
            Navigation.findNavController(holder.itemView).navigate(PatientClinicSearchDirections.view(clinic.id));
            // TODO safe nav
        });
    }

    public void clear() {
        clinic = null;
        nameView.setText("");
        view.setOnClickListener(view -> {});
    }

    public void update(Clinic clinic) {
        this.clinic = clinic;
    }
}
