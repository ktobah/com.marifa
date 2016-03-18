package com.marifa.parser;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by AHMED on 1/10/2016.
 */
public class ParseExcelFile {

    private static HSSFWorkbook workbook;
    private static HSSFSheet sheet;

    public ParseExcelFile(){

        try {
            FileInputStream excelFile = new FileInputStream(new File("C:\\Users\\AHMED\\Desktop\\pub.xls"));
            workbook = new HSSFWorkbook(excelFile);
            sheet = workbook.getSheetAt(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HSSFSheet getSheet(){
        return sheet;
    }
}
