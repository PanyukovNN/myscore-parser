package com.zylex.myscoreparser;

import com.zylex.myscoreparser.model.Coeffitient;
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
import java.util.Map;

public class Parser {

    private WebDriver driver;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private WebDriverWait wait;

    private String year;

    public Parser(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 15);
    }

    public List<Record> leagueParser(String leagueHref, String year) throws InterruptedException {
        this.year = year;
        driver.navigate().to(String.format("https://www.myscore.ru/football/%s/results/", leagueHref));
        showMore(driver);
        String pageSourse = driver.getPageSource();
        Document document = Jsoup.parse(pageSourse);
        return parseGameRecords(document);
    }

    private List<Record> parseGameRecords(Document document) {
        Elements gameRecords = document.select("div.event__match");
        List<Record> records = new ArrayList<>();
        String country = document.select("span.event__title--type").text();
        String league = document.select("span.event__title--name").text();
        int i = 0;
        for (Element gameRecord : gameRecords) {
            LocalDateTime gameDateTime = processDate(year, gameRecord);
            String firstCommand = gameRecord.select("div.event__participant--home").text();
            String secondCommand = gameRecord.select("div.event__participant--away").text();
            String[] scores = gameRecord.select("div.event__scores > span").text().split(" ");
            int firstBalls = Integer.parseInt(scores[0]);
            int secondBalls = Integer.parseInt(scores[1]);
            String coefHref = gameRecord.id().replace("g_1_", "");
            Record record = new Record(country, league, gameDateTime, firstCommand, secondCommand, firstBalls, secondBalls, coefHref);
            if (coefParser(record)) {
                records.add(record);
                System.out.println(++i + ") " + record);
            } else {
                records.add(record);
                System.out.println(++i + ") Отсутствуют значения коэффициентов : " + record);
            }
        }
        return records;
    }

    public boolean coefParser(Record record) {
        driver.navigate().to(String.format("https://www.myscore.ru/match/%s/#odds-comparison;1x2-odds;full-time", record.getCoefHref()));
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("odds_1x2")));
        } catch (TimeoutException e) {
            return false;
        }
        String pageSourse = driver.getPageSource();
        Document doc = Jsoup.parse(pageSourse);
        Element coefTable = doc.select("table#odds_1x2").first();
        Elements coefRecords = coefTable.select("tbody > tr");
        parseCoefficients(record, coefRecords);
        return true;
    }

    private static void showMore(WebDriver driver) throws InterruptedException {
        WebElement showLink = null;
        try {
            showLink = driver.findElement(By.className("event__more"));
        } catch (NoSuchElementException e) {
            return;
        }
        Thread.sleep(2000);
        while (true) {
            try {
                if (showLink.isDisplayed()) {
                    showLink.click();
                    showLink = driver.findElement(By.className("event__more"));
                }
            } catch (StaleElementReferenceException e) {
                break;
            } catch (ElementClickInterceptedException e) {
                System.out.println("Не могу кликнуть!");
            }
            Thread.sleep(2000);
        }
        Thread.sleep(2000);
    }

    private static LocalDateTime processDate(String year, Element element) {
        String time = element.select("div.event__time").text();
        time = time.replace(". ", "." + year + " ");
        LocalDateTime dateTime = LocalDateTime.parse(time, FORMATTER);
        int month = dateTime.getMonth().getValue();
        if (month >= 8) {
            dateTime = dateTime.minusYears(1);
        }
        return dateTime;
    }

    private void parseCoefficients(Record record, Elements coefRecords) {
        Map<String, Coeffitient> coeffitients = record.getCoeffitients();
        for (Element element : coefRecords) {
            String bookmaker = element.select("td.bookmaker > div > a").first().attr("title");
            if (checkBookmaker(bookmaker)) {
                String[] allCoef = element.select("td.kx > span").text().split(" ");
                Coeffitient coeffitient = new Coeffitient(fixBookmakerName(bookmaker), allCoef[0], allCoef[1], allCoef[2]);
                coeffitients.putIfAbsent(fixBookmakerName(bookmaker), coeffitient);
            }
        }
    }

    private String fixBookmakerName(String bookmaker) {
        if (bookmaker.equalsIgnoreCase("1xStavka.ru")) {
            return "1XBET";
        } else if (bookmaker.equalsIgnoreCase("Winline.ru")) {
            return "Winline";
        } else if (bookmaker.equalsIgnoreCase("Leon.ru")) {
            return "Leon";
        }
        return "wrong bookmaker name";
    }

    private boolean checkBookmaker(String bookmaker) {
        String[] bookmakers = {"1xStavka.ru", "Winline.ru", "Leon.ru"};
        for (String bm: bookmakers) {
            if (bm.equalsIgnoreCase(bookmaker)) {
                return true;
            }
        }
        return false;
    }
}
