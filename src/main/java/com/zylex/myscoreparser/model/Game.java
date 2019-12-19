package com.zylex.myscoreparser.model;

import java.time.LocalDateTime;
import java.util.*;

public class Game {

    private String country;

    private String league;

    private String season;

    private LocalDateTime dateTime;

    private String firstTeam;

    private String secondTeam;

    private int firstBalls;

    private int secondBalls;

    private String link;

    private Map<String, Coefficient> coefficients = new HashMap<>();

    private Map<String, StatisticsValue> statisticsItems = new HashMap<>();

    public Game(String country, String league, String season, LocalDateTime dateTime, String firstTeam, String secondTeam, int firstBalls, int secondBalls, String link) {
        this.country = country;
        this.league = league;
        this.season = season;
        this.dateTime = dateTime;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        this.firstBalls = firstBalls;
        this.secondBalls = secondBalls;
        this.link = link;
    }

    public String getCountry() {
        return country;
    }

    public String getLeague() {
        return league;
    }

    public String getSeason() {
        return season;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getFirstTeam() {
        return firstTeam;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public int getFirstBalls() {
        return firstBalls;
    }

    public int getSecondBalls() {
        return secondBalls;
    }

    public String getLink() {
        return link;
    }

    public Map<String, StatisticsValue> getStatisticsItems() {
        return statisticsItems;
    }

    public Map<String, Coefficient> getCoefficients() {
        return coefficients;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return country.equals(game.country) &&
                league.equals(game.league) &&
                season.equals(game.season) &&
                dateTime.equals(game.dateTime) &&
                firstTeam.equals(game.firstTeam) &&
                secondTeam.equals(game.secondTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, league, season, dateTime, firstTeam, secondTeam);
    }

    @Override
    public String toString() {
        return "Game{" +
                "country='" + country + '\'' +
                ", league='" + league + '\'' +
                ", season='" + season + '\'' +
                ", dateTime=" + dateTime +
                ", firstTeam='" + firstTeam + '\'' +
                ", secondTeam='" + secondTeam + '\'' +
                ", firstBalls=" + firstBalls +
                ", secondBalls=" + secondBalls +
                ", link='" + link + '\'' +
                ", coefficients=" + coefficients +
                ", statisticsItems=" + statisticsItems +
                '}';
    }
}
