package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.controller.ConsoleLogger;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Record;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ParseProcessor {

    private List<String> leagueSeasonLinks;

    private ExecutorService service = Executors.newFixedThreadPool(DriverFactory.THREADS);

    public ParseProcessor(List<String> leagueSeasonLinks) {
        this.leagueSeasonLinks = leagueSeasonLinks;
    }

    public List<Record> process() {
        try {
            List<String> archiveLinks = processArchiveLinks();
            List<List<Record>> leagueRecords = processLeagueRecords(archiveLinks);
            List<Record> records = processCoefficients(leagueRecords);
            ConsoleLogger.dropBlockLog();
            return records;
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        } finally {
            service.shutdown();
        }
    }

    private List<String> processArchiveLinks() throws InterruptedException, ExecutionException {
        ConsoleLogger.blockArchives.set(leagueSeasonLinks.size());
        ConsoleLogger.writeInLine("\nProcessing block №" + ConsoleLogger.blockNumber + " archives: 0 out of " + leagueSeasonLinks.size());
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : leagueSeasonLinks) {
            callableArchiveParsers.add(new CallableArchiveParser(countryLeague));
        }
        List<Future<List<String>>> futureArchiveParsers = service.invokeAll(callableArchiveParsers);
        return convertFutureArchiveLinks(futureArchiveParsers);
    }

    private List<String> convertFutureArchiveLinks(List<Future<List<String>>> futureArchiveParsers) throws InterruptedException, ExecutionException {
        List<String> archiveLinks = new ArrayList<>();
        for (Future<List<String>> future : futureArchiveParsers) {
            archiveLinks.addAll(future.get());
        }
        return archiveLinks;
    }

    private List<List<Record>> processLeagueRecords(List<String> archiveLinks) throws InterruptedException, ExecutionException {
        ConsoleLogger.blockLeagues.set(archiveLinks.size());
        ConsoleLogger.writeInLine("\nProcessing block №" + ConsoleLogger.blockNumber + " seasons: 0 out of " + archiveLinks.size());
        List<CallableLeagueParser> callableLeagueParsers = new ArrayList<>();
        for (String archiveLink : archiveLinks) {
            callableLeagueParsers.add(new CallableLeagueParser(archiveLink));
        }
        List<Future<List<Record>>> futureLeagueRecords = service.invokeAll(callableLeagueParsers);
        List<List<Record>> recordsLinks = convertFutureLeagueRecords(futureLeagueRecords);
        Comparator<List<Record>> sizeComparator = (o1, o2) -> Integer.compare(o2.size(), o1.size());
        Comparator<List<Record>> seasonComparator = Comparator.comparingInt(this::getSeasonStartYear);
        return recordsLinks.stream()
                .sorted(sizeComparator.thenComparing(seasonComparator))
                .collect(Collectors.toList());
    }

    private List<List<Record>> convertFutureLeagueRecords(List<Future<List<Record>>> futureLeagueRecords) throws InterruptedException, ExecutionException {
        List<List<Record>> recordsLinks = new ArrayList<>();
        for (Future<List<Record>> futureLeagueRecord: futureLeagueRecords) {
            recordsLinks.add(futureLeagueRecord.get());
        }
        return recordsLinks;
    }

    private int getSeasonStartYear(List<Record> records) {
        return Integer.parseInt(records.get(0).getSeason().substring(0, 4));
    }

    private List<Record> processCoefficients(List<List<Record>> recordsList) throws InterruptedException, ExecutionException {
        ConsoleLogger.writeInLine("\nProcessing block №" + ConsoleLogger.blockNumber + " coefficients: 00.00%");
        List<CallableCoefficientParser> callableCoefficientParsers = new ArrayList<>();
        for (List<Record> records : recordsList) {
            callableCoefficientParsers.add(new CallableCoefficientParser(records));
        }
        List<Future<List<Record>>> futureRecordsLists = service.invokeAll(callableCoefficientParsers);
        return processTotalRecords(futureRecordsLists);
    }

    private List<Record> processTotalRecords(List<Future<List<Record>>> futureRecordsLists) throws InterruptedException, ExecutionException {
        List<Record> records = new ArrayList<>();
        for (Future<List<Record>> futureRecordsList : futureRecordsLists) {
            List<Record> recordList = futureRecordsList.get();
            records.addAll(recordList);
        }
        return records;
    }
}
