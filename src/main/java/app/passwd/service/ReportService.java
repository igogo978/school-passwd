package app.passwd.service;

import app.passwd.model.UserImageItem;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    public Table getFilelistTable(PdfFont font, List<UserImageItem> items) throws IOException {

        List<String> tableHeader = new ArrayList<>();
        tableHeader.add("序");
        tableHeader.add("檔案名稱");
        tableHeader.add("建立者");
        tableHeader.add("宣導期限");
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 6, 1, 4})).useAllAvailableWidth();

        tableHeader.forEach(name -> {
            Cell header = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font)
                    .setFontSize(12)
                    .setPadding(0)
                    .setMarginRight(0)
                    .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 0.9f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(name));
            table.addCell(header);

        });



        for (int i = 0; i < items.size(); i++) {
            Cell sn = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(0)
                    .setMarginRight(0)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(String.valueOf(i + 1)));
            table.addCell(sn);

            Cell filename = new Cell()
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(items.get(i).getDescription()));
            table.addCell(filename);

            Cell owner = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(items.get(i).getUsername()));
            table.addCell(owner);


            Instant instant = Instant.ofEpochSecond(items.get(i).getExpired());

            ZonedDateTime present = instant.atZone(ZoneId.of("Asia/Taipei"));  //taipei時區
            String displayDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(present);

            Cell date = new Cell()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font)
                    .setFontSize(11)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(displayDate));
            table.addCell(date);

        }

        return table;
    }
}
