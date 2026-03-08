package com.example.sportconnect.controller;

import com.example.sportconnect.model.Sport;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.SportService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormSportController {

    @FXML private TextField txtNombre;
    @FXML private Label     lblMensaje;
    @FXML private Label     lblWelcome;
    @FXML private Label     lblTitulo;
    @FXML private Button    btnGuardar;

    private User  currentUser;
    private Sport sportEditar;
    private final SportService sportService = new SportService();

    public void initData(User currentUser, Sport sportEditar) {
        this.currentUser = currentUser;
        this.sportEditar = sportEditar;
        lblWelcome.setText(currentUser.getName());

        if (sportEditar != null) {
            lblTitulo.setText("Editar Deporte");
            txtNombre.setText(sportEditar.getNombre());
            btnGuardar.setDisable(false);
        } else {
            btnGuardar.setDisable(true);
            txtNombre.textProperty().addListener((o, v, n) ->
                    btnGuardar.setDisable(n.trim().isEmpty()));
        }
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { showError("El nombre no puede estar vacio."); return; }

        if (sportEditar == null) {
            sportService.save(new Sport(nombre));
            showSuccess("Deporte creado correctamente.");
            txtNombre.clear();
        } else {
            sportEditar.setNombre(nombre);
            sportService.save(sportEditar);
            showSuccess("Deporte actualizado correctamente.");
        }
    }

    private void showError(String m) { lblMensaje.setText(m); lblMensaje.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String m) { lblMensaje.setText(m); lblMensaje.setStyle("-fx-text-fill: #27ae60;"); }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/sport-view.fxml"));
            Parent root = loader.load();
            SportController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}