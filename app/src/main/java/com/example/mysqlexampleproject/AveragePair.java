package com.example.mysqlexampleproject;

import java.io.Serializable;
// An object that is used when the average data in lists.
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
