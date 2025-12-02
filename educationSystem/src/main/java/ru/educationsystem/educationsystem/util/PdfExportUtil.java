package ru.educationsystem.educationsystem.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class PdfExportUtil {

    public static void exportReportToPdf(String content, String filePath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Загрузка русского шрифта
        String fontPath = "src/main/resources/fonts/arial.ttf";
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        
        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        Font contentFont = new Font(baseFont, 12, Font.NORMAL);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);
        Font dateFont = new Font(baseFont, 10, Font.ITALIC);

        // Заголовок отчета
        Paragraph title = new Paragraph("ОТЧЕТ СИСТЕМЫ НАСТАВНИЧЕСТВА", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Содержимое отчета
        String[] lines = content.split("\\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                document.add(new Paragraph(" "));
                continue;
            }
            
            Font lineFont = contentFont;
            if (line.startsWith("ОТЧЕТ:") || line.startsWith("Период:") || line.startsWith("Направление:")) {
                lineFont = boldFont;
            }
            
            Paragraph paragraph = new Paragraph(line, lineFont);
            paragraph.setSpacingAfter(5);
            document.add(paragraph);
        }

        // Дата создания отчета
        Paragraph date = new Paragraph("Дата создания: " + java.time.LocalDate.now(), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingBefore(20);
        document.add(date);

        document.close();
    }
}