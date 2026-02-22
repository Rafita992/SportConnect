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
        // 1. Definimos la ruta exacta basada en tu estructura
        // El "/" inicial es fundamental para que busque desde la raíz de resources
        String fxmlPath = "/com/example/sportconnect/fxml/login-view.fxml";
        URL fxmlLocation = getClass().getResource(fxmlPath);

        // 2. Verificación de seguridad para evitar el error "Location is not set"
        if (fxmlLocation == null) {
            System.err.println(" ERROR: No se pudo encontrar el archivo FXML.");
            System.err.println("Ruta buscada: " + fxmlPath);
            System.err.println("Asegúrate de que el archivo esté en: src/main/resources/fxml/login-view.fxml");
            throw new IllegalStateException("La ubicación del FXML no está configurada (Location is not set).");
        }

        // 3. Carga del archivo FXML
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);

        try {
            Scene scene = new Scene(fxmlLoader.load(), 400, 500);
            stage.setTitle("SPORTCONNECT - Iniciar Sesión");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            System.out.println(" Aplicación iniciada correctamente.");
        } catch (IOException e) {
            System.err.println(" ERROR al cargar el archivo FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // 4. Cerramos Hibernate de forma segura al salir
        System.out.println("Cerrando conexión con la base de datos...");
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch();
    }
}