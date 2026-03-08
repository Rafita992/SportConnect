package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.Sport;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.CourtService;
import com.example.sportconnect.service.SportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class FormCourtController {

    @FXML private TextField       txtNombre;
    @FXML private ComboBox<Sport> cmbDeporte;
    @FXML private Label           lblMensaje;
    @FXML private Label           lblWelcome;
    @FXML private Label           lblTitulo;
    @FXML private Button          btnGuardar;

    private User  currentUser;
    private Court courtEditar;
    private final CourtService courtService = new CourtService();
    private final SportService sportService = new SportService();

    public void initData(User currentUser, Court courtEditar) {
        this.currentUser = currentUser;
        this.courtEditar = courtEditar;
        lblWelcome.setText(currentUser.getName());

        cargarDeportes();

        if (courtEditar != null) {
            lblTitulo.setText("Editar Pista");
            txtNombre.setText(courtEditar.getNombre());
            cmbDeporte.setValue(courtEditar.getSport());
            btnGuardar.setDisable(false);
        } else {
            btnGuardar.setDisable(true);
            setupValidacion();
        }
    }

    private void setupValidacion() {
        Runnable check = () -> {
            boolean ok = !txtNombre.getText().trim().isEmpty()
                    && cmbDeporte.getValue() != null;
            btnGuardar.setDisable(!ok);
        };
        txtNombre.textProperty().addListener((o, v, n) -> check.run());
        cmbDeporte.valueProperty().addListener((o, v, n) -> check.run());
    }

    private void cargarDeportes() {
        List<Sport> deportes = sportService.getAllSports();
        cmbDeporte.setItems(FXCollections.observableArrayList(deportes));
        cmbDeporte.setConverter(new StringConverter<>() {
            public String toString(Sport s) { return s != null ? s.getNombre() : ""; }
            public Sport fromString(String s) { return null; }
        });
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        Sport deporte = cmbDeporte.getValue();
        if (nombre.isEmpty() || deporte == null) { showError("Por favor, rellena todos los campos."); return; }

        if (courtEditar == null) {
            courtService.save(new Court(nombre, deporte));
            showSuccess("Pista creada correctamente.");
            limpiarFormulario();
        } else {
            courtEditar.setNombre(nombre);
            courtEditar.setSport(deporte);
            courtService.save(courtEditar);
            showSuccess("Pista actualizada correctamente.");
        }
    }

    private void limpiarFormulario() { txtNombre.clear(); cmbDeporte.setValue(null); }
    private void showError(String m) { lblMensaje.setText(m); lblMensaje.setStyle("-fx-text-fill: #e74c3c;"); }
    private void showSuccess(String m) { lblMensaje.setText(m); lblMensaje.setStyle("-fx-text-fill: #27ae60;"); }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/court-view.fxml"));
            Parent root = loader.load();
            CourtController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}