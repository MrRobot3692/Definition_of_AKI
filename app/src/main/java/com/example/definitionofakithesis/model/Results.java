package com.example.definitionofakithesis.model;

import java.io.Serializable;
import java.util.Date;

public class Results implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Float scr;
    private final Date date;

    public Results(Float scr, Date date){
        this.scr = scr;
        this.date = date;
    }

    public Float getScr() {
        return scr;
    }

    public Date getDate() {
        return date;
    }
}
