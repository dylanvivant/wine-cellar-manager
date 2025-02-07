package fr.cave.winecellar.ui;

import fr.cave.winecellar.model.Wine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class WineTablePanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;

    public WineTablePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Création du modèle de table
        String[] columns = {"Nom", "Année", "Prix", "Quantité", "Description"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Création de la table avec tri
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        JLabel searchLabel = new JLabel("Rechercher : ");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Ajout des composants au panel principal
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Gestionnaire d'événements pour la recherche
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
    }

    private void search() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void updateWines(List<Wine> wines) {
        model.setRowCount(0);
        for (Wine wine : wines) {
            model.addRow(new Object[]{
                    wine.getName(),
                    wine.getProductionYear(),
                    String.format("%.2f €", wine.getPrice()),
                    wine.getQuantity(),
                    wine.getDescription()
            });
        }
    }

    public void applyFilters(int maxPrice, int minYear, String sortCriteria) {
        RowFilter<DefaultTableModel, Integer> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                double price = Double.parseDouble(entry.getValue(2).toString()
                        .replace(" €", "").replace(",", "."));
                int year = Integer.parseInt(entry.getValue(1).toString());
                return price <= maxPrice && year >= minYear;
            }
        };

        sorter.setRowFilter(filter);

        // Appliquer le tri
        if (sortCriteria != null) {
            switch (sortCriteria) {
                case "Nom" -> sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
                case "Prix (croissant)" -> sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING)));
                case "Prix (décroissant)" -> sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.DESCENDING)));
                case "Année" -> sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.DESCENDING)));
            }
        }
    }
}