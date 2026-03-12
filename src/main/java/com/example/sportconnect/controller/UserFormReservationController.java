package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.CourtService;
import com.example.sportconnect.service.ReservationService;
import com.example.sportconnect.component.TimeSlotPicker;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.DateCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class UserFormReservationController {

    @FXML private ComboBox<Court> cmbPista;
    @FXML private DatePicker      dateFecha;
    @FXML private VBox            slotContainer;
    @FXML private Label           lblMensaje;
    @FXML private Label           lblWelcome;
    @FXML private Button          btnGuardar;

    private User currentUser;
    private TimeSlotPicker slotPicker;

    private final ReservationService reservationService = new ReservationService();
    private final CourtService       courtService       = new CourtService();

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());

        slotPicker = new TimeSlotPicker();
        slotContainer.getChildren().add(slotPicker);

        btnGuardar.setDisable(true);
        cargarPistas();
        configurarListeners();

        dateFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void configurarListeners() {
        cmbPista.setOnAction(e -> actualizarFranjas());
        dateFecha.setOnAction(e -> actualizarFranjas());
        slotPicker.setOnSelectionChanged(() -> btnGuardar.setDisable(slotPicker.getSelectedSlot() == null));
    }

    private void actualizarFranjas() {
        Court pista     = cmbPista.getValue();
        LocalDate fecha = dateFecha.getValue();
        if (pista == null || fecha == null) return;

        List<Reservation> reservasDelDia = reservationService.getAllReservations()
                .stream()
                .filter(r -> r.getCourt().getId().equals(pista.getId())
                        && r.getBookingDate().equals(fecha))
                .toList();

        slotPicker.render(fecha, reservasDelDia);
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
        TimeSlotPicker.TimeSlot slot = slotPicker.getSelectedSlot();

        if (pista == null || fecha == null) {
            showError("Por favor, selecciona pista y fecha.");
            return;
        }
        if (slot == null) {
            showError("Por favor, selecciona una franja horaria.");
            return;
        }

        Reservation nueva = new Reservation(fecha, slot.inicio, slot.fin, false, pista, currentUser);
        reservationService.save(nueva);
        showSuccess("Reserva creada correctamente.");
        cmbPista.setValue(null);
        dateFecha.setValue(null);
        slotContainer.getChildren().clear();
        slotPicker = new TimeSlotPicker();
        slotContainer.getChildren().add(slotPicker);
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
            Stage stage = (Stage) cmbPista.getScene().getWindow();
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
        } catch (Exception e) { e.printStackTrace(); }
    }
}