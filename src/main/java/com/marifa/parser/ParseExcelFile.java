package com.marifa.parser;

import com.cybozu.labs.langdetect.LangDetectException;
import com.marifa.mappings.MapColumnToProperty;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
 * The ParseExcelFile class parses the excel file and create a pre-formatted array
 * that contains all the columns mapped to the bibo ontology properties..
 * @param: inputFile the file used to extract the data.
 */
public class ParseExcelFile {

    private static ArrayList fileData = new ArrayList();
    private String inputFile, namespacePrefix;
    private MapColumnToProperty mapColumnToProperty;

    public ParseExcelFile(String inputFile, String namespacePrefix) {
        this.inputFile = inputFile;
        this.namespacePrefix = namespacePrefix;
    }

    public static ArrayList getFileData(){
        return fileData;
    }

    public static void main(String[] args) throws IOException, LangDetectException {
        /*ParseExcelFile test = new ParseExcelFile("C:\\Users\\AHMED\\Desktop\\pub.xls");
        test.parse();*/
    }

    public void parse() throws IOException, LangDetectException {
        File inputWorkbook = new File(inputFile);
        Workbook workbook;
        try {
            workbook = Workbook.getWorkbook(inputWorkbook);

            // Get the first sheet
            Sheet sheet = workbook.getSheet(0);

            //Loop over the first row and map the columns' headers.
            Cell cell;
            for (int columnsIndex = sheet.getColumns() - 1 ; columnsIndex >= 0; columnsIndex--){
                cell = sheet.getCell(columnsIndex, 0);
                fileData.add(cell.getContents());
            }
            // Loop over first the whole content of the excel file
            // Extract the content and then append it an array (formatted)
            /*for (int i = 0; i < sheet.getRows(); i++) {
                for (int j = sheet.getColumns() - 1; j >= 0; j--) {
                    cell = sheet.getCell(j, i);
                    fileData.add(cell.getContents());
                    *//*CellType type = cell.getType();
                    if (type == CellType.LABEL) {
                        System.out.println("I got a label "
                                + cell.getContents());
                    }

                    if (type == CellType.NUMBER) {
                        System.out.println("I got a number "
                                + cell.getContents());
                    }*//*
                }
            }*/
            //printContent(fileData);
            mapColumnToProperty = new MapColumnToProperty(fileData, namespacePrefix);
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    private void printContent(ArrayList list){
        for (int i=0; i < list.size(); i++){
            System.out.println(list.get(i).toString());
        }
    }

}