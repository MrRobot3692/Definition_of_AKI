package com.example.definitionofakithesis.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
import com.example.definitionofakithesis.model.Stages;

import java.text.DecimalFormat;
import java.util.List;

public class StageTableFragment extends DialogFragment {
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stage_table, container, false);

        TableLayout tableLayout = view.findViewById(R.id.stages_table);
        ((ImageView)view.findViewById(R.id.close_button)).setOnClickListener(v -> {
            dismiss();
        });

        TableRow tableRow = new TableRow(requireContext());
        TextView textView = new TextView(requireContext());

        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.relation_to_original_value));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.stage));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.creatinine_level));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.unit));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        textView = new TextView(requireContext());
        textView.setPadding(20,10,20,10);
        textView.setText(getResources().getString(R.string.replacement_therapy));
        textView.setBackgroundResource(R.drawable.cell_shape);
        tableRow.addView(textView);

        //Добавление строки в таблицу
        tableLayout.addView(tableRow);

        DbManager myDbManager = new DbManager(requireContext());
        myDbManager.openDb();
        List<Stages> stagesList = myDbManager.readStagesDatabase(myDbManager.db);
        myDbManager.closeDb();
        for (int i = 0; i < stagesList.size(); i++) {
            //Создание строки
            tableRow = new TableRow(requireContext());

            //Создание ячейки
            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(new DecimalFormat("#.##").format(stagesList.get(i).getRatio()) + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(stagesList.get(i).getStage() + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(new DecimalFormat("#.##").format(stagesList.get(i).getLevelIncrease()) + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(stagesList.get(i).getUnit() + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            textView = new TextView(requireContext());
            textView.setPadding(20,10,20,10);
            textView.setText(stagesList.get(i).isRenalTherapyRequired() + "");
            textView.setBackgroundResource(R.drawable.cell_shape);
            tableRow.addView(textView);

            //Добавление строки в таблицу
            tableLayout.addView(tableRow);
        }
        return view;
    }
}