package com.zylex.myscoreparser;

import com.zylex.myscoreparser.model.Record;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {
        System.setProperty("webdriver.chrome.driver", "C:\\java\\projects\\myscoreparser\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        String country = "england";
        String[] leagueYears = {"-2016-2017", "-2017-2018", "-2018-2019", ""};
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            Parser parser = new Parser(driver);
            for (String leagueYear: leagueYears) {
                String leagueHref = country + "/premier-league" + leagueYear;
                String year = leagueYear.isEmpty()
                        ? "2020"
                        : leagueYear.substring(6);
                List<Record> records = parser.leagueParser(leagueHref, year);
                Saver saver = new Saver();
                String leagueName = year.equals("2020")
                        ? leagueHref + "-2019-2020"
                        : leagueHref;
                saver.processSaving(records, leagueName);
            }
        } finally {
            driver.close();
        }
    }
}