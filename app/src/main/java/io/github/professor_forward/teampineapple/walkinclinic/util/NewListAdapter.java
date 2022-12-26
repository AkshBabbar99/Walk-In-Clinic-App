package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class NewListAdapter<T> extends MultiChoicePagedListAdapter<T, NewMultiChoiceViewHolder<T>> {
    private final ViewHolderFactory factory;
    private final OpenListener<T> openListener;

    public NewListAdapter(
            DiffUtil.ItemCallback<T> callback,
            ViewHolderFactory factory,
            OpenListener<T> openListener
    ) {
        super(callback);
        this.factory = factory;
        this.openListener = openListener;
    }

    @NonNull
    @Override
    public final NewMultiChoiceViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = factory.create(LayoutInflater.from(parent.getContext()), parent);
        return new NewMultiChoiceViewHolder<T>(binding, this, openListener);
    }

    @Override
    public final void onBindViewHolder(@NonNull NewMultiChoiceViewHolder<T> holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull NewMultiChoiceViewHolder<T> holder, int position) {
        T item = getItem(position);
        if (item != null) {
            holder.bind(item);
        } else {
            holder.clear();
        }
    }
}
