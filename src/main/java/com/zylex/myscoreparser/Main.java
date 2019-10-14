package com.zylex.myscoreparser;

import com.zylex.myscoreparser.model.Record;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\java\\projects\\myscoreparser\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        String[] leagueYears = {"-2016-2017", "-2017-2018", "-2018-2019", ""};
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            Parser parser = new Parser(driver, "2019");
            for (String leagueYear: leagueYears) {
                String leagueHref = "premier-league" + leagueYear;
                String year = leagueYear.isEmpty()
                        ? "2020"
                        : leagueYear.substring(6);
                List<Record> records = parser.leagueParser(leagueHref);
                Saver saver = new Saver();
                saver.processSaving(records, leagueHref, leagueYear);
            }
        } finally {
            driver.close();
        }
    }
}