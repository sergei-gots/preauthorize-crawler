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
import java.util.List;

import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;
import static org.apache.poi.ss.usermodel.BorderStyle.THIN;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER;
import static org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT;

@Slf4j
@RequiredArgsConstructor
public class ApiPreauthorizeXlsxWorkbookCreator {

    private final ApiPreauthorizeInfo apiPreauthorizeInfo;

    private Workbook workbook;

    public Workbook constructReport(LocalDateTime timeStamp) {

        workbook = new XSSFWorkbook();

        final CellStyle sheetHeaderCellStyle = createCellStyle(IndexedColors.WHITE, BOLD, LEFT);
        final CellStyle tableHeaderCellStyle = createCellStyle(IndexedColors.WHITE1);
        final CellStyle authorityCellStyle = createCellStyle(IndexedColors.TAN);
        final CellStyle noAuthorityCellStyle = createCellStyle(IndexedColors.LIGHT_GREEN);

        CreationHelper creationHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("API Methods Authorities Info");

        int rowNumber = 0;

        Row row = sheet.createRow(rowNumber++);
        Cell cell = row.createCell(0);
        cell.setCellValue("API Methods Authorities Info");
        cell.setCellStyle(sheetHeaderCellStyle);


        cell = row.createCell(1);
        cell.setCellValue("at " + timeStamp);
        cell.setCellStyle(sheetHeaderCellStyle);

        row = sheet.createRow(rowNumber++);

        int columnNumber = 0;
        cell = row.createCell(columnNumber++);
        cell.setCellValue("Controller");
        cell.setCellStyle(tableHeaderCellStyle);

        cell = row.createCell(columnNumber++);
        cell.setCellValue("Method");
        cell.setCellStyle(tableHeaderCellStyle);

        for (String authority : apiPreauthorizeInfo.getFoundAuthorities()) {

            cell = row.createCell(columnNumber);
            cell.setCellValue(authority);
            cell.setCellStyle(tableHeaderCellStyle);

            sheet.autoSizeColumn(columnNumber++);

        }

        for (ApiControllerInfo apiControllerInfo : apiPreauthorizeInfo.controllerInfos) {

            String className = apiControllerInfo.getClassName();
            String classPath =  className + '/';

            for (ApiMethodInfo methodInfo : apiControllerInfo.getMethods()) {

                row = sheet.createRow(rowNumber++);
                columnNumber = 0;
                row.createCell(columnNumber++).setCellValue(className);
                String methodName = methodInfo.getMethodName();
                cell = row.createCell(columnNumber++);
                cell.setCellValue(methodName);
                cell.setHyperlink(createHyperLink(creationHelper, classPath + methodName));
                setHyperlinkStyle(workbook, cell);
                List<String> methodAuthorities = methodInfo.getAuthorities();

                for (String authority : apiPreauthorizeInfo.getFoundAuthorities()) {

                    cell = row.createCell(columnNumber++);
                    if (methodAuthorities.contains(authority)) {
                        cell.setCellValue(authority);
                        cell.setCellStyle(authorityCellStyle);
                    } else {
                        cell.setCellValue("-");
                        cell.setCellStyle(noAuthorityCellStyle);
                    }

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
            log.info("Workbook Excel successfully closed");
        } catch (IOException e) {
            log.error("Failure during close Excel workbook occurred: {}", e.getMessage());
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

    private CellStyle createCellStyle(IndexedColors backgroundColour) {
        return createCellStyle(backgroundColour, PLAIN, CENTER);
    }

    private CellStyle createCellStyle(IndexedColors backgroundColour, int fontStyle, HorizontalAlignment horizontalAlignment) {

        CellStyle style = workbook.createCellStyle();

        style.setBorderBottom(THIN);
        style.setBorderTop(THIN);
        style.setBorderLeft(THIN);
        style.setBorderRight(THIN);

        style.setAlignment(horizontalAlignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(fontStyle == BOLD);
        style.setFont(font);

        if (backgroundColour != IndexedColors.AUTOMATIC) {
            style.setFillForegroundColor(backgroundColour.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }

        return style;

    }
}