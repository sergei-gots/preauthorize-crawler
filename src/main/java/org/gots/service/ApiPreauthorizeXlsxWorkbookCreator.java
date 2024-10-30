package org.gots.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gots.domain.ApiControllerInfo;
import org.gots.domain.ApiMethodInfo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ApiPreauthorizeXlsxWorkbookCreator {

    private final ApiPreauthorizeInfo apiPreauthorizeInfo;

    private Workbook workbook;

    public Workbook constructReport() {
        workbook = new XSSFWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Информация о правах доступа для методов API Controller-ов");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String nowText = now.toString().replace(':', '-');

        int rowNumber = 0;

        Row row = sheet.createRow(rowNumber++);
        Cell cell = row.createCell(0);
        cell.setCellValue("Информация о правах доступа для методов API Controller-ов");


        row = sheet.createRow(rowNumber++);
        cell = row.createCell(0);
        cell.setCellValue("По состоянию на " + now);

        row = sheet.createRow(rowNumber++);

        int columnNumber = 0;
        cell = row.createCell(columnNumber++);
        cell.setCellValue("Контролллер");

        cell = row.createCell(columnNumber++);
        cell.setCellValue("Метод");
        for (String authority : apiPreauthorizeInfo.getFoundAuthorities()) {
            cell = row.createCell(columnNumber);
            cell.setCellValue(authority);
            sheet.autoSizeColumn(columnNumber++);
        }
        for (ApiControllerInfo apiControllerInfo : apiPreauthorizeInfo.controllerInfos) {
            for (ApiMethodInfo methodInfo : apiControllerInfo.getApiMethodInfos()) {
                row = sheet.createRow(rowNumber++);
                columnNumber = 0;
                row.createCell(columnNumber++).setCellValue(methodInfo.getClassName());
                String javaMethodName = methodInfo.getJavaMethodName();
                cell = row.createCell(columnNumber++);
                cell.setCellValue(javaMethodName);
                cell.setHyperlink(createHyperLink(creationHelper, javaMethodName));
                setHyperlinkStyle(workbook, cell);
                List<String> methodAuthorities = methodInfo.getAuthorities();
                for (String authority : apiPreauthorizeInfo.getFoundAuthorities()) {
                    cell = row.createCell(columnNumber++);
                    cell.setCellValue(methodAuthorities.contains(authority) ? authority : "-");
                }
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        return workbook;
    }

    public void close() {
        try {
            workbook.close();
            log.info("Workbook Excel успешно закрыт");
        } catch (IOException e) {
            log.error("Ошибка при закрытии workbook Excel: {}", e.getMessage());
        }
    }

    private void setHyperlinkStyle(Workbook workbook, Cell cell) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE); // Подчеркивание
        font.setColor(IndexedColors.BLUE.getIndex()); // Синий цвет
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }

    private Hyperlink createHyperLink(CreationHelper creationHelper, String methodName) {
        Hyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.URL);
        hyperlink.setAddress(apiPreauthorizeInfo.getBaseSwaggerUrl() + methodName);
        return hyperlink;
    }
}