package com.example.sportconnect.controller;

import com.example.sportconnect.model.User;
import com.example.sportconnect.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormUserController {

    @FXML private TextField     txtNombre;
    @FXML private TextField     txtApellido;
    @FXML private TextField     txtEmail;
    @FXML private TextField     txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox      chkAdmin;
    @FXML private Label         lblMensaje;
    @FXML private Label         lblWelcome;
    @FXML private Button        btnGuardar;

    private User currentUser;
    private User usuarioEditar;
    private final UserService userService = new UserService();

    public void initData(User currentUser, User usuarioEditar) {
        this.currentUser  = currentUser;
        this.usuarioEditar = usuarioEditar;
        lblWelcome.setText(currentUser.getName());

        if (usuarioEditar != null) {
            // Modo edicion: rellenar campos y habilitar boton
            txtNombre.setText(usuarioEditar.getName());
            txtApellido.setText(usuarioEditar.getLastName());
            txtEmail.setText(usuarioEditar.getEmail());
            txtTelefono.setText(usuarioEditar.getPhone() != null ? usuarioEditar.getPhone() : "");
            chkAdmin.setSelected(usuarioEditar.getIsAdmin());
            btnGuardar.setDisable(false);
        } else {
            // Modo crear: boton deshabilitado, activar listeners
            btnGuardar.setDisable(true);
            setupValidacion();
        }
    }

    private void setupValidacion() {
        Runnable check = () -> {
            boolean ok = !txtNombre.getText().trim().isEmpty()
                    && !txtApellido.getText().trim().isEmpty()
                    && !txtEmail.getText().trim().isEmpty()
                    && !txtPassword.getText().trim().isEmpty();
            btnGuardar.setDisable(!ok);
        };
        txtNombre.textProperty().addListener((o, v, n) -> check.run());
        txtApellido.textProperty().addListener((o, v, n) -> check.run());
        txtEmail.textProperty().addListener((o, v, n) -> check.run());
        txtPassword.textProperty().addListener((o, v, n) -> check.run());
    }

    @FXML
    private void handleGuardar() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email    = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String password = txtPassword.getText().trim();
        boolean isAdmin = chkAdmin.isSelected();

        if (usuarioEditar == null) {
            User newUser = new User(nombre, apellido, email, password, isAdmin);
            if (!telefono.isEmpty()) newUser.setPhone(telefono);
            boolean guardado = userService.registerNewUser(newUser);
            if (guardado) {
                showSuccess("Usuario creado correctamente.");
                limpiarFormulario();
            } else {
                showError("El email ya esta en uso.");
            }
        } else {
            usuarioEditar.setName(nombre);
            usuarioEditar.setLastName(apellido);
            usuarioEditar.setEmail(email);
            usuarioEditar.setPhone(telefono.isEmpty() ? null : telefono);
            usuarioEditar.setIsAdmin(isAdmin);
            if (!password.isEmpty()) usuarioEditar.setPassword(password);
            userService.saveUser(usuarioEditar);
            showSuccess("Usuario actualizado correctamente.");
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtPassword.clear();
        chkAdmin.setSelected(false);
    }

    private void showError(String m) { lblMensaje.setText(m); lblMensaje.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String m) { lblMensaje.setText(m); lblMensaje.setStyle("-fx-text-fill: #27ae60;"); }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/user-view.fxml"));
            Parent root = loader.load();
            UserController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}