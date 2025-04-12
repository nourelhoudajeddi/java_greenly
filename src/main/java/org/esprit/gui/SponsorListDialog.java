package org.esprit.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.esprit.models.Sponsor;
import org.esprit.models.Event;
import org.esprit.services.SponsorService;

public class SponsorListDialog extends Stage {
    private final Event event;
    private final SponsorService sponsorService;
    private TableView<Sponsor> sponsorTable;

    public SponsorListDialog(Event event) {
        this.event = event;
        this.sponsorService = new SponsorService();
        setupDialog();
    }

    private void setupDialog() {
        setTitle("Sponsors for " + event.getEventName());
        initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getStyleClass().add("linear-grad");

        // Create table
        sponsorTable = new TableView<>();
        
        TableColumn<Sponsor, String> nameCol = new TableColumn<>("Organization");
        nameCol.setCellValueFactory(data -> data.getValue().sponsorNameProperty());
        nameCol.setPrefWidth(200);
        
        TableColumn<Sponsor, Number> montantCol = new TableColumn<>("Amount");
        montantCol.setCellValueFactory(data -> data.getValue().montantProperty());
        montantCol.setPrefWidth(100);
        
        TableColumn<Sponsor, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> data.getValue().emailProperty());
        emailCol.setPrefWidth(200);

        sponsorTable.getColumns().addAll(nameCol, montantCol, emailCol);
        
        // Load data
        try {
            sponsorTable.setItems(FXCollections.observableArrayList(
                sponsorService.getByEventId(event.getId())
            ));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to load sponsors: " + e.getMessage());
        }

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button4");
        closeButton.setOnAction(e -> close());

        root.getChildren().addAll(sponsorTable, closeButton);

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
