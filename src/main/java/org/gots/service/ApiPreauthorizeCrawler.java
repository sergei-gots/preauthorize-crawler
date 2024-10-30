package org.gots.service;

import lombok.extern.slf4j.Slf4j;
import org.gots.apputil.CommandLineArguments;
import org.gots.domain.ApiControllerInfo;
import org.gots.domain.ApiMethodInfo;
import org.gots.domain.ApiPreauthorizeCrawlerCommandLineArguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ApiPreauthorizeCrawler {

    Pattern restControllerPattern = Pattern.compile(
            "@RestController"
    );

    Pattern classNamePattern = Pattern.compile(
            "@RestController.*?public\\s+class\\s+([A-Za-z\\d]+)",
            Pattern.DOTALL
    );

    Pattern methodDeclarationPattern = Pattern.compile(
            "(@([a-zA-Z]{3,6})Mapping).*?return",
            Pattern.DOTALL
    );

    Pattern javaMethodNamePattern = Pattern.compile(
            "public\\s+[\\w<>\\[\\]\\s]+\\s+([a-z]\\w*)\\s*\\(",
            Pattern.DOTALL
    );

    Pattern authoritiesPattern = Pattern.compile(
            "@PreAuthorize\\s*\\(\\s*\"has(?:Any)?Authority\\s*\\('([',\\w\\s]+)\\)",
            Pattern.DOTALL
    );

    Pattern authorityPattern = Pattern.compile("(\\w+)'(?:\\s*,\\s*')*");

    private ApiPreauthorizeInfo apiPreauthorizeInfo;

    private String restControllerMask;


    public void process(CommandLineArguments commandLineArguments, ApiPreauthorizeInfo apiPreauthorizeInfo) throws IOException{
        this.apiPreauthorizeInfo = apiPreauthorizeInfo;
        this.restControllerMask = commandLineArguments.getArgumentValue(ApiPreauthorizeCrawlerCommandLineArguments.REST_CONTROLLER_MASK_KEY);
        apiPreauthorizeInfo.setBaseSwaggerUrl(commandLineArguments.getArgumentValue(ApiPreauthorizeCrawlerCommandLineArguments.SWAGGER_HOST_ARGUMENT_KEY));

        File projectDir = new File(commandLineArguments.getArgumentValue(ApiPreauthorizeCrawlerCommandLineArguments.PROJECT_DIR_ARGUMENT_KEY)).getAbsoluteFile();

        if (!(projectDir.exists() && projectDir.isDirectory())) {
            log.error("Directory '{}' does not exist", projectDir.toPath());
            throw new IllegalArgumentException("Dir not found");
        }

        readJavaFiles(projectDir);
        sortJavaFilesAlphabetically();
        processAllMethods();
    }

    private void sortJavaFilesAlphabetically() {
        apiPreauthorizeInfo.getControllerInfos().sort(Comparator.naturalOrder());
    }

    private void processAllMethods() {
        for (ApiControllerInfo apiControllerInfo : apiPreauthorizeInfo.getControllerInfos()) {
            String className = apiControllerInfo.getClassName();
            String classFileContent = apiControllerInfo.getFileContent();

            log.info("Processing the controller {}", className);

            Matcher methodDeclarationMatcher = methodDeclarationPattern.matcher(classFileContent);

            List<ApiMethodInfo> methodInfos = apiControllerInfo.getMethods();

            while (methodDeclarationMatcher.find()) {
                String methodFullName = methodDeclarationMatcher.group(0);
                ApiMethodInfo apiMethodInfo = new ApiMethodInfo();
                apiMethodInfo.setName(extractJavaMethodName(methodFullName));
                apiMethodInfo.setAuthorities(extractAuthorities(methodFullName));
                apiMethodInfo.printLog();
                methodInfos.add(apiMethodInfo);
            }
        }
    }

    private List<String> extractAuthorities(String methodFullDeclaration) {
        Matcher authoritiesMatcher = authoritiesPattern.matcher(methodFullDeclaration);

        String authoritiesMatch = (authoritiesMatcher.find()) ? authoritiesMatcher.group(1) : null;
        List<String> authorities = new ArrayList<>();

        if (authoritiesMatch == null) {
            return authorities;
        }

        Matcher authorityMatcher = authorityPattern.matcher(authoritiesMatch);

        List<String> foundAuthorities = apiPreauthorizeInfo.getFoundAuthorities();
        while (authorityMatcher.find()) {
            String foundAuthority = authorityMatcher.group(1);
            authorities.add(foundAuthority);
            if (!foundAuthorities.contains(foundAuthority)) {
                foundAuthorities.add(foundAuthority);
            }
        }
        return authorities;
    }

    private String extractJavaMethodName(String fullMethodDeclarationMatch) {

        Matcher matcher = javaMethodNamePattern.matcher(fullMethodDeclarationMatch);

        return (matcher.find()) ? matcher.group(1) : null;
    }


    private void readJavaFiles(File directory) throws IOException{
        log.trace("Crawling throughout the directory '{}'", directory);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    readJavaFiles(file);
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    readJavaFile(file);
                }
            }
        }
    }

    private void readJavaFile(File javaFile) throws IOException {

        Path filePath = javaFile.toPath();

        String fileContent = Files.readString(filePath);
        Matcher restControllerMatcher = restControllerPattern.matcher(fileContent);
        if (!restControllerMatcher.find()) {
            return;
        }

        Matcher classNameMatcher = classNamePattern.matcher(fileContent);
        if (!classNameMatcher.find()) {
            return;
        }

        log.info("Presumably a controller class found in the file '{}'", filePath);


        String className = classNameMatcher.group(1);
        int restControllerMaskIndex = className.indexOf(this.restControllerMask);
        if (restControllerMaskIndex > 1) {
            className = className.substring(0, restControllerMaskIndex);
        }
        className = className.toLowerCase();

        ApiControllerInfo apiControllerInfo = new ApiControllerInfo();
        apiControllerInfo.setClassName(className);
        apiControllerInfo.setFileContent(fileContent);
        apiPreauthorizeInfo.getControllerInfos().add(apiControllerInfo);
        log.info("File '{}' successfully read ", filePath);
    }

}