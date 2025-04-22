package org.esprit.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import org.esprit.models.Event;
import org.esprit.services.EventService;
import java.time.LocalDateTime;
import java.util.List;

public class EventManagementApp extends Application {
    private EventService eventService;
    private TableView<Event> eventTable;
    private TextField eventNameField;
    private TextField descriptionField;
    private TextField locationField;
    private DatePicker datePicker;
    private ComboBox<String> statusComboBox;

    @Override
    public void start(Stage primaryStage) {
        eventService = new EventService();

        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("linear-grad");
        
        // Create the form
        VBox formBox = createFormBox();
        mainLayout.setLeft(formBox);

        // Create the table
        createEventTable();
        mainLayout.setCenter(eventTable);

        // Create buttons
        HBox buttonBox = createButtonBox();
        mainLayout.setBottom(buttonBox);

        // Load the CSS
        Scene scene = new Scene(mainLayout, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        primaryStage.setTitle("Event Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load initial data
        refreshTable();
    }

    private VBox createFormBox() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setAlignment(Pos.TOP_LEFT);
        formBox.setPrefWidth(300);

        eventNameField = new TextField();
        eventNameField.setPromptText("Event Name");
        
        descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        
        locationField = new TextField();
        locationField.setPromptText("Location");
        
        datePicker = new DatePicker();
        datePicker.setPromptText("Event Date");
        
        statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "SCHEDULED", "CONFIRMED", "CANCELLED"
        ));
        statusComboBox.setPromptText("Status");
        statusComboBox.getStyleClass().add("combo-box");

        formBox.getChildren().addAll(
            new Label("Event Name:"), eventNameField,
            new Label("Description:"), descriptionField,
            new Label("Location:"), locationField,
            new Label("Date:"), datePicker,
            new Label("Status:"), statusComboBox
        );

        return formBox;
    }

    private void createEventTable() {
        eventTable = new TableView<>();
        
        TableColumn<Event, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        
        TableColumn<Event, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().eventNameProperty());
        
        TableColumn<Event, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().eventDescriptionProperty());
        
        TableColumn<Event, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(data -> data.getValue().locationProperty());
        
        TableColumn<Event, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().eventDateProperty());
        
        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        eventTable.getColumns().addAll(idCol, nameCol, descCol, locCol, dateCol, statusCol);
        
        // Add selection listener
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                eventNameField.setText(newSelection.getEventName());
                descriptionField.setText(newSelection.getEventDescription());
                locationField.setText(newSelection.getLocation());
                datePicker.setValue(newSelection.getEventDate().toLocalDate());
                statusComboBox.setValue(newSelection.getStatus());
            }
        });
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add Event");
        addButton.getStyleClass().add("button4");
        addButton.setOnAction(e -> addEvent());

        Button updateButton = new Button("Update Event");
        updateButton.getStyleClass().add("button4");
        updateButton.setOnAction(e -> updateEvent());

        Button deleteButton = new Button("Delete Event");
        deleteButton.getStyleClass().add("button4");
        deleteButton.setOnAction(e -> deleteEvent());        Button clearButton = new Button("Clear Form");
        clearButton.getStyleClass().add("button4");
        clearButton.setOnAction(e -> clearForm());

        Button listAttendeesButton = new Button("List Attendees");
        listAttendeesButton.getStyleClass().add("button4");
        listAttendeesButton.setOnAction(e -> showAttendees());

        Button listSponsorsButton = new Button("List Sponsors");
        listSponsorsButton.getStyleClass().add("button4");
        listSponsorsButton.setOnAction(e -> showSponsors());

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton, 
                                     listAttendeesButton, listSponsorsButton);
        return buttonBox;
    }

    private void addEvent() {
        try {
            Event event = new Event();            event.setOrganizerId(1); // Default organizer ID
            event.setEventName(eventNameField.getText());
            event.setEventDescription(descriptionField.getText());
            event.setLocation(locationField.getText());
            event.setEventDate(LocalDateTime.of(datePicker.getValue(), LocalDateTime.now().toLocalTime()));
            event.setStatus(statusComboBox.getValue());

            eventService.add(event);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event added successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add event: " + e.getMessage());
        }
    }

    private void updateEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an event to update.");
            return;
        }

        try {
            selectedEvent.setEventName(eventNameField.getText());
            selectedEvent.setEventDescription(descriptionField.getText());
            selectedEvent.setLocation(locationField.getText());
            selectedEvent.setEventDate(LocalDateTime.of(datePicker.getValue(), LocalDateTime.now().toLocalTime()));
            selectedEvent.setStatus(statusComboBox.getValue());

            eventService.update(selectedEvent);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event updated successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update event: " + e.getMessage());
        }
    }

    private void deleteEvent() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an event to delete.");
            return;
        }

        try {
            eventService.delete(selectedEvent);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event deleted successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete event: " + e.getMessage());
        }
    }

    private void refreshTable() {
        try {
            List<Event> events = eventService.getAll();
            eventTable.setItems(FXCollections.observableArrayList(events));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load events: " + e.getMessage());
        }
    }

    private void clearForm() {
        eventNameField.clear();
        descriptionField.clear();
        locationField.clear();
        datePicker.setValue(null);
        statusComboBox.setValue(null);
        eventTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAttendees() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an event to view attendees.");
            return;
        }

        new AttendeeListDialog(selectedEvent).show();
    }

    private void showSponsors() {
        Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an event to view sponsors.");
            return;
        }

        new SponsorListDialog(selectedEvent).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
