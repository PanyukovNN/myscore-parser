package com.zylex.myscoreparser.model;

import java.time.LocalDateTime;
import java.util.*;

public class Game {

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

    public Game(String country, String leagueName, String season, LocalDateTime gameDate, String firstCommand, String secondCommand, int firstBalls, int secondBalls, String coefHref) {
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

    public void setCoefHref(String coefHref) {
        this.coefHref = coefHref;
    }

    public Map<String, Coefficient> getCoefficients() {
        return coefficients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return country.equals(game.country) &&
                leagueName.equals(game.leagueName) &&
                season.equals(game.season) &&
                gameDate.equals(game.gameDate) &&
                firstCommand.equals(game.firstCommand) &&
                secondCommand.equals(game.secondCommand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, leagueName, season, gameDate, firstCommand, secondCommand);
    }

    @Override
    public String toString() {
        return "Game{" +
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
