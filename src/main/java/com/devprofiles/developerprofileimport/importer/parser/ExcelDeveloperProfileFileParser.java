package com.devprofiles.developerprofileimport.importer.parser;

import com.devprofiles.developerprofileimport.importer.dto.DeveloperProfileRawRow;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class ExcelDeveloperProfileFileParser implements DeveloperProfileFileParser {

    private static final List<String> SUPPORTED_EXTENSIONS = List.of(".xlsx", ".xls", ".xlsm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public boolean supports(String filename) {
        if (filename == null) {
            return false;
        }
        String lower = filename.toLowerCase(Locale.ENGLISH);
        return SUPPORTED_EXTENSIONS.stream().anyMatch(lower::endsWith);
    }

    @Override
    public List<DeveloperProfileRawRow> parse(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                return List.of();
            }
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                return List.of();
            }
            Map<Integer, String> headers = readHeaders(headerRow);
            List<DeveloperProfileRawRow> rows = new ArrayList<>();
            for (int i = headerRow.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> values = new LinkedHashMap<>();
                boolean hasValue = false;
                for (Map.Entry<Integer, String> entry : headers.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    String value = cell != null ? readCell(cell) : null;
                    if (value != null && !value.isBlank()) {
                        hasValue = true;
                    }
                    values.put(entry.getValue(), value != null ? value.trim() : null);
                }
                if (hasValue) {
                    rows.add(new DeveloperProfileRawRow(row.getRowNum() + 1, values));
                }
            }
            return rows;
        } catch (EncryptedDocumentException ex) {
            throw new IOException("Unable to read encrypted workbook", ex);
        }
    }

    private Map<Integer, String> readHeaders(Row headerRow) {
        Map<Integer, String> headers = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            String value = readCell(cell);
            if (value != null && !value.isBlank()) {
                headers.put(cell.getColumnIndex(), value.trim());
            }
        }
        return headers;
    }

    private String readCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.BLANK) {
            return null;
        }
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cellType == CellType.BOOLEAN) {
            return Boolean.toString(cell.getBooleanCellValue());
        }
        if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
            }
            double numericValue = cell.getNumericCellValue();
            if (Math.floor(numericValue) == numericValue) {
                return Long.toString((long) numericValue);
            }
            return Double.toString(numericValue);
        }
        if (cellType == CellType.FORMULA) {
            return readFormulaCell(cell);
        }
        return cell.toString();
    }

    private String readFormulaCell(Cell formulaCell) {
        FormulaEvaluator evaluator = formulaCell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        CellValue value = evaluator.evaluate(formulaCell);
        if (value == null) {
            return null;
        }
        return switch (value.getCellType()) {
            case STRING -> value.getStringValue();
            case BOOLEAN -> Boolean.toString(value.getBooleanValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(formulaCell)) {
                    yield formulaCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER);
                }
                double numericValue = value.getNumberValue();
                if (Math.floor(numericValue) == numericValue) {
                    yield Long.toString((long) numericValue);
                }
                yield Double.toString(numericValue);
            }
            default -> null;
        };
    }
}
