package com.marifa.parser;

import com.cybozu.labs.langdetect.LangDetectException;
import com.marifa.mappings.MapColumnToProperty;
import com.marifa.mappings.Mappings;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/*
 * The ExcelParser class parses the excel file and create a pre-formatted array
 * that contains all the columns mapped to the bibo ontology properties.
 */
public class ExcelParser {

    private static ArrayList columnData = new ArrayList();
    private static String[][] fileData;
    private String inputFile, namespacePrefix;
    private MapColumnToProperty mapColumnToProperty;
    private String[] mappedColumns;

    public ExcelParser(String inputFile, String namespacePrefix) {
        this.inputFile = inputFile;
        this.namespacePrefix = namespacePrefix;
    }

    public static String[][] getParsedData() {
        return fileData;
    }

    public void parse() throws IOException, LangDetectException, URISyntaxException {
        File inputWorkbook = new File(inputFile);
        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = workbook.getSheet(0);
            //Loop over the first row and map the columns' headers.
            Cell cell;
            int columnsIndex;
            for (columnsIndex = sheet.getColumns() - 1; columnsIndex >= 0; columnsIndex--) {
                cell = sheet.getCell(columnsIndex, 0);
                columnData.add(cell.getContents());
            }
            mapColumnToProperty = new MapColumnToProperty(columnData, namespacePrefix);
            mappedColumns = mapColumnToProperty.getMappedColumns();

            // Loop over the whole content of the excel file
            // Extract the content and then append it an array (formatted)
            int mappedColumnsIndex = 0, extractedPropertiesPerRow = 0, ignoredCells = 0, totalnumber = 0;
            String content;
            fileData = new String[sheet.getRows() - 1][sheet.getColumns()];
            for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++) {
                for (columnsIndex = sheet.getColumns() - 1; columnsIndex >= 0; columnsIndex--) {
                    cell = sheet.getCell(columnsIndex, rowIndex);
                    content = cell.getContents();
                    if (!content.isEmpty() && content != null) {
                        fileData[rowIndex - 1][columnsIndex] = mappedColumns[mappedColumnsIndex] + "/*" + content;
                        extractedPropertiesPerRow++;
                        mappedColumnsIndex++;
                        totalnumber++;
                    } else {
                        fileData[rowIndex - 1][columnsIndex] = "";
                        mappedColumnsIndex++;
                        ignoredCells++;
                        totalnumber++;
                    }
                }
                Mappings.logMessage(Level.INFO, "Total extracted properties for  row " + rowIndex + ": " + extractedPropertiesPerRow);
                extractedPropertiesPerRow = 0;
                mappedColumnsIndex = 0;
            }
            Mappings.logMessage(Level.INFO, "**************************************");
            Mappings.logMessage(Level.INFO, "Total Number of cells: " + totalnumber);
            Mappings.logMessage(Level.INFO, "Total Number of used cells: " + (totalnumber - ignoredCells));
            Mappings.logMessage(Level.INFO, "Total Number of ignored cells: " + ignoredCells);
            Mappings.logMessage(Level.INFO, "**************************************");
        } catch (BiffException e) {
            Mappings.logMessage(Level.FATAL, e.getMessage());
        }
    }
}