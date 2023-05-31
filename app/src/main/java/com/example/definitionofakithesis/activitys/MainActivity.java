package com.example.definitionofakithesis.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.definitionofakithesis.AKINClassification.NewClassificationStrategy;
import com.example.definitionofakithesis.DataValidation.DefaultValidationStrategy;
import com.example.definitionofakithesis.DataValidation.Validation;
import com.example.definitionofakithesis.FileWork;
import com.example.definitionofakithesis.AKINClassification.Classification;
import com.example.definitionofakithesis.GraphWork;
import com.example.definitionofakithesis.fragments.StageTableFragment;
import com.example.definitionofakithesis.model.Coefficients;
import com.example.definitionofakithesis.DateTimeWork;
import com.example.definitionofakithesis.fragments.BottomMenuFragment;
import com.example.definitionofakithesis.model.Patients;
import com.example.definitionofakithesis.R;
import com.example.definitionofakithesis.db.DbManager;
import com.example.definitionofakithesis.fragments.EnterNameFragment;
import com.example.definitionofakithesis.model.Stages;
import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final Calendar pickedDateCalendar = Calendar.getInstance();
    private static final Calendar todayDateCalendar = Calendar.getInstance();
    private static final long graphLength = 28;
    private static final String TAG = "MyApp";

    private final DbManager myDbManager = new DbManager(this);

    public List<Patients> patientsList = new ArrayList<>();
    public List<Coefficients> coefficientsList = new ArrayList<>();
    public List<Stages> stagesList = new ArrayList<>();
    public int currentTab = 0;
    private GraphWork graphWork;
    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDbManager.openDb();
        //создание панели навигации
        Toolbar mActionBarToolbar = findViewById(R.id.bottom_app_bar);
        mActionBarToolbar.setTitle("");
        setSupportActionBar(mActionBarToolbar);
        //вызов функции для инициализации элементов интерфейся
        Log.i(TAG, "[OnCreate] запуск");
        initialize();
    }

    public void initialize(){
        //заполнение коэффициентоа
        coefficientsList = myDbManager.readCoefficientDatabase(myDbManager.db);
        stagesList = myDbManager.readStagesDatabase(myDbManager.db);

        graphView = findViewById(R.id.graph);
        graphWork = new GraphWork(graphView);

        ((TextView)findViewById(R.id.SCR_conclusion)).setOnClickListener(v -> {
            StageTableFragment stageTableFragment = new StageTableFragment();
            stageTableFragment.show(getSupportFragmentManager(), "custom");
        });

        //добавляем действие при смене вкладки с пациентом
        ((TabLayout)findViewById(R.id.tab_layout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                setScrData(DateTimeWork.calendarToString(pickedDateCalendar));
                determinateAKI();
                graphView.addSeries(graphWork.drawPatientGraph(patientsList.get(currentTab), graphLength));
                Log.i(TAG, "[tabLayout] Выбрана вкладка: Имя " + tab.getText() + ", ID пациента " + patientsList.get(currentTab).getId());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
        //заполнение панели вкладок и установка текущего значения креатинина
        fillTabLayout();
        //setScrData(DateTimeWork.calendarToString(pickedDateCalendar));
        //добавление действия при изменении текста в поле креатинина
        EditText scrEditText = findViewById(R.id.editTextScr);
        scrEditText.addTextChangedListener(new TextWatcher() {
            boolean wasEmpty = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                wasEmpty = scrEditText.getText().toString().equals("");
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(scrEditText.getTag() != null) return;
                if(scrEditText.getText().toString().equals("") && !wasEmpty){
                    deleteResultFromDb();
                    graphView.addSeries(graphWork.drawPatientGraph(patientsList.get(currentTab), graphLength));
                    return;
                }
                if(scrEditText.getText().toString().equals("")) return;
                checkScrValid(scrEditText.getText().toString());
                if(wasEmpty) insertResultInDb(Float.parseFloat(scrEditText.getText().toString()), patientsList.get(currentTab).getId(), DateTimeWork.calendarToString(pickedDateCalendar));
                else updateResultInDb(Float.parseFloat(scrEditText.getText().toString()));
                graphView.addSeries(graphWork.drawPatientGraph(patientsList.get(currentTab), graphLength));
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        //добавляем действия для элементов навигации по дате
        findViewById(R.id.dateTextView).setOnClickListener(view -> { datePick(); });
        findViewById(R.id.datePickButton).setOnClickListener(view -> { datePick(); });
        findViewById(R.id.previousDateButton).setOnClickListener(view -> { previousDate(); });
        findViewById(R.id.nextDateButton).setOnClickListener(view -> { nextDate(); });
        //устанавливаем дату для отобрадения
        setDateText(todayDateCalendar.get(Calendar.DAY_OF_MONTH), todayDateCalendar.get(Calendar.MONTH), todayDateCalendar.get(Calendar.YEAR));
        //принимаем файл открытый извне
        Intent intent = getIntent();
        if(intent!=null){
            String action=intent.getAction();
            String type=intent.getType();
            FileWork hf = new FileWork(this);
            if(!Intent.ACTION_VIEW.equals(action) && type == null) return;
            System.out.println(intent.getData().getPath());

            if(intent.getData().getPath().endsWith(".dat")){
                Patients patients = hf.importPatient(intent);
                if(patients == null) return;
                insertPatientInDb(patients);
                fillTabLayout();
                int id = 0;
                for(int i = 0; i<patientsList.size(); i++)
                    if(patientsList.get(i).getId()>id) id = patientsList.get(i).getId();
                for(int i = 0; i<patients.getResultList().size(); i++)
                    insertResultInDb(patients.getResultList().get(i).getScr(), id, DateTimeWork.dateToString(patients.getResultList().get(i).getDate()));
                graphView.addSeries(graphWork.drawPatientGraph(patientsList.get(currentTab), graphLength));
                Toast.makeText(this, getResources().getString(R.string.success_patient_import), Toast.LENGTH_SHORT).show();
            }
            if(intent.getData().getPath().endsWith(".xlsx") || intent.getData().getPath().endsWith(".xls")){
                int fileType = hf.detectDataType(intent);
                switch (fileType){
                    case 0:
                        break;
                    case 1:
                        List<Coefficients> cl = hf.parseCoefficientsExcel(intent);
                        if(cl == null) return;
                        coefficientsList = cl;
                        myDbManager.rewriteCoefficientDatabase(myDbManager.db, coefficientsList);
                        determinateAKI();
                        graphView.addSeries(graphWork.drawPatientGraph(patientsList.get(currentTab), graphLength));
                        Toast.makeText(this, getResources().getString(R.string.success_ratio_import), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        List<Stages> st = hf.parseStagesExcel(intent);
                        if(st == null) return;
                        stagesList = st;
                        myDbManager.rewriteStagesDatabase(myDbManager.db, stagesList);
                        determinateAKI();
                        graphView.addSeries(graphWork.drawPatientGraph(patientsList.get(currentTab), graphLength));
                        Toast.makeText(this, getResources().getString(R.string.success_stages_import), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myDbManager.closeDb();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final int actionItem = R.id.actionItem;
        final int optionItem = R.id.optionItem;
        switch (item.getItemId()){
            case optionItem:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            case actionItem:
                BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
                bottomMenuFragment.show(getSupportFragmentManager(), bottomMenuFragment.getTag());
                break;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void updateResultsData(float min, float max, String result, String referenceResult, String conclusion){
        //заполнение данных пациента
        ((TextView) findViewById(R.id.gestation_result)).setText(patientsList.get(currentTab).getGestation() + " " + getResources().getString(R.string.week));
        ((TextView) findViewById(R.id.birthday_result)).setText(DateTimeWork.dateToString(patientsList.get(currentTab).getBirthday()));
        //заполнение данных измерений
        ((TextView) findViewById(R.id.SCR_min_result)).setText(min + " " + getResources().getString(R.string.unit_name));
        ((TextView) findViewById(R.id.SCR_max_result)).setText(max + " " + getResources().getString(R.string.unit_name));
        ((TextView) findViewById(R.id.SCR_result)).setText(result);
        ((TextView) findViewById(R.id.SCR_average_difference)).setText(referenceResult);
        ((TextView) findViewById(R.id.SCR_conclusion)).setText(conclusion);
    }

    public void deletePatientFromDb(){
        try{
            Log.i(TAG, "[deletePatientFromDb] Удаление пациента: ID" + patientsList.get(currentTab).getId());
            myDbManager.deleteFromPatientsDb(myDbManager.db, patientsList.get(currentTab).getId());
            fillTabLayout();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void deleteResultFromDb(){
        try{
            Log.i(TAG, "[deleteResultFromDb] Удаление результатов");
            myDbManager.deleteFromResultsDb(myDbManager.db, patientsList.get(currentTab).getId(), DateTimeWork.calendarToString(pickedDateCalendar));
            patientsList = myDbManager.readPatientDatabase(myDbManager.db);
            determinateAKI();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void updateResultInDb(float scr){
        try{
            Log.i(TAG, "[updateResultInDb] Обновление данных таблицы: SCR " + scr + ", дата " + DateTimeWork.calendarToString(pickedDateCalendar) + ", пациент " + patientsList.get(currentTab).getId());
            myDbManager.updateResultsDb(myDbManager.db, scr, DateTimeWork.calendarToString(pickedDateCalendar), patientsList.get(currentTab).getId());
            patientsList = myDbManager.readPatientDatabase(myDbManager.db);
            determinateAKI();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void insertResultInDb(float scr, int patientId, String date){
        try{
            Log.i(TAG, "[insertResultInDb] Добавление данных таблицы: SCR " + scr + ", дата " + DateTimeWork.calendarToString(pickedDateCalendar) + ", пациент " + patientsList.get(currentTab).getId());
            myDbManager.insertToResultsDb(myDbManager.db, scr, date, patientId);
            patientsList = myDbManager.readPatientDatabase(myDbManager.db);
            determinateAKI();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void insertPatientInDb(Patients patients){
        try{
            Log.i(TAG, "[insertPatientInDb] Добавление данных таблицы: Имя " + patients.getName());
            myDbManager.insertToPatientsDb(myDbManager.db, patients.getName(), patients.getGestation(), DateTimeWork.dateToString(patients.getBirthday()));
            patientsList = myDbManager.readPatientDatabase(myDbManager.db);
            determinateAKI();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint({"Range", "SetTextI18n"})
    public void fillTabLayout(){
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        patientsList = myDbManager.readPatientDatabase(myDbManager.db);
        //вызов окна ввода данных пациента есть список пациентов пуст
        if(patientsList.size()<1) {
            EnterNameFragment dialog = new EnterNameFragment();
            dialog.show(getSupportFragmentManager(), "custom");
            return;
        }
        //заполнение tabLayout
        for(int i = 0; i < patientsList.size(); i++)
            tabLayout.addTab(tabLayout.newTab().setText(patientsList.get(i).getName()));
    }

    @SuppressLint("SetTextI18n")
    private void setScrData(String date){
        EditText editText = findViewById(R.id.editTextScr);
        editText.setTag("change it programmatically");
        editText.setText("");
        editText.setTag(null);
        if(patientsList.size()<1 || patientsList.get(currentTab).getResultList().size()<1) return;
        //заполнение результата измерений SCR
        for(int i = 0; i < patientsList.get(currentTab).getResultList().size(); i++){
            if(!Objects.equals(patientsList.get(currentTab).getResultList().get(i).getDate(), DateTimeWork.stringToDate(date))) continue;
            editText.setTag("change it programmatically");
            editText.setText(patientsList.get(currentTab).getResultList().get(i).getScr().toString());
            editText.setTag(null);
            checkScrValid(patientsList.get(currentTab).getResultList().get(i).getScr().toString());
            Log.i(TAG, "[setScrData2] ID пациента: " + patientsList.get(currentTab).getId() + " Значение Scr: " + patientsList.get(currentTab).getResultList().get(i).getScr());
            return;
        }
    }

    private boolean checkScrValid(String scrText){
        TextView error = findViewById(R.id.validationError);
        Validation validation = new Validation(new DefaultValidationStrategy());
        try {
            if(!validation.validate(Float.parseFloat(scrText))) {
                error.setVisibility(View.VISIBLE);
                return false;
            } else {
                error.setVisibility(View.GONE);
                return true;
            }
        } catch (Exception ignored) {}
        return true;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void determinateAKI(){
        float min, max, refMin, refMax;
        //отрисовка пустых данных
        graphView.addSeries(graphWork.drawReferenceGraph(coefficientsList,
                patientsList.get(currentTab),
                graphLength,
                false));
        List<Float> items = graphWork.get7Days(patientsList.get(currentTab).getResultList());
        updateResultsData(0, 0,
                getResources().getString(R.string.insufficient_data),
                getResources().getString(R.string.insufficient_data),
                getResources().getString(R.string.the_patient_is_healthy));
        //если количество результатов равно 0 то выход из функции
        if(items.size()<1) return;
        List<Float> refItems = graphWork.getReference7Days(patientsList.get(currentTab));

        Collections.sort(items);
        Collections.sort(refItems);
        min = items.get(0);
        refMin = refItems.get(0);
        max = items.get(items.size() - 1);
        refMax = refItems.get(refItems.size()-1);

        //если количество результатов меньше трех то рассчеты не проводятся
        if(items.size()<3){
            updateResultsData(min, max,
                    getResources().getString(R.string.insufficient_data),
                    getResources().getString(R.string.insufficient_data),
                    getResources().getString(R.string.the_patient_is_healthy));
            return;
        }

        Classification classification = new Classification(new NewClassificationStrategy(stagesList));
        int stage = classification.classify(min, max);
        String result = String.format("%.2f", max / min);
        String referenceResult = String.format("%.2f", refMax / refMin);
        if(stage > 0){
            graphView.addSeries(graphWork.drawReferenceGraph(coefficientsList, patientsList.get(currentTab), graphLength, true));
            updateResultsData(min, max,
                    result + " " + getResources().getString(R.string.unit_name),
                    referenceResult + " " + getResources().getString(R.string.unit_name),
                    stage + getResources().getString(R.string.stage));
        } else {
            graphView.addSeries(graphWork.drawReferenceGraph(coefficientsList, patientsList.get(currentTab), graphLength, false));
            updateResultsData(min, max,
                    result + " " + getResources().getString(R.string.unit_name),
                    referenceResult + " " + getResources().getString(R.string.unit_name),
                    getResources().getString(R.string.the_patient_is_healthy));
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDateText(int day, int month, int year) {
        ((TextView)findViewById(R.id.dateTextView)).setText(day + "." + (month + 1) + "." + year);
    }

    private void datePick() {
        DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
            pickedDateCalendar.set(Calendar.YEAR, year);
            pickedDateCalendar.set(Calendar.MONTH, monthOfYear);
            pickedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            liquidityDate();
            setDateText(pickedDateCalendar.get(Calendar.DAY_OF_MONTH), pickedDateCalendar.get(Calendar.MONTH), pickedDateCalendar.get(Calendar.YEAR));
            setScrData(DateTimeWork.calendarToString(pickedDateCalendar));
        };

        new DatePickerDialog(MainActivity.this, d,
                pickedDateCalendar.get(Calendar.YEAR),
                pickedDateCalendar.get(Calendar.MONTH),
                pickedDateCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void nextDate(){
        pickedDateCalendar.add(Calendar.DATE, 1);
        if(!liquidityDate()) return;
        setDateText(pickedDateCalendar.get(Calendar.DAY_OF_MONTH), pickedDateCalendar.get(Calendar.MONTH), pickedDateCalendar.get(Calendar.YEAR));
        setScrData(DateTimeWork.calendarToString(pickedDateCalendar));
    }
    private void previousDate(){
        pickedDateCalendar.add(Calendar.DATE, -1);
        setDateText(pickedDateCalendar.get(Calendar.DAY_OF_MONTH), pickedDateCalendar.get(Calendar.MONTH), pickedDateCalendar.get(Calendar.YEAR));
        setScrData(DateTimeWork.calendarToString(pickedDateCalendar));
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
}