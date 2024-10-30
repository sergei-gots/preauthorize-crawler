package org.gots.app;

import lombok.extern.slf4j.Slf4j;
import org.gots.service.ApiPreauthorizeCrawler;
import org.gots.domain.ApiPreauthorizeCrawlerCommandLineArguments;
import org.gots.service.ApiPreauthorizeInfo;
import org.gots.service.ApiPreauthorizeXlsxFileWriter;
import org.gots.service.ApiPreauthorizeXlsxWorkbookCreator;
import org.gots.apputil.CommandLineParser;
import org.gots.apputil.AppAbleToShowHelp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class ApiPreauthorizeCrawlerApp implements AppAbleToShowHelp {


    ApiPreauthorizeCrawlerCommandLineArguments apiPreauthorizeCrawlerCommandLineArguments = new ApiPreauthorizeCrawlerCommandLineArguments();

    @Override
    public String getHelp() {
        return """
                      Usage:
                      --projectdir=<path>   Set the project's directory (by default = '.')
                      --outputdir=<path>    Set the output directory (by default = 'scratches)
                      --swaggerhost=<url>   Set the Swagger host URL (by default = 'http://localhost:8080/webapp/swagger-ui/index.html?configUrl=/webapp/v3/api-docs/swagger-config#/'" +
                      --help                Show this help message
                      Thank you for using this @Preauthorize-crawler
                      Best wishes, Sergei:)
                    """;
    }

    @Override
    public void printHelp() {
        System.out.println(getHelp());
    }

    public static void main(String[] args) throws IOException {
        new ApiPreauthorizeCrawlerApp(args);
    }

    private ApiPreauthorizeCrawlerApp(String[] args) throws IOException {
        log.info("@Preathorize-info-crawler Gots S.");

        ApiPreauthorizeCrawler apiPreauthorizeCrawler = new ApiPreauthorizeCrawler();
        ApiPreauthorizeInfo apiPreauthorizeInfo = new ApiPreauthorizeInfo();

        CommandLineParser.parseArgs(apiPreauthorizeCrawlerCommandLineArguments, args, this);
        apiPreauthorizeCrawler.process(apiPreauthorizeCrawlerCommandLineArguments,
                apiPreauthorizeInfo);
        ApiPreauthorizeXlsxWorkbookCreator apiAuthoritiesXlsxWorkbookCreator = new ApiPreauthorizeXlsxWorkbookCreator(apiPreauthorizeInfo);
        ApiPreauthorizeXlsxFileWriter apiPreauthorizeXlsxFileWriter = new ApiPreauthorizeXlsxFileWriter();

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        apiPreauthorizeXlsxFileWriter.writeReport(apiAuthoritiesXlsxWorkbookCreator.constructReport(now), now, apiPreauthorizeCrawlerCommandLineArguments);
        apiAuthoritiesXlsxWorkbookCreator.close();

    }
}