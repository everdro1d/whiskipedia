package com.everdro1d.whiskipedia.ui.panels;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class RecipeDetailsPanel extends JScrollPane {
    // TODO populate recipe panel contents from recipe object
    // TODO update upon selecting a new object from the list

    private JPanel topPanel;

    // UI Text Defaults ---
    private String recipeDetailsTitleText = "Recipe Details";

    public RecipeDetailsPanel() {
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                .contains("RecipeDetailsPanel")
        ) {
            //addComponentToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeGUIComponents();
    }

    private void addComponentToLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("recipeDetailsTitleText", recipeDetailsTitleText);


        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            localeManager.addClassSpecificMap("MainWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("MainWindow", "RecipeDetailsPanel", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "RecipeDetailsPanel");
        recipeDetailsTitleText = varMap.getOrDefault("recipeDetailsTitleText", recipeDetailsTitleText);
    }

    private void initializeGUIComponents() {
        this.setMinimumSize(new Dimension(500, 300));
        this.setBorder(BorderFactory.createTitledBorder(recipeDetailsTitleText));

        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        if (guiDebugColoring) topPanel.setBackground(Color.BLUE);

        this.setViewportView(topPanel);
    }
}
