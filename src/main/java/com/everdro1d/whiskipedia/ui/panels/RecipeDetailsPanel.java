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

    }

    private void initializeGUIComponents() {
        this.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, MIN_PANEL_HEIGHT));
        this.setBorder(BorderFactory.createTitledBorder(recipeDetailsTitleText));

        this.setLayout(new GridBagLayout());
        if (guiDebugColoring) this.setBackground(Color.BLUE);
        {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 0;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.fill = GridBagConstraints.BOTH;

            recipeTitleLabel = new JLabel("Placeholder"); //TODO add edit button in right hand corner (icon)
            recipeTitleLabel.setFont(new Font(MainWindow.fontName, Font.BOLD, MainWindow.fontSize + 10));
            recipeTitleLabel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
            if (guiDebugColoring) recipeTitleLabel.setBackground(Color.RED);
            this.add(recipeTitleLabel, c);

            c.gridy++;

            descriptionDisplayArea = new JTextArea("Placeholder");
            descriptionDisplayArea.setBorder(BorderFactory.createTitledBorder(descriptionAreaTitleText));
            descriptionDisplayArea.setEditable(false);
            descriptionDisplayArea.setFocusable(false);
            descriptionDisplayArea.setLineWrap(true);
            descriptionDisplayArea.setWrapStyleWord(true);
            descriptionDisplayArea.setFont(MainWindow.FONT);
            if (guiDebugColoring) descriptionDisplayArea.setBackground(Color.PINK);
            this.add(descriptionDisplayArea, c);

            c.gridy++;
            c.weighty = 1;

            ingredientInstructionsContainer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            if (guiDebugColoring) ingredientInstructionsContainer.setBackground(Color.ORANGE);
            this.add(ingredientInstructionsContainer, c);
            {
                int min_width = (int)(MIN_PANEL_WIDTH/2.5);
                int min_height = MIN_PANEL_HEIGHT/3;

                // --- Ingredients ---
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
        }
    }

    public void setRecipeDetails(String key) {
        // Fetch details of recipe object ---
        RecipeObject r = RecipeWorker.getRecipeIDTrie().get(key);

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
