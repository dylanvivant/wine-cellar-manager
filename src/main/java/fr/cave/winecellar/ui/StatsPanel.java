package fr.cave.winecellar.ui;

import fr.cave.winecellar.model.Wine;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsPanel extends JPanel {
    private final JLabel totalBottlesLabel;
    private final JLabel totalValueLabel;
    private final JPanel yearDistributionPanel;

    public StatsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel pour les statistiques globales
        JPanel globalStatsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        globalStatsPanel.setBackground(Color.WHITE);
        globalStatsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        totalBottlesLabel = createStyledLabel("Total des bouteilles : 0");
        totalValueLabel = createStyledLabel("Valeur totale : 0.00 €");

        globalStatsPanel.add(totalBottlesLabel);
        globalStatsPanel.add(totalValueLabel);

        // Panel pour la distribution par année
        yearDistributionPanel = new JPanel();
        yearDistributionPanel.setLayout(new BoxLayout(yearDistributionPanel, BoxLayout.Y_AXIS));
        yearDistributionPanel.setBackground(Color.WHITE);
        yearDistributionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Titre du panel
        JLabel titleLabel = new JLabel("Statistiques de la Cave");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assemblage
        add(titleLabel, BorderLayout.NORTH);
        add(globalStatsPanel, BorderLayout.CENTER);
        add(yearDistributionPanel, BorderLayout.SOUTH);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(14f));
        return label;
    }

    public void updateStats(List<Wine> wines) {
        // Calcul du total des bouteilles
        int totalBottles = wines.stream()
                .mapToInt(Wine::getQuantity)
                .sum();

        // Calcul de la valeur totale
        double totalValue = wines.stream()
                .mapToDouble(wine -> wine.getPrice() * wine.getQuantity())
                .sum();

        // Mise à jour des labels
        totalBottlesLabel.setText(String.format("Total des bouteilles : %d", totalBottles));
        totalValueLabel.setText(String.format("Valeur totale : %.2f €", totalValue));

        // Distribution par année
        Map<Integer, Long> yearDistribution = wines.stream()
                .collect(Collectors.groupingBy(
                        Wine::getProductionYear,
                        Collectors.summingLong(Wine::getQuantity)
                ));

        // Mise à jour du panel de distribution
        yearDistributionPanel.removeAll();
        yearDistributionPanel.add(createStyledLabel("Distribution par année :"));
        yearDistributionPanel.add(Box.createVerticalStrut(10));

        yearDistribution.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(java.util.Comparator.reverseOrder()))
                .forEach(entry -> {
                    JPanel yearPanel = new JPanel(new BorderLayout());
                    yearPanel.setBackground(Color.WHITE);
                    yearPanel.add(new JLabel(entry.getKey().toString()), BorderLayout.WEST);

                    // Création d'une barre de progression pour visualiser la quantité
                    JProgressBar progressBar = new JProgressBar(0, totalBottles);
                    progressBar.setValue(entry.getValue().intValue());
                    progressBar.setStringPainted(true);
                    progressBar.setString(entry.getValue() + " bouteilles");
                    progressBar.setPreferredSize(new Dimension(200, 20));
                    yearPanel.add(progressBar, BorderLayout.CENTER);

                    yearDistributionPanel.add(yearPanel);
                    yearDistributionPanel.add(Box.createVerticalStrut(5));
                });

        revalidate();
        repaint();
    }
}