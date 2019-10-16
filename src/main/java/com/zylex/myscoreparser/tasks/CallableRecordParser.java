package com.zylex.myscoreparser.tasks;

import com.zylex.myscoreparser.DriverFactory;
import com.zylex.myscoreparser.Main;
import com.zylex.myscoreparser.model.LeagueResult;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.service.LeagueParser;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CallableRecordParser implements Callable<LeagueResult> {

    private String leagueLink;

    public CallableRecordParser(String leagueLink) {
        this.leagueLink = leagueLink;
    }

    public LeagueResult call() throws InterruptedException {
        WebDriver driver = null;
        while (driver == null) {
            driver = DriverFactory.drivers.poll();
            Thread.sleep(20);
        }
        try {
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            LeagueParser leagueParser = new LeagueParser(driver);
            List<Record> records = leagueParser.processLeagueParsing(leagueLink);
            Main.totalRecords.addAndGet(records.size());
            return new LeagueResult(leagueLink, records);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            DriverFactory.drivers.add(driver);
        }
        return new LeagueResult("", new ArrayList<>());
    }
}
