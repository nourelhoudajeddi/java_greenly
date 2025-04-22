package org.esprit.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.esprit.models.Attendee;
import org.esprit.models.Event;
import org.esprit.services.AttendeeService;

public class AttendeeRegistrationDialog extends Stage {
    private final Event event;
    private final AttendeeService attendeeService;
    private TextField nameField;
    private TextField phoneField;
    private TextField emailField;

    public AttendeeRegistrationDialog(Event event) {
        this.event = event;
        this.attendeeService = new AttendeeService();
        
        setupDialog();
    }

    private void setupDialog() {
        setTitle("Register as Attendee");
        initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("linear-grad");

        // Event details
        Label eventLabel = new Label("Event: " + event.getEventName());
        eventLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        grid.add(eventLabel, 0, 0, 2, 1);

        // Form fields
        nameField = createFormField(grid, "Name:", 1);
        phoneField = createFormField(grid, "Phone:", 2);
        emailField = createFormField(grid, "Email:", 3);

        // Buttons
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button4");
        registerButton.setOnAction(e -> handleRegistration());

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button4");
        cancelButton.setOnAction(e -> close());

        grid.add(registerButton, 0, 4);
        grid.add(cancelButton, 1, 4);

        Scene scene = new Scene(grid, 400, 300);
        scene.getStylesheets().addAll(
            getClass().getResource("/styles/style.css").toExternalForm(),
            getClass().getResource("/styles/front-office.css").toExternalForm()
        );
        setScene(scene);
    }

    private TextField createFormField(GridPane grid, String label, int row) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: white;");
        grid.add(l, 0, row);
        
        TextField field = new TextField();
        field.setPromptText("Enter " + label.toLowerCase().replace(":", ""));
        grid.add(field, 1, row);
        
        return field;
    }

    private void handleRegistration() {
        if (validateFields()) {
            try {
                Attendee attendee = new Attendee();
                attendee.setEvent(event);
                attendee.setName(nameField.getText());
                attendee.setPhoneNumber(phoneField.getText());
                attendee.setEmail(emailField.getText());

                attendeeService.add(attendee);
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Successfully registered for " + event.getEventName());
                close();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Registration failed: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your name");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your phone number");
            return false;
        }
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
