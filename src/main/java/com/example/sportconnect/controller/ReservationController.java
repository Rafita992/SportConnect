package com.example.sportconnect.controller;

import com.example.sportconnect.service.ReservationService;
import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class ReservationController {

    // ── TABLA ──
    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, String>  colUsuario;
    @FXML private TableColumn<Reservation, String>  colPista;
    @FXML private TableColumn<Reservation, String>  colFecha;
    @FXML private TableColumn<Reservation, String>  colInicio;
    @FXML private TableColumn<Reservation, String>  colFin;
    @FXML private TableColumn<Reservation, String>  colEstado;
    @FXML private TableColumn<Reservation, Void>    colAcciones;

    // ── PAGINACIÓN ──
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label  lblPagina;
    @FXML private Label  lblWelcome;

    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private List<Reservation> allReservations;

    private User currentUser;
    private final ReservationService reservationService = new ReservationService();

    // ─────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        setupColumns();
        loadReservations();
    }

    private void setupColumns() {
        colUsuario.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUser().getName() + " " + data.getValue().getUser().getLastName()));

        colPista.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCourt().getNombre()));

        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getBookingDate().toString()));

        colInicio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStartTime().toString()));

        colFin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEndTime().toString()));

        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCancelled() ? "Cancelada" : "Activa"));

        // Columna acciones con botones editar y eliminar
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("edit-btn");
                btnDelete.getStyleClass().add("delete-btn");

                btnEdit.setOnAction(e -> {
                    Reservation r = getTableView().getItems().get(getIndex());
                    handleEdit(r);
                });

                btnDelete.setOnAction(e -> {
                    Reservation r = getTableView().getItems().get(getIndex());
                    handleDelete(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadReservations() {
        allReservations = reservationService.getAllReservations();
        System.out.println("Reservas cargadas: " + allReservations.size());
        showPage(0);
    }

    // ─────────────────────────────────────────────
    // PAGINACIÓN
    // ─────────────────────────────────────────────

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        currentPage = page;

        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allReservations.size());

        tableReservations.setItems(
                FXCollections.observableArrayList(allReservations.subList(from, to)));

        lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
        btnAnterior.setDisable(currentPage == 0);
        btnSiguiente.setDisable(currentPage >= totalPages - 1);
    }

    @FXML
    private void handleAnterior() {
        if (currentPage > 0) showPage(currentPage - 1);
    }

    @FXML
    private void handleSiguiente() {
        int totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    // ─────────────────────────────────────────────
    // ACCIONES
    // ─────────────────────────────────────────────

    @FXML
    private void handleNewReservation() {
        // TODO: abrir formulario de nueva reserva
        System.out.println("Nueva reserva");
    }

    private void handleEdit(Reservation reservation) {
        // TODO: abrir formulario de edición
        System.out.println("Editar reserva: " + reservation.getId());
    }

    private void handleDelete(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar reserva");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Se eliminará la reserva permanentemente.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reservationService.delete(reservation);
                loadReservations();
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/dashboard-view.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}