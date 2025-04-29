package tn.esprit.dao;

import tn.esprit.entities.Produit;
import tn.esprit.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ProduitDAO implements CrudDAO<Produit> {
    
    @Override
    public void insert(Produit produit) throws SQLException {
        String sql = "INSERT INTO produit (nom, prix, description, date, categorie) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, produit.getNom());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setString(3, produit.getDescription());
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));
            pstmt.setString(5, produit.getCategorie());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Produit produit) throws SQLException {
        String sql = "UPDATE produit SET nom = ?, prix = ?, description = ?, categorie = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produit.getNom());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setString(3, produit.getDescription());
            pstmt.setString(4, produit.getCategorie());
            pstmt.setInt(5, produit.getId());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Produit getOne(int id) throws SQLException {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractProduitFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Produit> getAll() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(extractProduitFromResultSet(rs));
            }
        }
        return produits;
    }

    private Produit extractProduitFromResultSet(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("id"));
        produit.setNom(rs.getString("nom"));
        produit.setPrix(rs.getDouble("prix"));
        produit.setDescription(rs.getString("description"));
        produit.setDate(rs.getDate("date").toLocalDate());
        produit.setCategorie(rs.getString("categorie"));
        return produit;
    }

    // Méthodes de recherche supplémentaires
    public List<Produit> findByCategorie(String categorie) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE categorie = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categorie);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(extractProduitFromResultSet(rs));
                }
            }
        }
        return produits;
    }

    public List<Produit> findByPrixRange(double min, double max) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE prix BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, min);
            pstmt.setDouble(2, max);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(extractProduitFromResultSet(rs));
                }
            }
        }
        return produits;
    }



    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT categorie FROM produit WHERE categorie IS NOT NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("categorie"));
            }
        }
        return categories;
    }

    public void updateCategorie(String oldCategorie, String newCategorie) throws SQLException {
        String sql = "UPDATE produit SET categorie = ? WHERE categorie = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newCategorie);
            pstmt.setString(2, oldCategorie);
            pstmt.executeUpdate();
        }
    }

    public void deleteCategorie(String categorie) throws SQLException {
        String sql = "UPDATE produit SET categorie = NULL WHERE categorie = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categorie);
            pstmt.executeUpdate();
        }
    }
} 