package com.zylex.myscoreparser.repository;

public class Repository {

    private static Repository instance;

    private Repository() {
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    private String[] countryLeagues = {
//            "bulgaria/parva-liga",
//            "bolivia/division-profesional",
//            "bosnia-and-herzegovina/premier-league",
//            "brazil/serie-a",
//            "brazil/serie-b",
//            "hungary/otp-bank-liga",
//            "hungary/merkantil-bank-liga",
//            "venezuela/primera-division",
//            "vietnam/v-league",
//            "ghana/premier-league",
//            "guatemala/liga-nacional",
//            "germany/bundesliga",
//            "germany/2-bundesliga"
            // TEST DATA
            "australia/a-league",
            "austria/tipico-bundesliga",
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
            "belgium/proximus-league"
    };


    public String[] getCountryLeagues() {
        return countryLeagues;
    }
}
