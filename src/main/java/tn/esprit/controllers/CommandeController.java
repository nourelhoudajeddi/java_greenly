package tn.esprit.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.controllers.AdminDashboardController;
import tn.esprit.dao.CommandeDAO;
import tn.esprit.dao.ProduitDAO;
import tn.esprit.entities.Commande;
import tn.esprit.entities.Produit;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CommandeController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statutFilter;
    @FXML private DatePicker dateFilter;
    @FXML private TableView<Commande> commandesTable;
    @FXML private TableColumn<Commande, String> numeroCommandeCol;
    @FXML private TableColumn<Commande, String> produitCol;
    @FXML private TableColumn<Commande, Double> montantCol;
    @FXML private TableColumn<Commande, LocalDate> dateCol;
    @FXML private TableColumn<Commande, String> statutCol;
    @FXML private TableColumn<Commande, Void> actionsCol;
    @FXML private Label enAttenteLbl;
    @FXML private Label enCoursLbl;
    @FXML private Label livreesLbl;
    @FXML private Label totalLbl;
    @FXML private Label totalCommandesLbl;
    @FXML private Label montantTotalLbl;
    @FXML private VBox modificationForm;
    @FXML private TextField modifNumeroCommande;
    @FXML private ComboBox<Produit> modifProduit;
    @FXML private TextField modifMontant;
    @FXML private ComboBox<String> modifStatut;
    @FXML private DatePicker modifDate;

    private final CommandeDAO commandeDAO = new CommandeDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final ObservableList<Commande> commandes = FXCollections.observableArrayList();
    private FilteredList<Commande> filteredCommandes;
    private Commande selectedCommande;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupFilters();
        loadCommandes();
        setupModificationForm();
    }

    private void setupTable() {
        numeroCommandeCol.setCellValueFactory(new PropertyValueFactory<>("numerot"));
        
        produitCol.setCellValueFactory(cellData -> {
            try {
                Produit produit = produitDAO.getOne(cellData.getValue().getProduitId());
                return new SimpleStringProperty(produit != null ? produit.getNom() : "");
            } catch (SQLException e) {
                return new SimpleStringProperty("");
            }
        });
        
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        montantCol.setCellFactory(tc -> new TableCell<Commande, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DT", price));
                }
                getStyleClass().add("amount-cell");
            }
        });
        
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        dateCol.setCellFactory(tc -> new TableCell<Commande, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                getStyleClass().add("date-cell");
            }
        });
        
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        statutCol.setCellFactory(tc -> new TableCell<Commande, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                } else {
                    setText(statut);
                    getStyleClass().removeAll("status-pending", "status-processing", "status-completed", "status-cancelled");
                    switch (statut) {
                        case "En attente" -> getStyleClass().add("status-pending");
                        case "En cours" -> getStyleClass().add("status-processing");
                        case "Livrée" -> getStyleClass().add("status-completed");
                        case "Annulée" -> getStyleClass().add("status-cancelled");
                    }
                }
            }
        });
        
        setupActionsColumn();
    }

    private void setupFilters() {
        statutFilter.getItems().addAll(
            "Tous les statuts",
            "En attente",
            "En cours",
            "Livrée",
            "Annulée"
        );
        statutFilter.setValue("Tous les statuts");
        
        statutFilter.setOnAction(e -> filterCommandes());
        dateFilter.setOnAction(e -> filterCommandes());
        searchField.textProperty().addListener((obs, old, newValue) -> filterCommandes());
    }

    private void loadCommandes() {
        try {
            commandes.setAll(commandeDAO.getAll());
            filteredCommandes = new FilteredList<>(commandes);
            commandesTable.setItems(filteredCommandes);
            updateStatistics();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Erreur lors du chargement des commandes: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        int enAttente = 0, enCours = 0, livrees = 0;
        double montantTotal = 0;

        for (Commande c : filteredCommandes) {
            switch (c.getStatut()) {
                case "En attente": enAttente++; break;
                case "En cours": enCours++; break;
                case "Livrée": livrees++; break;
            }
            montantTotal += c.getMontant();
        }

        enAttenteLbl.setText(String.valueOf(enAttente));
        enCoursLbl.setText(String.valueOf(enCours));
        livreesLbl.setText(String.valueOf(livrees));
        totalLbl.setText(String.valueOf(filteredCommandes.size()));
        
        totalCommandesLbl.setText("Total des commandes : " + filteredCommandes.size());
        montantTotalLbl.setText(String.format("Montant total : %.2f DT", montantTotal));
    }

    private void setupActionsColumn() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("", new FontAwesomeIconView(FontAwesomeIcon.PENCIL));
            private final Button deleteBtn = new Button("", new FontAwesomeIconView(FontAwesomeIcon.TRASH));
            private final HBox actionsContainer = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("button-edit");
                deleteBtn.getStyleClass().add("button-delete");
                actionsContainer.getStyleClass().add("actions-container");
                
                editBtn.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    showModificationForm(commande);
                });

                deleteBtn.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    handleDelete(commande);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionsContainer);
                }
            }
        });
    }

    private void filterCommandes() {
        String searchText = searchField.getText().toLowerCase();
        String statut = statutFilter.getValue();
        LocalDate filterDate = dateFilter.getValue();

        filteredCommandes.setPredicate(commande -> {
            boolean matchSearch = searchText == null || searchText.isEmpty() ||
                                commande.getNumerot().toLowerCase().contains(searchText);
            
            boolean matchStatut = "Tous les statuts".equals(statut) ||
                                commande.getStatut().equals(statut);
            
            boolean matchDate = filterDate == null ||
                              commande.getDateCommande().equals(filterDate);

            return matchSearch && matchStatut && matchDate;
        });

        updateStatistics();
    }

    private void showModificationForm(Commande commande) {
        selectedCommande = commande;
        modifNumeroCommande.setText(commande.getNumerot());
        try {
            Produit produit = produitDAO.getOne(commande.getProduitId());
            modifProduit.setValue(produit);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                     "Erreur lors du chargement du produit: " + e.getMessage());
        }
        modifMontant.setText(String.valueOf(commande.getMontant()));
        modifStatut.setValue(commande.getStatut());
        modifDate.setValue(commande.getDateCommande());
        modificationForm.setVisible(true);
    }

    @FXML
    private void handleSaveModification() {
        try {
            if (selectedCommande != null && modifProduit.getValue() != null) {
                selectedCommande.setNumerot(modifNumeroCommande.getText());
                selectedCommande.setProduitId(modifProduit.getValue().getId());
                selectedCommande.setMontant(Double.parseDouble(modifMontant.getText()));
                selectedCommande.setStatut(modifStatut.getValue());
                selectedCommande.setDateCommande(modifDate.getValue());

                commandeDAO.update(selectedCommande);
                loadCommandes();
                closeModificationForm();
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                         "La commande a été modifiée avec succès.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Erreur lors de la modification de la commande: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Le montant doit être un nombre valide.");
        }
    }

    private void handleDelete(Commande commande) {
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
                loadCommandes();
                showAlert(Alert.AlertType.INFORMATION, "Succès",
                         "La commande a été supprimée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                         "Erreur lors de la suppression de la commande: " + e.getMessage());
            }
        }
    }

    @FXML
    private void closeModificationForm() {
        modificationForm.setVisible(false);
        selectedCommande = null;
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        statutFilter.setValue("Tous les statuts");
        dateFilter.setValue(null);
        filterCommandes();
    }

    @FXML
    private void retourCatalogue() {
        AdminDashboardController adminController = (AdminDashboardController)
            commandesTable.getScene().getWindow().getUserData();
        if (adminController != null) {
            adminController.showProduits();
        }
    }

    private void setupModificationForm() {
        try {
            // Configuration du ComboBox des produits
            List<Produit> produits = produitDAO.getAll();
            modifProduit.setItems(FXCollections.observableArrayList(produits));
            modifProduit.setCellFactory(p -> new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit produit, boolean empty) {
                    super.updateItem(produit, empty);
                    if (empty || produit == null) {
                        setText(null);
                    } else {
                        setText(produit.getNom());
                    }
                }
            });
            modifProduit.setButtonCell(new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit produit, boolean empty) {
                    super.updateItem(produit, empty);
                    if (empty || produit == null) {
                        setText(null);
                    } else {
                        setText(produit.getNom());
                    }
                }
            });

            // Configuration du ComboBox des statuts
            modifStatut.setItems(FXCollections.observableArrayList(
                "En attente", "En cours", "Livrée", "Annulée"
            ));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 