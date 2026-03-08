package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.CourtService;
import com.example.sportconnect.service.ReservationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class UserFormReservationController {

    @FXML private ComboBox<Court> cmbPista;
    @FXML private DatePicker      dateFecha;
    @FXML private TextField       txtInicio;
    @FXML private TextField       txtFin;
    @FXML private Label           lblMensaje;
    @FXML private Label           lblWelcome;

    private User currentUser;
    private final ReservationService reservationService = new ReservationService();
    private final CourtService       courtService       = new CourtService();
    private final DateTimeFormatter  timeFormatter      = DateTimeFormatter.ofPattern("HH:mm");

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        cargarPistas();
        // No permitir fechas pasadas
        dateFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void cargarPistas() {
        List<Court> pistas = courtService.getAllCourts();
        cmbPista.setItems(FXCollections.observableArrayList(pistas));
        cmbPista.setConverter(new StringConverter<>() {
            public String toString(Court c) { return c != null ? c.getNombre() + " (" + c.getSport().getNombre() + ")" : ""; }
            public Court fromString(String s) { return null; }
        });
    }

    @FXML
    private void handleReservar() {
        Court pista     = cmbPista.getValue();
        LocalDate fecha = dateFecha.getValue();
        String inicio   = txtInicio.getText().trim();
        String fin      = txtFin.getText().trim();

        if (pista == null || fecha == null || inicio.isEmpty() || fin.isEmpty()) {
            showError("Por favor, rellena todos los campos.");
            return;
        }

        LocalTime horaInicio, horaFin;
        try {
            horaInicio = LocalTime.parse(inicio, timeFormatter);
            horaFin    = LocalTime.parse(fin, timeFormatter);
        } catch (DateTimeParseException e) {
            showError("Formato de hora incorrecto. Usa HH:mm (ej: 10:00).");
            return;
        }

        if (!horaFin.isAfter(horaInicio)) {
            showError("La hora de fin debe ser posterior a la de inicio.");
            return;
        }

        Reservation nueva = new Reservation(fecha, horaInicio, horaFin, false, pista, currentUser);
        reservationService.save(nueva);
        showSuccess("Reserva creada correctamente.");
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        cmbPista.setValue(null);
        dateFecha.setValue(null);
        txtInicio.clear();
        txtFin.clear();
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
                    "/com/example/sportconnect/fxml/user-home-view.fxml"));
            Parent root = loader.load();
            UserHomeController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) txtInicio.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}