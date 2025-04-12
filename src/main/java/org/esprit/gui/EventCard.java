package org.esprit.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.esprit.models.Event;

import java.time.format.DateTimeFormatter;

public class EventCard extends VBox {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private final Event event;

    public EventCard(Event event) {
        this.event = event;
        setAlignment(Pos.CENTER);
        setPadding(new Insets(15));
        setSpacing(10);
        getStyleClass().add("event-card");
        setMinWidth(300);
        setMaxWidth(300);

        Label nameLabel = new Label(event.getEventName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setWrapText(true);

        Label descriptionLabel = new Label(event.getEventDescription());
        descriptionLabel.setWrapText(true);

        Label dateLabel = new Label("Date: " + event.getEventDate().format(DATE_FORMATTER));
        Label locationLabel = new Label("Location: " + event.getLocation());        Label statusLabel = new Label("Status: " + event.getStatus());
        statusLabel.getStyleClass().add("status-label");

        // Create button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button attendeeBtn = new Button("Register");
        attendeeBtn.getStyleClass().add("button4");
        attendeeBtn.setOnAction(e -> new AttendeeRegistrationDialog(event).showAndWait());

        Button sponsorBtn = new Button("Sponsor");
        sponsorBtn.getStyleClass().add("button4");
        sponsorBtn.setOnAction(e -> new SponsorRegistrationDialog(event).showAndWait());

        buttonBox.getChildren().addAll(attendeeBtn, sponsorBtn);

        getChildren().addAll(nameLabel, descriptionLabel, dateLabel, locationLabel, statusLabel, buttonBox);
    }

    public Event getEvent() {
        return event;
    }
}
