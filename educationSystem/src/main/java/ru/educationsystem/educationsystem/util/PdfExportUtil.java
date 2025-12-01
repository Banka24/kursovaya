package ru.educationsystem.educationsystem.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfExportUtil {

    public static void exportReportToPdf(String content, String filePath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Заголовок отчета
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("ОТЧЕТ СИСТЕМЫ НАСТАВНИЧЕСТВА", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Содержимое отчета
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        String[] lines = content.split("");

        for (String line : lines) {
            Paragraph paragraph = new Paragraph(line, contentFont);
            paragraph.setSpacingAfter(5);

            if (line.startsWith("ОТЧЕТ:") || line.startsWith("Период:") || line.startsWith("Направление:")) {
                paragraph.getFont().setStyle(Font.BOLD);
            }

            document.add(paragraph);
        }

        // Дата создания отчета
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
        Paragraph date = new Paragraph("Дата создания: " + java.time.LocalDate.now(), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingBefore(20);
        document.add(date);

        document.close();
    }
}