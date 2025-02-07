package fr.cave.winecellar.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class FilterPanel extends JPanel {
    private final JSlider priceSlider;
    private final JSlider yearSlider;
    private final JComboBox<String> sortBox;
    private WineTablePanel tablePanel;

    public FilterPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Filtres",
                        TitledBorder.LEFT,
                        TitledBorder.TOP
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Filtre de prix
        JPanel pricePanel = new JPanel(new BorderLayout());
        priceSlider = new JSlider(0, 1000, 1000);
        priceSlider.setMajorTickSpacing(200);
        priceSlider.setPaintTicks(true);
        priceSlider.setPaintLabels(true);
        JLabel priceLabel = new JLabel("Prix maximum : ");
        pricePanel.add(priceLabel, BorderLayout.NORTH);
        pricePanel.add(priceSlider, BorderLayout.CENTER);

        // Filtre d'année
        JPanel yearPanel = new JPanel(new BorderLayout());
        yearSlider = new JSlider(1900, 2024, 1900);
        yearSlider.setMajorTickSpacing(20);
        yearSlider.setPaintTicks(true);
        yearSlider.setPaintLabels(true);
        JLabel yearLabel = new JLabel("Année minimum : ");
        yearPanel.add(yearLabel, BorderLayout.NORTH);
        yearPanel.add(yearSlider, BorderLayout.CENTER);

        // Tri
        JPanel sortPanel = new JPanel(new BorderLayout());
        String[] sortOptions = {"Nom", "Prix (croissant)", "Prix (décroissant)", "Année"};
        sortBox = new JComboBox<>(sortOptions);
        JLabel sortLabel = new JLabel("Trier par : ");
        sortPanel.add(sortLabel, BorderLayout.NORTH);
        sortPanel.add(sortBox, BorderLayout.CENTER);

        // Ajout des composants
        add(Box.createVerticalStrut(10));
        add(pricePanel);
        add(Box.createVerticalStrut(20));
        add(yearPanel);
        add(Box.createVerticalStrut(20));
        add(sortPanel);
        add(Box.createVerticalStrut(10));

        // Définir une taille préférée pour le panel
        setPreferredSize(new Dimension(250, 400));
    }

    public void setTablePanel(WineTablePanel tablePanel) {
        this.tablePanel = tablePanel;
        setupListeners();
    }

    private void setupListeners() {
        // Écouteur pour le slider de prix
        priceSlider.addChangeListener(e -> applyFilters());

        // Écouteur pour le slider d'année
        yearSlider.addChangeListener(e -> applyFilters());

        // Écouteur pour la combobox de tri
        sortBox.addActionListener(e -> applyFilters());
    }

    private void applyFilters() {
        if (tablePanel != null) {
            tablePanel.applyFilters(
                    priceSlider.getValue(),
                    yearSlider.getValue(),
                    (String) sortBox.getSelectedItem()
            );
        }
    }
}