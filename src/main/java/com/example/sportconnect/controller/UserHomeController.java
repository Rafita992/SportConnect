package com.example.sportconnect.controller;

import com.example.sportconnect.model.Reservation;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.ReservationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserHomeController {

    @FXML private TableView<Reservation>           tableReservations;
    @FXML private TableColumn<Reservation, String> colPista;
    @FXML private TableColumn<Reservation, String> colFecha;
    @FXML private TableColumn<Reservation, String> colInicio;
    @FXML private TableColumn<Reservation, String> colFin;
    @FXML private TableColumn<Reservation, String> colEstado;
    @FXML private TableColumn<Reservation, Void>   colAcciones;

    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label  lblPagina;
    @FXML private Label  lblWelcome;
    @FXML private Label  lblBienvenida;

    private static final int PAGE_SIZE = 5;
    private int currentPage = 0;
    private List<Reservation> allReservations;

    private User currentUser;
    private final ReservationService reservationService = new ReservationService();

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        lblBienvenida.setText("¡Bienvenido, " + user.getName() + "!");
        setupColumns();
        loadReservations();
    }

    private void setupColumns() {
        tableReservations.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableReservations.setPrefHeight(5 * 40 + 30);

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

        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Reservation r = getTableView().getItems().get(getIndex());
                    // Solo mostrar cancelar si la reserva está activa
                    if (!r.getCancelled()) {
                        Button btnCancelar = new Button("Cancelar");
                        btnCancelar.getStyleClass().add("delete-btn");
                        btnCancelar.setOnAction(e -> handleCancelar(getTableView().getItems().get(getIndex())));
                        setGraphic(btnCancelar);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void loadReservations() {
        // Obtener solo las reservas del usuario actual, ordenadas por fecha, solo futuras
        allReservations = reservationService.getReservationsByUser(currentUser)
                .stream()
                .filter(r -> !r.getBookingDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Reservation::getBookingDate)
                        .thenComparing(Reservation::getStartTime))
                .collect(Collectors.toList());
        showPage(0);
    }

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        currentPage = page;
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allReservations.size());
        tableReservations.setItems(FXCollections.observableArrayList(allReservations.subList(from, to)));
        lblPagina.setText("Página " + (currentPage + 1) + " de " + totalPages);
        btnAnterior.setDisable(currentPage == 0);
        btnSiguiente.setDisable(currentPage >= totalPages - 1);
    }

    @FXML private void handleAnterior() { if (currentPage > 0) showPage(currentPage - 1); }

    @FXML private void handleSiguiente() {
        int totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    private void handleCancelar(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar reserva");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Se eliminara la reserva del " + reservation.getBookingDate());
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reservationService.delete(reservation);
                loadReservations();
            }
        });
    }

    @FXML private void handleNewReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/user-form-reservation-view.fxml"));
            Parent root = loader.load();
            UserFormReservationController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}