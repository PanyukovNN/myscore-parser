package com.zylex.myscoreparser.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Record {

    private String country;

    private String leagueName;

    private LocalDateTime gameDate;

    private String firstCommand;

    private String secondCommand;

    private int firstBalls;

    private int secondBalls;

    private String coefHref;

    private List<Coeffitient> coeffitients = new ArrayList<>();

    public Record(String country, String leagueName, LocalDateTime gameDate, String firstCommand, String secondCommand, int firstBalls, int secondBalls, String coefHref) {
        this.country = country;
        this.leagueName = leagueName;
        this.gameDate = gameDate;
        this.firstCommand = firstCommand;
        this.secondCommand = secondCommand;
        this.firstBalls = firstBalls;
        this.secondBalls = secondBalls;
        this.coefHref = coefHref;
    }

    public String getCountry() {
        return country;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public LocalDateTime getGameDate() {
        return gameDate;
    }

    public String getFirstCommand() {
        return firstCommand;
    }

    public String getSecondCommand() {
        return secondCommand;
    }

    public int getFirstBalls() {
        return firstBalls;
    }

    public int getSecondBalls() {
        return secondBalls;
    }

    public String getCoefHref() {
        return coefHref;
    }

    public List<Coeffitient> getCoeffitients() {
        return coeffitients;
    }

    @Override
    public String toString() {
        return "Record{" +
                "country='" + country + '\'' +
                ", leagueName='" + leagueName + '\'' +
                ", gameDate=" + gameDate +
                ", firstCommand='" + firstCommand + '\'' +
                ", secondCommand='" + secondCommand + '\'' +
                ", firstBalls=" + firstBalls +
                ", secondBalls=" + secondBalls +
                ", coefHref='" + coefHref + '\'' +
                ", \tcoeffitients=" + coeffitients +
                '}';
    }
}
