package org.esprit.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import org.esprit.models.Event;
import org.esprit.services.EventService;
import java.util.List;

public class FrontOfficeWindow extends Application {
    private EventService eventService;
    private FlowPane eventCardsContainer;

    @Override
    public void start(Stage primaryStage) {
        eventService = new EventService();

        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("linear-grad");
        
        // Top section with title
        Label titleLabel = new Label("Upcoming Events");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20));
        mainLayout.setTop(titleBox);


        eventCardsContainer = new FlowPane();
        eventCardsContainer.setHgap(20);
        eventCardsContainer.setVgap(20);
        eventCardsContainer.setPadding(new Insets(20));
        eventCardsContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(eventCardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainLayout.setCenter(scrollPane);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("button4");
        backBtn.setOnAction(e -> {
            new DashboardWindow().start(new Stage());
            primaryStage.close();
        });

        buttonBox.getChildren().add(backBtn);
        mainLayout.setBottom(buttonBox);

        // Load events
        loadEvents();

        // Add custom styles for event cards
        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().addAll(
            getClass().getResource("/styles/style.css").toExternalForm(),
            getClass().getResource("/styles/front-office.css").toExternalForm()
        );

        primaryStage.setTitle("Event Management - Front Office");
        primaryStage.setScene(scene);
        primaryStage.show();
    }    private void loadEvents() {
        try {
            List<Event> events = eventService.getAll();
            eventCardsContainer.getChildren().clear();
            for (Event event : events) {
                EventCard card = new EventCard(event);
                eventCardsContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load events: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
