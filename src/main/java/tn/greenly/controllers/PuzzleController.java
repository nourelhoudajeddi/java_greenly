package tn.greenly.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PuzzleController {

    @FXML
    private WebView webView; // Le WebView dans le fichier FXML

    @FXML
    public void initialize() {
        // Récupérer l'engine du WebView
        WebEngine webEngine = webView.getEngine();

        // Charger l'URL du puzzle de Jigsaw Planet
        webEngine.load("https://www.jigsawplanet.com/?rc=play&pid=033d869035ea");
    }
}
