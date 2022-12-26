package io.github.professor_forward.teampineapple.walkinclinic.patient;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.professor_forward.teampineapple.walkinclinic.repo.Clinic;

class PatientClinicSearchAdapter extends PagedListAdapter<Clinic, RecyclerView.ViewHolder> {
    public PatientClinicSearchAdapter() {
        super(Clinic.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return PatientClinicSearchViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        }
        else {
            throw new UnsupportedOperationException();
            //((EmployeeUserViewHolder) holder).update(getItem(position));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Clinic clinic = getItem(position);
        if (clinic != null) {
            ((PatientClinicSearchViewHolder) holder).bind(holder, clinic);
        }
        else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            ((PatientClinicSearchViewHolder) holder).clear();
        }
    }
}
