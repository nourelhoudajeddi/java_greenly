package tn.esprit.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.dao.ProduitDAO;
import tn.esprit.entities.Produit;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;





public class ProduitController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private ComboBox<String> rechercheCategorie;
    @FXML private TextField prixMinField;
    @FXML private TextField prixMaxField;
    @FXML private Label nomError;
    @FXML private Label prixError;
    @FXML private Label descriptionError;
    @FXML private Label categorieError;
    @FXML private FlowPane produitContainer;

    private final ProduitDAO produitDAO = new ProduitDAO();
    private Produit produitSelectionne;  // Variable pour stocker le produit sélectionné
    private final ObservableList<Produit> produitList = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList(
            "Électronique", "Vêtements", "Alimentation", "Maison", "Sport", "Autre"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categorieCombo.setItems(categories);
        rechercheCategorie.setItems(categories);
        refreshProducts();
    }

    @FXML
    private void handleAjouter() {
        if (validateInputs()) {
            try {
                Produit produit = new Produit();
                produit.setNom(nomField.getText());
                produit.setPrix(Double.parseDouble(prixField.getText()));
                produit.setDescription(descriptionField.getText());
                produit.setDate(LocalDate.now());
                produit.setCategorie(categorieCombo.getValue());

                produitDAO.insert(produit);

                // ✉️ Ajouter l'envoi de mail juste après l'insertion
                sendMail(produit.getNom(), produit.getCategorie());

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Le produit a été ajouté avec succès.");
                clearFields();
                refreshProducts();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du produit: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleModifier() {
        if (produitSelectionne != null) {  // Vérifier si un produit a été sélectionné
            if (validateInputs()) {  // Valider les champs
                try {
                    // Mettre à jour les informations du produit sélectionné
                    produitSelectionne.setNom(nomField.getText());
                    produitSelectionne.setPrix(Double.parseDouble(prixField.getText()));
                    produitSelectionne.setDescription(descriptionField.getText());
                    produitSelectionne.setCategorie(categorieCombo.getValue());

                    // Appeler la méthode pour mettre à jour le produit dans la base de données
                    produitDAO.update(produitSelectionne);

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le produit a été modifié avec succès.");
                    clearFields();  // Réinitialiser les champs
                    refreshProducts();  // Rafraîchir la liste des produits
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du produit: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun produit sélectionné", "Veuillez sélectionner un produit à modifier.");
        }
    }

    @FXML
    private void handleSupprimer() {
        showAlert(Alert.AlertType.INFORMATION, "Info", "Pour supprimer, sélectionnez directement dans la base ou améliorez le système.");
    }

    @FXML
    private void handleEffacer() {
        clearFields();
    }

    @FXML
    private void handleRechercheCategorie() {
        String categorie = rechercheCategorie.getValue();
        if (categorie != null) {
            try {
                produitList.setAll(produitDAO.findByCategorie(categorie));
                displayProducts();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRecherchePrix() {
        try {
            double min = Double.parseDouble(prixMinField.getText());
            double max = Double.parseDouble(prixMaxField.getText());
            produitList.setAll(produitDAO.findByPrixRange(min, max));
            displayProducts();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer des prix valides.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    @FXML
    private void handleReinitialiser() {
        rechercheCategorie.setValue(null);
        prixMinField.clear();
        prixMaxField.clear();
        refreshProducts();
    }

    private void refreshProducts() {
        try {
            produitList.setAll(produitDAO.getAll());
            displayProducts();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    private void displayProducts() {
        produitContainer.getChildren().clear();

        for (Produit produit : produitList) {
            produitContainer.getChildren().add(createProduitCard(produit));
        }
    }

    private VBox createProduitCard(Produit produit) {
        // Création de la carte produit
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setPrefHeight(250);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // HBox pour placer les boutons en haut à droite
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.TOP_RIGHT);

        // Bouton de modification
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        editIcon.setSize("20px");
        Button editBtn = new Button("", editIcon);
        editBtn.getStyleClass().add("button-edit"); // tu peux styliser ce bouton avec CSS si tu veux

        // Bouton de suppression
        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
        deleteIcon.setSize("20px");
        deleteIcon.setStyle("-fx-fill: red;");
        Button deleteBtn = new Button("", deleteIcon);
        deleteBtn.getStyleClass().add("button-delete");

        // Ajouter les boutons à la barre du haut
        topBar.getChildren().addAll(editBtn, deleteBtn);

        // Action bouton Modifier
        editBtn.setOnAction(event -> {
            produitSelectionne = produit;  // Stocker le produit sélectionné
            nomField.setText(produit.getNom());
            prixField.setText(String.valueOf(produit.getPrix()));
            descriptionField.setText(produit.getDescription());
            categorieCombo.setValue(produit.getCategorie());
        });

        // Action bouton Supprimer
        deleteBtn.setOnAction(event -> {
            try {
                produitDAO.delete(produit.getId());
                produitList.remove(produit);
                refreshProducts();  // Important de bien rafraîchir ici
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du produit: " + e.getMessage());
            }
        });

        // Les autres infos produit
        Label nomLabel = new Label(produit.getNom());
        Label prixLabel = new Label(String.format("%.2f DT", produit.getPrix()));
        Label descriptionLabel = new Label(produit.getDescription());
        Label categorieLabel = new Label("Catégorie : " + produit.getCategorie());

        // Ajouter tous les composants à la carte
        card.getChildren().addAll(topBar, nomLabel, prixLabel, descriptionLabel, categorieLabel);

        return card;
    }


    private void clearFields() {
        nomField.clear();
        prixField.clear();
        descriptionField.clear();
        categorieCombo.setValue(null);

        // Effacer les erreurs
        nomError.setText("");
        prixError.setText("");
        descriptionError.setText("");
        categorieError.setText("");
    }

    private boolean validateInputs() {
        boolean isValid = true;

        nomError.setText("");
        prixError.setText("");
        descriptionError.setText("");
        categorieError.setText("");

        String nom = nomField.getText().trim();
        String prixText = prixField.getText().trim();
        String description = descriptionField.getText().trim();
        String categorie = categorieCombo.getValue();

        if (nom.isEmpty()) {
            nomError.setText("Le nom est requis.");
            isValid = false;
        } else if (nom.length() < 3) {
            nomError.setText("Au moins 3 caractères requis.");
            isValid = false;
        }

        if (prixText.isEmpty()) {
            prixError.setText("Le prix est requis.");
            isValid = false;
        } else {
            try {
                double prix = Double.parseDouble(prixText);
                if (prix < 0) {
                    prixError.setText("Le prix doit être positif.");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                prixError.setText("Le prix doit être un nombre valide.");
                isValid = false;
            }
        }

        if (description.isEmpty()) {
            descriptionError.setText("La description est requise.");
            isValid = false;
        } else if (description.length() < 10) {
            descriptionError.setText("Au moins 10 caractères requis.");
            isValid = false;
        }

        if (categorie == null || categorie.trim().isEmpty()) {
            categorieError.setText("La catégorie est requise.");
            isValid = false;
        }

        return isValid;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void sendMail(String nomProduit, String categorieProduit) {
        try {
            // Paramètres du serveur SMTP
            String host = "smtp.gmail.com";
            String port = "587";
            String username = "adembenahmed51@gmail.com"; // <-- Remplacer par ton email
            String password = "kqku ggkk povn dwkv"; // <-- Remplacer par ton mot de passe d'application Gmail (PAS ton mot de passe normal !)

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            // Définir l'expéditeur avec un nom personnalisé
            message.setFrom(new InternetAddress(username, "Greenly")); // <-- Ajouter le nom "Greenly" ici
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("adembenahmed51@gmail.com")); // <-- Remplacer par l'email du destinataire
            message.setSubject("Nouveau produit ajouté : " + nomProduit);

            String content = "Bonjour,\n\n"
                    + "Un nouveau produit a été ajouté dans la boutique.\n\n"
                    + "Nom du produit : " + nomProduit + "\n"
                    + "Catégorie : " + categorieProduit + "\n\n"
                    + "Cordialement,\nVotre équipe.";

            message.setText(content);

            Transport.send(message);

            System.out.println("Email envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
