package com.example.sportconnect.controller;

import com.example.sportconnect.model.User;
import com.example.sportconnect.service.StatsService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.Map;

public class DashboardController {

    @FXML private Label lblWelcome;
    @FXML private Label lblReservasHoy;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblPistaMasReservada;
    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis   yAxis;

    private User currentUser;
    private final StatsService statsService = new StatsService();

    public void initData(User user) {
        if (user == null || !user.getIsAdmin()) return;
        this.currentUser = user;
        lblWelcome.setText("Hola, " + user.getName());
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        // Tarjetas
        lblReservasHoy.setText(String.valueOf(statsService.getReservasHoy()));
        lblTotalUsuarios.setText(String.valueOf(statsService.getTotalUsuarios()));
        lblPistaMasReservada.setText(statsService.getPistaMasReservada());

        // Grafica
        barChart.getData().clear();
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Long> datos = statsService.getReservasUltimos7Dias();
        datos.forEach((fecha, count) -> series.getData().add(new XYChart.Data<>(fecha, count)));
        barChart.getData().add(series);

        // Estilo de las barras
        barChart.lookupAll(".bar").forEach(node ->
                node.setStyle("-fx-background-color: #00d2ff; -fx-background-radius: 4;"));
    }

    @FXML private void handleUsuarios(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/user-view.fxml", event);
    }

    @FXML private void handleReservas(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/reservation-view.fxml", event);
    }

    @FXML private void handlePistas(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/court-view.fxml", event);
    }

    @FXML private void handleSports(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/sport-view.fxml", event);
    }

    private void navegarA(String fxml, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserController uc) uc.initData(currentUser);
            else if (controller instanceof ReservationController rc) rc.initData(currentUser);
            else if (controller instanceof CourtController cc) cc.initData(currentUser);
            else if (controller instanceof SportController sc) sc.initData(currentUser);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}