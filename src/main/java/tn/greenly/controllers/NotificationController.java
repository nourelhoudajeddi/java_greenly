package tn.greenly.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class NotificationController {

    // Méthode pour afficher une notification élégante
    public void showNotification(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Personnalisation de l'alerte
        alert.getDialogPane().getStyleClass().add("custom-alert"); // Ajout d'une classe CSS pour la personnalisation

        // Définir la taille de l'alerte
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        // Animation pour faire disparaître l'alerte après 3 secondes
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> alert.close());
        delay.play();

        // Afficher l'alerte
        alert.show();
    }
}
