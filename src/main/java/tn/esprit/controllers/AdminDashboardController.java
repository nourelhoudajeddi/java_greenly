package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    
    @FXML private Node produitsView;
    @FXML private Node commandesView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Afficher la vue des produits par défaut
        showProduits();
        
        // Attendre que la scène soit initialisée pour stocker la référence
        Platform.runLater(() -> {
            if (produitsView.getScene() != null) {
                produitsView.getScene().getWindow().setUserData(this);
            }
        });
    }

    @FXML
    public void showProduits() {
        if (produitsView != null) {
            produitsView.setVisible(true);
        }
        if (commandesView != null) {
            commandesView.setVisible(false);
        }
    }

    @FXML
    public void showCommandes() {
        if (produitsView != null) {
            produitsView.setVisible(false);
        }
        if (commandesView != null) {
            commandesView.setVisible(true);
        }
    }

    @FXML
    public void handleLogout() {
        System.exit(0);
    }
} 