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

    private static final int PAGE_SIZE = 10;
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
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getId().toString()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNombre()));

        colPistas.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCourts() != null
                                ? String.valueOf(data.getValue().getCourts().size())
                                : "0"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("edit-btn");
                btnDelete.getStyleClass().add("delete-btn");

                btnEdit.setOnAction(e -> {
                    Sport s = getTableView().getItems().get(getIndex());
                    handleEdit(s);
                });

                btnDelete.setOnAction(e -> {
                    Sport s = getTableView().getItems().get(getIndex());
                    handleDelete(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
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
        int totalPages = (int) Math.ceil((double) allSports.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    @FXML
    private void handleNewSport() {
        // TODO: abrir formulario de nuevo deporte
        System.out.println("Nuevo deporte");
    }

    private void handleEdit(Sport sport) {
        // TODO: abrir formulario de edición
        System.out.println("Editar deporte: " + sport.getId());
    }

    private void handleDelete(Sport sport) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar deporte");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Se eliminará el deporte permanentemente.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sportService.delete(sport);
                loadSports();
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

            Stage stage = (Stage) tableSports.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}