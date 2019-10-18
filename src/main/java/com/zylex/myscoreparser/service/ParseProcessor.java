package com.zylex.myscoreparser.service;

import com.zylex.myscoreparser.DriverFactory;
import com.zylex.myscoreparser.exceptions.ParseProcessorException;
import com.zylex.myscoreparser.model.Record;
import com.zylex.myscoreparser.repository.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ParseProcessor {

    private String[] countryLeagues;

    private ExecutorService service = Executors.newFixedThreadPool(DriverFactory.THREADS);

    public ParseProcessor(Repository repository) {
        this.countryLeagues = repository.getCountryLeagues();
    }

    public List<Record> process() {
        try {
            List<String> archiveLinks = processArchiveLinks();
            List<List<Record>> leagueRecords = processLeagueRecords(archiveLinks);
            return processCoefficients(leagueRecords);
        } catch (InterruptedException | ExecutionException e) {
            throw new ParseProcessorException(e.getMessage(), e);
        } finally {
            service.shutdown();
            DriverFactory.quitDrivers();
        }
    }

    private List<String> processArchiveLinks() throws InterruptedException, ExecutionException {
        List<CallableArchiveParser> callableArchiveParsers = new ArrayList<>();
        for (String countryLeague : countryLeagues) {
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
        List<CallableCoefficientParser> callableCoefficientParsers = new ArrayList<>();
        for (List<Record> records : recordsList) {
            callableCoefficientParsers.add(new CallableCoefficientParser(records));
        }
        List<Future<List<Record>>> futureRecordsLists = service.invokeAll(callableCoefficientParsers);
        return processRecords(futureRecordsLists);
    }

    private List<Record> processRecords(List<Future<List<Record>>> futureRecordsLists) throws InterruptedException, ExecutionException {
        List<Record> records = new ArrayList<>();
        for (Future<List<Record>> futureRecordsList : futureRecordsLists) {
            List<Record> recordList = futureRecordsList.get();
            records.addAll(recordList);
        }
        return records;
    }
}
