package com.zylex.myscoreparser.model;

public class StatisticsValue {

    private String homeValue;

    private String awayValue;

    public StatisticsValue(String homeValue, String awayValue) {
        this.homeValue = homeValue;
        this.awayValue = awayValue;
    }

    public String getHomeValue() {
        return homeValue;
    }

    public String getAwayValue() {
        return awayValue;
    }

    @Override
    public String toString() {
        return "StatisticsValue{" +
                "homeValue='" + homeValue + '\'' +
                ", awayValue='" + awayValue + '\'' +
                '}';
    }
}
