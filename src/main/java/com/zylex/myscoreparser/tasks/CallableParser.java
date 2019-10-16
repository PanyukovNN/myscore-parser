package com.zylex.myscoreparser.tasks;

import com.zylex.myscoreparser.DriverFactory;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.service.CoefficientParser;
import com.zylex.myscoreparser.Saver;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CallableParser implements Callable<Void> {

    private List<Record> records;

    private String leagueLink;

    public CallableParser(String leagueLink, List<Record> records) {
        this.leagueLink = leagueLink;
        this.records = records;
    }

    public Void call() throws InterruptedException {
        WebDriver driver = null;
        while (driver == null) {
            driver = DriverFactory.drivers.poll();
            Thread.sleep(10);
        }
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            CoefficientParser coefficientParser = new CoefficientParser(driver);
            coefficientParser.processCoefficientParsing(records);
            Saver saver = new Saver();
            saver.processSaving(records, leagueLink);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DriverFactory.drivers.add(driver);
        }
        return null;
    }
}
