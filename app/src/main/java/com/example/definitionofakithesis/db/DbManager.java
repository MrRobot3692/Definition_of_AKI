package com.example.definitionofakithesis.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.definitionofakithesis.DateTimeWork;
import com.example.definitionofakithesis.model.Coefficients;
import com.example.definitionofakithesis.model.Patients;
import com.example.definitionofakithesis.model.Results;
import com.example.definitionofakithesis.model.Stages;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    public SQLiteDatabase db = null;
    Context context;
    DbHelper myDbHelper = new DbHelper(context, DbNameClass.DATABASE_NAME, null, DbNameClass.DATABASE_VERSION);

    public DbManager(Context context){
        this.context = context;
    }

    public void openDb(){
        myDbHelper = new DbHelper(context, DbNameClass.DATABASE_NAME, null, DbNameClass.DATABASE_VERSION);
        db = myDbHelper.getWritableDatabase();
    }

    public void closeDb(){
        myDbHelper = new DbHelper(context, DbNameClass.DATABASE_NAME, null, DbNameClass.DATABASE_VERSION);
        myDbHelper.close();
    }

    public void updateResultsDb(SQLiteDatabase db, Float scr, String date, Integer patient){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.resultDbNames.COLUMN_SCR, scr);
        String[] whereArg = new String[]{date, patient.toString()};
        db.update(DbNameClass.resultDbNames.TABLE_NAME, contentValues, "date=? AND patient=?", whereArg);
    }

    @SuppressLint("Recycle")
    public void insertToResultsDb(SQLiteDatabase db, Float aki, String date, Integer patient){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.resultDbNames.COLUMN_SCR, aki);
        contentValues.put(DbNameClass.resultDbNames.COLUMN_DATE, date);
        contentValues.put(DbNameClass.resultDbNames.FOREIGN_KEY, patient);
        db.insert(DbNameClass.resultDbNames.TABLE_NAME, null, contentValues);
    }

    public void insertToPatientsDb(SQLiteDatabase db, String name, Integer gestation, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.patientDbNames.COLUMN_NAME, name);
        contentValues.put(DbNameClass.patientDbNames.COLUMN_GESTATION, gestation);
        contentValues.put(DbNameClass.patientDbNames.COLUMN_BIRTHDAY, date);
        db.insert(DbNameClass.patientDbNames.TABLE_NAME, null, contentValues);
    }

    public void deleteFromPatientsDb(SQLiteDatabase db, Integer patient){
        String[] whereArg = new String[]{patient.toString()};
        db.delete(DbNameClass.patientDbNames.TABLE_NAME, "id_patient=?", whereArg);
        db.delete(DbNameClass.resultDbNames.TABLE_NAME, "patient=?", whereArg);
    }

    public void deleteFromResultsDb(SQLiteDatabase db, Integer patient, String date){
        String[] whereArg = new String[]{patient.toString(), date};
        db.delete(DbNameClass.resultDbNames.TABLE_NAME, "patient=? and date=?", whereArg);
    }

    public void insertToCoefficient(SQLiteDatabase db, Integer gestation, double G, double Td, double C0, double Gk, Integer AKI){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_GESTATION, gestation);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_G, G);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_Td, Td);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_C0, C0);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_Gk, Gk);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_AKI, AKI);
        db.insert(DbNameClass.coefficientDbNames.TABLE_NAME, null, contentValues);
    }

    public void insertToStages(SQLiteDatabase db, double ratio, String stage, double increase, String unit, Integer renalTherapy){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.stagesDbNames.RATIO, ratio);
        contentValues.put(DbNameClass.stagesDbNames.STAGE, stage);
        contentValues.put(DbNameClass.stagesDbNames.INCREASE, increase);
        contentValues.put(DbNameClass.stagesDbNames.UNIT, unit);
        contentValues.put(DbNameClass.stagesDbNames.RENAL_THERAPY, renalTherapy);
        db.insert(DbNameClass.stagesDbNames.TABLE_NAME, null, contentValues);
    }

    public void rewriteStagesDatabase(SQLiteDatabase db, List<Stages> stagesList){
        db.execSQL(DbNameClass.stagesDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.stagesDbNames.CREATE_TABLE);
        for(int i = 0; i<stagesList.size(); i++){
            if(!stagesList.get(i).isRenalTherapyRequired()){
                insertToStages(db, stagesList.get(i).getRatio(),
                        stagesList.get(i).getStage(),
                        stagesList.get(i).getLevelIncrease(),
                        stagesList.get(i).getUnit(),
                        0);
            } else {
                insertToStages(db, stagesList.get(i).getRatio(),
                        stagesList.get(i).getStage(),
                        stagesList.get(i).getLevelIncrease(),
                        stagesList.get(i).getUnit(),
                        1);
            }
        }
    }

    public void rewriteCoefficientDatabase(SQLiteDatabase db, List<Coefficients> coefficientsList){
        db.execSQL(DbNameClass.coefficientDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.coefficientDbNames.CREATE_TABLE);
        for(int i = 0; i<coefficientsList.size(); i++){
            if(!coefficientsList.get(i).getAKI()){
                insertToCoefficient(db, coefficientsList.get(i).getGestation(),
                        coefficientsList.get(i).getG(),
                        coefficientsList.get(i).getTd(),
                        coefficientsList.get(i).getC0(),
                        coefficientsList.get(i).getGk(),
                        0);
            } else {
                insertToCoefficient(db, coefficientsList.get(i).getGestation(),
                        coefficientsList.get(i).getG(),
                        coefficientsList.get(i).getTd(),
                        coefficientsList.get(i).getC0(),
                        coefficientsList.get(i).getGk(),
                        1);
            }
        }
    }

    @SuppressLint("Range")
    public List<Patients> readPatientDatabase(SQLiteDatabase db){
        List<Patients> patientsList = new ArrayList<>();
        Patients patient;
        Results result;
        Cursor cursor;

        Log.i("MyApp", "[readPatientDatabase] Чтение базы данных");

        //заполнение пациентов
        cursor = db.rawQuery("SELECT * FROM " + DbNameClass.patientDbNames.TABLE_NAME, null);
        while(cursor.moveToNext()){
            try{
                patient = new Patients(cursor.getInt(cursor.getColumnIndex(DbNameClass.patientDbNames.ID)),
                        cursor.getString(cursor.getColumnIndex(DbNameClass.patientDbNames.COLUMN_NAME)),
                        new ArrayList<Results>(), cursor.getInt(cursor.getColumnIndex(DbNameClass.patientDbNames.COLUMN_GESTATION)),
                        DateTimeWork.stringToDate(cursor.getString(cursor.getColumnIndex(DbNameClass.patientDbNames.COLUMN_BIRTHDAY))));
                patientsList.add(patient);
            } catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
            }
        }

        //заполнение результатов для пациентов
        cursor = db.rawQuery("SELECT * FROM " + DbNameClass.resultDbNames.TABLE_NAME, null);
        while(cursor.moveToNext()){
            try{
                //заполняем результаты
                result = new Results(cursor.getFloat(cursor.getColumnIndex(DbNameClass.resultDbNames.COLUMN_SCR)),
                        DateTimeWork.stringToDate(cursor.getString(cursor.getColumnIndex(DbNameClass.resultDbNames.COLUMN_DATE))));
                for(int i = 0; i<patientsList.size(); i++){
                    //если для результата по вторичному ключу найден пациент то добавляем результат к нему
                    if(cursor.getInt(cursor.getColumnIndex(DbNameClass.resultDbNames.FOREIGN_KEY)) != patientsList.get(i).getId()) continue;
                    patientsList.get(i).addResultToLust(result);
                }
            } catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
            }
        }
        return patientsList;
    }

    @SuppressLint("Range")
    public List<Stages> readStagesDatabase(SQLiteDatabase db){
        List<Stages> stagesList = new ArrayList<>();
        Stages stages;
        Cursor cursor;
        boolean renalTherapy;

        cursor = db.rawQuery("SELECT * FROM " + DbNameClass.stagesDbNames.TABLE_NAME, null);
        while (cursor.moveToNext()){
            try {
                renalTherapy = cursor.getInt(cursor.getColumnIndex(DbNameClass.stagesDbNames.RENAL_THERAPY)) == 1;
                stages = new Stages(cursor.getDouble(cursor.getColumnIndex(DbNameClass.stagesDbNames.RATIO)),
                        cursor.getString(cursor.getColumnIndex(DbNameClass.stagesDbNames.STAGE)),
                        cursor.getDouble(cursor.getColumnIndex(DbNameClass.stagesDbNames.INCREASE)),
                        cursor.getString(cursor.getColumnIndex(DbNameClass.stagesDbNames.UNIT)),
                        renalTherapy);

                stagesList.add(stages);
            } catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
            }
        }

        cursor.close();

        return stagesList;
    }

    @SuppressLint("Range")
    public List<Coefficients> readCoefficientDatabase(SQLiteDatabase db){
        List<Coefficients> coefficientsList = new ArrayList<>();
        Coefficients coefficients;
        Cursor cursor;
        boolean haveAKI;

        Log.i("MyApp", "[readCoefficientDatabase] Чтение базы данных");

        //заполнение коэффициентов
        cursor = db.rawQuery("SELECT * FROM " + DbNameClass.coefficientDbNames.TABLE_NAME, null);
        while (cursor.moveToNext()){
            try {
                haveAKI = cursor.getInt(cursor.getColumnIndex(DbNameClass.coefficientDbNames.COLUMN_AKI)) == 1;

                coefficients = new Coefficients(cursor.getInt(cursor.getColumnIndex(DbNameClass.coefficientDbNames.COLUMN_GESTATION)),
                        cursor.getFloat(cursor.getColumnIndex(DbNameClass.coefficientDbNames.COLUMN_G)),
                        cursor.getFloat(cursor.getColumnIndex(DbNameClass.coefficientDbNames.COLUMN_Td)),
                        cursor.getFloat(cursor.getColumnIndex(DbNameClass.coefficientDbNames.COLUMN_C0)),
                        cursor.getFloat(cursor.getColumnIndex(DbNameClass.coefficientDbNames.COLUMN_Gk)),
                        haveAKI);

                coefficientsList.add(coefficients);
            } catch (IndexOutOfBoundsException ex){
                ex.printStackTrace();
            }
        }
        cursor.close();

        return coefficientsList;
    }
}
