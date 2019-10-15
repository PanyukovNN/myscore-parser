package com.zylex.myscoreparser.processor;

import com.zylex.myscoreparser.Main;
import com.zylex.myscoreparser.model.Record;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ConnectionProcessor implements Callable<String> {

    private String leagueHref;

    public ConnectionProcessor(String leagueHref) {
        this.leagueHref = leagueHref;
    }

    public String call() {
        WebDriver driver = new ChromeDriver();
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            Parser parser = new Parser(driver);
            List<Record> records = parser.leagueParser(leagueHref);
            Main.totalRecords.addAndGet(records.size());
            String year = records.get(records.size() - 1).getGameDate().getYear() + "";
            Saver saver = new Saver();
            saver.processSaving(records, leagueHref);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
        return leagueHref.replace("/", "_") + " is finished at " + new Date();
    }
}
