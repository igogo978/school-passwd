package app.passwd.service;

import app.passwd.model.LearningAccount;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ReadLearningAccount {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public List<LearningAccount> readxls(File file) throws IOException, InvalidFormatException {
        List<LearningAccount> accounts = new ArrayList<>();

        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fis);

        // Return first sheet from the XLSX  workbook
        Sheet sheet = workbook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = sheet.iterator();
        LearningAccount account;
        //讀列
        while (rowIterator.hasNext()) {
            account = new LearningAccount();
            Row row = rowIterator.next();

            //讀欄
            // For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cell.setCellType(CellType.STRING);

                switch (cell.getColumnIndex()) {
                    case 0:    //第一個欄位, 班級
                        account.setClassname(cell.getStringCellValue());
                        break;
                    case 1:    //第2個欄位, 班級
                        account.setSeatno(cell.getStringCellValue());
                        break;
                    case 2:    //第一個欄位, 班級
                        byte[] byteArray = cell.getStringCellValue().getBytes(Charset.forName("UTF-8"));
                        account.setName(new String(byteArray, "UTF-8"));
                        break;
                    case 3:    //第一個欄位, 班級
                        account.setLearningaccount(cell.getStringCellValue());
                        break;
                    default:

                }


            }
            accounts.add(account);
        }

        return accounts;

    }

    public List<LearningAccount> readods(File file) throws IOException {
        List<LearningAccount> learningaccounts = new ArrayList<>();
        LearningAccount account;
        final org.jopendocument.dom.spreadsheet.Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);
        FileInputStream fis = new FileInputStream(file);

        for (int i = 0; i < sheet.getRowCount(); i++) {
            account = new LearningAccount();
//           logger.info(sheet.getImmutableCellAt(0,i).getTextValue());
            account.setClassname(sheet.getImmutableCellAt(0, i).getTextValue());
            account.setSeatno(sheet.getImmutableCellAt(1, i).getTextValue());
            account.setName(sheet.getImmutableCellAt(2, i).getTextValue());
            account.setLearningaccount(sheet.getImmutableCellAt(3, i).getTextValue());

            learningaccounts.add(account);
        }
        return learningaccounts;

    }


}
