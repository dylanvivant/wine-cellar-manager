package fr.cave.winecellar.dao;

import fr.cave.winecellar.model.Wine;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WineDAOSQLite implements WineDAO {
    private final DatabaseManager dbManager;

    public WineDAOSQLite() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Optional<Wine> findByNameAndYear(String name, int year) {
        String sql = "SELECT * FROM wine WHERE name = ? AND production_year = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(resultSetToWine(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Long create(Wine wine) {
        // Vérifier si le vin existe déjà
        Optional<Wine> existingWine = findByNameAndYear(wine.getName(), wine.getProductionYear());

        if (existingWine.isPresent()) {
            // Mettre à jour la quantité du vin existant
            Wine updatedWine = existingWine.get();
            updatedWine.setQuantity(updatedWine.getQuantity() + wine.getQuantity());
            update(updatedWine);
            return updatedWine.getId();
        }

        // Si le vin n'existe pas, créer une nouvelle entrée
        String sql = """
            INSERT INTO wine (name, description, quantity, production_year, 
                            purchase_date, price, position, notes, rating,
                            expiration_date, image, aging_phase, tasting_notes, 
                            drinking_window)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, wine.getName());
            pstmt.setString(2, wine.getDescription());
            pstmt.setInt(3, wine.getQuantity());
            pstmt.setInt(4, wine.getProductionYear());
            pstmt.setString(5, wine.getPurchaseDate() != null ? wine.getPurchaseDate().toString() : null);
            pstmt.setDouble(6, wine.getPrice());
            pstmt.setString(7, wine.getPosition());
            pstmt.setString(8, wine.getNotes());
            pstmt.setInt(9, wine.getRating());
            pstmt.setString(10, wine.getExpirationDate() != null ? wine.getExpirationDate().toString() : null);
            pstmt.setString(11, wine.getImage());
            pstmt.setString(12, wine.getAgingPhase());
            pstmt.setString(13, wine.getTastingNotes());
            pstmt.setString(14, wine.getDrinkingWindow());

            pstmt.executeUpdate();

            // SQLite specific way to get the last inserted id
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Wine> findById(Long id) {
        String sql = "SELECT * FROM wine WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(resultSetToWine(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Wine> findAll() {
        List<Wine> wines = new ArrayList<>();
        String sql = "SELECT * FROM wine";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                wines.add(resultSetToWine(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wines;
    }

    @Override
    public boolean update(Wine wine) {
        String sql = """
            UPDATE wine 
            SET name = ?, description = ?, quantity = ?, production_year = ?,
                purchase_date = ?, price = ?, position = ?, notes = ?, rating = ?,
                expiration_date = ?, image = ?, aging_phase = ?, tasting_notes = ?,
                drinking_window = ?
            WHERE id = ?
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, wine.getName());
            pstmt.setString(2, wine.getDescription());
            pstmt.setInt(3, wine.getQuantity());
            pstmt.setInt(4, wine.getProductionYear());
            pstmt.setString(5, wine.getPurchaseDate() != null ? wine.getPurchaseDate().toString() : null);
            pstmt.setDouble(6, wine.getPrice());
            pstmt.setString(7, wine.getPosition());
            pstmt.setString(8, wine.getNotes());
            pstmt.setInt(9, wine.getRating());
            pstmt.setString(10, wine.getExpirationDate() != null ? wine.getExpirationDate().toString() : null);
            pstmt.setString(11, wine.getImage());
            pstmt.setString(12, wine.getAgingPhase());
            pstmt.setString(13, wine.getTastingNotes());
            pstmt.setString(14, wine.getDrinkingWindow());
            pstmt.setLong(15, wine.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM wine WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Wine> findByYear(int year) {
        List<Wine> wines = new ArrayList<>();
        String sql = "SELECT * FROM wine WHERE production_year = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                wines.add(resultSetToWine(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wines;
    }

    private Wine resultSetToWine(ResultSet rs) throws SQLException {
        Wine wine = new Wine();
        wine.setId(rs.getLong("id"));
        wine.setName(rs.getString("name"));
        wine.setDescription(rs.getString("description"));
        wine.setQuantity(rs.getInt("quantity"));
        wine.setProductionYear(rs.getInt("production_year"));

        String purchaseDate = rs.getString("purchase_date");
        if (purchaseDate != null) {
            wine.setPurchaseDate(LocalDate.parse(purchaseDate));
        }

        wine.setPrice(rs.getDouble("price"));
        wine.setPosition(rs.getString("position"));
        wine.setNotes(rs.getString("notes"));
        wine.setRating(rs.getInt("rating"));

        String expirationDate = rs.getString("expiration_date");
        if (expirationDate != null) {
            wine.setExpirationDate(LocalDate.parse(expirationDate));
        }

        wine.setImage(rs.getString("image"));
        wine.setAgingPhase(rs.getString("aging_phase"));
        wine.setTastingNotes(rs.getString("tasting_notes"));
        wine.setDrinkingWindow(rs.getString("drinking_window"));

        return wine;
    }
}