package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.exceptions.LeagueParserException;
import com.zylex.myscoreparser.model.Record;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableLeagueParser implements Callable<List<Record>> {

    private String leagueLink;

    private WebDriver driver = null;

    private WebDriverWait wait;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    CallableLeagueParser(String leagueLink) {
        this.leagueLink = leagueLink;
    }

    public List<Record> call() {
        try {
            getDriver();
            return processLeagueParsing(leagueLink);
        } catch (InterruptedException e) {
            throw new LeagueParserException(e.getMessage(), e);
        } finally {
            DriverFactory.drivers.add(driver);
        }
    }

    private void getDriver() throws InterruptedException {
        while (driver == null) {
            driver = DriverFactory.drivers.poll();
            Thread.sleep(10);
        }
        wait = new WebDriverWait(driver, 60);
    }

    private List<Record> processLeagueParsing(String leagueLink) throws InterruptedException {
        driver.navigate().to(String.format("https://www.myscore.ru/football/%sresults/", leagueLink));
        showMore(driver);
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        return parseGameRecords(document);
    }

    private List<Record> parseGameRecords(Document document) {
        List<Record> records = new ArrayList<>();
        Elements gameRecords = document.select("div.event__match");
        String country = document.select("span.event__title--type").first().text();
        String league = document.select("div.teamHeader__name").first().text();
        String season = document.select("div.teamHeader__text").first().text().replace("/", "");
        int seasonStartMonth = findSeasonStartMonth(gameRecords.last());
        for (Element gameRecord : gameRecords) {
            String seasonStartYear = season.substring(0, 4);
            LocalDateTime gameDateTime = processDate(seasonStartYear, seasonStartMonth, gameRecord);
            String firstCommand = gameRecord.select("div.event__participant--home").text();
            String secondCommand = gameRecord.select("div.event__participant--away").text();
            String[] scores = gameRecord.select("div.event__scores > span").text().split(" ");
            int firstBalls = Integer.parseInt(scores[0]);
            int secondBalls = Integer.parseInt(scores[1]);
            String coefHref = gameRecord.id().replace("g_1_", "");
            Record record = new Record(country, league, season, gameDateTime, firstCommand, secondCommand, firstBalls, secondBalls, coefHref);
            records.add(record);
        }
        ConsoleLogger.totalRecords.addAndGet(records.size());
        return records;
    }

    private void showMore(WebDriver driver) throws InterruptedException {
        while (true) {
            try {
                wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                Thread.sleep(2000);
                if (driver.findElements(By.className("event__more")).size() > 0) {
                    driver.findElement(By.className("event__more")).click();
                } else {
                    break;
                }
            } catch (NoSuchElementException | StaleElementReferenceException | ElementClickInterceptedException e) {
                System.out.println("Can't click \"show more\" button, trying again...");
            }
        }
    }

    private int findSeasonStartMonth(Element lastGameRecord) {
        String time = lastGameRecord.select("div.event__time").text();
        return Integer.parseInt(time.substring(3, 5));
    }

    private LocalDateTime processDate(String startYear, int seasonStartMonth, Element element) {
        String time = element.select("div.event__time").text();
        time = time.replace(". ", "." + startYear + " ").substring(0, 16);
        LocalDateTime dateTime = LocalDateTime.parse(time, FORMATTER);
        int month = dateTime.getMonth().getValue();
        if (month < seasonStartMonth) {
            dateTime = dateTime.plusYears(1);
        }
        return dateTime;
    }
}
