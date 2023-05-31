package com.example.definitionofakithesis.AKINClassification;

public class Classification {
    private final ClassificationStrategy strategy;

    public Classification(ClassificationStrategy strategy) {
        this.strategy = strategy;
    }

    public int classify(double SC0, double SC1) {
        return strategy.classify(SC0, SC1);
    }
}
