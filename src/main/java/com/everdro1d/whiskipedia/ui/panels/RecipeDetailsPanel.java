package com.everdro1d.whiskipedia.ui.panels;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class RecipeDetailsPanel extends JPanel {
    // TODO populate recipe panel contents from recipe object
    // TODO update upon selecting a new object from the list


    // UI Text Defaults ---
    private String recipeDetailsTitleText = "Recipe Details";

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
        this.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, MIN_PANEL_HEIGHT));
        this.setBorder(BorderFactory.createTitledBorder(recipeDetailsTitleText));

        this.setLayout(new GridBagLayout());
        if (guiDebugColoring) this.setBackground(Color.BLUE);
        {
            /* TODO
             * # Title
             * > Description
             * | Ingredients | Instructions |
             */
        }
    }
}
