package com.everdro1d.whiskipedia.ui.panels;

import com.everdro1d.whiskipedia.core.RecipeObject;
import com.everdro1d.whiskipedia.core.RecipeWorker;
import com.everdro1d.whiskipedia.ui.MainWindow;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class RecipeDetailsPanel extends JPanel {

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

    // UI Text Defaults ---
    private String recipeDetailsTitleText = "Recipe Details";
    private String detailsEmptyLabelText = "No Recipe Selected";
    private String descriptionAreaTitleText = "Description";
    private String ingredientsPaneTitleText = "Ingredients List";
    private String instructionsPaneTitleText = "Instructions";

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

            //TODO add edit button in right hand corner (icon) and/or serving size label

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

        // Finish ---
        instructionsScrollPane.getViewport().setViewPosition(new Point(0, 0));

        this.revalidate();
        this.repaint();
    }
}
