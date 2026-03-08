package com.example.sportconnect.controller;

import com.example.sportconnect.model.Sport;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.SportService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class SportController {

    @FXML private TableView<Sport> tableSports;
    @FXML private TableColumn<Sport, String> colId;
    @FXML private TableColumn<Sport, String> colNombre;
    @FXML private TableColumn<Sport, String> colPistas;
    @FXML private TableColumn<Sport, Void>   colAcciones;

    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label  lblPagina;
    @FXML private Label  lblWelcome;

    private static final int PAGE_SIZE = 5;
    private int currentPage = 0;
    private List<Sport> allSports;

    private User currentUser;
    private final SportService sportService = new SportService();

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        setupColumns();
        loadSports();
    }

    private void setupColumns() {
        tableSports.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableSports.setPrefHeight(5 * 40 + 30);

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId().toString()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        colPistas.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty("-"));

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

    private void loadSports() {
        allSports = sportService.getAllSports();
        showPage(0);
    }

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allSports.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        currentPage = page;
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allSports.size());
        tableSports.setItems(FXCollections.observableArrayList(allSports.subList(from, to)));
        lblPagina.setText("Pagina " + (currentPage + 1) + " de " + totalPages);
        btnAnterior.setDisable(currentPage == 0);
        btnSiguiente.setDisable(currentPage >= totalPages - 1);
    }

    @FXML private void handleAnterior() { if (currentPage > 0) showPage(currentPage - 1); }

    @FXML private void handleSiguiente() {
        int totalPages = (int) Math.ceil((double) allSports.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    @FXML private void handleNewSport() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-sport-view.fxml"));
            Parent root = loader.load();
            FormSportController controller = loader.getController();
            controller.initData(currentUser, null);
            Stage stage = (Stage) tableSports.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleEdit(Sport sport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-sport-view.fxml"));
            Parent root = loader.load();
            FormSportController controller = loader.getController();
            controller.initData(currentUser, sport);
            Stage stage = (Stage) tableSports.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleDelete(Sport sport) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar deporte");
        alert.setHeaderText("Esta seguro?");
        alert.setContentText("Se eliminara el deporte permanentemente.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sportService.delete(sport);
                loadSports();
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
            Stage stage = (Stage) tableSports.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}