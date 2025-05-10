/*
 * dro1dDev - created: 2025-05-08
 */

package com.everdro1d.whiskipedia.ui;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.ButtonAction.showSettingsWindow;
import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class MainWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow tab hierarchy for organization -----------|
    public static JFrame topFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JLabel logoIconContainer;
                private JLabel titleLabel;
                private JButton settingsButton;
                private JSeparator titleSeparator;
            private JPanel centerPanel;
                private JPanel recipePanel;
            private JPanel southPanel;
            private JPanel eastPanel;
            private JPanel westPanel;
                private JPanel recipeListPanel;

    // End of Swing components --------------------------------------------|

    // UI Text Defaults ---------------------------------------------------|
    public static String titleText = "Whiskipedia";
    // End of UI Text Defaults --------------------------------------------|

    // NOTE: font name and size for the application
    public static String fontName = "Tahoma";
    public static int fontSize = 16;
    public static final Font font = new Font(fontName, Font.PLAIN, fontSize);
    public static final Font boldFont = new Font(fontName, Font.BOLD, fontSize);
    public static final Font smallFont = new Font(fontName, Font.PLAIN, (fontSize - 2));

    private final int MIN_WINDOW_WIDTH = 900;
    private final int EDGE_PADDING = 15;
    private final int MIN_WINDOW_HEIGHT = 600;


    // End of variables -----------------------------------------------------------------------------------------------|

    public MainWindow() {
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
        topFrame.setLocationRelativeTo(null);

        topFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                windowPosition = SwingGUI.getFramePositionOnScreen(topFrame);
            }
        });
    }

    private void initializeGUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        topFrame.add(mainPanel);
        {
            northPanel = new JPanel();
            northPanel.setLayout(new GridBagLayout());
            northPanel.setPreferredSize(new Dimension(MIN_WINDOW_WIDTH - (EDGE_PADDING * 2), 60));
            GridBagConstraints northGBC = new GridBagConstraints();
            // set defaults for gbc
            northGBC.gridx = 0;
            northGBC.gridy = 0;
            northGBC.weightx = 0;
            northGBC.weighty = 1;
            northGBC.anchor = GridBagConstraints.LINE_START;
            northGBC.fill = GridBagConstraints.VERTICAL;
            northGBC.insets = new Insets(4, EDGE_PADDING, 4, 4);
            mainPanel.add(northPanel, BorderLayout.NORTH);
            {
                logoIconContainer = new JLabel();
                logoIconContainer.setPreferredSize(new Dimension(50, 50));
                Icon logoIcon = SwingGUI.getApplicationIcon("images/logoIcon50.png", this.getClass());
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
                titleLabel.setFont(new Font(fontName, Font.BOLD, fontSize + 12));
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

                settingsButton = new JButton();
                settingsButton.setPreferredSize(new Dimension(50, 50));
                Icon settingsIcon = SwingGUI.getApplicationIcon("images/settings.png", this.getClass());
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
                northGBC.gridwidth = 2; // number of columns the separator will take
                northGBC.weightx = 1.0; // horizontal area fill weight for the separator
                northGBC.fill = GridBagConstraints.HORIZONTAL; // try to autofill the area
                northGBC.anchor = GridBagConstraints.CENTER; // center the separator

                titleSeparator = new JSeparator();
                titleSeparator.setPreferredSize(new Dimension(MIN_WINDOW_WIDTH - (EDGE_PADDING * 4), 4));
                northPanel.add(titleSeparator, northGBC);
            }

            centerPanel = new JPanel();
            centerPanel.setLayout(new GridBagLayout());
            GridBagConstraints centerGBC = new GridBagConstraints();
            centerGBC.gridx = 0;
            centerGBC.gridy = 0;
            centerGBC.weightx = 0;
            centerGBC.weighty = 1;
            centerGBC.anchor = GridBagConstraints.CENTER;
            centerGBC.fill = GridBagConstraints.HORIZONTAL;
            centerGBC.insets = new Insets(4, 4, 4, 4);
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            {
                // Add recipe panel
                centerGBC.gridx++;
                centerGBC.weightx = 1;
                centerGBC.fill = GridBagConstraints.BOTH;

                recipePanel = new JPanel();
                recipePanel.setLayout(new BorderLayout());
                if (guiDebugColoring) recipePanel.setBackground(Color.BLUE);
                centerPanel.add(recipePanel, centerGBC);
            }

            southPanel = new JPanel();
            southPanel.setLayout(new GridBagLayout());
            GridBagConstraints southGBC = new GridBagConstraints();
            southGBC.gridx = 0;
            southGBC.gridy = 0;
            southGBC.weightx = 0;
            southGBC.weighty = 1;
            southGBC.anchor = GridBagConstraints.CENTER;
            southGBC.fill = GridBagConstraints.HORIZONTAL;
            southGBC.insets = new Insets(4, 4, 4, 4);
            mainPanel.add(southPanel, BorderLayout.SOUTH);
            {
                // Add components to southPanel
                JPanel spacer = new JPanel();
                spacer.setMinimumSize(new Dimension(50, 50));
                if (guiDebugColoring) spacer.setBackground(Color.GREEN);
                southPanel.add(spacer, southGBC);
            }

            eastPanel = new JPanel();
            eastPanel.setMinimumSize(new Dimension(EDGE_PADDING, 10));
            if (guiDebugColoring) eastPanel.setBackground(Color.RED);
            mainPanel.add(eastPanel, BorderLayout.EAST);

            westPanel = new JPanel();
            westPanel.setMinimumSize(new Dimension(EDGE_PADDING, 10));
            if (guiDebugColoring) westPanel.setBackground(Color.RED);
            mainPanel.add(westPanel, BorderLayout.WEST);
            //TODO add placeholder recipeListPanel
        }
    }

    public void darkModeSwitch() {
        SwingGUI.switchLightOrDarkMode(darkMode, windowFrameArray);

        Icon i = SwingGUI.changeIconColor(
                settingsButton.getIcon(),
                UIManager.getColor("RootPane.foreground")
        );
        settingsButton.setIcon(i);
    }
}
