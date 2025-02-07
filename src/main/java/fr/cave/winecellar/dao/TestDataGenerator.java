package fr.cave.winecellar.dao;

import fr.cave.winecellar.model.Wine;
import java.time.LocalDate;
import java.util.Random;

public class TestDataGenerator {
    private static final String[] WINE_NAMES = {
            "Château Margaux", "Château Latour", "Château Haut-Brion", "Château Mouton Rothschild",
            "Château Lafite Rothschild", "Romanée-Conti", "Château Pétrus", "Domaine Leroy",
            "Screaming Eagle", "Château d'Yquem", "Opus One", "Château Palmer"
    };

    private static final String[] DESCRIPTIONS = {
            "Vin rouge puissant et équilibré", "Notes de fruits rouges et d'épices",
            "Arômes de cassis et de vanille", "Tanins soyeux et belle longueur",
            "Bouquet complexe et élégant", "Structure remarquable et finale persistante"
    };

    public static void generateTestData(WineDAO wineDAO) {
        Random random = new Random();

        for (int i = 0; i < 150; i++) {
            Wine wine = new Wine();

            // Nom du vin avec année pour assurer l'unicité
            String baseName = WINE_NAMES[random.nextInt(WINE_NAMES.length)];
            wine.setName(baseName + " " + (char)('A' + random.nextInt(3)));

            // Année entre 2000 et 2023
            wine.setProductionYear(2000 + random.nextInt(24));

            // Prix entre 10 et 1000 euros
            wine.setPrice(10 + random.nextDouble() * 990);

            // Quantité entre 1 et 20 bouteilles
            wine.setQuantity(1 + random.nextInt(20));

            // Description aléatoire
            wine.setDescription(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);

            // Date d'achat
            wine.setPurchaseDate(LocalDate.now().minusDays(random.nextInt(365)));

            // Sauvegarde dans la base de données
            wineDAO.create(wine);
        }
    }
}