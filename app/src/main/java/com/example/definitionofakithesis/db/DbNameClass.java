package com.example.definitionofakithesis.db;

public interface DbNameClass {
    int DATABASE_VERSION = 32;
    String DATABASE_NAME = "Aki.db";

    interface patientDbNames{
        String TABLE_NAME = "patients";
        String ID = "id_patient";
        String COLUMN_NAME = "name";
        String COLUMN_GESTATION = "gestation";
        String COLUMN_BIRTHDAY = "birthday";

        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                ID + " INTEGER NOT NULL, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_GESTATION + " INTEGER, " +
                COLUMN_BIRTHDAY + " TEXT, " +
                "PRIMARY KEY(" + ID + " AUTOINCREMENT)" +
                ");";
        String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    interface resultDbNames{
        String TABLE_NAME = "aki_results";
        String ID = "id_result";
        String COLUMN_SCR = "scr";
        String COLUMN_DATE = "date";
        String FOREIGN_KEY = "patient";

        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                ID + " INTEGER NOT NULL, " +
                COLUMN_SCR + " NUMERIC, " +
                COLUMN_DATE + " TEXT, " +
                FOREIGN_KEY + " INTEGER NOT NULL, " +
                "PRIMARY KEY("+ID+" AUTOINCREMENT), " +
                "FOREIGN KEY(" + FOREIGN_KEY + ") REFERENCES " + patientDbNames.TABLE_NAME + "(" + patientDbNames.ID + ")" +
                ");";
        String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    interface coefficientDbNames{
        String TABLE_NAME = "coefficients";
        String ID = "id_coefficient";
        String COLUMN_GESTATION = "gestational_age";
        String COLUMN_G = "G";
        String COLUMN_Td = "Td";
        String COLUMN_C0 = "C0";
        String COLUMN_Gk = "Gk";
        String COLUMN_AKI = "AKI";

        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                ID + " INTEGER NOT NULL, " +
                COLUMN_GESTATION + " INTEGER, " +
                COLUMN_G + " NUMERIC, " +
                COLUMN_Td + " NUMERIC, " +
                COLUMN_C0 + " NUMERIC, " +
                COLUMN_Gk + " NUMERIC, " +
                COLUMN_AKI + " INTEGER, " +
                "PRIMARY KEY(" + ID + " AUTOINCREMENT)" +
                ");";
        String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    interface stagesDbNames{
        String TABLE_NAME = "stages";
        String ID = "id_stages";
        String RATIO = "ratio";
        String STAGE = "stage_name";
        String INCREASE = "level_increase";
        String UNIT = "unit";
        String RENAL_THERAPY = "renal_therapy";

        String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" +
                ID + " INTEGER NOT NULL, " +
                RATIO + " NUMERIC, " +
                STAGE + " TEXT, " +
                INCREASE + " NUMERIC, " +
                UNIT + " TEXT, " +
                RENAL_THERAPY + " INTEGER, " +
                "PRIMARY KEY(" + ID + " AUTOINCREMENT)" +
                ");";
        String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
