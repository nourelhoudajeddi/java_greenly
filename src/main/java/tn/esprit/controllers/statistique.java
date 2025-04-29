package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class statistique {

    // Exemple de méthode pour ouvrir une popup avec le PieChart
    public void afficherStatistiquesCategorie(Map<String, Integer> statsCategorie) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL); // bloque la fenêtre principale
        popup.setTitle("Statistiques des catégories");

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Ajouter les données au PieChart
        for (Map.Entry<String, Integer> entry : statsCategorie.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Répartition des produits par catégorie");

        VBox root = new VBox(pieChart);
        Scene scene = new Scene(root, 500, 400);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // Exemple de récupération des stats (à adapter à ta base de données)
    public Map<String, Integer> getStatsParCategorie() {
        Map<String, Integer> stats = new HashMap<>();

        // Remplace ceci par ta logique de récupération réelle des catégories depuis ta base
        stats.put("Plantes", 10);
        stats.put("Engrais", 5);
        stats.put("Pots", 8);

        return stats;
    }

    // À appeler par un bouton ou autre :
    public void ouvrirPopupStatistiques() {
        Map<String, Integer> stats = getStatsParCategorie();
        afficherStatistiquesCategorie(stats);
    }
}
