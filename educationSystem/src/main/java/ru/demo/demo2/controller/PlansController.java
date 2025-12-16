package ru.demo.demo2.controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.util.Optional;

public class PlansController {
    @FXML private TableView<DevelopmentPlan> plansTable;
    @FXML private TableColumn<DevelopmentPlan, String> idColumn, pairColumn, titleColumn, descriptionColumn, deadlineColumn;
    private final DevelopmentPlanDao planDao = new DevelopmentPlanDao();
    private final PairDao pairDao = new PairDao();
    private final ObservableList<DevelopmentPlan> list = FXCollections.observableArrayList();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().toString()));
        pairColumn.setCellValueFactory(c -> {
            Pair p = c.getValue().getPair();
            return new SimpleStringProperty(p != null ? p.getMentorFio() + " - " + p.getMenteeFio() : "");
        });
        titleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription() != null ? c.getValue().getDescription() : ""));
        deadlineColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeadline() != null ? c.getValue().getDeadline().toString() : ""));
        plansTable.setItems(list);
        load();
    }

    @FXML private void onAddClick() { showDialog(null); }
    @FXML private void onEditClick() { DevelopmentPlan p = plansTable.getSelectionModel().getSelectedItem(); if (p != null) showDialog(p); }
    @FXML private void onDeleteClick() {
        DevelopmentPlan p = plansTable.getSelectionModel().getSelectedItem();
        if (p != null && new Alert(Alert.AlertType.CONFIRMATION, "Удалить план?").showAndWait().get() == ButtonType.OK) {
            planDao.delete(p); load();
        }
    }
    @FXML private void onRefreshClick() { load(); }
    private void load() { list.clear(); list.addAll(planDao.findAll()); }

    private void showDialog(DevelopmentPlan pt) {
        Dialog<DevelopmentPlan> dlg = new Dialog<>();
        dlg.setTitle(pt == null ? "Добавить план" : "Редактировать план");
        ButtonType save = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
        ComboBox<Pair> pair = new ComboBox<>(FXCollections.observableArrayList(pairDao.findAll()));
        pair.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Pair p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getMentorFio() + " - " + p.getMenteeFio());
            }
        });
        pair.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Pair p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getMentorFio() + " - " + p.getMenteeFio());
            }
        });
        TextField title = new TextField();
        TextArea description = new TextArea(); description.setPrefRowCount(3);
        DatePicker deadline = new DatePicker();
        if (pt != null) {
            pair.setValue(pt.getPair()); title.setText(pt.getTitle());
            description.setText(pt.getDescription()); deadline.setValue(pt.getDeadline());
        }
        g.add(new Label("Пара:"), 0, 0); g.add(pair, 1, 0);
        g.add(new Label("Название:"), 0, 1); g.add(title, 1, 1);
        g.add(new Label("Описание:"), 0, 2); g.add(description, 1, 2);
        g.add(new Label("Дедлайн:"), 0, 3); g.add(deadline, 1, 3);
        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(b -> {
            if (b == save) {
                DevelopmentPlan p = pt != null ? pt : new DevelopmentPlan();
                p.setPair(pair.getValue()); p.setTitle(title.getText());
                p.setDescription(description.getText()); p.setDeadline(deadline.getValue());
                return p;
            }
            return null;
        });
        Optional<DevelopmentPlan> r = dlg.showAndWait();
        r.ifPresent(p -> {
            if (pt == null) {
                planDao.save(p);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно добавлена", ButtonType.OK).showAndWait();
            } else {
                planDao.update(p);
                new Alert(Alert.AlertType.INFORMATION, "Запись успешно обновлена", ButtonType.OK).showAndWait();
            }
            load();
        });
    }
}