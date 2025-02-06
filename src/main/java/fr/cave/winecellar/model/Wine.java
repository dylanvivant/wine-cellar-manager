package fr.cave.winecellar.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Wine {
    private Long id;
    private String name;
    private String description;
    private int quantity;
    private int productionYear;
    private LocalDate purchaseDate;
    private double price;
    private String position;
    private String notes;
    private int rating;

    // Les champs bonus
    private LocalDate expirationDate;
    private String image;
    private String agingPhase;
    private String tastingNotes;
    private String drinkingWindow;
}