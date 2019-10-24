package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.exceptions.CoefficientParserException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Record;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CallableCoefficientParser implements Callable<List<Record>> {

    private WebDriver driver;

    private WebDriverWait wait;

    private List<Record> records;

    private int playOffRecords = 0;

    private int noCoefficientRecords = 0;

    private DriverFactory driverFactory;

    CallableCoefficientParser(DriverFactory driverFactory, List<Record> records) {
        this.records = records;
        this.driverFactory = driverFactory;
    }

    public List<Record> call() {
        try {
            driver = driverFactory.getDriver();
            wait = new WebDriverWait(driver, 2);
            processCoefficientParsing();
            return records;
        } catch (InterruptedException e) {
            throw new CoefficientParserException(e.getMessage(), e);
        } finally {
            driverFactory.addDriverToQueue(driver);
            ConsoleLogger.totalPlayOffRecords.addAndGet(playOffRecords);
            ConsoleLogger.totalWithNoCoef.addAndGet(noCoefficientRecords);
        }
    }

    private void processCoefficientParsing() {
        for (Record record : records) {
            ConsoleLogger.logRecord();
            driver.navigate().to(String.format("https://www.myscore.ru/match/%s/#odds-comparison;1x2-odds;full-time", record.getCoefHref()));
            if (!coefficientTableExists()) {
                continue;
            }
            String pageSource = driver.getPageSource();
            Document document = Jsoup.parse(pageSource);
            if (isPlayOff(document)) {
                continue;
            }
            parseCoefficients(record, document);
        }
    }

    private boolean coefficientTableExists() {
        try {
            Thread.sleep(1200);
            if (!driver.getCurrentUrl().contains("full-time")) {
                throw new TimeoutException();
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("odds_1x2")));
        } catch (TimeoutException | InterruptedException ignore) {
            noCoefficientRecords++;
            return false;
        }
        return true;
    }

    private boolean isPlayOff(Document document) {
        String league = document.select("span.description__country > a").text();
        if (league.contains("плей-офф")) {
            playOffRecords++;
            return true;
        }
        return false;
    }

    private void parseCoefficients(Record record, Document document) {
        Elements coef1x2Records = document.select("div#block-1x2-ft > table#odds_1x2 > tbody > tr");
        Elements coefDchRecords = document.select("div#block-double-chance-ft > table#odds_dch > tbody > tr");
        Map<String, Coefficient> coefficients = record.getCoefficients();
        process1x2Coefficients(coef1x2Records, coefficients);
        processDchCoefficients(coefDchRecords, coefficients);
    }

    private void process1x2Coefficients(Elements coef1x2Records, Map<String, Coefficient> coefficients) {
        for (Element element : coef1x2Records) {
            String bookmaker = element.select("td.bookmaker > div > a").first().attr("title");
            bookmaker = fixBookmakerName(bookmaker);
            if (checkBookmaker(bookmaker)) {
                String[] allCoef = element.select("td.kx > span").text().split(" ");
                Coefficient coefficient = new Coefficient(bookmaker,
                        allCoef[0],
                        allCoef[1],
                        allCoef[2],
                        getMaxOfThree(allCoef),
                        getMinOfThree(allCoef));
                coefficients.putIfAbsent(bookmaker, coefficient);
            }
        }
    }

    private void processDchCoefficients(Elements coefDchRecords, Map<String, Coefficient> coefficients) {
        for (Element element : coefDchRecords) {
            String bookmaker = element.select("td.bookmaker > div > a").first().attr("title");
            bookmaker = fixBookmakerName(bookmaker);
            if (checkBookmaker(bookmaker)) {
                String[] allCoef = element.select("td.kx > span").text().split(" ");
                Coefficient coefficient = coefficients.get(bookmaker);
                if (coefficient != null) {
                    if (allCoef[0].equals("-") || allCoef[1].equals("-") || allCoef[2].equals("-")) {
                        coefficient.setDch1X("-");
                        coefficient.setDchX2("-");
                    } else {
                        coefficient.setDch1X(allCoef[0]);
                        coefficient.setDchX2(allCoef[2]);
                    }
                }
            }
        }
    }

    private String getMaxOfThree(String[] args) {
        try {
            double one = Double.parseDouble(args[0]);
            double two = Double.parseDouble(args[1]);
            double three = Double.parseDouble(args[2]);
            return String.valueOf(Math.max(Math.max(one, two), three));
        } catch (NumberFormatException e) {
            return "-";
        }
    }

    private String getMinOfThree(String[] args) {
        try {
            double one = Double.parseDouble(args[0]);
            double two = Double.parseDouble(args[1]);
            double three = Double.parseDouble(args[2]);
            return String.valueOf(Math.min(Math.min(one, two), three));
        } catch (NumberFormatException e) {
            return "-";
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
        return "Wrong bookmaker name";
    }

    private boolean checkBookmaker(String bookmaker) {
        String[] bookmakers = {"1XBET", "Winline", "Leon"};
        for (String bm : bookmakers) {
            if (bm.equalsIgnoreCase(bookmaker)) {
                return true;
            }
        }
        return false;
    }
}
