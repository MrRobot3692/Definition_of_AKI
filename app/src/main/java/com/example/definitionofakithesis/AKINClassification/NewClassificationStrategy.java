package com.example.definitionofakithesis.AKINClassification;

import com.example.definitionofakithesis.model.Stages;

import java.util.List;

public class NewClassificationStrategy implements ClassificationStrategy {
    private final List<Stages> stagesArrayList;

    public NewClassificationStrategy(List<Stages> stagesArrayList){
        this.stagesArrayList = stagesArrayList;
    }

    @Override
    public int classify(double SC0, double SC1) {
        double SCIncrease = SC1 - SC0;
        double SCRatio = SC1 / SC0;
        int stage = 0;
        for(int i = 0; i < stagesArrayList.size(); i++){
            if (SCRatio >= stagesArrayList.get(i).getRatio())
                stage = i + 1;
            if (SCIncrease >= stagesArrayList.get(i).getLevelIncrease())
                stage = i + 1;
            if (stagesArrayList.get(i).isRenalTherapyRequired())
                stage = i + 1;
        }
        return stage;
    }
}
