package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.CourtService;
import com.example.sportconnect.service.ReservationService;
import com.example.sportconnect.service.UserService;
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

public class FormReservationController {

    @FXML private ComboBox<User>  cmbUsuario;
    @FXML private ComboBox<Court> cmbPista;
    @FXML private DatePicker      dateFecha;
    @FXML private TextField       txtInicio;
    @FXML private TextField       txtFin;
    @FXML private Label           lblMensaje;
    @FXML private Label           lblWelcome;
    @FXML private Label           lblTitulo;

    private User currentUser;
    private Reservation reservacionEditar;

    private final ReservationService reservationService = new ReservationService();
    private final UserService        userService        = new UserService();
    private final CourtService       courtService       = new CourtService();

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * currentUser = admin logueado
     * reservacionEditar = null si crear, Reservation si editar
     */
    public void initData(User currentUser, Reservation reservacionEditar) {
        this.currentUser = currentUser;
        this.reservacionEditar = reservacionEditar;
        lblWelcome.setText(currentUser.getName());

        cargarCombos();

        if (reservacionEditar != null) {
            lblTitulo.setText("Editar Reserva");
            cmbUsuario.setValue(reservacionEditar.getUser());
            cmbPista.setValue(reservacionEditar.getCourt());
            dateFecha.setValue(reservacionEditar.getBookingDate());
            txtInicio.setText(reservacionEditar.getStartTime().format(timeFormatter));
            txtFin.setText(reservacionEditar.getEndTime().format(timeFormatter));
        }
    }

    private void cargarCombos() {
        // Cargar usuarios
        List<User> usuarios = userService.getAllUsers();
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarios));
        cmbUsuario.setConverter(new StringConverter<>() {
            public String toString(User u) { return u != null ? u.getName() + " " + u.getLastName() : ""; }
            public User fromString(String s) { return null; }
        });

        // Cargar pistas
        List<Court> pistas = courtService.getAllCourts();
        cmbPista.setItems(FXCollections.observableArrayList(pistas));
        cmbPista.setConverter(new StringConverter<>() {
            public String toString(Court c) { return c != null ? c.getNombre() : ""; }
            public Court fromString(String s) { return null; }
        });
    }

    @FXML
    private void handleGuardar() {
        User usuario   = cmbUsuario.getValue();
        Court pista    = cmbPista.getValue();
        LocalDate fecha = dateFecha.getValue();
        String inicio  = txtInicio.getText().trim();
        String fin     = txtFin.getText().trim();

        // Validacion
        if (usuario == null || pista == null || fecha == null || inicio.isEmpty() || fin.isEmpty()) {
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

        if (reservacionEditar == null) {
            // MODO CREAR
            Reservation nueva = new Reservation(fecha, horaInicio, horaFin, false, pista, usuario);
            reservationService.save(nueva);
            showSuccess("Reserva creada correctamente.");
            limpiarFormulario();
        } else {
            // MODO EDITAR
            reservacionEditar.setUser(usuario);
            reservacionEditar.setCourt(pista);
            reservacionEditar.setBookingDate(fecha);
            reservacionEditar.setStartTime(horaInicio);
            reservacionEditar.setEndTime(horaFin);
            reservationService.save(reservacionEditar);
            showSuccess("Reserva actualizada correctamente.");
        }
    }

    private void limpiarFormulario() {
        cmbUsuario.setValue(null);
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
                    "/com/example/sportconnect/fxml/reservation-view.fxml"));
            Parent root = loader.load();
            ReservationController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) txtInicio.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}