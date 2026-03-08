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

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox chkAdmin;
    @FXML private Label lblMensaje;
    @FXML private Label lblWelcome;

    private User currentUser;  // admin logueado
    private User usuarioEditar; // null si es nuevo, usuario si es edicion
    private final UserService userService = new UserService();

    /**
     * currentUser = admin logueado
     * usuarioEditar = null si crear, User si editar
     */
    public void initData(User currentUser, User usuarioEditar) {
        this.currentUser = currentUser;
        this.usuarioEditar = usuarioEditar;
        lblWelcome.setText(currentUser.getName());

        if (usuarioEditar != null) {
            // Modo edicion: rellenamos los campos con los datos existentes
            txtNombre.setText(usuarioEditar.getName());
            txtApellido.setText(usuarioEditar.getLastName());
            txtEmail.setText(usuarioEditar.getEmail());
            txtTelefono.setText(usuarioEditar.getPhone() != null ? usuarioEditar.getPhone() : "");
            chkAdmin.setSelected(usuarioEditar.getIsAdmin());
        }
    }

    @FXML
    private void handleGuardar() {
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email    = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String password = txtPassword.getText().trim();
        boolean isAdmin = chkAdmin.isSelected();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty()) {
            showError("Por favor, rellena todos los campos obligatorios.");
            return;
        }

        if (usuarioEditar == null) {
            // MODO CREAR
            if (password.isEmpty()) {
                showError("La contrasena es obligatoria.");
                return;
            }
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
            // MODO EDITAR
            usuarioEditar.setName(nombre);
            usuarioEditar.setLastName(apellido);
            usuarioEditar.setEmail(email);
            usuarioEditar.setPhone(telefono.isEmpty() ? null : telefono);
            usuarioEditar.setIsAdmin(isAdmin);
            if (!password.isEmpty()) {
                usuarioEditar.setPassword(password);
            }
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

    private void showError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #27ae60;");
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}