package com.example.sportconnect.controller;

import com.example.sportconnect.model.User;
import com.example.sportconnect.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField     txtNombre;
    @FXML private TextField     txtApellido;
    @FXML private TextField     txtEmail;
    @FXML private TextField     txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtPasswordConfirm;
    @FXML private Button        btnRegistrar;
    @FXML private Label         lblMensaje;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        Runnable check = () -> {
            boolean ok = !txtNombre.getText().trim().isEmpty()
                    && !txtApellido.getText().trim().isEmpty()
                    && !txtEmail.getText().trim().isEmpty()
                    && !txtPassword.getText().trim().isEmpty()
                    && !txtPasswordConfirm.getText().trim().isEmpty();
            btnRegistrar.setDisable(!ok);
        };
        txtNombre.textProperty().addListener((o, v, n) -> check.run());
        txtApellido.textProperty().addListener((o, v, n) -> check.run());
        txtEmail.textProperty().addListener((o, v, n) -> check.run());
        txtPassword.textProperty().addListener((o, v, n) -> check.run());
        txtPasswordConfirm.textProperty().addListener((o, v, n) -> check.run());
    }

    @FXML
    private void handleRegistrar() {
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String email     = txtEmail.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String password  = txtPassword.getText().trim();
        String password2 = txtPasswordConfirm.getText().trim();

        if (!password.equals(password2)) {
            showError("Las contraseñas no coinciden.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError("El correo electrónico no es válido.");
            return;
        }

        User nuevoUsuario = new User(nombre, apellido, email, password, false);
        if (!telefono.isEmpty()) nuevoUsuario.setPhone(telefono);

        boolean guardado = userService.registerNewUser(nuevoUsuario);
        if (guardado) {
            showSuccess("Cuenta creada correctamente. Ya puedes iniciar sesión.");
            limpiarFormulario();
        } else {
            showError("Ese correo electrónico ya está registrado.");
        }
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            double x = stage.getX(), y = stage.getY();
            double w = stage.getWidth(), h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Iniciar Sesión");
            stage.setX(x); stage.setY(y);
            stage.setWidth(w); stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void limpiarFormulario() {
        txtNombre.clear(); txtApellido.clear(); txtEmail.clear();
        txtTelefono.clear(); txtPassword.clear(); txtPasswordConfirm.clear();
    }

    private void showError(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.setStyle("-fx-text-fill: #27ae60;");
    }
}