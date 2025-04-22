package org.esprit.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.esprit.models.Sponsor;
import org.esprit.models.Event;
import org.esprit.services.SponsorService;

public class SponsorRegistrationDialog extends Stage {
    private final Event event;
    private final SponsorService sponsorService;
    private TextField nameField;
    private TextField montantField;
    private TextField emailField;

    public SponsorRegistrationDialog(Event event) {
        this.event = event;
        this.sponsorService = new SponsorService();
        
        setupDialog();
    }

    private void setupDialog() {
        setTitle("Register as Sponsor");
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
        nameField = createFormField(grid, "Organization Name:", 1);
        montantField = createFormField(grid, "Sponsorship Amount:", 2);
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
                Sponsor sponsor = new Sponsor();
                sponsor.setEvent(event);
                sponsor.setSponsorName(nameField.getText());
                sponsor.setMontant(Integer.parseInt(montantField.getText()));
                sponsor.setEmail(emailField.getText());

                sponsorService.add(sponsor);
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Successfully registered as sponsor for " + event.getEventName());
                close();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Please enter a valid number for sponsorship amount");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Registration failed: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter your organization name");
            return false;
        }
        if (montantField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter the sponsorship amount");
            return false;
        }
        try {
            Integer.parseInt(montantField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid number for sponsorship amount");
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
