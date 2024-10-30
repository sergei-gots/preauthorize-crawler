package org.gots.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.gots.apputil.CommandLineArguments;
import org.gots.domain.ApiPreauthorizeCrawlerCommandLineArguments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
public class ApiPreauthorizeXlsxFileWriter {

    private final static String FILENAME_EXTENSION = ".xlsx";


    public void writeReport(Workbook workbook, LocalDateTime timeStamp, CommandLineArguments commandLineArguments) {
        String nowText = timeStamp.toString().replace(':', '-');
        saveFile(workbook, "api-preauthorize-report-" + nowText, commandLineArguments);
    }

    private void saveFile(Workbook workbook, String fileName, CommandLineArguments commandLineArguments) {

        String outputPath = commandLineArguments.getArgumentValue(ApiPreauthorizeCrawlerCommandLineArguments.OUTPUT_DIR_ARGUMENT_KEY);
        checkOutputPath(outputPath);
        File file = new File(outputPath + File.separator + fileName + FILENAME_EXTENSION);


        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            log.info("File successfully saved: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failure during file writing occurred: {}", e.getMessage());
        }
    }

    private void checkOutputPath(String outputPath) {
        Path currentDirPath = Paths.get("").toAbsolutePath();
        Path fullOutputPath = currentDirPath.resolve(outputPath);

        if (Files.exists(fullOutputPath)) {
            return;
        }
        try {
            Files.createDirectories(fullOutputPath);
        } catch (IOException e) {
            log.error("Failure during directory '{}' creation: {}", fullOutputPath, e.getMessage());
        }
    }
}