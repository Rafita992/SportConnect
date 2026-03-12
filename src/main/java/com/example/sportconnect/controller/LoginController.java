package com.example.sportconnect.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.UserService;

public class LoginController {

    @FXML private TextField     textEmail;
    @FXML private PasswordField textPassword;
    @FXML private Label         lblMessage;
    @FXML private Button        btnLogin;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email    = textEmail.getText().trim();
        String password = textPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Por favor, rellena todos los campos.");
            return;
        }

        try {
            User user = userService.login(email, password);

            if (user != null) {
                if (user.getIsAdmin()) {
                    navigateToDashboard(user);
                } else {
                    navigateToUserHome(user);
                }
            } else {
                showError("Correo o contraseña incorrectos.");
            }

        } catch (Exception e) {
            showError("Error tecnico: No se pudo conectar con el servidor.");
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.initData(user);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Panel de administración");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) {
            showError("Error al cargar el dashboard.");
            e.printStackTrace();
        }
    }

    private void navigateToUserHome(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/user-home-view.fxml"));
            Parent root = loader.load();
            UserHomeController controller = loader.getController();
            controller.initData(user);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Inicio");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) {
            showError("Error al cargar la vista de usuario.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #27ae60;");
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/register-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            double x = stage.getX(), y = stage.getY();
            double w = stage.getWidth(), h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Registro");
            stage.setX(x); stage.setY(y);
            stage.setWidth(w); stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}