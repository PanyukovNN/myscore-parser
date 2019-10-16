package com.zylex.myscoreparser.repository;

public class Repository {

    private static Repository instanse;

    private Repository() {
    }

    public static Repository getInstanse() {
        if (instanse == null) {
            instanse = new Repository();
        }
        return instanse;
    }

    private String[] countryLeagues = {
            "australia/a-league"};
            /*"austria/tipico-bundesliga",
            "austria/2-liga",
            "azerbaijan/premier-league",
            "algeria/division-1",
            "england/championship",
            "argentina/superliga",
            "argentina/primera-nacional",
            "armenia/premier-league",
            "bahrain/premier-league",
            "belarus/vysshaya-liga",
            "belgium/jupiler-league",
            "belgium/proximus-league"};*/

    public String[] getCountryLeagues() {
        return countryLeagues;
    }
}
