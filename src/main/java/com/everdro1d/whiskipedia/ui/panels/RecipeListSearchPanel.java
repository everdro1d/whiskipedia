package com.everdro1d.whiskipedia.ui.panels;

import com.everdro1d.libs.swing.components.LabeledTextField;
import com.everdro1d.whiskipedia.ui.MainWindow;

import javax.swing.*;
import java.awt.*;

import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class RecipeListSearchPanel extends JPanel {
    private JPanel listUtilsPanel;
        private JPanel listUtilsMenuBar;
        private LabeledTextField searchBar;
    private JPanel listDisplayPanel;

    private final int MIN_PANEL_WIDTH = 180;

    public RecipeListSearchPanel() {
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                    .contains("RecipeListSearchPanel")
        ) {
            //addComponentToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeGUIComponents();
    }

    private void addComponentToLocale() {
        Map<String, String> map = new TreeMap<>();

        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            localeManager.addClassSpecificMap("MainWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("MainWindow", "RecipeListSearchPanel", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "RecipeListSearchPanel");

    }

    private void initializeGUIComponents() {
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, 100));
        this.setPreferredSize(new Dimension(MIN_PANEL_WIDTH, 100));
        this.setBorder(BorderFactory.createTitledBorder("Recipe List"));

        listUtilsPanel = new JPanel();
        listUtilsPanel.setLayout(new BoxLayout(listUtilsPanel, BoxLayout.Y_AXIS));
        this.add(listUtilsPanel, BorderLayout.NORTH);
        {
            listUtilsMenuBar = new JPanel();
            listUtilsMenuBar.setLayout(new GridBagLayout());
            listUtilsMenuBar.setBorder(BorderFactory.createEmptyBorder());
            listUtilsMenuBar.setPreferredSize(new Dimension(MIN_PANEL_WIDTH, 25));
            if (guiDebugColoring) listUtilsMenuBar.setBackground(Color.GREEN);
            listUtilsPanel.add(listUtilsMenuBar);
            {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                // TODO

                JButton placeholder = new JButton();
                placeholder.setPreferredSize(new Dimension(20, 20));
                listUtilsMenuBar.add(placeholder);
            }

            searchBar = new LabeledTextField("Search Recipe");
            searchBar.setFont(MainWindow.FONT);
            listUtilsPanel.add(searchBar);
        }

        listDisplayPanel = new JPanel();
        listDisplayPanel.setLayout(new BoxLayout(listDisplayPanel, BoxLayout.Y_AXIS));
        listDisplayPanel.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, 25));
        this.add(listDisplayPanel, BorderLayout.CENTER);
    }

    public LabeledTextField getSearchBar() {
        return searchBar;
    }
}
