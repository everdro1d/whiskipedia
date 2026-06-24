/*
 * dro1dDev - created: 2025-05-08
 */

package com.everdro1d.whiskipedia.ui;

import com.everdro1d.libs.swing.ImageUtils;
import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.components.TrackingFrame;
import com.everdro1d.whiskipedia.ui.panels.RecipeDetailsPanel;
import com.everdro1d.whiskipedia.ui.panels.RecipeListSearchPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.ButtonAction.showSettingsWindow;
import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class MainWindow extends TrackingFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow tab hierarchy for organization -----------|
    public static JFrame topFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JLabel logoIconContainer;
                private JLabel titleLabel;
                private JButton settingsButton;
                private JSeparator titleSeparator;
            private JSplitPane centerPanel;
                private RecipeListSearchPanel recipeListPanel;
                private RecipeDetailsPanel recipeDetailsPanel;
            private JPanel southPanel;
            private JPanel eastPanel;
            private JPanel westPanel;

    // End of Swing components --------------------------------------------|

    // UI Text Defaults ---------------------------------------------------|
    public static String titleText = "Whiskipedia";
    // End of UI Text Defaults --------------------------------------------|

    // NOTE: font name and size for the application
    public static String fontName = "Tahoma";
    public static int fontSize = 16;
    public static final Font FONT = new Font(fontName, Font.PLAIN, fontSize);
    public static final Font BOLD_FONT = new Font(fontName, Font.BOLD, fontSize);
    public static final Font SMALL_FONT = new Font(fontName, Font.PLAIN, (fontSize - 2));

    private final int MIN_WINDOW_WIDTH = 900;
    public static final int EDGE_PADDING = 15;
    private final int MIN_WINDOW_HEIGHT = 600;


    // End of variables -----------------------------------------------------------------------------------------------|

    public MainWindow() {
        super(prefs, "mainWindow");
        // if the locale does not contain the class, add it and it's components
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            //addClassToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeWindowProperties();
        initializeGUIComponents();

        topFrame.setVisible(true);

        SwingGUI.setHandCursorToClickableComponents(topFrame);
    }

    // TODO 1: add any UI Text Defaults to these locale classes
    private void addClassToLocale() {
        Map<String, Map<String, String>> map = new TreeMap<>();
        map.put("Main", new TreeMap<>());
        Map<String, String> mainMap = map.get("Main");
        mainMap.put("titleText", titleText);

        localeManager.addClassSpecificMap("MainWindow", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("MainWindow");
        titleText = varMap.getOrDefault("titleText", titleText);
    }

    private void initializeWindowProperties() {
        topFrame = this;
        topFrame.setTitle(titleText);
        topFrame.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topFrame.setResizable(true);
    }

    private void initializeGUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        topFrame.add(mainPanel);
        {
            northPanel = new JPanel();
            northPanel.setLayout(new GridBagLayout());
            northPanel.setPreferredSize(new Dimension(MIN_WINDOW_WIDTH - (EDGE_PADDING * 2), 70));
            if (guiDebugColoring) northPanel.setBackground(Color.BLACK);
            mainPanel.add(northPanel, BorderLayout.NORTH);
            {
                GridBagConstraints northGBC = new GridBagConstraints();
                // set defaults for gbc
                northGBC.gridx = 0;
                northGBC.gridy = 0;
                northGBC.weightx = 0;
                northGBC.weighty = 1;
                northGBC.anchor = GridBagConstraints.LINE_START;
                northGBC.fill = GridBagConstraints.VERTICAL;
                northGBC.insets = new Insets(4, EDGE_PADDING, 4, 4);

                logoIconContainer = new JLabel();
                logoIconContainer.setPreferredSize(new Dimension(50, 50));
                Icon logoIcon = ImageUtils.getApplicationIcon("images/logoIcon50.png", this.getClass());
                logoIconContainer.setIcon(logoIcon);
                if (guiDebugColoring) {
                    logoIconContainer.setBackground(Color.PINK);
                    logoIconContainer.setOpaque(true);
                }
                logoIconContainer.setHorizontalAlignment(SwingConstants.CENTER);
                northPanel.add(logoIconContainer, northGBC);

                northGBC.gridx++;
                northGBC.weightx = 1;
                northGBC.fill = GridBagConstraints.BOTH;
                northGBC.insets = new Insets(4, 4, 4, 4);

                titleLabel = new JLabel(titleText);
                titleLabel.setFont(new Font(fontName, Font.BOLD, fontSize + 16));
                titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
                if (guiDebugColoring) {
                    titleLabel.setBackground(Color.YELLOW);
                    titleLabel.setOpaque(true);
                }
                northPanel.add(titleLabel, northGBC);

                // Add settings button
                northGBC.gridx++; // increments the column of gbc
                northGBC.weightx = 0; // take up as little as possible horizontal fill space
                northGBC.fill = GridBagConstraints.NONE; // dont try to autofill space
                northGBC.anchor = GridBagConstraints.LINE_END; // anchor at the right-most corner
                northGBC.insets = new Insets(4, 4, 4, EDGE_PADDING - 8);

                settingsButton = new JButton();
                settingsButton.setPreferredSize(new Dimension(50, 50));
                Icon settingsIcon = ImageUtils.getApplicationIcon("images/settings.png", this.getClass());
                settingsButton.setIcon(settingsIcon);
                settingsButton.setBorderPainted(false);
                if (guiDebugColoring) settingsButton.setBackground(Color.PINK);
                settingsButton.setContentAreaFilled(guiDebugColoring);
                northPanel.add(settingsButton, northGBC);

                settingsButton.addActionListener(e -> showSettingsWindow());

                // Add a separator
                northGBC.weightx = 0; // this resets the gbc
                northGBC.gridx = 0; // ^
                northGBC.gridy++; // moves the gbc down a row
                northGBC.gridwidth = 3; // number of columns the separator will take
                northGBC.weightx = 1.0; // horizontal area fill weight for the separator
                northGBC.fill = GridBagConstraints.HORIZONTAL; // try to autofill the area
                northGBC.anchor = GridBagConstraints.CENTER; // center the separator
                northGBC.insets = new Insets(4, EDGE_PADDING, 4, EDGE_PADDING);

                titleSeparator = new JSeparator();
                titleSeparator.setPreferredSize(new Dimension(MIN_WINDOW_WIDTH - (EDGE_PADDING * 4), 4));
                northPanel.add(titleSeparator, northGBC);
            }

            centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            {
                recipeListPanel = new RecipeListSearchPanel();
                centerPanel.setLeftComponent(recipeListPanel);

                recipeDetailsPanel = new RecipeDetailsPanel();
                centerPanel.setRightComponent(recipeDetailsPanel);
            }

            southPanel = new JPanel();
            southPanel.setLayout(new GridBagLayout());
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                GridBagConstraints southGBC = new GridBagConstraints();
                southGBC.gridx = 0;
                southGBC.gridy = 0;
                southGBC.weightx = 0;
                southGBC.weighty = 1;
                southGBC.anchor = GridBagConstraints.CENTER;
                southGBC.fill = GridBagConstraints.HORIZONTAL;
                southGBC.insets = new Insets(4, 4, 4, 4);

                // Add components to southPanel
                JPanel spacer = new JPanel();
                spacer.setMinimumSize(new Dimension(50, 50));
                if (guiDebugColoring) spacer.setBackground(Color.GREEN);
                southPanel.add(spacer, southGBC);

                //TODO is this panel still needed if we put recipe
                //     mgmt buttons by the search util buttons?
            }

            eastPanel = new JPanel();
            eastPanel.setMinimumSize(new Dimension(EDGE_PADDING, 10));
            if (guiDebugColoring) eastPanel.setBackground(Color.RED);
            mainPanel.add(eastPanel, BorderLayout.EAST);

            westPanel = new JPanel();
            westPanel.setMinimumSize(new Dimension(EDGE_PADDING, 10));
            if (guiDebugColoring) westPanel.setBackground(Color.RED);
            mainPanel.add(westPanel, BorderLayout.WEST);
        }
    }

    public void darkModeSwitch() {
        SwingGUI.switchLightOrDarkMode(darkMode, windowFrameArray);

        Icon i = ImageUtils.changeIconColor(
                settingsButton.getIcon(),
                UIManager.getColor("RootPane.foreground")
        );
        settingsButton.setIcon(i);

        recipeListPanel.getSearchBar().setForeground(
                UIManager.getColor("RootPane.foreground")
        );
    }

    public int getMinimumWindowWidth() {
        return MIN_WINDOW_WIDTH;
    }

    public int getMinimumWindowHeight() {
        return MIN_WINDOW_HEIGHT;
    }

    public int getCenterPanelDividerLocation() {
        return centerPanel.getDividerLocation();
    }

    public void setCenterPanelDividerLocation(int l) {
        centerPanel.setDividerLocation(l);
    }

    public RecipeDetailsPanel getRecipeDetailsPanel() {
        return recipeDetailsPanel;
    }
}
