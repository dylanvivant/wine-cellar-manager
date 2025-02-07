package fr.cave.winecellar;

import fr.cave.winecellar.dao.TestDataGenerator;
import fr.cave.winecellar.dao.WineDAOSQLite;
import fr.cave.winecellar.ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ajoutez cette ligne ici pour générer les données de test
        TestDataGenerator.generateTestData(new WineDAOSQLite());

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}