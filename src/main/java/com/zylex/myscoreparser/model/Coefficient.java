package com.zylex.myscoreparser.model;

public class Coefficient {

    private String bookmaker;

    private String firstWin = "-";

    private String tie = "-";

    private String secondWin = "-";

    private String max1x2 = "-";

    private String min1x2 = "-";

    private String dch1X = "-";

    private String dchX2 = "-";

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

    public void setDch1X(String dch1X) {
        this.dch1X = dch1X;
    }

    public void setDchX2(String dchX2) {
        this.dchX2 = dchX2;
    }

    public String getDch1X() {
        return dch1X;
    }

    public String getDchX2() {
        return dchX2;
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
                ", dch1X='" + dch1X + '\'' +
                ", dchX2='" + dchX2 + '\'' +
                '}';
    }
}
