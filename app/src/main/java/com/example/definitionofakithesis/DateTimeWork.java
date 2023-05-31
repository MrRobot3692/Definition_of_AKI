package com.example.definitionofakithesis;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeWork {
    public static Date stringToDate(String aDate) {
        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = simpledateformat.parse(aDate, pos);
        return date;
    }

    public static String dateToString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String str = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
        return str;
    }

    public static String calendarToString(Calendar calendar){
        return calendar.get(Calendar.DAY_OF_MONTH) +
                "." + (calendar.get(Calendar.MONTH) + 1) +
                "." + calendar.get(Calendar.YEAR);
    }
}
