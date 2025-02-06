package fr.cave.winecellar.model;

import lombok.Data;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cellar {
    private int maxCapacity;
    private List<Wine> wines;
    private double temperature;
    private double humidity;

    private int rows;
    private int columns;
    private int itemsPerLevel;
    private boolean isHeadToTail;

    public Cellar(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.wines = new ArrayList<>();
        this.temperature = 12.0; // Température par défaut en celsius
        this.humidity = 70.0;    // Humidité par défaut en pourcentage
    }

    public boolean addWine(Wine wine) {
        if (wines.size() < maxCapacity) {
            return wines.add(wine);
        }
        return false;
    }

    public boolean removeWine(Wine wine) {
        return wines.remove(wine);
    }

    public List<Wine> getAllWines() {
        return new ArrayList<>(wines);  // Retourne une copie de la liste
    }

    // Méthode pour calculer l'espace disponible
    public int getAvailableSpace() {
        return maxCapacity - wines.size();
    }

    // Méthode pour calculer la valeur totale de la cave
    public double getTotalValue() {
        return wines.stream()
                .mapToDouble(wine -> wine.getPrice() * wine.getQuantity())
                .sum();
    }
}
