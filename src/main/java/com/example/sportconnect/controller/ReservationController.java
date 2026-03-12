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

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, String> colUsuario;
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

    private static final int PAGE_SIZE = 5;
    private int currentPage = 0;
    private List<Reservation> allReservations;

    private User currentUser;
    private final ReservationService reservationService = new ReservationService();

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        setupColumns();
        loadReservations();
    }

    private void setupColumns() {
        tableReservations.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colUsuario.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getUser().getName() + " " + data.getValue().getUser().getLastName()));

        colPista.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCourt().getNombre()));

        colFecha.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBookingDate().toString()));

        colInicio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStartTime().toString()));

        colFin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEndTime().toString()));

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
                    Button btnEdit   = new Button("✏");
                    Button btnDelete = new Button("🗑");
                    btnEdit.getStyleClass().add("edit-btn");
                    btnDelete.getStyleClass().add("delete-btn");
                    btnEdit.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                    btnDelete.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
                    setGraphic(new HBox(6, btnEdit, btnDelete));
                }
            }
        });
    }

    private void loadReservations() {
        allReservations = reservationService.getAllReservations();
        showPage(0);
    }

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        currentPage = page;
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allReservations.size());
        tableReservations.setItems(FXCollections.observableArrayList(allReservations.subList(from, to)));
        int rowCount = tableReservations.getItems().size();
        tableReservations.setPrefHeight(rowCount * 40 + 30);
        tableReservations.setMinHeight(rowCount * 40 + 30);
        tableReservations.setMaxHeight(rowCount * 40 + 30);
        tableReservations.refresh();
        lblPagina.setText("Pagina " + (currentPage + 1) + " de " + totalPages);
        btnAnterior.setDisable(currentPage == 0);
        btnSiguiente.setDisable(currentPage >= totalPages - 1);
    }

    @FXML private void handleAnterior() { if (currentPage > 0) showPage(currentPage - 1); }

    @FXML private void handleSiguiente() {
        int totalPages = (int) Math.ceil((double) allReservations.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    @FXML private void handleNewReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-reservation-view.fxml"));
            Parent root = loader.load();
            FormReservationController controller = loader.getController();
            controller.initData(currentUser, null);
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Editar Reserva");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleEdit(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-reservation-view.fxml"));
            Parent root = loader.load();
            FormReservationController controller = loader.getController();
            controller.initData(currentUser, reservation);
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Editar Reserva");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleDelete(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar reserva");
        alert.setHeaderText("Esta seguro?");
        alert.setContentText("Se eliminara la reserva permanentemente.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reservationService.delete(reservation);
                loadReservations();
            }
        });
    }

    @FXML private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/dashboard-view.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.initData(currentUser);
            Stage stage = (Stage) tableReservations.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Panel de administración");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}