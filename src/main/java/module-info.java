module org.example.sportconnect {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.naming;

    // Módulos para Hibernate 6 y persistencia moderna
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires org.mariadb.jdbc;
    requires net.bytebuddy;

    // Apertura de paquetes para que JavaFX e Hibernate puedan acceder a tus clases
    opens com.example.sportconnect.model to org.hibernate.orm.core;
    opens com.example.sportconnect.dao to org.hibernate.orm.core;
    opens com.example.sportconnect.controller to javafx.fxml;
    opens com.example.sportconnect.util to org.hibernate.orm.core;

    exports com.example.sportconnect;
}