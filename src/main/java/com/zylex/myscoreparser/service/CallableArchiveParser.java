package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.exceptions.ArchiveParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableArchiveParser implements Callable<List<String>> {

    private WebDriver driver = null;

    private String countryLeague;

    private DriverManager driverManager;

    CallableArchiveParser(DriverManager driverManager, String countryLeagues) {
        this.driverManager = driverManager;
        this.countryLeague = countryLeagues;
    }

    public List<String> call() {
        try {
            driver = driverManager.getDriver();
            WebDriverWait wait = new WebDriverWait(driver, 180);
            driver.navigate().to(String.format("https://www.myscore.ru/football/%s/archive/", countryLeague));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            List<String> archiveLinks = parseArchive();
            ConsoleLogger.logArchive();
            return archiveLinks;
        } catch (InterruptedException e) {
            throw new ArchiveParserException(e.getMessage(), e);
        } finally {
            driverManager.addDriverToQueue(driver);
        }
    }

    private List<String> parseArchive() {
        String pageSource = driver.getPageSource();
        Document doc = Jsoup.parse(pageSource);
        Elements archiveElements = doc.select("div.leagueTable__season > div.leagueTable__seasonName > a");
        List<String> archiveSeasons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String seasonLink = archiveElements.get(i).attr("href").replace("/football/", "");
            archiveSeasons.add(seasonLink);
        }
        return archiveSeasons;
    }
}
