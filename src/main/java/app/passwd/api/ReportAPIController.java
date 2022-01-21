package app.passwd.api;

import app.passwd.service.ReportService;
import app.passwd.service.UseritemService;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class ReportAPIController {


    private final Logger logger = LoggerFactory.getLogger(ReportAPIController.class);
    @Autowired
    ReportService reportService;

    @Autowired
    @Lazy
    UseritemService useritemService;

    @ResponseBody
    @GetMapping("/api/report")
    public ResponseEntity<Resource> getReport() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        Instant instant = Instant.now();
        ZonedDateTime nowPresent = instant.atZone(ZoneId.of("Asia/Taipei"));  //taipei時區
        String updateDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(nowPresent);


        FontProgram fontProgram = FontProgramFactory.createFont("/opt/font/kai.ttf");
        PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H);

        document.add(new Paragraph().add(
                        new Text("更新時間: " + updateDate)
                                .setFont(font)
                                .setFontSize(10)
                ).setTextAlignment(TextAlignment.LEFT)
        );


        Table table = reportService.getFilelistTable(font, useritemService.getUseritemsEnabled(instant.getEpochSecond()));
        document.add(table);

        document.close();
        logger.info("pdf done");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reportlpdf");
        ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }
}
