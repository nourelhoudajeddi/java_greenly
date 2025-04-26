package tn.greenly.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.greenly.entites.Formation;

import java.io.IOException;
import java.net.URL;

public class DetailFormationController {

    @FXML
    private Label nomFormationLabel;
    @FXML
    private Label descriptionFormationLabel;
    @FXML
    private Label dureeFormationLabel;
    @FXML
    private Label modeFormationLabel;
    @FXML
    private Label dateDebutLabel;
    @FXML
    private Label dateFinLabel;

    @FXML
    private Button btnmodule;

    @FXML
    private Button btnformation;

    @FXML
    private WebView mapView; // WebView pour afficher la carte

    // Méthode d'initialisation des données de la formation
    public void initData(Formation formation) {
        // Remplir les informations de la formation
        if (formation != null) {
            nomFormationLabel.setText("📘 " + formation.getNomFormation());
            descriptionFormationLabel.setText("📝 " + formation.getDescriptionFormation());
            dureeFormationLabel.setText("⏳ " + formation.getDureeFormation() + " h");
            modeFormationLabel.setText("📡 " + formation.getModeFormation());
            dateDebutLabel.setText("📅 Début : " + formation.getDateDebutFormation());
            dateFinLabel.setText("📅 Fin : " + formation.getDateFinFormation());

            // Charger le fichier map.html depuis les ressources
            WebEngine webEngine = mapView.getEngine();
            // Utiliser getClass().getResource pour obtenir le chemin relatif vers la ressource
            URL url = getClass().getResource("/map.html");
            if (url != null) {
                webEngine.load(url.toExternalForm()); // Charger la carte depuis le fichier map.html
            } else {
                System.out.println("Fichier map.html non trouvé !");
            }
        } else {
            System.out.println("Les informations de la formation sont nulles !");
        }
    }

    // Méthode pour naviguer vers les modules
    @FXML
    private void gotomodules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherModulesFront.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnmodule.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la vue des modules : " + e.getMessage());
        }
    }

    // Méthode pour naviguer vers les formations
    @FXML
    private void gotoformations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFormationsFront.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnformation.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
