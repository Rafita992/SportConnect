package com.example.sportconnect.controller;

import com.example.sportconnect.model.Court;
import com.example.sportconnect.model.Sport;
import com.example.sportconnect.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Label lblWelcome;

    private User currentUser;

    /**
     * Método llamado desde el LoginController tras un login exitoso.
     * Recibe el usuario logueado y verifica que sea admin.
     */
    public void initData(User user) {
        // Protección: si no es admin, no debería estar aquí
        if (user == null || !user.getIsAdmin()) {
            // TODO: redirigir a la vista de usuario normal
            return;
        }
        this.currentUser = user;
        lblWelcome.setText("Hola, " + user.getName());
    }

    // ── HANDLERS DE LAS CARDS ──

    @FXML
    private void handleUsuarios(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/user-view.fxml"));
            Parent root = loader.load();

            UserController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReservas(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/reservation-view.fxml"));
            Parent root = loader.load();

            ReservationController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handlePistas(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/court-view.fxml"));
            Parent root = loader.load();

            CourtController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSports(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/sport-view.fxml"));
            Parent root = loader.load();

            SportController controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        // TODO: volver a la vista de Login
        System.out.println("Logout clicked");
    }
}