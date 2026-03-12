package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.CourtService;
import com.example.sportconnect.service.ReservationService;
import com.example.sportconnect.service.UserService;
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

public class FormReservationController {

    @FXML private ComboBox<User>  cmbUsuario;
    @FXML private ComboBox<Court> cmbPista;
    @FXML private DatePicker      dateFecha;
    @FXML private VBox            slotContainer;
    @FXML private Label           lblMensaje;
    @FXML private Label           lblWelcome;
    @FXML private Label           lblTitulo;
    @FXML private Button          btnGuardar;

    private User        currentUser;
    private Reservation reservacionEditar;
    private TimeSlotPicker slotPicker;

    private final ReservationService reservationService = new ReservationService();
    private final UserService        userService        = new UserService();
    private final CourtService       courtService       = new CourtService();

    public void initData(User currentUser, Reservation reservacionEditar) {
        this.currentUser       = currentUser;
        this.reservacionEditar = reservacionEditar;
        lblWelcome.setText(currentUser.getName());

        slotPicker = new TimeSlotPicker();
        slotContainer.getChildren().add(slotPicker);

        cargarCombos();
        configurarListeners();

        if (reservacionEditar != null) {
            btnGuardar.setDisable(false);
        } else {
            btnGuardar.setDisable(true);
            // Habilitar cuando usuario + pista + fecha esten seleccionados (franja se valida al pulsar)
            Runnable check = () -> {
                boolean ok = cmbUsuario.getValue() != null
                        && cmbPista.getValue() != null
                        && dateFecha.getValue() != null;
                if (ok) actualizarFranjas();
            };
            cmbUsuario.valueProperty().addListener((o, v, n) -> check.run());
            cmbPista.valueProperty().addListener((o, v, n) -> check.run());
            dateFecha.valueProperty().addListener((o, v, n) -> check.run());
        }

        // No permitir fechas pasadas
        dateFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(java.time.LocalDate.now()));
            }
        });

        if (reservacionEditar != null) {
            lblTitulo.setText("Editar Reserva");
            cmbUsuario.setValue(reservacionEditar.getUser());
            cmbPista.setValue(reservacionEditar.getCourt());
            dateFecha.setValue(reservacionEditar.getBookingDate());
            actualizarFranjas();
        }
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
                        && r.getBookingDate().equals(fecha)
                        && (reservacionEditar == null || !r.getId().equals(reservacionEditar.getId())))
                .toList();

        slotPicker.render(fecha, reservasDelDia);
    }

    private void cargarCombos() {
        List<User> usuarios = userService.getAllUsers();
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarios));
        cmbUsuario.setConverter(new StringConverter<>() {
            public String toString(User u) { return u != null ? u.getName() + " " + u.getLastName() : ""; }
            public User fromString(String s) { return null; }
        });

        List<Court> pistas = courtService.getAllCourts();
        cmbPista.setItems(FXCollections.observableArrayList(pistas));
        cmbPista.setConverter(new StringConverter<>() {
            public String toString(Court c) { return c != null ? c.getNombre() : ""; }
            public Court fromString(String s) { return null; }
        });
    }

    @FXML
    private void handleGuardar() {
        User usuario    = cmbUsuario.getValue();
        Court pista     = cmbPista.getValue();
        LocalDate fecha = dateFecha.getValue();
        TimeSlotPicker.TimeSlot slot = slotPicker.getSelectedSlot();

        if (usuario == null || pista == null || fecha == null) {
            showError("Por favor, rellena todos los campos.");
            return;
        }
        if (slot == null) {
            showError("Por favor, selecciona una franja horaria.");
            return;
        }

        if (reservacionEditar == null) {
            Reservation nueva = new Reservation(fecha, slot.inicio, slot.fin, false, pista, usuario);
            reservationService.save(nueva);
            showSuccess("Reserva creada correctamente.");
            cmbUsuario.setValue(null);
            cmbPista.setValue(null);
            dateFecha.setValue(null);
            slotContainer.getChildren().clear();
            slotPicker = new TimeSlotPicker();
            slotContainer.getChildren().add(slotPicker);
        } else {
            reservacionEditar.setUser(usuario);
            reservacionEditar.setCourt(pista);
            reservacionEditar.setBookingDate(fecha);
            reservacionEditar.setStartTime(slot.inicio);
            reservacionEditar.setEndTime(slot.fin);
            reservationService.save(reservacionEditar);
            showSuccess("Reserva actualizada correctamente.");
        }
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
            Stage stage = (Stage) cmbPista.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Reservas");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}