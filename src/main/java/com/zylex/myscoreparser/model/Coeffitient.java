package com.zylex.myscoreparser.model;

public class Coeffitient {

    private String bookmaker;

    private String firstWin;

    private String Tie;

    private String secondWin;

    public Coeffitient(String bookmaker, String firstWin, String tie, String secondWin) {
        this.bookmaker = bookmaker;
        this.firstWin = firstWin;
        Tie = tie;
        this.secondWin = secondWin;
    }

    @Override
    public String toString() {
        return "Coeffitient{" +
                "bookmaker='" + bookmaker + '\'' +
                ", firstWin='" + firstWin + '\'' +
                ", Tie='" + Tie + '\'' +
                ", secondWin='" + secondWin + '\'' +
                '}';
    }
}
