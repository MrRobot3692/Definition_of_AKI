package com.example.definitionofakithesis.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.definitionofakithesis.FileWork;
import com.example.definitionofakithesis.activitys.MainActivity;
import com.example.definitionofakithesis.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class BottomMenuFragment extends BottomSheetDialogFragment {
    private View view;
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.bottom_menu_fragment_layout, container, false);
        activity = (MainActivity) getActivity();
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onStart() {
        super.onStart();
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.add_new_patient:
                    EnterNameFragment dialog = new EnterNameFragment();
                    dialog.show(getParentFragmentManager(), "custom");
                    dismiss();
                    break;
                case R.id.delete_patient:
                    ConfirmFragment confirm = new ConfirmFragment();
                    confirm.show(getParentFragmentManager(), "custom");
                    dismiss();
                    break;
                case R.id.export_patient:
                    handleDat();
                    dismiss();
                    break;
            }
            return true;
        });
    }

    private void handleDat(){
        FileWork hf = new FileWork(requireContext());
        hf.exportPatient(activity.patientsList.get(activity.currentTab));

        String fileName = activity.patientsList.get(activity.currentTab).getName() + ".dat";

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(requireContext(),
                "com.example.definitionofakithesis.contentProvider",
                requireContext().getFileStreamPath(fileName));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("application/dat");
        PackageManager pm = getActivity().getPackageManager();
        if(shareIntent.resolveActivity(pm) != null)
            startActivity(Intent.createChooser(shareIntent, null));
    }
}
