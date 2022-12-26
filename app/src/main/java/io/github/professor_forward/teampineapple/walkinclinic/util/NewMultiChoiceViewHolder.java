package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.view.View;
import android.widget.CheckBox;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import io.github.professor_forward.teampineapple.walkinclinic.BR;
import io.github.professor_forward.teampineapple.walkinclinic.R;
import io.reactivex.disposables.Disposable;

public final class NewMultiChoiceViewHolder<T> extends RecyclerView.ViewHolder {
    private final MutableLiveData<Boolean> sel = new MutableLiveData<>(false);
    private final ViewDataBinding binding;
    private final View root;
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListener;
    private T item;
    private Disposable sub;
    private final MultiChoicePagedListAdapter<T, NewMultiChoiceViewHolder<T>> adapter;

    public NewMultiChoiceViewHolder(
            ViewDataBinding binding,
            MultiChoicePagedListAdapter<T, NewMultiChoiceViewHolder<T>> adapter,
            OpenListener<T> openListener
    ) {
        super(binding.getRoot());
        this.adapter = adapter;
        this.binding = binding;
        this.root = binding.getRoot();
        CheckBox checkbox = binding.getRoot().findViewById(R.id.multiSelector);

        LiveData<Boolean> selectable = ReactiveUtil.observableToLiveData(adapter.isSelectable());
        selectable.observeForever(x -> checkbox.setVisibility(x ? View.VISIBLE : View.GONE));

        sel.observeForever(checkbox::setChecked);
        checkbox.setOnCheckedChangeListener((view, checked) -> {
            if (checked != (sel.getValue() == Boolean.TRUE)) {
                setSelectedIfSelectable(checked);
            }
        });

        onClickListener = x -> {
            if (selectable.getValue() == Boolean.TRUE) {
                setSelectedIfSelectable(sel.getValue() != Boolean.TRUE);
            } else if (openListener != null) {
                openListener.open(root, item);
            }
        };

        onLongClickListener = x -> {
            adapter.setSelectableOn(root);
            setSelectedIfSelectable(true);
            return true;
        };
    }

    @OverridingMethodsMustInvokeSuper
    public void bind(T item) {
        clear();
        this.item = item;

        sub = adapter.isSelected(getAdapterPosition())
                .distinctUntilChanged()
                .subscribe(sel::setValue);
        binding.setVariable(BR.item, item);
        binding.executePendingBindings();

        root.setOnClickListener(onClickListener);
        root.setOnLongClickListener(onLongClickListener);
    }

    @OverridingMethodsMustInvokeSuper
    public void clear() {
        if (sub != null) {
            sub.dispose();
            sub = null;
        }
        item = null;

        root.setOnClickListener(null);
        root.setOnLongClickListener(null);
    }

    private void setSelectedIfSelectable(boolean isSelected) {
        adapter.setSelectedIfSelectable(getAdapterPosition(), isSelected);
    }
}
