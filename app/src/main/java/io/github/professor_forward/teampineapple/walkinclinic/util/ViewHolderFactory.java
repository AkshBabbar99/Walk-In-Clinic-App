package io.github.professor_forward.teampineapple.walkinclinic.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

public interface ViewHolderFactory {
    ViewDataBinding create(LayoutInflater inflater, ViewGroup parent);
}
