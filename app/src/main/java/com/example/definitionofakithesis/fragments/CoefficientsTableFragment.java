package com.example.definitionofakithesis.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.definitionofakithesis.R;
import com.example.definitionofakithesis.activitys.MainActivity;
import com.example.definitionofakithesis.db.DbManager;
import com.example.definitionofakithesis.model.Coefficients;

import java.text.DecimalFormat;
import java.util.List;

public class CoefficientsTableFragment extends DialogFragment {
    View view;
    MainActivity activity;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_coefficients_table, container, false);

        TableLayout tableLayout = view.findViewById(R.id.coefficients_table);
        ((ImageView)view.findViewById(R.id.close_button)).setOnClickListener(v -> {
            dismiss();
        });

        TableRow tableRow = new TableRow(requireContext());
        TextView textView = new TextView(requireContext());

        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.gestational_age));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText("G");
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText("Td");
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText("C0");
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText("Gk");
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.having_aki));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        //Добавление строки в таблицу
        tableLayout.addView(tableRow);

        DbManager myDbManager = new DbManager(requireContext());
        myDbManager.openDb();
        List<Coefficients> coefficientsList = myDbManager.readCoefficientDatabase(myDbManager.db);
        myDbManager.closeDb();
        for (int i = 0; i < coefficientsList.size(); i++) {
            //Создание строки
            tableRow = new TableRow(requireContext());

            //Создание ячейки
            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(coefficientsList.get(i).getGestation() + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(new DecimalFormat("#.##").format(coefficientsList.get(i).getG()) + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(new DecimalFormat("#.##").format(coefficientsList.get(i).getTd()) + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(new DecimalFormat("#.##").format(coefficientsList.get(i).getC0()) + "" + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(new DecimalFormat("#.##").format(coefficientsList.get(i).getGk()) + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(coefficientsList.get(i).getAKI() + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            //Добавление строки в таблицу
            tableLayout.addView(tableRow);
        }
        return view;
    }
}