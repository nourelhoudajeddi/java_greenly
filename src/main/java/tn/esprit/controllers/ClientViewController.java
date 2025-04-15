package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import tn.esprit.dao.CommandeDAO;
import tn.esprit.dao.ProduitDAO;
import tn.esprit.entities.Commande;
import tn.esprit.entities.Produit;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;

public class ClientViewController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieFilter;
    @FXML private Slider prixSlider;
    @FXML private Label prixLabel;
    @FXML private GridPane produitsGrid;
    @FXML private VBox catalogueView;
    @FXML private VBox commandesView;
    @FXML private TableView<Commande> commandesTable;
    @FXML private TableColumn<Commande, String> numeroCommandeCol;
    @FXML private TableColumn<Commande, String> produitCol;
    @FXML private TableColumn<Commande, Double> montantCol;
    @FXML private TableColumn<Commande, LocalDate> dateCol;
    @FXML private TableColumn<Commande, String> statutCol;
    @FXML private TableColumn<Commande, Void> actionsCol;

    private final ProduitDAO produitDAO = new ProduitDAO();
    private final CommandeDAO commandeDAO = new CommandeDAO();
    private final ObservableList<Produit> produits = FXCollections.observableArrayList();
    private final ObservableList<Commande> commandes = FXCollections.observableArrayList();
    
    // ID utilisateur simulé (à remplacer par l'authentification réelle)
    private final String userId = "USER123";

    private FilteredList<Produit> filteredProduits;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupFilters();
        loadProduits();
        setupCommandesTable();
        setupSearch();
        
        // Mettre à jour le label de prix lorsque le slider change
        prixSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            prixLabel.setText(String.format("%.0f DT", newVal.doubleValue()));
            filterProduits();
        });
    }

    private void setupFilters() {
        // Configuration du filtre de catégorie
        try {
            // Récupérer toutes les catégories de la base de données
            List<String> categories = produitDAO.getAllCategories();
            categories.add(0, "Toutes les catégories");
            categorieFilter.getItems().setAll(categories);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des catégories: " + e.getMessage());
        }
        
        categorieFilter.setValue("Toutes les catégories");
        categorieFilter.setOnAction(e -> filterProduits());

        // Configuration du slider de prix
        prixSlider.setMin(0);
        prixSlider.setMax(1000);
        prixSlider.setValue(1000);
        prixSlider.valueProperty().addListener((obs, oldVal, newVal) -> filterProduits());

        // Initialize FilteredList
        filteredProduits = new FilteredList<>(produits, p -> true);
    }

    private void loadProduits() {
        try {
            produits.setAll(produitDAO.getAll());
            filterProduits(); // This will trigger the display update
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    private void afficherProduits() {
        produitsGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        
        for (Produit produit : filteredProduits) {
            VBox produitCard = createProduitCard(produit);
            produitsGrid.add(produitCard, column, row);
            
            column++;
            if (column > 2) { // 3 produits par ligne
                column = 0;
                row++;
            }
        }
    }

    private VBox createProduitCard(Produit produit) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);

        // Image du produit (placeholder)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        
        // Utiliser une image par défaut ou charger depuis une URL
        try {
            // Essayer de charger une image depuis une URL (à remplacer par votre logique)
            Image image = new Image("https://via.placeholder.com/180?text=" + produit.getNom());
            imageView.setImage(image);
        } catch (Exception e) {
            // En cas d'erreur, utiliser une icône
            FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.SHOPPING_BAG);
            icon.setSize("100");
            icon.setStyle("-fx-fill: #3498db;");
            card.getChildren().add(icon);
        }

        // Informations du produit
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        
        Label nomLabel = new Label(produit.getNom());
        nomLabel.getStyleClass().add("card-title");
        nomLabel.setWrapText(true);
        nomLabel.setMaxWidth(280);
        nomLabel.setAlignment(Pos.CENTER);

        HBox prixBox = new HBox(5);
        prixBox.setAlignment(Pos.CENTER);
        Label prixLabel = new Label(String.format("%.2f DT", produit.getPrix()));
        prixLabel.getStyleClass().add("price-label");
        prixBox.getChildren().add(prixLabel);

        Label descriptionLabel = new Label(produit.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(280);
        descriptionLabel.setMaxHeight(80);
        descriptionLabel.setAlignment(Pos.CENTER);

        // Catégorie
        HBox categorieBox = new HBox(5);
        categorieBox.setAlignment(Pos.CENTER);
        FontAwesomeIconView tagIcon = new FontAwesomeIconView(FontAwesomeIcon.TAG);
        tagIcon.setSize("12");
        tagIcon.setStyle("-fx-fill: #7f8c8d;");
        Label categorieLabel = new Label(produit.getCategorie());
        categorieLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        categorieBox.getChildren().addAll(tagIcon, categorieLabel);

        // Bouton Commander
        Button commanderBtn = new Button("Commander");
        commanderBtn.getStyleClass().add("button-primary");
        commanderBtn.setMaxWidth(Double.MAX_VALUE);
        commanderBtn.setPadding(new Insets(10, 0, 10, 0));
        commanderBtn.setOnAction(e -> handleCommander(produit));

        // Ajouter tous les éléments à la carte
        if (imageView.getImage() != null) {
            card.getChildren().add(imageView);
        }
        infoBox.getChildren().addAll(nomLabel, prixBox, descriptionLabel, categorieBox);
        card.getChildren().addAll(infoBox, commanderBtn);
        
        return card;
    }

    private void setupCommandesTable() {
        numeroCommandeCol.setCellValueFactory(new PropertyValueFactory<>("numerot"));
        produitCol.setCellValueFactory(cellData -> {
            try {
                Produit produit = produitDAO.getOne(cellData.getValue().getProduitId());
                return new javafx.beans.property.SimpleStringProperty(produit != null ? produit.getNom() : "");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        setupActionsColumn();
        loadCommandes();
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("", new FontAwesomeIconView(FontAwesomeIcon.TRASH));

            {
                deleteBtn.getStyleClass().add("button-danger");
                deleteBtn.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    handleSupprimerCommande(commande);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, deleteBtn);
                    actions.setAlignment(Pos.CENTER);
                    setGraphic(actions);
                }
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProduits();
        });
    }

    private void filterProduits() {
        String categorie = categorieFilter.getValue();
        double maxPrix = prixSlider.getValue();
        String searchText = searchField.getText();
        
        filteredProduits.setPredicate(produit -> {
            boolean matchCategorie = categorie.equals("Toutes les catégories") || 
                                   produit.getCategorie().equals(categorie);
            boolean matchPrix = produit.getPrix() <= maxPrix;
            boolean matchSearch = searchText == null || searchText.isEmpty() ||
                                produit.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                                produit.getDescription().toLowerCase().contains(searchText.toLowerCase());
            
            return matchCategorie && matchPrix && matchSearch;
        });
        
        afficherProduits();
    }

    @FXML
    private void handleCommander(Produit produit) {
        try {
            Commande commande = new Commande();
            commande.setNumerot(generateNumeroCommande());
            commande.setProduitId(produit.getId());
            commande.setMontant(produit.getPrix());
            commande.setDateCommande(LocalDate.now());
            commande.setStatut("En attente");
            
            commandeDAO.insert(commande);
            loadCommandes();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Commande effectuée avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la commande: " + e.getMessage());
        }
    }

    private void handleSupprimerCommande(Commande commande) {
        if (!"En attente".equals(commande.getStatut())) {
            showAlert(Alert.AlertType.WARNING, "Attention", 
                     "Vous ne pouvez supprimer que les commandes en attente.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la commande");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette commande ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                commandeDAO.delete(commande.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La commande a été supprimée avec succès.");
                loadCommandes();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Erreur lors de la suppression de la commande: " + e.getMessage());
            }
        }
    }

    private void loadCommandes() {
        try {
            commandes.setAll(commandeDAO.getAll());
            commandesTable.setItems(commandes);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Erreur lors du chargement des commandes: " + e.getMessage());
        }
    }

    @FXML
    private void showMesCommandes() {
        catalogueView.setVisible(false);
        commandesView.setVisible(true);
        loadCommandes();
    }

    @FXML
    private void retourCatalogue() {
        commandesView.setVisible(false);
        catalogueView.setVisible(true);
    }

    @FXML
    private void resetFilters() {
        categorieFilter.setValue("Toutes les catégories");
        prixSlider.setValue(prixSlider.getMax());
        searchField.clear();
        loadProduits();
    }

    private String generateNumeroCommande() {
        return "CMD" + System.currentTimeMillis();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 