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
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label lblWelcome;
    @FXML private Label lblReservasHoy;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblPistaMasReservada;
    @FXML private BarChart<String, Number> barChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private ScrollPane scrollPane;
    @FXML private HBox statsRow;
    @FXML private HBox cardsRow;
    @FXML private VBox statCard1;
    @FXML private VBox statCard2;
    @FXML private VBox statCard3;
    @FXML private VBox statCardWide;
    @FXML private VBox cardUsuarios;
    @FXML private VBox cardReservas;
    @FXML private VBox cardPistas;
    @FXML private VBox cardDeportes;

    private User currentUser;
    private final StatsService statsService = new StatsService();

    public void initData(User user) {
        if (user == null || !user.getIsAdmin()) return;
        this.currentUser = user;
        lblWelcome.setText("Hola, " + user.getName());
        cargarEstadisticas();
        configurarResponsive();
    }

    private void configurarResponsive() {
        // Listener en el ancho del scrollPane para repartir tamaños dinámicamente
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            ajustarTamanios(newVal.doubleValue());
        });
        // Aplicar también al inicio
        javafx.application.Platform.runLater(() ->
            ajustarTamanios(scrollPane.getWidth())
        );
    }

    private void ajustarTamanios(double anchoDisponible) {
        double padding = 80; // 40px cada lado
        double spacing = 15;
        double ancho = anchoDisponible - padding;

        // --- Fila de estadísticas ---
        // 3 cards pequeños + 1 wide (doble ancho)
        // Total gaps: 3 * spacing = 45
        double anchoStats = (ancho - 3 * spacing) / 5.0; // 5 partes: 3 pequeños + 2 para el wide
        double anchoWide  = anchoStats * 2;

        List.of(statCard1, statCard2, statCard3).forEach(c -> {
            c.setPrefWidth(anchoStats);
            c.setMinWidth(anchoStats);
            c.setMaxWidth(anchoStats);
        });
        statCardWide.setPrefWidth(anchoWide);
        statCardWide.setMinWidth(anchoWide);
        statCardWide.setMaxWidth(anchoWide);

        // --- Fila de cards de gestión ---
        // 4 cards iguales
        double anchoCard = (ancho - 3 * spacing) / 4.0;
        List.of(cardUsuarios, cardReservas, cardPistas, cardDeportes).forEach(c -> {
            c.setPrefWidth(anchoCard);
            c.setMinWidth(anchoCard);
            c.setMaxWidth(anchoCard);
        });
    }

    private void cargarEstadisticas() {
        lblReservasHoy.setText(String.valueOf(statsService.getReservasHoy()));
        lblTotalUsuarios.setText(String.valueOf(statsService.getTotalUsuarios()));
        lblPistaMasReservada.setText(statsService.getPistaMasReservada());

        barChart.getData().clear();
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Long> datos = statsService.getReservasUltimos7Dias();
        datos.forEach((fecha, count) -> series.getData().add(new XYChart.Data<>(fecha, count)));
        barChart.getData().add(series);

        barChart.lookupAll(".bar").forEach(node ->
                node.setStyle("-fx-background-color: #00d2ff; -fx-background-radius: 4;"));
    }

    @FXML private void handleUsuarios(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/user-view.fxml", event, "SPORTCONNECT - Usuarios");
    }

    @FXML private void handleReservas(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/reservation-view.fxml", event, "SPORTCONNECT - Reservas");
    }

    @FXML private void handlePistas(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/court-view.fxml", event, "SPORTCONNECT - Pistas");
    }

    @FXML private void handleSports(MouseEvent event) {
        navegarA("/com/example/sportconnect/fxml/sport-view.fxml", event, "SPORTCONNECT - Deportes");
    }

    private void navegarA(String fxml, MouseEvent event, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserController uc) uc.initData(currentUser);
            else if (controller instanceof ReservationController rc) rc.initData(currentUser);
            else if (controller instanceof CourtController cc) cc.initData(currentUser);
            else if (controller instanceof SportController sc) sc.initData(currentUser);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            double x = stage.getX(), y = stage.getY(), w = stage.getWidth(), h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.setX(x); stage.setY(y); stage.setWidth(w); stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleLogout(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sportconnect/fxml/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            double x = stage.getX(), y = stage.getY(), w = stage.getWidth(), h = stage.getHeight();
            stage.setScene(new Scene(root));
            stage.setTitle("SPORTCONNECT - Iniciar Sesión");
            stage.setX(x); stage.setY(y); stage.setWidth(w); stage.setHeight(h);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
