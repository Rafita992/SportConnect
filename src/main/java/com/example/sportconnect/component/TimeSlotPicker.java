package com.example.sportconnect.component;

import com.example.sportconnect.model.Reservation;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotPicker extends VBox {

    public static class TimeSlot {
        public final LocalTime inicio;
        public final LocalTime fin;

        public TimeSlot(LocalTime inicio, LocalTime fin) {
            this.inicio = inicio;
            this.fin    = fin;
        }

        @Override
        public String toString() {
            return String.format("%02d:%02d - %02d:%02d",
                    inicio.getHour(), inicio.getMinute(),
                    fin.getHour(), fin.getMinute());
        }
    }

    public static final List<TimeSlot> SLOTS = new ArrayList<>();
    static {
        LocalTime t = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(21, 30);
        while (!t.isAfter(end.minusMinutes(90))) {
            SLOTS.add(new TimeSlot(t, t.plusMinutes(90)));
            t = t.plusMinutes(90);
        }
    }

    private TimeSlot selectedSlot = null;
    private Runnable onSelectionChanged;

    public TimeSlotPicker() {
        super(10);
        setPadding(new Insets(10, 0, 10, 0));
    }

    public void render(LocalDate date, List<Reservation> reservasDelDia) {
        getChildren().clear();
        selectedSlot = null;

        Label title = new Label("Franja horaria");
        title.getStyleClass().add("input-label");
        getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        boolean esHoy = date.equals(LocalDate.now());

        int col = 0, row = 0;
        for (TimeSlot slot : SLOTS) {
            boolean pasado  = esHoy && !slot.inicio.isAfter(LocalTime.now());
            boolean ocupado = pasado || isOcupado(slot, reservasDelDia);

            Label lbl = new Label(slot.toString());
            lbl.setPrefWidth(170);
            lbl.setPrefHeight(45);
            lbl.setAlignment(javafx.geometry.Pos.CENTER);
            lbl.getStyleClass().add("slot");
            lbl.getStyleClass().add(ocupado ? "slot-ocupado" : "slot-libre");

            if (!ocupado) {
                lbl.setCursor(javafx.scene.Cursor.HAND);
                lbl.setOnMouseClicked(e -> {
                    // Deseleccionar todos
                    grid.getChildren().forEach(node -> {
                        if (node instanceof Label l) {
                            l.getStyleClass().remove("slot-seleccionado");
                            String txt = l.getText();
                            TimeSlot s = SLOTS.stream()
                                    .filter(sl -> sl.toString().equals(txt))
                                    .findFirst().orElse(null);
                            boolean p = esHoy && s != null && !s.inicio.isAfter(LocalTime.now());
                            if (s != null && !isOcupado(s, reservasDelDia) && !p) {
                                if (!l.getStyleClass().contains("slot-libre"))
                                    l.getStyleClass().add("slot-libre");
                            }
                        }
                    });
                    // Seleccionar actual
                    lbl.getStyleClass().remove("slot-libre");
                    lbl.getStyleClass().add("slot-seleccionado");
                    selectedSlot = slot;
                    if (onSelectionChanged != null) onSelectionChanged.run();
                });
            }

            grid.add(lbl, col, row);
            col++;
            if (col == 2) { col = 0; row++; }
        }

        getChildren().add(grid);
    }

    private boolean isOcupado(TimeSlot slot, List<Reservation> reservas) {
        if (reservas == null) return false;
        for (Reservation r : reservas) {
            if (r.getCancelled()) continue;
            if (slot.inicio.isBefore(r.getEndTime()) && slot.fin.isAfter(r.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    public TimeSlot getSelectedSlot() { return selectedSlot; }
    public void setOnSelectionChanged(Runnable r) { this.onSelectionChanged = r; }
}