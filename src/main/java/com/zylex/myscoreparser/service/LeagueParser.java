package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.model.Record;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LeagueParser {

    private WebDriver driver;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private WebDriverWait wait;

    public LeagueParser(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }

    public List<Record> processLeagueParsing(String leagueHref) throws InterruptedException {
        driver.navigate().to(String.format("https://www.myscore.ru/football/%sresults/", leagueHref));
        showMore(driver);
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);
        return parseGameRecords(document);
    }

    private List<Record> parseGameRecords(Document document) {
        List<Record> records = new ArrayList<>();
        Elements gameRecords = document.select("div.event__match");
        String country = document.select("span.event__title--type").first().text();
        String league = document.select("span.event__title--name").first().text();
        String season = document.select("div.teamHeader__text").first().text();
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
        return records;
    }

    private void showMore(WebDriver driver) throws InterruptedException {
        WebElement showLink;
        while (true) {
            try {
                Thread.sleep(1500);
                showLink = driver.findElement(By.className("event__more"));
                showLink.click();
            } catch (NoSuchElementException e) {
                break;
            } catch (ElementClickInterceptedException e) {
                System.out.println("Can't click, trying again...");
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
