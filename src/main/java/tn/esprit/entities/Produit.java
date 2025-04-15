package tn.esprit.entities;

import java.time.LocalDate;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private String description;
    private LocalDate date;
    private String categorie;

    public Produit() {
    }

    public Produit(int id, String nom, double prix, String description, LocalDate date, String categorie) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.date = date;
        this.categorie = categorie;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", categorie='" + categorie + '\'' +
                '}';
    }
} 