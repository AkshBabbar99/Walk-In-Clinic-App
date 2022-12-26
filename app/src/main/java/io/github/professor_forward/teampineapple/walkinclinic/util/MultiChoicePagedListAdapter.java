package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;

import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class MultiChoicePagedListAdapter<T, VH extends RecyclerView.ViewHolder> extends PagedListAdapter<T, VH> {
    private final Map<T, BehaviorSubject<Boolean>> selected = new HashMap<>();
    private final BehaviorSubject<Boolean> isSelectable = BehaviorSubject.createDefault(false);
    private AbsListView.MultiChoiceModeListener multiChoiceModeListener;
    private ActionMode actionMode;

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (multiChoiceModeListener != null) {
                multiChoiceModeListener.onCreateActionMode(mode, menu);
            }
            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (multiChoiceModeListener != null) {
                return multiChoiceModeListener.onPrepareActionMode(mode, menu);
            }
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (multiChoiceModeListener != null) {
                return multiChoiceModeListener.onActionItemClicked(mode, item);
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            setSelectableOff();

            if (multiChoiceModeListener != null) {
                multiChoiceModeListener.onDestroyActionMode(mode);
            }
        }
    };


    protected MultiChoicePagedListAdapter(DiffUtil.ItemCallback<T> differ) {
        super(differ);
    }

    private void setSelected(int i, boolean newValue) {
        if (!isSelectable.getValue())
            throw new IllegalStateException("Can't set selections when not selectable");

        isSelected(i, newValue).onNext(newValue);
        notifyItemChanged(i);

        if (multiChoiceModeListener != null) {
            multiChoiceModeListener.onItemCheckedStateChanged(actionMode, i, getItemId(i), newValue);
        }
    }

    public void setSelectedIfSelectable(int i, boolean isSelected) {
        if (!isSelectable.getValue()) return;
        setSelected(i, isSelected);
    }

    public Observable<Boolean> isSelected(int i) {
        return isSelected(i, false);
    }

    private BehaviorSubject<Boolean> isSelected(int i, boolean defaultValue) {
        T item = getItem(i);

        if (!selected.containsKey(getItem(i))) {
            BehaviorSubject<Boolean> subject = BehaviorSubject.createDefault(defaultValue);
            subject.doOnDispose(() -> {
                if (!subject.hasObservers()) {
                    selected.remove(item);
                }
            });
            selected.put(item, subject);
            return subject;
        }
        return selected.get(item);
    }

    public Observable<Boolean> isSelectable() {
        return isSelectable;
    }

    public void setSelectableOn(View view) {
        if (isSelectable.getValue()) return;

        isSelectable.onNext(true);
        view.startActionMode(actionModeCallback);

    }

    public void setSelectableOff() {
        if (!isSelectable.getValue()) return;
        isSelectable.onNext(false);
        clearSelections();
    }

    public AbsListView.MultiChoiceModeListener getMultiChoiceModeListener() {
        return multiChoiceModeListener;
    }

    public void setMultiChoiceModeListener(AbsListView.MultiChoiceModeListener multiChoiceModeListener) {
        this.multiChoiceModeListener = multiChoiceModeListener;
    }

    private void clearSelections() {
        for (BehaviorSubject<Boolean> isSelected : selected.values()) {
            if (isSelected.getValue()) {
                isSelected.onNext(false);
            }
        }
    }

    public List<T> getSelectedItems() {
        List<T> items = new ArrayList<>();
        for (Map.Entry<T, BehaviorSubject<Boolean>> entry : selected.entrySet()) {
            if (entry.getValue().getValue()) {
                items.add(entry.getKey());
            }
        }
        return items;
    }
}
