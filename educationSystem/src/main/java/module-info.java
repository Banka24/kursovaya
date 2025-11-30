module ru.educationsystem.educationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires java.sql;
    requires java.desktop;
    requires itextpdf;

    opens ru.educationsystem.educationsystem to javafx.fxml;
    opens ru.educationsystem.educationsystem.model to org.hibernate.orm.core;
    opens ru.educationsystem.educationsystem.repository to org.hibernate.orm.core;

    exports ru.educationsystem.educationsystem;
    exports ru.educationsystem.educationsystem.controller;
    exports ru.educationsystem.educationsystem.service;
    exports ru.educationsystem.educationsystem.model;
    exports ru.educationsystem.educationsystem.repository;
    exports ru.educationsystem.educationsystem.util;

    opens ru.educationsystem.educationsystem.controller to javafx.fxml;
}
