package tn.esprit.entities;

import java.time.LocalDate;

public class Commande {
    private int id;
    private String numerot;
    private int produitId;
    private double montant;
    private LocalDate dateCommande;
    private String statut;

    public Commande() {
    }

    public Commande(String numerot, int produitId, double montant, LocalDate dateCommande, String statut) {
        this.numerot = numerot;
        this.produitId = produitId;
        this.montant = montant;
        this.dateCommande = dateCommande;
        this.statut = statut;
    }

    // Getters
    public int getId() { return id; }
    public String getNumerot() { return numerot; }
    public int getProduitId() { return produitId; }
    public double getMontant() { return montant; }
    public LocalDate getDateCommande() { return dateCommande; }
    public String getStatut() { return statut; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNumerot(String numerot) { this.numerot = numerot; }
    public void setProduitId(int produitId) { this.produitId = produitId; }
    public void setMontant(double montant) { this.montant = montant; }
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", numerot='" + numerot + '\'' +
                ", produitId=" + produitId +
                ", montant=" + montant +
                ", dateCommande=" + dateCommande +
                ", statut='" + statut + '\'' +
                '}';
    }
} 