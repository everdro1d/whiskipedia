/*
 * dro1dDev 2025 - SwingGUI Templates - MainWindow.java
 */

package main.com.everdro1d.swingtemplate.ui;

import com.everdro1d.libs.swing.SwingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.TreeMap;

import static main.com.everdro1d.swingtemplate.core.ButtonAction.showSettingsWindow;
import static main.com.everdro1d.swingtemplate.core.MainWorker.*;

public class MainWindow extends JFrame {
    // Variables ------------------------------------------------------------------------------------------------------|

    // Swing components - Follow tab hierarchy for organization -----------|
    public static JFrame topFrame;
        private JPanel mainPanel;
            private JPanel northPanel;
                private JLabel titleLabel;
                private JButton settingsButton;
                private JSeparator titleSeparator;
            private JPanel centerPanel;
                private JLabel exampleLabel;
                private JButton darkModeButton;

    // End of Swing components --------------------------------------------|

    // UI Text Defaults ---------------------------------------------------|
    // TODO 1: add to locale default methods
    public static String titleText = "SwingGUI Application Template";
    public static String darkModeButtonText = "Dark Mode Switch";
    // End of UI Text Defaults --------------------------------------------|

    // NOTE: font name and size for the application
    public static String fontName = "Tahoma";
    public static int fontSize = 16;
    public static final Font font = new Font(fontName, Font.PLAIN, fontSize);
    public static final Font boldFont = new Font(fontName, Font.BOLD, fontSize);
    public static final Font smallFont = new Font(fontName, Font.PLAIN, (fontSize - 2));

    private final int WINDOW_WIDTH = 600;
    private final int EDGE_PADDING = 15;
    private final int WINDOW_HEIGHT = 400;


    // End of variables -----------------------------------------------------------------------------------------------|

    public MainWindow() {
        // if the locale does not contain the class, add it and it's components
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            addClassToLocale();
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
        mainMap.put("darkModeButtonText", darkModeButtonText);

        localeManager.addClassSpecificMap("MainWindow", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getAllVariablesWithinClassSpecificMap("MainWindow");
        titleText = varMap.getOrDefault("titleText", titleText);
        darkModeButtonText = varMap.getOrDefault("darkModeButtonText", darkModeButtonText);
    }

    /**
     * Initialize the window.
     */
    private void initializeWindowProperties() {
        topFrame = this;
        topFrame.setTitle(titleText);
        topFrame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topFrame.setResizable(false); // TODO: resizeable?
        topFrame.setLocationRelativeTo(null);

        topFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                windowPosition = SwingGUI.getFramePositionOnScreen(topFrame);
            }
        });
    }

    /**
     * Initialize the GUI components.
     */
    private void initializeGUIComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        topFrame.add(mainPanel);
        {
            // TODO: add components here
            northPanel = new JPanel();
            northPanel.setLayout(new GridBagLayout());
            northPanel.setPreferredSize(new Dimension(WINDOW_WIDTH - (EDGE_PADDING * 2), 60));
            GridBagConstraints northGBC = new GridBagConstraints();
            // set defaults for gbc
            northGBC.gridx = 0;
            northGBC.gridy = 0;
            northGBC.weightx = 0;
            northGBC.weighty = 1;
            northGBC.anchor = GridBagConstraints.CENTER;
            northGBC.fill = GridBagConstraints.HORIZONTAL;
            northGBC.insets = new Insets(4, 4, 4, 4);
            mainPanel.add(northPanel, BorderLayout.NORTH);
            {
                // Add components to northPanel
                JPanel spacer = new JPanel();
                spacer.setMinimumSize(new Dimension(50, 50));
                northPanel.add(spacer, northGBC);

                // Add title text
                northGBC.gridx++; // increment gbc to second column
                northGBC.weightx = 1; // fill as much horizontally as possible

                titleLabel = new JLabel(titleText);
                titleLabel.setFont(new Font(fontName, Font.BOLD, fontSize + 12));
                titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
                settingsButton.setContentAreaFilled(false);
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
                titleSeparator.setPreferredSize(new Dimension(WINDOW_WIDTH - (EDGE_PADDING * 4), 4));
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
                exampleLabel = new JLabel(titleText);
                exampleLabel.setFont(boldFont);
                exampleLabel.setAlignmentX(SwingConstants.CENTER);
                exampleLabel.setAlignmentY(SwingConstants.CENTER);
                centerPanel.add(exampleLabel, centerGBC);

                centerGBC.gridy++; // vertical position between components

                darkModeButton = new JButton(darkModeButtonText);
                darkModeButton.setFont(boldFont);
                darkModeButton.setAlignmentX(SwingConstants.CENTER);
                darkModeButton.setAlignmentY(SwingConstants.CENTER);
                centerPanel.add(darkModeButton, centerGBC);

                darkModeButton.addActionListener(e -> {
                    darkMode = !darkMode;
                    SwingGUI.switchLightOrDarkMode(darkMode, windowFrameArray);
                    customActionsOnDarkModeSwitch();
                });
            }
        }
    }

    public void customActionsOnDarkModeSwitch() {
        Icon i = SwingGUI.changeIconColor(
                settingsButton.getIcon(),
                UIManager.getColor("RootPane.foreground")
        );
        settingsButton.setIcon(i);
    }
}
