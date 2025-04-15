package tn.esprit.dao;

import tn.esprit.entities.Commande;
import tn.esprit.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO implements CrudDAO<Commande> {
    
    @Override
    public void insert(Commande commande) throws SQLException {
        String sql = "INSERT INTO commande (numerot, produit_id, montant, date, statut) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, commande.getNumerot());
            pstmt.setInt(2, commande.getProduitId());
            pstmt.setDouble(3, commande.getMontant());
            pstmt.setDate(4, java.sql.Date.valueOf(commande.getDateCommande()));
            pstmt.setString(5, commande.getStatut());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Commande commande) throws SQLException {
        String sql = "UPDATE commande SET numerot = ?, produit_id = ?, montant = ?, date = ?, statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, commande.getNumerot());
            pstmt.setInt(2, commande.getProduitId());
            pstmt.setDouble(3, commande.getMontant());
            pstmt.setDate(4, java.sql.Date.valueOf(commande.getDateCommande()));
            pstmt.setString(5, commande.getStatut());
            pstmt.setInt(6, commande.getId());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM commande WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Commande getOne(int id) throws SQLException {
        String sql = "SELECT * FROM commande WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCommandeFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Commande> getAll() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                commandes.add(extractCommandeFromResultSet(rs));
            }
        }
        return commandes;
    }

    private Commande extractCommandeFromResultSet(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setId(rs.getInt("id"));
        commande.setNumerot(rs.getString("numerot"));
        commande.setProduitId(rs.getInt("produit_id"));
        commande.setMontant(rs.getDouble("montant"));
        java.sql.Date sqlDate = rs.getDate("date");
        if (sqlDate != null) {
            commande.setDateCommande(sqlDate.toLocalDate());
        }
        commande.setStatut(rs.getString("statut"));
        return commande;
    }

    // Méthodes de recherche supplémentaires
    public List<Commande> findByStatut(String statut) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE statut = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(extractCommandeFromResultSet(rs));
                }
            }
        }
        return commandes;
    }

    public List<Commande> findByDateRange(Date debut, Date fin) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, debut);
            pstmt.setDate(2, fin);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(extractCommandeFromResultSet(rs));
                }
            }
        }
        return commandes;
    }
} 