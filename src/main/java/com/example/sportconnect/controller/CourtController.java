package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.User;
import com.example.sportconnect.service.CourtService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class CourtController {

    @FXML private TableView<Court> tableCourts;
    @FXML private TableColumn<Court, String> colId;
    @FXML private TableColumn<Court, String> colNombre;
    @FXML private TableColumn<Court, String> colDeporte;
    @FXML private TableColumn<Court, Void>   colAcciones;

    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label  lblPagina;
    @FXML private Label  lblWelcome;

    private static final int PAGE_SIZE = 5;
    private int currentPage = 0;
    private List<Court> allCourts;

    private User currentUser;
    private final CourtService courtService = new CourtService();

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        setupColumns();
        loadCourts();
    }

    private void setupColumns() {
        tableCourts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId().toString()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        colDeporte.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getSport().getNombre()));

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

    private void loadCourts() {
        allCourts = courtService.getAllCourts();
        showPage(0);
    }

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allCourts.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        currentPage = page;
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allCourts.size());
        tableCourts.setItems(FXCollections.observableArrayList(allCourts.subList(from, to)));
        int rowCount = tableCourts.getItems().size();
        tableCourts.setPrefHeight(rowCount * 40 + 30);
        tableCourts.setMinHeight(rowCount * 40 + 30);
        tableCourts.setMaxHeight(rowCount * 40 + 30);
        tableCourts.refresh();
        lblPagina.setText("Pagina " + (currentPage + 1) + " de " + totalPages);
        btnAnterior.setDisable(currentPage == 0);
        btnSiguiente.setDisable(currentPage >= totalPages - 1);
    }

    @FXML private void handleAnterior() { if (currentPage > 0) showPage(currentPage - 1); }

    @FXML private void handleSiguiente() {
        int totalPages = (int) Math.ceil((double) allCourts.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    @FXML private void handleNewCourt() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-court-view.fxml"));
            Parent root = loader.load();
            FormCourtController controller = loader.getController();
            controller.initData(currentUser, null);
            Stage stage = (Stage) tableCourts.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Editar Pista");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleEdit(Court court) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-court-view.fxml"));
            Parent root = loader.load();
            FormCourtController controller = loader.getController();
            controller.initData(currentUser, court);
            Stage stage = (Stage) tableCourts.getScene().getWindow();
            double x = stage.getX();
            double y = stage.getY();
            double w = stage.getWidth();
            double h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Editar Pista");
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(w);
            stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleDelete(Court court) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar pista");
        alert.setHeaderText("Esta seguro?");
        alert.setContentText("Se eliminara la pista permanentemente.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                courtService.delete(court);
                loadCourts();
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
            Stage stage = (Stage) tableCourts.getScene().getWindow();
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