module ru.educationsystem.educationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;
    requires java.desktop;
    requires java.naming;
    requires itextpdf;
    requires org.hibernate.validator;

    opens ru.educationsystem.educationsystem to javafx.fxml, javafx.controls;
    opens ru.educationsystem.educationsystem.model to org.hibernate.orm.core;
    opens ru.educationsystem.educationsystem.repository to org.hibernate.orm.core;
    opens ru.educationsystem.educationsystem.controller to javafx.fxml, javafx.controls;

    exports ru.educationsystem.educationsystem;
    exports ru.educationsystem.educationsystem.model;
    exports ru.educationsystem.educationsystem.repository;
    exports ru.educationsystem.educationsystem.service;
    exports ru.educationsystem.educationsystem.controller;
    exports ru.educationsystem.educationsystem.util;
}