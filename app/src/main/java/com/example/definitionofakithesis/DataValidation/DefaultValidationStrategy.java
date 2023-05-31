package com.example.definitionofakithesis.DataValidation;

public class DefaultValidationStrategy implements ValidationStrategy{
    @Override
    public boolean validate(Float data) {
        if(data > 300 || data < 30) return false;
        return true;
    }
}
