package tn.greenly.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import tn.greenly.entites.Module;
import tn.greenly.entites.Formation;
import tn.greenly.services.FormationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DetailsModuleController {

    @FXML
    private Label nomModuleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label nbHeuresLabel;
    @FXML
    private Label niveauLabel;
    @FXML
    private Label categorieLabel;
    @FXML
    private Label statutLabel;
    @FXML
    private Label dateCreationLabel;

    @FXML
    private Button btnmodule;

    @FXML
    private Button btnformation;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label statistiquesLabel;

    @FXML
    private Button btnPuzzle;

    public void initData(Module module) {
        // Vérifier si le module est valide
        if (module == null || module.getId() == 0) {
            System.out.println("Module invalide.");
            return; // ou afficher un message d'erreur à l'utilisateur
        }

        try {
            nomModuleLabel.setText(module.getNomModule());
            descriptionLabel.setText(module.getDescriptionModule());
            nbHeuresLabel.setText(String.valueOf(module.getNbHeures()));
            niveauLabel.setText(module.getNiveau());
            categorieLabel.setText(module.getCategorie());
            statutLabel.setText(module.isStatut() ? "Actif" : "Inactif");
            dateCreationLabel.setText(module.getDatecreationModule().toString());

            FormationService formationService = new FormationService();
            List<Formation> formations = formationService.getFormationsParModule(module); // appeller la méthode

            int dureeTotaleFormations = formations.stream()
                    .mapToInt(Formation::getDureeFormation)
                    .sum();

            int nbHeuresModule = module.getNbHeures();  // ou bien somme totale de plusieurs modules si besoin

            String statistiqueTexte;
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            if (nbHeuresModule > 0) {
                double ratio = (double) dureeTotaleFormations / nbHeuresModule;
                int pourcentage = (int) (ratio * 100);

                statistiqueTexte = "Les formations représentent " + pourcentage + "% de la durée totale de module.";

                pieChartData.add(new PieChart.Data("Les formations", pourcentage));
                pieChartData.add(new PieChart.Data("Reste de module", 100 - pourcentage));
            } else {
                statistiqueTexte = "Aucun module défini ou durée de module nulle.";
                pieChartData.add(new PieChart.Data("Aucune donnée", 100));
            }

            pieChart.setData(pieChartData);
            statistiquesLabel.setText(statistiqueTexte);


        } catch (SQLException e) {
            e.printStackTrace();
            statistiquesLabel.setText("Erreur lors de la récupération des formations.");
        }
    }

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
    private void goToPuzzle() {
        try {
            // Charger le fichier FXML du puzzle
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/puzzle.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la vue du puzzle
            Scene scene = new Scene(root, 800, 600);

            // Obtenir la fenêtre actuelle (Stage)
            Stage stage = (Stage) btnPuzzle.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la vue du puzzle : " + e.getMessage());
        }
    }



}
