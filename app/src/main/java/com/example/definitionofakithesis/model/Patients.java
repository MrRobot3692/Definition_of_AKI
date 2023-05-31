package com.example.definitionofakithesis.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Patients implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Integer id, gestation;
    private final String name;
    private final List<Results> resultList;
    private final Date birthday;

    public Patients(Integer id, String name, List<Results> resultList, Integer gestation, Date birthday){
        this.id = id;
        this.name = name;
        this.resultList = resultList;
        this.gestation = gestation;
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Results> getResultList() {
        return resultList;
    }

    public void addResultToLust(Results results){
        resultList.add(results);
    }

    public Integer getGestation() {
        return gestation;
    }

    public Date getBirthday() {
        return birthday;
    }
}
