package com.example.mysqlexampleproject;

public class AveragePair {

    private final double avg;
    private final double current;

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
