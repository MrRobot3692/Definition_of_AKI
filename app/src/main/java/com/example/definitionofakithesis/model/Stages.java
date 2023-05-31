package com.example.definitionofakithesis.model;

public class Stages {
    private final double ratio;
    private final String stage;
    private final double levelIncrease;
    private final String unit;
    private final boolean renalTherapyRequired;

    public Stages(double ratio, String stage, double levelIncrease, String unit, boolean renalTherapyRequired){
        this.ratio = ratio;
        this.stage = stage;
        this.levelIncrease = levelIncrease;

        this.unit = unit;
        this.renalTherapyRequired = renalTherapyRequired;
    }

    public double getRatio() {
        return ratio;
    }

    public String getStage() {
        return stage;
    }

    public double getLevelIncrease() {
        return levelIncrease;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isRenalTherapyRequired() {
        return renalTherapyRequired;
    }


}
