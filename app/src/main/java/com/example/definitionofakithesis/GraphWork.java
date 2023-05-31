package com.example.definitionofakithesis;

import android.graphics.Color;
import android.util.Log;

import com.example.definitionofakithesis.model.Coefficients;
import com.example.definitionofakithesis.model.Patients;
import com.example.definitionofakithesis.model.Results;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GraphWork {
    private final GraphView graphView;
    private double G, Td, C0, Gk;

    public GraphWork(GraphView graphView) {
        this.graphView = graphView;
    }

    public LineGraphSeries<DataPoint> drawReferenceGraph(List<Coefficients> coefficientsList, Patients patients, long graphLength, boolean AKI){
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for(int i = 0; i<coefficientsList.size(); i++){
            if(Objects.equals(coefficientsList.get(i).getGestation(), patients.getGestation()) && coefficientsList.get(i).getAKI() == AKI){
                G = coefficientsList.get(i).getG();
                Td = coefficientsList.get(i).getTd();
                C0 = coefficientsList.get(i).getC0();
                Gk = coefficientsList.get(i).getGk();
            }
        }
        series.setTitle("Усредненный график");
        double index = 0;
        while (index< graphLength){
            series.appendData(new DataPoint(index, f(index, Td, G, C0, Gk)), false, (int) (graphLength * 10 + 10));
            index += 0.1;
        }
        graphView.removeAllSeries();
        graphView.setCursorMode(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(graphLength);
        return series;
    }

    public LineGraphSeries<DataPoint> drawPatientGraph(Patients patients, long graphLength){
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        Calendar patientBirthdayCalendar = Calendar.getInstance();
        patientBirthdayCalendar.setTime(patients.getBirthday());
        for(int i = 0; i <= graphLength; i++){
            Date date = new Date(DateTimeWork.stringToDate(
                      patientBirthdayCalendar.get(Calendar.DAY_OF_MONTH) + "." +
                            (patientBirthdayCalendar.get(Calendar.MONTH) + 1) + "." +
                            patientBirthdayCalendar.get(Calendar.YEAR)).getTime() +
                            ((long) i *24*60*60*1000));
            for(int j = 0; j < patients.getResultList().size(); j++){
                if(!Objects.equals(date, patients.getResultList().get(j).getDate())) continue;
                series.appendData(new DataPoint(i, patients.getResultList().get(j).getScr()), false, (int) graphLength + 1);
            }
        }
        series.setColor(Color.RED);
        series.setTitle("График пациента");
        return series;
    }

    public List<Float> get7Days(List<Results> resultsList){
        List<Float> items = new ArrayList<>();
        //заполнение дат
        Date currentDate = Calendar.getInstance().getTime();
        Date sevenDayBeforeDate = new Date(currentDate.getTime() - 604800000L);
        //сортировка результатов по дате
        Collections.sort(resultsList, (o1, o2) -> {
            if (o1.getDate().after(o2.getDate())) return 1;
            else if (o1.getDate().before(o2.getDate())) return -1;
            else return 0;
        });
        //добавление результатов в список
        for(int i = 0; i < resultsList.size(); i++){
            Date patientDate = resultsList.get(i).getDate();
            if (!(patientDate.before(currentDate) && patientDate.after(sevenDayBeforeDate)))
                continue;
            items.add(resultsList.get(i).getScr());
        }
        return items;
    }

    public List<Float> getReference7Days(Patients patients){
        List<Float> items = new ArrayList<>();
        //сортировка результатов по дате
        Collections.sort(patients.getResultList(), (o1, o2) -> {
            if (o1.getDate().after(o2.getDate())) return 1;
            else if (o1.getDate().before(o2.getDate())) return -1;
            else return 0;
        });
        //заполнение дат
        Date currentDate = patients.getResultList().get(patients.getResultList().size()-1).getDate();
        Date sevenDayBeforeDate = new Date(currentDate.getTime() - 604800000L);
        Date startDate = patients.getBirthday();
        //добавление результатов в список
        if(startDate.after(sevenDayBeforeDate))
            sevenDayBeforeDate = startDate;
        long diffSevenDays = TimeUnit.DAYS.convert(Math.abs(currentDate.getTime() - sevenDayBeforeDate.getTime()), TimeUnit.MILLISECONDS);
        long diffStartGraph = TimeUnit.DAYS.convert(Math.abs(currentDate.getTime() - startDate.getTime()), TimeUnit.MILLISECONDS);
        //Log.i("MyApp", diffSevenDays + " " + diffStartGraph + " " + TimeUnit.DAYS.convert(startDate.getTime(), TimeUnit.MILLISECONDS));
        for(int i = 0; i < diffSevenDays; i++){
            items.add((float) f(diffStartGraph-i, Td, G, C0, Gk));
        }
        return items;
    }

    private double f(double t, double Td, double G, double C0, double Gk){
        double e = 2.71828;
        double k = G/Gk;
        double k2 = Math.log(2)/k;
        if(t<Td) return G*t+C0;
        if(t>=Td) return Gk + Math.pow(e,-k * (t-Td)) * (C0 + Gk * Td * Math.log(2)/k2 - Gk);
        return G*t+C0;
    }
}
