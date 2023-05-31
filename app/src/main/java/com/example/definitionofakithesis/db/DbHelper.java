package com.example.definitionofakithesis.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbNameClass.patientDbNames.CREATE_TABLE);
        db.execSQL(DbNameClass.resultDbNames.CREATE_TABLE);
        db.execSQL(DbNameClass.coefficientDbNames.CREATE_TABLE);
        db.execSQL(DbNameClass.stagesDbNames.CREATE_TABLE);
        fillDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbNameClass.patientDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.resultDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.coefficientDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.stagesDbNames.DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbNameClass.patientDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.resultDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.coefficientDbNames.DELETE_TABLE);
        db.execSQL(DbNameClass.stagesDbNames.DELETE_TABLE);
        onCreate(db);
    }

    private void insertToCoefficient(SQLiteDatabase db, Integer gestation, double G, double Td, double C0, double Gk, Integer AKI){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_GESTATION, gestation);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_G, G);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_Td, Td);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_C0, C0);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_Gk, Gk);
        contentValues.put(DbNameClass.coefficientDbNames.COLUMN_AKI, AKI);
        db.insert(DbNameClass.coefficientDbNames.TABLE_NAME, null, contentValues);
    }

    private void insertToStages(SQLiteDatabase db, double ratio, String stage, double increase, String unit, Integer renalTherapy){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbNameClass.stagesDbNames.RATIO, ratio);
        contentValues.put(DbNameClass.stagesDbNames.STAGE, stage);
        contentValues.put(DbNameClass.stagesDbNames.INCREASE, increase);
        contentValues.put(DbNameClass.stagesDbNames.UNIT, unit);
        contentValues.put(DbNameClass.stagesDbNames.RENAL_THERAPY, renalTherapy);
        db.insert(DbNameClass.stagesDbNames.TABLE_NAME, null, contentValues);
    }

    private void fillDb(SQLiteDatabase db){
        insertToCoefficient(db, 25, 4.4, 4.3, 67.5, 36.7, 0);
        insertToCoefficient(db, 26, 5.0, 3.9, 63.3, 36.9, 0);
        insertToCoefficient(db, 27, 5.0, 3.9, 63.3, 36.9, 0);
        insertToCoefficient(db, 28, 5.1, 3.6, 63.9, 49.7, 0);
        insertToCoefficient(db, 29, 5.1, 3.6, 63.9, 49.7, 0);
        insertToCoefficient(db, 30, 4.4, 3.4, 72.9, 31.6, 0);
        insertToCoefficient(db, 31, 4.4, 3.4, 72.9, 31.6, 0);
        insertToCoefficient(db, 32, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 33, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 34, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 35, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 36, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 37, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 38, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 39, 5.2, 3.8, 73.1, 25.4, 0);
        insertToCoefficient(db, 40, 5.2, 3.8, 73.1, 25.4, 0);

        insertToCoefficient(db, 25, 7.3, 3.7, 60.9, 96.0, 1);
        insertToCoefficient(db, 26, 6.1, 4.3, 54.9, 79.4, 1);
        insertToCoefficient(db, 27, 6.1, 4.3, 54.9, 79.4, 1);
        insertToCoefficient(db, 28, 6.7, 4.1, 62.3, 57.9, 1);
        insertToCoefficient(db, 29, 6.7, 4.1, 62.3, 57.9, 1);
        insertToCoefficient(db, 30, 6.1, 3.6, 67.1, 53.6, 1);
        insertToCoefficient(db, 31, 6.1, 3.6, 67.1, 53.6, 1);
        insertToCoefficient(db, 32, 6.7, 4.3, 67.4, 28.9, 1);
        insertToCoefficient(db, 33, 6.7, 4.3, 67.4, 28.9, 1);
        insertToCoefficient(db, 34, 6.7, 4.3, 67.4, 28.9, 1);
        insertToCoefficient(db, 35, 6.7, 4.3, 67.4, 28.9, 1);
        insertToCoefficient(db, 36, 6.7, 4.3, 67.4, 28.9, 1);
        insertToCoefficient(db, 37, 6.7, 4.3, 67.4, 28.9, 1);
        insertToCoefficient(db, 38, 6.7, 4.3, 67.4, 28.9, 1);

        insertToStages(db, 1.5, "1 Стадия", 26.52, "мкмоль/л", 0);
        insertToStages(db, 2, "2 Стадия", 176.8, "мкмоль/л", 0);
        insertToStages(db, 2.9, "3 Стадия", 221, "мкмоль/л", 0);
    }
}
