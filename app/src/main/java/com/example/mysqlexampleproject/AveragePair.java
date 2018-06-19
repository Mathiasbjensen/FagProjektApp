package com.example.mysqlexampleproject;

import java.io.Serializable;

public class AveragePair implements Serializable{

    private final double avg;
    private double current;

    public AveragePair(double avg, double current) {
        this.avg = avg;
        this.current = current;

    }
    public double getCurrent() {
        return current;
    }


    public double getAvg() {
        return avg;
    }
}
