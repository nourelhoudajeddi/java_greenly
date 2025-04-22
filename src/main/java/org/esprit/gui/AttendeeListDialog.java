package org.esprit.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.esprit.models.Attendee;
import org.esprit.models.Event;
import org.esprit.services.AttendeeService;

public class AttendeeListDialog extends Stage {
    private final Event event;
    private final AttendeeService attendeeService;
    private TableView<Attendee> attendeeTable;

    public AttendeeListDialog(Event event) {
        this.event = event;
        this.attendeeService = new AttendeeService();
        setupDialog();
    }

    private void setupDialog() {
        setTitle("Attendees for " + event.getEventName());
        initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getStyleClass().add("linear-grad");

        // Create table
        attendeeTable = new TableView<>();
        
        TableColumn<Attendee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        
        TableColumn<Attendee, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> data.getValue().phoneNumberProperty());
        
        TableColumn<Attendee, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> data.getValue().emailProperty());

        attendeeTable.getColumns().addAll(nameCol, phoneCol, emailCol);
        
        // Load data
        try {
            attendeeTable.setItems(FXCollections.observableArrayList(
                attendeeService.getByEventId(event.getId())
            ));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to load attendees: " + e.getMessage());
        }

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button4");
        closeButton.setOnAction(e -> close());

        root.getChildren().addAll(attendeeTable, closeButton);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        setScene(scene);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
