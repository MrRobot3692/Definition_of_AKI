package com.example.definitionofakithesis;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.definitionofakithesis.model.Coefficients;
import com.example.definitionofakithesis.model.Patients;
import com.example.definitionofakithesis.model.Stages;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileWork {
    private final Context context;

    public FileWork(Context context) {
        this.context = context;
    }

    public Patients importPatient(Intent intent){
        Uri uriFile = intent.getData();
        try {
            FileInputStream fis = new FileInputStream(context.getContentResolver().openFileDescriptor(uriFile, "r").getFileDescriptor());
            ObjectInputStream ois = new ObjectInputStream(fis);
            Patients patient = (Patients) ois.readObject();
            fis.close();
            ois.close();
            return patient;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void exportPatient(Patients patient){
        String fileName = patient.getName() + ".dat";
        //Log.i("MyApp", patient.getResultList().size() + "");
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(patient);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, context.getFileStreamPath(fileName) + "", Toast.LENGTH_SHORT).show();
        Log.i("MyApp", "[HandleFiles.exportFile] " + context.getFileStreamPath(fileName));
    }

    public int detectDataType(Intent intent){
        Uri uriFile = intent.getData();
        try {
            FileInputStream fis = new FileInputStream(context.getContentResolver().openFileDescriptor(uriFile, "r").getFileDescriptor());
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            String cellName;
            Row row = sheet.getRow(0);
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            cellName = getStringCell(row, 0, formulaEvaluator);
            if(Objects.equals(cellName, "Срок гестации") || Objects.equals(cellName, "Gestational age"))
                return 1;
            if(Objects.equals(cellName, "Отношение к исходному значению") || Objects.equals(cellName, "Relation to original value"))
                return 2;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public ArrayList<Stages> parseStagesExcel(Intent intent){
        Uri uriFile = intent.getData();
        ArrayList<Stages> stagesList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(context.getContentResolver().openFileDescriptor(uriFile, "r").getFileDescriptor());
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for(int r = 1; r<rowsCount; r++){
                Row row = sheet.getRow(r);
                double ratio = 0, increase = 0;
                String stage = "", unit = "", renalTherapy = "";
                int cellsCount = 5;
                for (int c = 0; c<cellsCount; c++) {
                    switch (c) {
                        case 0: ratio = getDoubleCell(row, c, formulaEvaluator); break;
                        case 1: stage = getStringCell(row, c, formulaEvaluator); break;
                        case 2: increase = getDoubleCell(row, c, formulaEvaluator); break;
                        case 3: unit = getStringCell(row, c, formulaEvaluator); break;
                        case 4: renalTherapy = getStringCell(row, c, formulaEvaluator); break;
                    }

                }
                Stages stages;
                if(Objects.equals(renalTherapy, "false"))
                    stages = new Stages(ratio, stage, increase, unit, false);
                else
                    stages = new Stages(ratio, stage, increase, unit, true);
                stagesList.add(stages);
            }
            return stagesList;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<Coefficients> parseCoefficientsExcel(Intent intent){
        Uri uriFile = intent.getData();
        ArrayList<Coefficients> coefficientList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(context.getContentResolver().openFileDescriptor(uriFile, "r").getFileDescriptor());
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for(int r = 1; r<rowsCount; r++){
                Row row = sheet.getRow(r);
                double gestation = 0, G = 0, Td = 0, C0 = 0, Gk = 0;
                String AKI = "";
                int cellsCount = 6;
                for (int c = 0; c<cellsCount; c++) {
                    double value = getDoubleCell(row, c, formulaEvaluator);
                    switch (c) {
                        case 0: gestation = value; break;
                        case 1: G = value; break;
                        case 2: Td = value; break;
                        case 3: C0 = value; break;
                        case 4: Gk = value; break;
                        case 5: AKI = getStringCell(row, c, formulaEvaluator); break;
                    }
                }
                Coefficients coefficients;
                if(Objects.equals(AKI, "false"))
                    coefficients = new Coefficients((int) gestation, G, Td, C0, Gk, false);
                else
                    coefficients = new Coefficients((int) gestation, G, Td, C0, Gk, true);
                coefficientList.add(coefficients);
            }
            return coefficientList;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    protected double getDoubleCell(Row row, int c, FormulaEvaluator formulaEvaluator) {
        double numericValue = 0;
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);

            numericValue = cellValue.getNumberValue();
        } catch (NullPointerException ignored) {
        }
        return numericValue;
    }

    protected String getStringCell(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String stringValue = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);

            stringValue = cellValue.getStringValue();
        } catch (NullPointerException ignored) {
        }
        return stringValue;
    }
}
