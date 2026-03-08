package com.example.sportconnect.controller;

import com.example.sportconnect.model.User;
import com.example.sportconnect.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class UserController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colNombre;
    @FXML private TableColumn<User, String> colApellido;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colTelefono;
    @FXML private TableColumn<User, String> colAdmin;
    @FXML private TableColumn<User, Void>   colAcciones;

    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label  lblPagina;
    @FXML private Label  lblWelcome;

    private static final int PAGE_SIZE = 5;
    private int currentPage = 0;
    private List<User> allUsers;

    private User currentUser;
    private final UserService userService = new UserService();

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        setupColumns();
        loadUsers();
    }

    private void setupColumns() {
        tableUsers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableUsers.setPrefHeight(5 * 40 + 30);

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId().toString()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        colApellido.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));

        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        colTelefono.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPhone() != null ? data.getValue().getPhone() : "-"));

        colAdmin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIsAdmin() ? "Si" : "No"));

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

    private void loadUsers() {
        allUsers = userService.getAllUsers();
        showPage(0);
    }

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allUsers.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        currentPage = page;
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allUsers.size());
        tableUsers.setItems(FXCollections.observableArrayList(allUsers.subList(from, to)));
        lblPagina.setText("Pagina " + (currentPage + 1) + " de " + totalPages);
        btnAnterior.setDisable(currentPage == 0);
        btnSiguiente.setDisable(currentPage >= totalPages - 1);
    }

    @FXML private void handleAnterior() { if (currentPage > 0) showPage(currentPage - 1); }

    @FXML private void handleSiguiente() {
        int totalPages = (int) Math.ceil((double) allUsers.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    @FXML private void handleNewUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-user-view.fxml"));
            Parent root = loader.load();
            FormUserController controller = loader.getController();
            controller.initData(currentUser, null);
            Stage stage = (Stage) tableUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleEdit(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/form-user-view.fxml"));
            Parent root = loader.load();
            FormUserController controller = loader.getController();
            controller.initData(currentUser, user);
            Stage stage = (Stage) tableUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleDelete(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar usuario");
        alert.setHeaderText("Esta seguro?");
        alert.setContentText("Se eliminara el usuario permanentemente.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user);
                loadUsers();
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
            Stage stage = (Stage) tableUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}