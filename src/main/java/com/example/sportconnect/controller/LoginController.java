package com.example.sportconnect.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.UserService;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    // Instanciamos el servicio (en proyectos más grandes usarías Inyección de Dependencias)
    private final UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // 1. Validación básica de UI
        if (email.isEmpty() || password.isEmpty()) {
            showError("Por favor, rellena todos los campos.");
            return;
        }

        try {
            // 2. Llamada al Service (Lógica de negocio)
            User user = userService.login(email, password);

            if (user != null) {
                showSuccess("¡Bienvenido, " + user.getName() + "!");

                // 3. Aquí llamarías a un método para cambiar de ventana
                // navigateToDashboard(user);

            } else {
                showError("Correo o contraseña incorrectos.");
            }

        } catch (Exception e) {
            // Gestión de errores de conexión o base de datos
            showError("Error técnico: No se pudo conectar con el servidor.");
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para limpiar el código del controlador
    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #e74c3c;"); // Rojo
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #27ae60;"); // Verde
    }
}