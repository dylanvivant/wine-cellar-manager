package fr.cave.winecellar.ui;

import fr.cave.winecellar.model.Wine;

import javax.swing.*;
import java.awt.*;

public class WineCellRenderer extends JPanel implements ListCellRenderer<Wine> {
    private final JLabel nameLabel = new JLabel();
    private final JLabel detailsLabel = new JLabel();

    public WineCellRenderer() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Configuration des labels
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        detailsLabel.setFont(detailsLabel.getFont().deriveFont(12f));
        detailsLabel.setForeground(Color.GRAY);

        // Ajout des composants
        add(nameLabel, BorderLayout.NORTH);
        add(detailsLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Wine> list, Wine wine, int index,
            boolean isSelected, boolean cellHasFocus) {

        // Configuration du texte
        nameLabel.setText(wine.getName());
        detailsLabel.setText(String.format("%d | %d bouteille(s) | %.2f €",
                wine.getProductionYear(), wine.getQuantity(), wine.getPrice()));

        // Configuration des couleurs
        if (isSelected) {
            setBackground(new Color(63, 81, 181));
            nameLabel.setForeground(Color.WHITE);
            detailsLabel.setForeground(Color.WHITE);
        } else {
            setBackground(Color.WHITE);
            nameLabel.setForeground(Color.BLACK);
            detailsLabel.setForeground(Color.GRAY);
        }

        return this;
    }
}