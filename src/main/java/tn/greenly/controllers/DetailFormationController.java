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

import java.io.BufferedReader;
import java.io.FileReader;
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

    @FXML
    private Button btnSurvey;

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
            URL url = getClass().getResource("/map1.html");
            if (url != null) {
                webEngine.load(url.toExternalForm()); // Charger la carte depuis le fichier map.html
                // Lire les coordonnées depuis le fichier et les passer au script JavaScript
                loadCoordinatesAndSetMarker(webEngine);
            } else {
                System.out.println("Fichier map.html non trouvé !");
            }
        } else {
            System.out.println("Les informations de la formation sont nulles !");
        }
    }

    // Méthode pour lire les coordonnées depuis coordinates.txt et les passer à la carte
    private void loadCoordinatesAndSetMarker(WebEngine webEngine) {
        try {
            URL fileUrl = getClass().getClassLoader().getResource("coordinates.txt");
            if (fileUrl != null) {
                try (BufferedReader br = new BufferedReader(new FileReader(fileUrl.getPath()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            String[] coordinates = line.split(",");
                            if (coordinates.length == 2) {
                                double lat = Double.parseDouble(coordinates[0].trim());
                                double lon = Double.parseDouble(coordinates[1].trim());

                                // Passer les coordonnées au WebView (via JavaScript)
                                String script = String.format("setCoordinates(%f, %f);", lat, lon);
                                webEngine.executeScript(script); // Exécuter le script JavaScript pour mettre à jour la carte
                            } else {
                                System.out.println("Format de coordonnées invalide : " + line);
                            }
                        }
                    }
                }
            } else {
                System.out.println("Fichier coordinates.txt non trouvé !");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la lecture du fichier coordinates.txt : " + e.getMessage());
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

    @FXML
    private void goToSurvey() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SurveyPage.fxml"));  // New FXML page for survey
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Survey");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page du quiz : " + e.getMessage());
        }
    }
}
