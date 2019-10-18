package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.DriverFactory;
import com.zylex.myscoreparser.Main;
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
import java.util.concurrent.TimeUnit;

public class CallableCoefficientParser implements Callable<List<Record>> {

    private WebDriver driver;

    private WebDriverWait wait;

    private List<Record> records;

    private int playOffRecords = 0;

    private int noCoefficientRecords = 0;

    CallableCoefficientParser(List<Record> records) {
        this.records = records;
    }

    public List<Record> call() {
        try {
            getDriver();
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            processCoefficientParsing(records);
            return records;
        } catch (InterruptedException e) {
            throw new CoefficientParserException(e.getMessage(), e);
        } finally {
            DriverFactory.drivers.add(driver);
            Main.totalPlayOffRecords.addAndGet(playOffRecords);
            Main.totalWithNoCoef.addAndGet(noCoefficientRecords);
        }
    }

    private void getDriver() throws InterruptedException {
        while (driver == null) {
            driver = DriverFactory.drivers.poll();
            Thread.sleep(10);
        }
        wait = new WebDriverWait(driver, 2);
    }

    private void processCoefficientParsing(List<Record> records) {
        int i = 1;
        for (Record record : records) {
            driver.navigate().to(String.format("https://www.myscore.ru/match/%s/#odds-comparison;1x2-odds;full-time", record.getCoefHref()));
            if (!coefficientTableExists()) {
                System.out.println(i++ + ") No coefficients: " + record);
                continue;
            }
            String pageSourse = driver.getPageSource();
            Document document = Jsoup.parse(pageSourse);
            if (isPlayOff(document)) {
                System.out.println(i++ + ") Play-off record: " + record);
                continue;
            }
            parseCoefficients(record, document);
            System.out.println(i++ + ") " + record);
        }
        Record tempRecord = records.get(0);
        System.out.printf("Finished: %s_%s_%s \nRecords number: %d \nRecords without coefficients: %d\n Play-off records: %d\n",
                tempRecord.getCountry(),
                tempRecord.getLeagueName(),
                tempRecord.getSeason(),
                records.size(),
                noCoefficientRecords,
                playOffRecords);
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
        if (!league.contains("Тур")) {
            playOffRecords++;
            return true;
        }
        return false;
    }

    private void parseCoefficients(Record record, Document document) {
        Elements coef1x2Records = document.select("table#odds_1x2")
                .first()
                .select("tbody > tr");
        Elements coefDchRecords = document.select("table#odds_dch")
                .first()
                .select("tbody > tr");

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
                coefficient.setMaxDch(getMaxOfThree(allCoef));
                coefficient.setMinDch(getMinOfThree(allCoef));
            }
        }
    }

    private String getMaxOfThree(String[] args) {
        try {
            double one = Double.parseDouble(args[0]);
            double two = Double.parseDouble(args[1]);
            double three = Double.parseDouble(args[2]);
            return String.valueOf(Math.max(Math.max(one, two), three));
        } catch (Exception e) {
            return "-";
        }
    }

    private String getMinOfThree(String[] args) {
        try {
            double one = Double.parseDouble(args[0]);
            double two = Double.parseDouble(args[1]);
            double three = Double.parseDouble(args[2]);
            return String.valueOf(Math.min(Math.min(one, two), three));
        } catch (Exception e) {
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
        for (String bm: bookmakers) {
            if (bm.equalsIgnoreCase(bookmaker)) {
                return true;
            }
        }
        return false;
    }
}
