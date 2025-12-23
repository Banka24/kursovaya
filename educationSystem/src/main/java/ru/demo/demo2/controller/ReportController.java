package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import ru.demo.demo2.service.ProgressTrackingService;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import java.io.File;
import java.io.FileOutputStream;

public class ReportController {
    @FXML private TableView<Pair> pairsTable;
    @FXML private TableColumn<Pair, String> idColumn, mentorColumn, menteeColumn, statusColumn, meetingsColumn, plansColumn, avgRatingColumn;
    @FXML private Label summaryLabel;
    private final PairDao pairDao = new PairDao();
    private final ProgressTrackingService progressService = new ProgressTrackingService();
    private final ObservableList<Pair> list = FXCollections.observableArrayList();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        mentorColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMentorFio()));
        menteeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMenteeFio()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(getStatusText(c.getValue().getStatus())));
        meetingsColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(progressService.getMeetingsCount(c.getValue().getId()))));
        plansColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(progressService.getPlansCount(c.getValue().getId()))));
        avgRatingColumn.setCellValueFactory(c -> {
            Double avg = progressService.getAverageMentorRating(c.getValue().getId());
            return new SimpleStringProperty(String.format("%.1f", avg));
        });
        pairsTable.setItems(list);
        load();
    }

    private String getStatusText(String status) {
        if ("active".equals(status)) return "Активна";
        if ("paused".equals(status)) return "Приостановлена";
        if ("completed".equals(status)) return "Завершена";
        return status;
    }

    @FXML
    private void onRefreshClick() {
        load();
    }
    
    @FXML
    private void onExportToPdfClick() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить отчет в PDF");
            fileChooser.setInitialFileName("report.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showSaveDialog(pairsTable.getScene().getWindow());
            
            if (file != null) {
                exportToPdf(file.getAbsolutePath());
                showInfo("Отчет успешно экспортирован в PDF");
            }
        } catch (Exception e) {
            showError("Ошибка при экспорте в PDF: " + e.getMessage());
        }
    }

    private void load() {
        try {
            list.clear();
            list.addAll(pairDao.findAll());
            long active = list.stream().filter(p -> "active".equals(p.getStatus())).count();
            long completed = list.stream().filter(p -> "completed".equals(p.getStatus())).count();
            int totalMeetings = 0;
            int totalPlans = 0;
            for (Pair p : list) {
                totalMeetings += progressService.getMeetingsCount(p.getId());
                totalPlans += progressService.getPlansCount(p.getId());
            }
            summaryLabel.setText("Всего пар: " + list.size() + ", активных: " + active + ", завершенных: " +
                    completed + ", встреч: " + totalMeetings + ", планов: " + totalPlans);

        } catch (Exception e) {
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
    
    private void exportToPdf(String filePath) throws Exception {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        var fontStream = getClass().getResourceAsStream("/fonts/arial.ttf");
        if (fontStream == null) {
            throw new Exception("Не найден файл шрифта /fonts/arial.ttf");
        }
        byte[] fontBytes = fontStream.readAllBytes();
        PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
        document.setFont(font);

        Paragraph title = new Paragraph("Отчет: Эффективность наставничества")
            .setFontSize(18)
            .setBold();
        document.add(title);

        long active = list.stream().filter(p -> "active".equals(p.getStatus())).count();
        long completed = list.stream().filter(p -> "completed".equals(p.getStatus())).count();
        int totalMeetings = 0;
        int totalPlans = 0;
        for (Pair p : list) {
            totalMeetings += progressService.getMeetingsCount(p.getId());
            totalPlans += progressService.getPlansCount(p.getId());
        }
        
        Paragraph summary = new Paragraph(
            "Всего пар: " + list.size() + ", активных: " + active + ", завершенных: " + completed + 
            ", встреч: " + totalMeetings + ", планов: " + totalPlans
        ).setFontSize(12);
        document.add(summary);
        document.add(new Paragraph("\n"));
        
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 2, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Paragraph("ID").setBold());
        table.addHeaderCell(new Paragraph("Наставник").setBold());
        table.addHeaderCell(new Paragraph("Подопечный").setBold());
        table.addHeaderCell(new Paragraph("Статус").setBold());
        table.addHeaderCell(new Paragraph("Встреч").setBold());
        table.addHeaderCell(new Paragraph("Планов").setBold());
        table.addHeaderCell(new Paragraph("Ср. рейтинг").setBold());

        for (Pair pair : list) {
            table.addCell(new Paragraph(pair.getId().toString()));
            table.addCell(new Paragraph(pair.getMentorFio()));
            table.addCell(new Paragraph(pair.getMenteeFio()));
            table.addCell(new Paragraph(getStatusText(pair.getStatus())));
            table.addCell(new Paragraph(String.valueOf(progressService.getMeetingsCount(pair.getId()))));
            table.addCell(new Paragraph(String.valueOf(progressService.getPlansCount(pair.getId()))));
            Double avg = progressService.getAverageMentorRating(pair.getId());
            table.addCell(new Paragraph(String.format("%.1f", avg)));
        }
        
        document.add(table);
        document.close();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}