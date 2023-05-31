package com.example.definitionofakithesis.DataValidation;

public class Validation {
    private final DefaultValidationStrategy strategy;

    public Validation(DefaultValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(Float data){ return strategy.validate(data);}
}
