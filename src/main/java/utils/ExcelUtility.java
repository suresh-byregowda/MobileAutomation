package utils;

import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;

public final class ExcelUtility {

    private static Workbook workbook;

    private ExcelUtility() {}

    /* =======================================================
       LOAD EXCEL FROM CLASSPATH (ONCE)
       ======================================================= */
    public static void loadExcel(String excelKey) {

        String resourcePath = ConfigReader.get(excelKey);
        if (resourcePath == null) {
            throw new RuntimeException("Excel path not found in config for key: " + excelKey);
        }

        try (InputStream is = ExcelUtility.class
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException("Excel file not found on classpath: " + resourcePath);
            }

            workbook = WorkbookFactory.create(is);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load Excel for key: " + excelKey, e);
        }
    }

    /* =======================================================
       CELL DATA
       ======================================================= */
    public static String getCellData(String sheetName, int row, int col) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return "";

        Row r = sheet.getRow(row);
        if (r == null) return "";

        Cell cell = r.getCell(col);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /* =======================================================
       ROW / COLUMN COUNTS
       ======================================================= */
    public static int getRowCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet == null ? 0 : sheet.getPhysicalNumberOfRows();
    }

    public static int getColumnCount(String sheetName, int rowNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null || sheet.getRow(rowNum) == null) return 0;
        return sheet.getRow(rowNum).getPhysicalNumberOfCells();
    }

    /* =======================================================
       READ SHEET AS MAP (HEADER → VALUE)
       ======================================================= */
    public static List<Map<String, String>> getSheetData(String sheetName) {

        List<Map<String, String>> sheetData = new ArrayList<>();
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return sheetData;

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return sheetData;

        int colCount = headerRow.getPhysicalNumberOfCells();
        int rowCount = sheet.getPhysicalNumberOfRows();

        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Map<String, String> rowData = new LinkedHashMap<>();
            for (int j = 0; j < colCount; j++) {
                String key = headerRow.getCell(j).getStringCellValue();
                String value = getCellData(sheetName, i, j);
                rowData.put(key, value);
            }
            sheetData.add(rowData);
        }
        return sheetData;
    }

    /* =======================================================
       CLOSE WORKBOOK
       ======================================================= */
    public static void closeExcel() {
        try {
            if (workbook != null) workbook.close();
        } catch (Exception ignored) {}
    }
}
