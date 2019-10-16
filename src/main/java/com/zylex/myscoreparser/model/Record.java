package com.zylex.myscoreparser.model;

import java.time.LocalDateTime;
import java.util.*;

public class Record {

    private String country;

    private String leagueName;

    private String season;

    private LocalDateTime gameDate;

    private String firstCommand;

    private String secondCommand;

    private int firstBalls;

    private int secondBalls;

    private String coefHref;

    private Map<String, Coefficient> coefficients = new HashMap<>();

    public Record(String country, String leagueName, String season, LocalDateTime gameDate, String firstCommand, String secondCommand, int firstBalls, int secondBalls, String coefHref) {
        this.country = country;
        this.leagueName = leagueName;
        this.season = season;
        this.gameDate = gameDate;
        this.firstCommand = firstCommand;
        this.secondCommand = secondCommand;
        this.firstBalls = firstBalls;
        this.secondBalls = secondBalls;
        this.coefHref = coefHref;
    }

    public Record(String season) {
        this.season = season;
    }

    public String getCountry() {
        return country;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getSeason() {
        return season;
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

    public Map<String, Coefficient> getCoefficients() {
        return coefficients;
    }

    @Override
    public String toString() {
        return "Record{" +
                "country='" + country + '\'' +
                ", leagueName='" + leagueName + '\'' +
                ", season='" + season + '\'' +
                ", gameDate=" + gameDate +
                ", firstCommand='" + firstCommand + '\'' +
                ", secondCommand='" + secondCommand + '\'' +
                ", firstBalls=" + firstBalls +
                ", secondBalls=" + secondBalls +
                ", coefHref='" + coefHref + '\'' +
                ", \tcoeffitients=" + coefficients +
                '}';
    }
}
