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

    // ── TABLA ──
    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colNombre;
    @FXML private TableColumn<User, String> colApellido;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colTelefono;
    @FXML private TableColumn<User, String> colAdmin;
    @FXML private TableColumn<User, Void>   colAcciones;

    // ── PAGINACIÓN ──
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label  lblPagina;
    @FXML private Label  lblWelcome;

    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private List<User> allUsers;

    private User currentUser;
    private final UserService userService = new UserService();

    // ─────────────────────────────────────────────
    // INICIALIZACIÓN
    // ─────────────────────────────────────────────

    public void initData(User user) {
        this.currentUser = user;
        lblWelcome.setText(user.getName());
        setupColumns();
        loadUsers();
    }

    private void setupColumns() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getId().toString()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getName()));

        colApellido.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getLastName()));

        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEmail()));

        colTelefono.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPhone() != null ? data.getValue().getPhone() : "-"));

        colAdmin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIsAdmin() ? "Sí" : "No"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("✏");
            private final Button btnDelete = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("edit-btn");
                btnDelete.getStyleClass().add("delete-btn");

                btnEdit.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    handleEdit(u);
                });

                btnDelete.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    handleDelete(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void loadUsers() {
        allUsers = userService.getAllUsers();
        showPage(0);
    }

    // ─────────────────────────────────────────────
    // PAGINACIÓN
    // ─────────────────────────────────────────────

    private void showPage(int page) {
        int totalPages = (int) Math.ceil((double) allUsers.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        currentPage = page;

        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allUsers.size());

        tableUsers.setItems(FXCollections.observableArrayList(allUsers.subList(from, to)));

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
        int totalPages = (int) Math.ceil((double) allUsers.size() / PAGE_SIZE);
        if (currentPage < totalPages - 1) showPage(currentPage + 1);
    }

    // ─────────────────────────────────────────────
    // ACCIONES
    // ─────────────────────────────────────────────

    @FXML
    private void handleNewUser() {
        // TODO: abrir formulario de nuevo usuario
        System.out.println("Nuevo usuario");
    }

    private void handleEdit(User user) {
        // TODO: abrir formulario de edición
        System.out.println("Editar usuario: " + user.getId());
    }

    private void handleDelete(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar usuario");
        alert.setHeaderText("¿Estás seguro?");
        alert.setContentText("Se eliminará el usuario permanentemente.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user);
                loadUsers();
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

            Stage stage = (Stage) tableUsers.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}