package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.dao.ProduitDAO;
import tn.esprit.entities.Produit;
import javafx.scene.control.Alert.AlertType;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProduitController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private TableView<Produit> produitTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nomColumn;
    @FXML private TableColumn<Produit, Double> prixColumn;
    @FXML private TableColumn<Produit, String> descriptionColumn;
    @FXML private TableColumn<Produit, LocalDate> dateColumn;
    @FXML private TableColumn<Produit, String> categorieColumn;
    @FXML private ComboBox<String> rechercheCategorie;
    @FXML private TextField prixMinField;
    @FXML private TextField prixMaxField;

    private final ProduitDAO produitDAO = new ProduitDAO();
    private final ObservableList<Produit> produitList = FXCollections.observableArrayList();
    private final ObservableList<String> categories = FXCollections.observableArrayList(
            "Électronique", "Vêtements", "Alimentation", "Maison", "Sport", "Autre"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prixColumn.setCellFactory(tc -> new TableCell<Produit, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DT", price));
                }
            }
        });
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(tc -> new TableCell<Produit, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
        
        // Configuration des catégories
        categorieCombo.setItems(categories);
        rechercheCategorie.setItems(categories);
        
        // Chargement des produits
        refreshTable();
        
        // Listener pour la sélection dans la table
        produitTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nomField.setText(newSelection.getNom());
                prixField.setText(String.valueOf(newSelection.getPrix()));
                descriptionField.setText(newSelection.getDescription());
                categorieCombo.setValue(newSelection.getCategorie());
            }
        });
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
                showAlert(AlertType.INFORMATION, "Succès", "Le produit a été ajouté avec succès.");
                clearFields();
                refreshTable();
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du produit: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleModifier() {
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showAlert(AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à modifier.");
            return;
        }

        if (validateInputs()) {
            try {
                selectedProduit.setNom(nomField.getText());
                selectedProduit.setPrix(Double.parseDouble(prixField.getText()));
                selectedProduit.setDescription(descriptionField.getText());
                selectedProduit.setCategorie(categorieCombo.getValue());

                produitDAO.update(selectedProduit);
                showAlert(AlertType.INFORMATION, "Succès", "Le produit a été modifié avec succès.");
                clearFields();
                refreshTable();
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la modification du produit: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSupprimer() {
        Produit selectedProduit = produitTable.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showAlert(AlertType.WARNING, "Attention", "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                produitDAO.delete(selectedProduit.getId());
                showAlert(AlertType.INFORMATION, "Succès", "Le produit a été supprimé avec succès.");
                clearFields();
                refreshTable();
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la suppression du produit: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEffacer() {
        clearFields();
        produitTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRechercheCategorie() {
        String categorie = rechercheCategorie.getValue();
        if (categorie != null) {
            try {
                produitList.setAll(produitDAO.findByCategorie(categorie));
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRecherchePrix() {
        try {
            double min = Double.parseDouble(prixMinField.getText());
            double max = Double.parseDouble(prixMaxField.getText());
            produitList.setAll(produitDAO.findByPrixRange(min, max));
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Attention", "Veuillez entrer des prix valides.");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    @FXML
    private void handleReinitialiser() {
        rechercheCategorie.setValue(null);
        prixMinField.clear();
        prixMaxField.clear();
        refreshTable();
    }

    private void refreshTable() {
        try {
            produitList.setAll(produitDAO.getAll());
            produitTable.setItems(produitList);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    private void clearFields() {
        nomField.clear();
        prixField.clear();
        descriptionField.clear();
        categorieCombo.setValue(null);
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errorMessage.append("Le nom est requis.\n");
        }

        try {
            double prix = Double.parseDouble(prixField.getText());
            if (prix < 0) {
                errorMessage.append("Le prix doit être positif.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Le prix doit être un nombre valide.\n");
        }

        if (descriptionField.getText().trim().isEmpty()) {
            errorMessage.append("La description est requise.\n");
        }

        if (categorieCombo.getValue() == null) {
            errorMessage.append("La catégorie est requise.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(AlertType.WARNING, "Validation", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 