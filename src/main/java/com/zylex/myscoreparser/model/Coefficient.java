package com.zylex.myscoreparser.model;

public class Coefficient {

    private String bookmaker;

    private String firstWin;

    private String tie;

    private String secondWin;

    public Coefficient(String bookmaker, String firstWin, String tie, String secondWin) {
        this.bookmaker = bookmaker;
        this.firstWin = firstWin;
        this.tie = tie;
        this.secondWin = secondWin;
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

    @Override
    public String toString() {
        return "Coeffitient{" +
                "bookmaker='" + bookmaker + '\'' +
                ", firstWin='" + firstWin + '\'' +
                ", tie='" + tie + '\'' +
                ", secondWin='" + secondWin + '\'' +
                '}';
    }
}
