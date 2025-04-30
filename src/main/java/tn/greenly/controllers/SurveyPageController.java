package tn.greenly.controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class SurveyPageController {

    @FXML
    private WebView surveyWebView;

    // URL par défaut de ton sondage (en attendant que tu récupères l'URL via l'API)
    private static final String DEFAULT_SURVEY_URL = "https://fr.surveymonkey.com/r/N3Q5TQL";

    private static final String API_BASE_URL = "https://api.surveymonkey.com/v3/";
    private static final String ACCESS_TOKEN = "LXs5ovMHyr4wSkLSS8XATd8dXqKOod6y0wUePv2y1PXk05rZOkYNOwOMuIrZALh.c829UcOBzNNDe37AaGYQlIzmWoy5Hbaq8WXSxhDdEhzu03uxDivx-ykHRjhzy3Zf";  // Remplace par ton access token

    // Méthode pour charger l'URL du sondage via WebView
    public void initialize() {
        // Charger l'URL par défaut si on ne récupère rien de l'API
        String surveyUrl = DEFAULT_SURVEY_URL;

        // Appel à l'API pour récupérer les sondages
        try {
            surveyUrl = getSurveyUrl(); // Appel API pour récupérer l'URL du sondage
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'appel API : " + e.getMessage());
        }

        // Charger l'URL dans le WebView
        WebEngine webEngine = surveyWebView.getEngine();
        webEngine.load(surveyUrl);
    }

    // Méthode pour récupérer l'URL du sondage via l'API
    private String getSurveyUrl() throws IOException {
        String endpoint = "surveys";  // Liste des sondages
        String surveyUrl = null;

        // Créer une requête HTTP GET
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_BASE_URL + endpoint);
            request.setHeader("Authorization", "Bearer " + ACCESS_TOKEN);  // Ajouter le token d'accès

            // Exécuter la requête
            HttpResponse response = client.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            // Analyser la réponse JSON et extraire l'URL du sondage
            // Supposons que la réponse contient un champ URL (exemple)
            // Tu devras adapter cette logique en fonction de la structure réelle de la réponse de l'API

            // Exemple : Supposons que la réponse est un JSON qui contient l'URL du sondage
            // Le traitement de la réponse dépend de la structure réelle de l'API.
            // Pour le moment, on suppose qu'on récupère l'URL d'un sondage spécifique
            surveyUrl = extractSurveyUrlFromResponse(responseBody);
        }

        return surveyUrl;
    }

    // Méthode pour extraire l'URL du sondage depuis la réponse JSON (exemple simple)
    private String extractSurveyUrlFromResponse(String responseBody) {
        // À adapter selon la structure exacte de la réponse JSON
        // Par exemple, ici on cherche un champ "url" dans la réponse
        // Utilise une bibliothèque comme Gson ou Jackson pour analyser le JSON
        // Si la réponse contient l'URL du sondage, retourne-la

        // Exemple fictif de recherche d'URL dans le JSON
        // String url = ... ;
        return DEFAULT_SURVEY_URL;  // Retourne une valeur par défaut en cas d'échec
    }

    // Méthode pour revenir à la page précédente (DetailFormation)
    @FXML
    private void goBackToDetail() {
        Stage stage = (Stage) surveyWebView.getScene().getWindow();
        stage.close();  // Ferme la fenêtre actuelle et revient à la précédente
    }
}
