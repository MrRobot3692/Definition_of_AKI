package com.example.definitionofakithesis.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.definitionofakithesis.R;
import com.example.definitionofakithesis.activitys.MainActivity;

public class ConfirmFragment extends DialogFragment {
    View view;
    MainActivity activity;
    Button confirmButton;
    Button cancelButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirm, container, false);
        activity = (MainActivity) getActivity();
        confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> {
            assert activity != null;
            activity.deletePatientFromDb();
            dismiss();
        });
        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
        return view;
    }
}