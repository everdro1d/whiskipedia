package com.everdro1d.whiskipedia.ui.panels;

import com.everdro1d.libs.io.Files;
import com.everdro1d.whiskipedia.core.MainWorker;
import com.everdro1d.whiskipedia.core.RecipeObject;
import com.everdro1d.whiskipedia.core.RecipeWorker;
import com.everdro1d.whiskipedia.ui.MainWindow;
import com.everdro1d.whiskipedia.ui.dialogs.ImageViewerDialog;
import com.everdro1d.whiskipedia.ui.dialogs.MetadataDialog;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class RecipeDetailsPanel extends JPanel {
    public static ImageViewerDialog imageViewerDialog;

    private CardLayout cardLayout;
    private JLabel detailsEmptyLabel;
    private JPanel detailsPanel;
        private JLabel recipeTitleLabel;
        private JTextArea descriptionDisplayArea;
        private JSplitPane ingredientInstructionsContainer;
            private JScrollPane ingredientsScrollPane;
                private DefaultListModel<RecipeObject.Ingredient> ingredientsModel;
                private JList<RecipeObject.Ingredient> ingredientsList;
            private JScrollPane instructionsScrollPane;
                private JTextArea instructionsDisplayArea;
        private JPanel detailsExtrasPanel;
            private JLabel numberServingsLabel;
        private JPanel detailsButtonPanel;
            private JButton viewImagesButton;
            private JButton viewFilesButton;
            private JButton viewMetaButton;

    // UI Text Defaults ---
    private String recipeDetailsTitleText = "Recipe Details";
    private String detailsEmptyLabelText = "No Recipe Selected";
    private String descriptionAreaTitleText = "Description";
    private String ingredientsPaneTitleText = "Ingredients List";
    private String instructionsPaneTitleText = "Instructions";
    private String numberServingsLabelText = "Number of servings: %s";
    private String viewMetaButtonText = "View Metadata";
    private String viewFilesButtonText = "View Files";
    private String viewImagesButtonText = "View Images";

    // Other ---
    private final int MIN_PANEL_WIDTH = 515;
    private final int MIN_PANEL_HEIGHT = 470;

    public RecipeDetailsPanel() {
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                .contains("RecipeDetailsPanel")
        ) {
            //addComponentToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeGUIComponents();

        setRecipeDetails(RecipeWorker.selectedRecipe[0]);
    }

    private void addComponentToLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("recipeDetailsTitleText", recipeDetailsTitleText);
        map.put("ingredientsPaneTitleText", ingredientsPaneTitleText);
        map.put("instructionsPaneTitleText", instructionsPaneTitleText);
        map.put("descriptionAreaTitleText", descriptionAreaTitleText);
        map.put("detailsEmptyLabelText", detailsEmptyLabelText);
        map.put("numberServingsLabelText", numberServingsLabelText);
        map.put("viewMetaButtonText", viewMetaButtonText);
        map.put("viewFilesButtonText", viewFilesButtonText);
        map.put("viewImagesButtonText", viewImagesButtonText);


        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            localeManager.addClassSpecificMap("MainWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("MainWindow", "RecipeDetailsPanel", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "RecipeDetailsPanel");
        recipeDetailsTitleText = varMap.getOrDefault("recipeDetailsTitleText", recipeDetailsTitleText);
        ingredientsPaneTitleText = varMap.getOrDefault("ingredientsPaneTitleText", ingredientsPaneTitleText);
        instructionsPaneTitleText = varMap.getOrDefault("instructionsPaneTitleText", instructionsPaneTitleText);
        descriptionAreaTitleText = varMap.getOrDefault("descriptionAreaTitleText", descriptionAreaTitleText);
        detailsEmptyLabelText = varMap.getOrDefault("detailsEmptyLabelText", detailsEmptyLabelText);
        numberServingsLabelText = varMap.getOrDefault("numberServingsLabelText", numberServingsLabelText);
        viewMetaButtonText = varMap.getOrDefault("viewMetaButtonText", viewMetaButtonText);
        viewFilesButtonText = varMap.getOrDefault("viewFilesButtonText", viewFilesButtonText);
        viewImagesButtonText = varMap.getOrDefault("viewImagesButtonText", viewImagesButtonText);

    }

    private void initializeGUIComponents() {
        this.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, MIN_PANEL_HEIGHT));
        this.setBorder(BorderFactory.createTitledBorder(recipeDetailsTitleText));

        cardLayout = new CardLayout();
        this.setLayout(cardLayout);

        detailsEmptyLabel = new JLabel(detailsEmptyLabelText);
        detailsEmptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsEmptyLabel.setVerticalAlignment(SwingConstants.CENTER);
        detailsEmptyLabel.setFont(new Font(MainWindow.fontName, Font.PLAIN, MainWindow.fontSize + 10));

        detailsPanel = new JPanel(new GridBagLayout());
        if (guiDebugColoring) detailsPanel.setBackground(Color.BLUE);

        this.add(detailsPanel, "DETAILS");
        this.add(detailsEmptyLabel, "EMPTY");
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 0;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.BOTH;

            recipeTitleLabel = new JLabel("Placeholder");
            recipeTitleLabel.setFont(new Font(MainWindow.fontName, Font.BOLD, MainWindow.fontSize + 10));
            recipeTitleLabel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
            if (guiDebugColoring) recipeTitleLabel.setBackground(Color.RED);
            detailsPanel.add(recipeTitleLabel, c);

            //TODO add edit button in right hand corner (icon)

            c.gridy++;

            descriptionDisplayArea = new JTextArea("Placeholder");
            descriptionDisplayArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(descriptionAreaTitleText), BorderFactory.createEmptyBorder(0,2,2,2)));
            descriptionDisplayArea.setEditable(false);
            descriptionDisplayArea.setFocusable(false);
            descriptionDisplayArea.setLineWrap(true);
            descriptionDisplayArea.setWrapStyleWord(true);
            descriptionDisplayArea.setFont(MainWindow.FONT);
            if (guiDebugColoring) descriptionDisplayArea.setBackground(Color.PINK);
            detailsPanel.add(descriptionDisplayArea, c);

            c.gridy++;
            c.weighty = 1;

            //TODO add a convert button, pressing opens a conversion dialog
            // and will replace the ingredient in the list with the converted one
            // double click on list item should have the same effect?

            ingredientInstructionsContainer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            if (guiDebugColoring) ingredientInstructionsContainer.setBackground(Color.ORANGE);
            detailsPanel.add(ingredientInstructionsContainer, c);
            {
                int min_width = (int)(MIN_PANEL_WIDTH/2.5);
                int min_height = MIN_PANEL_HEIGHT/3;

                // --- Ingredients ---
                //TODO add button to convert ingredient units -> dialog
                // combobox-ingredient : label-amount -> combobox-new-unit
                // use options convert permanent (replace ingredients in recipe object)
                // or convert temporary (copy a temp recipe object, replace ingr., show in details, toss when done)
                // to this end, wrap ingredients in a panel and add a button below

                ingredientsModel = new DefaultListModel<>();

                ingredientsList = new JList<>(ingredientsModel);
                ingredientsList.setFont(MainWindow.FONT);
                if (guiDebugColoring) ingredientsList.setBackground(Color.YELLOW);

                ingredientsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                ingredientsList.setFocusable(false);
                ingredientsList.setLayoutOrientation(JList.VERTICAL_WRAP);

                ingredientsScrollPane = new JScrollPane(ingredientsList);
                ingredientsScrollPane.setBorder(BorderFactory.createTitledBorder(ingredientsPaneTitleText));
                ingredientsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                ingredientsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                ingredientsScrollPane.setMinimumSize(new Dimension(min_width, min_height));
                ingredientInstructionsContainer.setLeftComponent(ingredientsScrollPane);

                // --- Instructions ---
                instructionsScrollPane = new JScrollPane();
                instructionsScrollPane.setBorder(BorderFactory.createTitledBorder(instructionsPaneTitleText));
                instructionsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                instructionsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                instructionsScrollPane.setMinimumSize(new Dimension(min_width, min_height));
                {

                    instructionsDisplayArea = new JTextArea();
                    instructionsDisplayArea.setEditable(false);
                    instructionsDisplayArea.setFocusable(false);
                    instructionsDisplayArea.setLineWrap(true);
                    instructionsDisplayArea.setWrapStyleWord(true);
                    instructionsDisplayArea.setFont(MainWindow.FONT);
                    if (guiDebugColoring) instructionsDisplayArea.setBackground(Color.GREEN);
                    instructionsScrollPane.setViewportView(instructionsDisplayArea);
                }
                ingredientInstructionsContainer.setRightComponent(instructionsScrollPane);
            }

            c.weighty = 0;
            c.gridy++;

            detailsExtrasPanel = new JPanel();
            detailsExtrasPanel.setLayout(new GridBagLayout());
            detailsExtrasPanel.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
            detailsPanel.add(detailsExtrasPanel, c);
            {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 0;
                gbc.weighty = 1;
                gbc.anchor = GridBagConstraints.LINE_START;
                gbc.fill = GridBagConstraints.BOTH;

                numberServingsLabel = new JLabel(String.format(numberServingsLabelText, "1"));
                numberServingsLabel.setEnabled(true);
                numberServingsLabel.setFont(new Font(MainWindow.fontName, Font.ITALIC, MainWindow.SMALL_FONT.getSize()));
                numberServingsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4)); // fix: italic font lets trailing px get cutoff
                detailsExtrasPanel.add(numberServingsLabel, gbc);

                gbc.gridx++;
                gbc.weightx = 1;
                detailsExtrasPanel.add(new JPanel(), gbc);
                gbc.gridx++;
                gbc.weightx = 0;

                detailsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                detailsExtrasPanel.add(detailsButtonPanel, gbc);
                {
                    viewImagesButton = new JButton(viewImagesButtonText);
                    viewImagesButton.setFont(MainWindow.SMALL_FONT);
                    viewImagesButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                        imageViewerDialog = new ImageViewerDialog();
                        MainWorker.windowFrameArray[3] = imageViewerDialog;
                    }));
                    detailsButtonPanel.add(viewImagesButton);

                    viewFilesButton = new JButton(viewFilesButtonText);
                    viewFilesButton.setFont(MainWindow.SMALL_FONT);
                    viewFilesButton.addActionListener(e -> {
                        String s = File.separator;
                        String path = recipeRepositoryPath + s + RecipeWorker.selectedRecipe[0] + s + "files";
                        Files.openInFileManager(path);
                    });
                    detailsButtonPanel.add(viewFilesButton);

                    viewMetaButton = new JButton(viewMetaButtonText);
                    viewMetaButton.setFont(MainWindow.SMALL_FONT);
                    viewMetaButton.addActionListener(e -> {
                        MetadataDialog d = new MetadataDialog();
                    });
                    detailsButtonPanel.add(viewMetaButton);
                }

            }

            //TODO add buttons (right side) for viewing images, files, notes, etc.
            // use dialogs to show these
            // see https://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
            // for image viewer example

        }
    }

    public void setRecipeDetails(String key) {
        // Fetch details of recipe object ---
        RecipeObject r = RecipeWorker.getRecipeIDTrie().get(key);

        if (r == null) {
            cardLayout.show(this, "EMPTY");
            return;
        } else {
            cardLayout.show(this, "DETAILS");
        }

        recipeTitleLabel.setText(r.getName());

        descriptionDisplayArea.setText(r.getDescription());

        ingredientsModel.removeAllElements();
        ingredientsModel.addAll(r.getIngredients());

        instructionsDisplayArea.setText(r.getInstructions());

        numberServingsLabel.setText(String.format(numberServingsLabelText, r.getNumServings()));

        // Finish ---
        instructionsDisplayArea.setCaretPosition(0);

        this.revalidate();
        this.repaint();
    }
}
