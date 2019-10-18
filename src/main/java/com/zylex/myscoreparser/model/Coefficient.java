package com.zylex.myscoreparser.model;

public class Coefficient {

    private String bookmaker;

    private String firstWin = "-";

    private String tie = "-";

    private String secondWin = "-";

    private String max1x2 = "-";

    private String min1x2 = "-";

    private String maxDch = "-";

    private String minDch = "-";

    public Coefficient(String bookmaker, String firstWin, String tie, String secondWin, String max1x2, String min1x2) {
        this.bookmaker = bookmaker;
        this.firstWin = firstWin;
        this.tie = tie;
        this.secondWin = secondWin;
        this.max1x2 = max1x2;
        this.min1x2 = min1x2;
    }

    public String getBookmaker() {
        return bookmaker;
    }

    public String getFirstWin() {
        return firstWin;
    }

    public String getTie() {
        return tie;
    }

    public String getSecondWin() {
        return secondWin;
    }

    public String getMax1x2() {
        return max1x2;
    }

    public String getMin1x2() {
        return min1x2;
    }

    public void setMaxDch(String maxDch) {
        this.maxDch = maxDch;
    }

    public void setMinDch(String minDch) {
        this.minDch = minDch;
    }

    public String getMaxDch() {
        return maxDch;
    }

    public String getMinDch() {
        return minDch;
    }

    @Override
    public String toString() {
        return "Coefficient{" +
                "bookmaker='" + bookmaker + '\'' +
                ", firstWin='" + firstWin + '\'' +
                ", tie='" + tie + '\'' +
                ", secondWin='" + secondWin + '\'' +
                ", max1x2='" + max1x2 + '\'' +
                ", min1x2='" + min1x2 + '\'' +
                ", maxDch='" + maxDch + '\'' +
                ", minDch='" + minDch + '\'' +
                '}';
    }
}
