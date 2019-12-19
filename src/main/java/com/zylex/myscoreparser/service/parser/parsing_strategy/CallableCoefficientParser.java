package com.zylex.myscoreparser.service.parser.parsing_strategy;

import com.zylex.myscoreparser.controller.logger.BlockLogger;
import com.zylex.myscoreparser.exceptions.CoefficientParserException;
import com.zylex.myscoreparser.model.Coefficient;
import com.zylex.myscoreparser.model.Game;
import com.zylex.myscoreparser.service.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CallableCoefficientParser implements Callable<List<Game>> {

    private WebDriver driver;

    private WebDriverWait wait;

    private List<Game> games;

    private int playOffGames = 0;

    private int noDataGames = 0;

    private DriverManager driverManager;

    private List<Game> archiveGames;

    private BlockLogger logger;

    public CallableCoefficientParser(BlockLogger logger, DriverManager driverManager, List<Game> archiveGames, List<Game> games) {
        this.logger = logger;
        this.driverManager = driverManager;
        this.archiveGames = archiveGames;
        this.games = games;
    }

    public List<Game> call() {
        try {
            driver = driverManager.getDriver();
            wait = new WebDriverWait(driver, 2);
            processCoefficientParsing();
            return games;
        } catch (InterruptedException e) {
            throw new CoefficientParserException(e.getMessage(), e);
        } finally {
            driverManager.addDriverToQueue(driver);
            logger.setPlayOffGames(playOffGames);
            logger.setNoDataGames(noDataGames);
        }
    }

    private void processCoefficientParsing() {
        for (Game game : games) {
            logger.logGame();
            driver.navigate().to(String.format("https://www.myscore.ru/match/%s/#odds-comparison;1x2-odds;full-time", game.getLink()));
            if (!coefficientTableExists()) {
                continue;
            }
            String pageSource = driver.getPageSource();
            Document document = Jsoup.parse(pageSource);
            if (isPlayOff(document)) {
                continue;
            }
            parseCoefficients(game, document);
        }
    }

    private boolean coefficientTableExists() {
        try {
            //TODO remove sleep
            Thread.sleep(1200);
            if (!driver.getCurrentUrl().contains("full-time")) {
                throw new TimeoutException();
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("odds_1x2")));
        } catch (TimeoutException | InterruptedException ignore) {
            noDataGames++;
            return false;
        }
        return true;
    }

    private boolean isPlayOff(Document document) {
        String league = document.select("span.description__country > a").text();
        if (league.contains("плей-офф")) {
            playOffGames++;
            return true;
        }
        return false;
    }

    private void parseCoefficients(Game game, Document document) {
        Elements coef1x2Games = document.select("div#block-1x2-ft > table#odds_1x2 > tbody > tr");
        Elements coefDchGames = document.select("div#block-double-chance-ft > table#odds_dch > tbody > tr");
        Map<String, Coefficient> coefficients = game.getCoefficients();
        process1x2Coefficients(coef1x2Games, coefficients);
        processDchCoefficients(coefDchGames, coefficients);
        if (!coefficients.isEmpty()) {
            archiveGames.add(game);
        }
    }

    private void process1x2Coefficients(Elements coef1x2Games, Map<String, Coefficient> coefficients) {
        for (Element element : coef1x2Games) {
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

    private void processDchCoefficients(Elements coefDchGames, Map<String, Coefficient> coefficients) {
        for (Element element : coefDchGames) {
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
