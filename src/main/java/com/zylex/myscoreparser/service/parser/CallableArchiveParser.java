package com.zylex.myscoreparser.service.parser;

import com.zylex.myscoreparser.controller.logger.BlockLogger;
import com.zylex.myscoreparser.exceptions.ArchiveParserException;
import com.zylex.myscoreparser.service.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableArchiveParser implements Callable<List<String>> {

    private WebDriver driver;

    private String countryLeague;

    private DriverManager driverManager;

    private BlockLogger logger;

    CallableArchiveParser(BlockLogger logger, DriverManager driverManager, String countryLeagues) {
        this.logger = logger;
        this.driverManager = driverManager;
        this.countryLeague = countryLeagues;
    }

    public List<String> call() {
        try {
            driver = driverManager.getDriver();
            driver.navigate().to(String.format("https://www.myscore.ru/football/%s/archive/", countryLeague));
            return parseArchive();
        } catch (InterruptedException e) {
            throw new ArchiveParserException(e.getMessage(), e);
        } finally {
            driverManager.addDriverToQueue(driver);
        }
    }

    private List<String> parseArchive() {
        new WebDriverWait(driver, 180).ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.elementToBeClickable(By.className("leagueTable__seasonName")));
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        Elements archiveElements = document.select("div.leagueTable__season > div.leagueTable__seasonName > a");
        List<String> archiveSeasons = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String seasonLink = archiveElements.get(i).attr("href").replace("/football/", "");
            archiveSeasons.add(seasonLink);
        }
        logger.logArchive();
        return archiveSeasons;
    }
}
