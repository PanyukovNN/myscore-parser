package com.zylex.myscoreparser;

import com.zylex.myscoreparser.model.Record;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConnectionProcessor extends Thread {

    private String country;

    private String league;

    private String leagueYear;

    public ConnectionProcessor(String country, String league, String leagueYear) {
        this.country = country;
        this.league = league;
        this.leagueYear = leagueYear;
    }

    public void run() {
        WebDriver driver = new ChromeDriver();
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            Parser parser = new Parser(driver);
            String leagueHref = country + league + leagueYear;
            String year = leagueYear.isEmpty()
                    ? "2020"
                    : leagueYear.substring(6);
            List<Record> records = parser.leagueParser(leagueHref, year);
            Saver saver = new Saver();
            String leagueFullHref = year.equals("2020")
                    ? leagueHref + "-2019-2020"
                    : leagueHref;
            saver.processSaving(records, leagueFullHref);
        } catch (InterruptedException | ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }
}
