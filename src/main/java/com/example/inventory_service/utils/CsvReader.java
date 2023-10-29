package com.example.inventory_service.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;

public class CsvReader {

    private static final Logger LOG = LoggerFactory.getLogger(CsvReader.class);

    public static List<String[]> loadCSVData(String csvFileName) {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:" + csvFileName);
        } catch (FileNotFoundException e) {
            LOG.error("Error loading csv data. Given file {} not found.", csvFileName);
            return Collections.emptyList();
        }
        try {
            FileReader reader = new FileReader(file);
            try (CSVReader csvReader = new CSVReaderBuilder(reader).
                    withSkipLines(1)
                    .build()) {
                return csvReader.readAll();
            }
        } catch (Exception e) {
            LOG.error("Error loading csv data from " + csvFileName, e);
            return Collections.emptyList();
        }
    }
}
