package com.example.definitionofakithesis.model;

public class Coefficients {
    private final Integer gestation;
    private final double G, Td, C0, Gk;
    private final boolean AKI;

    public Coefficients(Integer gestation, double G, double Td, double C0, double Gk, boolean AKI){
        this.gestation = gestation;
        this.G = G;
        this.Td = Td;
        this.C0 = C0;
        this.Gk = Gk;
        this.AKI = AKI;
    }

    public Integer getGestation() {
        return gestation;
    }

    public double getG() {
        return G;
    }

    public double getTd() {
        return Td;
    }

    public double getC0() {
        return C0;
    }

    public double getGk() {
        return Gk;
    }

    public boolean getAKI() {
        return AKI;
    }
}
