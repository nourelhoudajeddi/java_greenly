package tn.greenly.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.greenly.entites.Formation;
import tn.greenly.services.FormationService;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AfficherFormations {

    @FXML
    private TableView<Formation> moduleTable;

    @FXML
    private TableColumn<Formation, Integer> colID;
    @FXML
    private TableColumn<Formation, String> colNomFormation;
    @FXML
    private TableColumn<Formation, String> colDescription;
    @FXML
    private TableColumn<Formation, Integer> colDuree;
    @FXML
    private TableColumn<Formation, String> colMode;
    @FXML
    private TableColumn<Formation, String> colDateDebut;
    @FXML
    private TableColumn<Formation, String> colDateFin;
    @FXML
    private TableColumn<Formation, String> colNomModule;
    @FXML
    private TableColumn<Formation, Void> colBtnModifier;
    @FXML
    private TableColumn<Formation, Void> colBtnSupprimer;

    @FXML
    private Button btnajf;
    @FXML
    private Button btnmodule;
    @FXML
    private Button btnformation;

    @FXML
    private TextField recherchef;

    @FXML
    private ComboBox<String> trifrorm; // attributs
    @FXML
    private ComboBox<String> trifor;

    @FXML
    private TableView<Formation> formationTable; // Déclare la TableView pour afficher les formations


    private final FormationService formationService = new FormationService();

    @FXML
    private Button btnExportPdf;

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colNomFormation.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nomFormation"));
        colDescription.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("descriptionFormation"));
        colDuree.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dureeFormation"));
        colMode.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("modeFormation"));
        colDateDebut.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dateDebutFormation"));
        colDateFin.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dateFinFormation"));
        colNomModule.setCellValueFactory(new PropertyValueFactory<>("nomModule"));

        addModifierButtonToTable();
        addSupprimerButtonToTable();

        loadFormations();
        trifrorm.getItems().addAll("Nom", "Durée", "Date début", "Date fin", "Mode");
        trifor.getItems().addAll("Ascendant", "Descendant");

        trifrorm.setValue("Nom");
        trifor.setValue("Ascendant");

        btnExportPdf = new Button("Exporter en PDF");
        btnExportPdf.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-cursor: hand;");
        btnExportPdf.setOnAction(event -> {
            try {
                exporterPDF();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        HBox topContainer = (HBox) btnajf.getParent(); // important
        topContainer.getChildren().add(btnExportPdf);



    }
    private void creerPageHTMLPourFormation(Formation formation) throws IOException {
        String fileName = "formation_" + formation.getId() + ".html";
        String htmlContent = "<html><head><title>" + formation.getNomFormation() + "</title></head><body>" +
                "<h1>" + formation.getNomFormation() + "</h1>" +
                "<p><strong>Description:</strong> " + formation.getDescriptionFormation() + "</p>" +
                "<p><strong>Durée:</strong> " + formation.getDureeFormation() + " heures</p>" +
                "<p><strong>Mode:</strong> " + formation.getModeFormation() + "</p>" +
                "<p><strong>Date début:</strong> " + formation.getDateDebutFormation() + "</p>" +
                "<p><strong>Date fin:</strong> " + formation.getDateFinFormation() + "</p>" +
                "<h2>Vidéo sur le recyclage :</h2>" +
                "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/TON_ID_YOUTUBE\" frameborder=\"0\" allowfullscreen></iframe>" +
                "</body></html>";

        java.nio.file.Files.write(java.nio.file.Paths.get(fileName), htmlContent.getBytes());
    }

    private void exporterPDF() {
        Document document = new Document();
        try {
            String outputPath = "formations_export.pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // En-tête avec logo
            Image logo = Image.getInstance("C:/Users/Nour/Downloads/pidev3A50S/piedev-java/logo.png");
            logo.scaleToFit(100, 100); // Ajuste la taille de l'image si nécessaire
            logo.setAlignment(Element.ALIGN_LEFT);
            document.add(logo);

            // Titre
            Paragraph title = new Paragraph("Historique des Formations", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Ligne vide

            // Table avec des couleurs et bordures
            PdfPTable table = new PdfPTable(7); // 7 colonnes
            table.setWidthPercentage(100); // Largeur du tableau
            table.setSpacingBefore(10f); // Espacement avant le tableau
            table.setSpacingAfter(10f); // Espacement après le tableau

            // Définir les en-têtes de la table
            table.addCell(createHeaderCell("Nom Formation"));
            table.addCell(createHeaderCell("Description"));
            table.addCell(createHeaderCell("Durée"));
            table.addCell(createHeaderCell("Mode"));
            table.addCell(createHeaderCell("Date Début"));
            table.addCell(createHeaderCell("Date Fin"));
            table.addCell(createHeaderCell("Nom Module"));

            // Ajouter les données dans la table
            for (Formation formation : formationTable.getItems()) {
                table.addCell(createDataCell(formation.getNomFormation()));
                table.addCell(createDataCell(formation.getDescriptionFormation()));
                table.addCell(createDataCell(formation.getDureeFormation() + " heures"));
                table.addCell(createDataCell(formation.getModeFormation()));
                table.addCell(createDataCell(formation.getDateDebutFormation().toString()));
                table.addCell(createDataCell(formation.getDateFinFormation().toString()));
                table.addCell(createDataCell(formation.getNomModule()));
            }

            document.add(table);

            // Pied de page
            Paragraph footer = new Paragraph("Exporté le : " + java.time.LocalDate.now(), new Font(Font.FontFamily.HELVETICA, 10));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // Message de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export PDF");
            alert.setHeaderText(null);
            alert.setContentText("Exportation réussie : " + outputPath);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        cell.setBackgroundColor(BaseColor.CYAN); // Fond bleu clair
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createDataCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }


    private void loadFormations() {
        try {
            List<Formation> formations = formationService.recuperer();
            ObservableList<Formation> observableFormations = FXCollections.observableArrayList(formations);
            formationTable.setItems(observableFormations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addModifierButtonToTable() {
        colBtnModifier.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Formation formation = getTableView().getItems().get(getIndex());
                    ouvrirPageModifier(formation); // Appel de la méthode qui ouvre la page de modification
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
    private void ouvrirPageModifier(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFormation.fxml"));
            Parent root = loader.load();

            ModifierFormationController controller = loader.getController();
            controller.initData(formation); // envoie les données de la formation à modifier

            // Changer la scène
            Stage stage = (Stage) formationTable.getScene().getWindow(); // <-- correction ici
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void addSupprimerButtonToTable() {
        colBtnSupprimer.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");

                btn.setOnAction(event -> {
                    Formation formation = getTableView().getItems().get(getIndex());
                    try {
                        formationService.supprimer(formation);
                        loadFormations(); // recharger après suppression
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void handleAjouterFormation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFormation.fxml"));
            Parent root = loader.load();
            btnajf.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAfficherModules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherModules.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnmodule.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAfficherFormations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFormations.fxml"));
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
        private void rechercherf() {
            String keyword = recherchef.getText().toLowerCase();

            try {
                // Récupère toutes les formations
                List<Formation> formations = formationService.recuperer(); // Assurez-vous d'avoir une méthode pour récupérer les formations
                List<Formation> filteredFormations = formations.stream()
                        .filter(formation ->
                                formation.getNomFormation().toLowerCase().contains(keyword) ||  // Recherche dans le nom de la formation
                                        formation.getDescriptionFormation().toLowerCase().contains(keyword) ||  // Recherche dans la description
                                        String.valueOf(formation.getDureeFormation()).contains(keyword) ||  // Recherche dans la durée de formation
                                        formation.getModeFormation().toLowerCase().contains(keyword) ||  // Recherche dans le mode de formation
                                        (formation.getDateDebutFormation() != null && formation.getDateDebutFormation().toString().contains(keyword)) ||  // Recherche dans la date de début
                                        (formation.getDateFinFormation() != null && formation.getDateFinFormation().toString().contains(keyword)) ||  // Recherche dans la date de fin
                                        (formation.getModule() != null && formation.getModule().getNomModule().toLowerCase().contains(keyword))  // Recherche dans le nom du module
                        )
                        .toList();

                // Met à jour la table avec les formations filtrées
                formationTable.setItems(FXCollections.observableArrayList(filteredFormations)); // Remplir la TableView avec les données filtrées
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    @FXML
    private void trier(ActionEvent event) {
        String attribut = trifrorm.getValue();
        String ordre = trifor.getValue();

        if (attribut == null || ordre == null) return;

        Comparator<Formation> comparator = null;

        switch (attribut) {
            case "Nom":
                comparator = Comparator.comparing(Formation::getNomFormation, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Durée":
                comparator = Comparator.comparingInt(Formation::getDureeFormation);
                break;
            case "Date début":
                comparator = Comparator.comparing(Formation::getDateDebutFormation);
                break;
            case "Date fin":
                comparator = Comparator.comparing(Formation::getDateFinFormation);
                break;
            case "Mode":
                comparator = Comparator.comparing(Formation::getModeFormation, String.CASE_INSENSITIVE_ORDER);
                break;
        }


            if (comparator != null) {
                if ("Descendant".equalsIgnoreCase(ordre)) { // ignore case
                    comparator = comparator.reversed();
                }
            }
            List<Formation> sortedList = new ArrayList<>(formationTable.getItems());
            sortedList.sort(comparator);
            formationTable.getItems().setAll(sortedList);
        }
    }






