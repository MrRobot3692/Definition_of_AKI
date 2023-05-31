package com.example.definitionofakithesis.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.definitionofakithesis.activitys.MainActivity;
import com.example.definitionofakithesis.R;
import com.example.definitionofakithesis.db.DbManager;
import com.example.definitionofakithesis.model.Coefficients;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EnterNameFragment extends DialogFragment implements View.OnClickListener {
    View view;
    EditText nameText;
    TextView birthdayText;
    Calendar pickedDateCalendar = Calendar.getInstance();
    Calendar todayDateCalendar = Calendar.getInstance();
    Spinner gestationSpinner;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.enter_name_dialog, container);
        view.findViewById(R.id.yes_button).setOnClickListener(this);
        view.findViewById(R.id.no_button).setOnClickListener(this);
        nameText = view.findViewById(R.id.enter_patient_name_edittext);
        gestationSpinner = view.findViewById(R.id.spinner_patient_gestation);
        birthdayText = view.findViewById(R.id.enter_patient_birthday);
        birthdayText.setText(todayDateCalendar.get(Calendar.DAY_OF_MONTH) + "." + (todayDateCalendar.get(Calendar.MONTH) + 1) + "." + todayDateCalendar.get(Calendar.YEAR));
        Objects.requireNonNull(getDialog()).setTitle("Title!");
        nameText.requestFocus();
        view.findViewById(R.id.date_pick_button).setOnClickListener(this);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fillGestation());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gestationSpinner.setAdapter(adapter);
        return view;
    }

    private ArrayList<Integer> fillGestation(){
        int min = 100, max = 0;
        DbManager myDbManager = new DbManager(requireContext());
        myDbManager.openDb();
        List<Coefficients> coefficientsList = myDbManager.readCoefficientDatabase(myDbManager.db);
        myDbManager.closeDb();
        for(int i = 0; i < coefficientsList.size(); i++){
            if(coefficientsList.get(i).getGestation() > max)
                max = coefficientsList.get(i).getGestation();
            if(coefficientsList.get(i).getGestation() < min)
                min = coefficientsList.get(i).getGestation();
        }

        ArrayList<Integer> gestation = new ArrayList<>();
        for(int i = min; i <= max; i++)
            gestation.add(i);
        return gestation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.no_button:
                dismiss();
                break;
            case R.id.yes_button:
                if(nameText.getText().toString().equals("")) {
                    Toast.makeText(getContext(), getResources().getString(R.string.fill_in_all_data), Toast.LENGTH_SHORT).show();
                    break;
                }
                DbManager myDbManager = new DbManager(getContext());
                myDbManager.openDb();
                myDbManager.insertToPatientsDb(myDbManager.db, nameText.getText().toString(), Integer.valueOf(gestationSpinner.getSelectedItem().toString()), birthdayText.getText().toString());
                myDbManager.closeDb();
                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                activity.fillTabLayout();
                dismiss();
                break;
            case R.id.date_pick_button:
                datePick();
                break;
        }
    }

    private void datePick() {
        DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
            pickedDateCalendar.set(Calendar.YEAR, year);
            pickedDateCalendar.set(Calendar.MONTH, monthOfYear);
            pickedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            liquidityDate();
            setDateText(pickedDateCalendar.get(Calendar.DAY_OF_MONTH), pickedDateCalendar.get(Calendar.MONTH), pickedDateCalendar.get(Calendar.YEAR));
        };

        new DatePickerDialog(getContext(), d,
                pickedDateCalendar.get(Calendar.YEAR),
                pickedDateCalendar.get(Calendar.MONTH),
                pickedDateCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private boolean liquidityDate(){
        Date dateNow = todayDateCalendar.getTime();
        Date dateThen = pickedDateCalendar.getTime();
        if(dateThen.after(dateNow)){
            pickedDateCalendar.setTime(todayDateCalendar.getTime());
            return false;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void setDateText(int day, int month, int year) {
        birthdayText.setText(day + "." + (month + 1) + "." + year);
    }

}
