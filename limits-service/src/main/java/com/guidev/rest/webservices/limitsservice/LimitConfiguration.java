package com.guidev.rest.webservices.limitsservice;

public class LimitConfiguration {

    private int minimum;
    private int maximum;

    protected LimitConfiguration(){}

    public LimitConfiguration(int maximum, int minimum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }
}
