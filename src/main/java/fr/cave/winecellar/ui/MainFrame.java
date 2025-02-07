package fr.cave.winecellar.ui;

import fr.cave.winecellar.dao.WineDAO;
import fr.cave.winecellar.dao.WineDAOSQLite;
import fr.cave.winecellar.model.Wine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainFrame extends JFrame {
    private final WineDAO wineDAO;
    private final JList<Wine> wineList;
    private final DefaultListModel<Wine> listModel;
    private final JPanel formPanel;
    private final StatsPanel statsPanel;
    private final WineTablePanel tablePanel;

    // Composants du formulaire
    private final JTextField nameField;
    private final JTextField yearField;
    private final JTextField priceField;
    private final JTextArea descriptionArea;
    private final JSpinner quantitySpinner;

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");

        JMenuItem exportItem = new JMenuItem("Exporter en CSV");
        exportItem.addActionListener(e -> exportToCSV());

        JMenuItem importItem = new JMenuItem("Importer depuis CSV");
        importItem.addActionListener(e -> importFromCSV());

        JMenuItem addPhotoItem = new JMenuItem("Ajouter une photo");
        addPhotoItem.addActionListener(e -> addPhoto());

        fileMenu.add(exportItem);
        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(addPhotoItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter la cave");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getPath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                // En-tête du CSV
                writer.write("Nom,Année,Prix,Quantité,Description,Date d'achat\n");

                // Données
                List<Wine> wines = wineDAO.findAll();
                for (Wine wine : wines) {
                    writer.write(String.format("%s,%d,%.2f,%d,\"%s\",%s\n",
                            wine.getName(),
                            wine.getProductionYear(),
                            wine.getPrice(),
                            wine.getQuantity(),
                            wine.getDescription().replace("\"", "\"\""),
                            wine.getPurchaseDate()
                    ));
                }

                JOptionPane.showMessageDialog(this,
                        "Export réussi !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'export : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importer un fichier CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers CSV", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String content = Files.readString(fileChooser.getSelectedFile().toPath());
                String[] lines = content.split("\n");

                // Ignorer l'en-tête
                for (int i = 1; i < lines.length; i++) {
                    String[] values = lines[i].split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (values.length >= 5) {
                        Wine wine = new Wine();
                        wine.setName(values[0].trim());
                        wine.setProductionYear(Integer.parseInt(values[1].trim()));
                        wine.setPrice(Double.parseDouble(values[2].trim()));
                        wine.setQuantity(Integer.parseInt(values[3].trim()));
                        wine.setDescription(values[4].replace("\"", "").trim());
                        if (values.length > 5) {
                            wine.setPurchaseDate(LocalDate.parse(values[5].trim()));
                        }
                        wineDAO.create(wine);
                    }
                }

                refreshWineList();
                JOptionPane.showMessageDialog(this,
                        "Import réussi !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'import : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addPhoto() {
        Wine selectedWine = wineList.getSelectedValue();
        if (selectedWine == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un vin d'abord",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une photo");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Images", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File source = fileChooser.getSelectedFile();
                String extension = source.getName().substring(source.getName().lastIndexOf('.'));
                File destination = new File("photos/" + selectedWine.getId() + extension);

                // Créer le dossier photos s'il n'existe pas
                destination.getParentFile().mkdirs();

                // Copier le fichier
                Files.copy(source.toPath(), destination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le chemin de l'image dans la base de données
                selectedWine.setImage(destination.getPath());
                wineDAO.update(selectedWine);

                JOptionPane.showMessageDialog(this,
                        "Photo ajoutée avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ajout de la photo : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public MainFrame() {

        wineDAO = new WineDAOSQLite();

        // Configuration de la fenêtre
        setTitle("Cave à Vin Manager");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        createMenuBar();

        // Création du panel principal avec des marges
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Panel de gauche avec la liste des vins
        listModel = new DefaultListModel<>();
        wineList = new JList<>(listModel);
        wineList.setFixedCellHeight(50);
        wineList.setCellRenderer(new WineCellRenderer());
        wineList.setBackground(Color.WHITE);
        wineList.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JScrollPane listScrollPane = new JScrollPane(wineList);
        listScrollPane.setPreferredSize(new Dimension(300, 0));

        // Panel des boutons sous la liste
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton addButton = createStyledButton("Ajouter");
        JButton deleteButton = createStyledButton("Supprimer");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Panel gauche combinant liste et boutons
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Formulaire à droite
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Création des champs du formulaire
        nameField = createStyledTextField();
        yearField = createStyledTextField();
        priceField = createStyledTextField();
        descriptionArea = createStyledTextArea();
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));

        // Ajout des composants au formulaire
        addFormField(formPanel, "Nom :", nameField, gbc, 0);
        addFormField(formPanel, "Année :", yearField, gbc, 1);
        addFormField(formPanel, "Prix :", priceField, gbc, 2);
        addFormField(formPanel, "Quantité :", quantitySpinner, gbc, 3);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Description :"), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // Bouton de sauvegarde
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = createStyledButton("Sauvegarder");
        formPanel.add(saveButton, gbc);

        // Création du panneau de statistiques
        statsPanel = new StatsPanel();
        JScrollPane statsScrollPane = new JScrollPane(statsPanel);
        statsScrollPane.setPreferredSize(new Dimension(300, 0));

        // Panel droit combinant formulaire et statistiques
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        rightPanel.add(formPanel);
        rightPanel.add(statsScrollPane);

        // Création des composants principaux
        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet "Vue Simple"
        JPanel simpleViewPanel = new JPanel(new BorderLayout(20, 20));
        simpleViewPanel.add(leftPanel, BorderLayout.WEST);
        simpleViewPanel.add(rightPanel, BorderLayout.CENTER);

        // Onglet "Vue Détaillée"
        JPanel detailedViewPanel = new JPanel(new BorderLayout(10, 10));
        // Dans la partie où vous créez les panneaux dans MainFrame
        tablePanel = new WineTablePanel();
        FilterPanel filterPanel = new FilterPanel();
        filterPanel.setTablePanel(tablePanel);
        detailedViewPanel.add(filterPanel, BorderLayout.WEST);
        detailedViewPanel.add(tablePanel, BorderLayout.CENTER);

        // Ajout des onglets
        tabbedPane.addTab("Vue Simple", new ImageIcon(), simpleViewPanel, "Vue simple avec liste");
        tabbedPane.addTab("Vue Détaillée", new ImageIcon(), detailedViewPanel, "Vue détaillée avec tableau");

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);

        // Événements
        addButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> saveWine());
        deleteButton.addActionListener(e -> deleteSelectedWine());
        wineList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Wine selectedWine = wineList.getSelectedValue();
                if (selectedWine != null) {
                    displayWine(selectedWine);
                }
            }
        });

        // Chargement initial des vins
        refreshWineList();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(63, 81, 181));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(5);
        return area;
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void clearForm() {
        nameField.setText("");
        yearField.setText("");
        priceField.setText("");
        descriptionArea.setText("");
        quantitySpinner.setValue(1);
        wineList.clearSelection();
    }

    private void saveWine() {
        try {
            Wine wine = new Wine();
            wine.setName(nameField.getText());
            wine.setProductionYear(Integer.parseInt(yearField.getText()));
            wine.setPrice(Double.parseDouble(priceField.getText()));
            wine.setDescription(descriptionArea.getText());
            wine.setQuantity((Integer) quantitySpinner.getValue());
            wine.setPurchaseDate(LocalDate.now());

            Long id = wineDAO.create(wine);
            if (id != null) {
                Optional<Wine> existingWine = wineDAO.findByNameAndYear(wine.getName(), wine.getProductionYear());
                if (existingWine.isPresent() && existingWine.get().getId().equals(id)) {
                    JOptionPane.showMessageDialog(this,
                            "La quantité a été mise à jour pour le vin existant : " + wine.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Nouveau vin ajouté avec succès !");
                }
                clearForm();
                refreshWineList();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez vérifier les valeurs numériques (année, prix)",
                    "Erreur de saisie",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedWine() {
        Wine selectedWine = wineList.getSelectedValue();
        if (selectedWine != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce vin ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                wineDAO.delete(selectedWine.getId());
                refreshWineList();
                clearForm();
            }
        }
    }

    private void displayWine(Wine wine) {
        nameField.setText(wine.getName());
        yearField.setText(String.valueOf(wine.getProductionYear()));
        priceField.setText(String.valueOf(wine.getPrice()));
        descriptionArea.setText(wine.getDescription());
        quantitySpinner.setValue(wine.getQuantity());
    }

    private void refreshWineList() {
        List<Wine> allWines = wineDAO.findAll();
        listModel.clear();
        allWines.forEach(listModel::addElement);
        statsPanel.updateStats(allWines);
        tablePanel.updateWines(allWines);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}