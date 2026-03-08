package com.example.sportconnect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.sportconnect.util.HibernateUtil;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        String fxmlPath = "/com/example/sportconnect/fxml/login-view.fxml";
        URL fxmlLocation = getClass().getResource(fxmlPath);

        if (fxmlLocation == null) {
            System.err.println("ERROR: No se pudo encontrar el archivo FXML.");
            throw new IllegalStateException("La ubicacion del FXML no esta configurada.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);

        try {
            Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
            stage.setTitle("SPORTCONNECT - Iniciar Sesion");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(550);
            stage.show();
            System.out.println("Aplicacion iniciada correctamente.");
        } catch (IOException e) {
            System.err.println("ERROR al cargar el archivo FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Cerrando conexion con la base de datos...");
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}