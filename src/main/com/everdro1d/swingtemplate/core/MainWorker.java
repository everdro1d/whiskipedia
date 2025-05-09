/*
 * dro1dDev 2025 - SwingGUI Templates - MainWorker.java
 */

package main.com.everdro1d.swingtemplate.core;

import com.everdro1d.libs.commands.CommandInterface;
import com.everdro1d.libs.commands.CommandManager;
import com.everdro1d.libs.core.*;
import com.everdro1d.libs.locale.*;
import com.everdro1d.libs.swing.*;
import com.everdro1d.libs.swing.dialogs.UpdateCheckerDialog;
import com.everdro1d.libs.swing.windows.DebugConsoleWindow;
import main.com.everdro1d.swingtemplate.ui.MainWindow;
import main.com.everdro1d.swingtemplate.core.commands.DebugCommand;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import static main.com.everdro1d.swingtemplate.core.ButtonAction.settingsWindow;

public class MainWorker {
    // Variables ------------------------------------------------------------------------------------------------------|

    // NOTE: Update checker info
    public static final String githubRepoURL = "https://github.com/everdro1d/SwingGUIApplicationTemplate/"; // TODO: replace this with relevant repo
    public static final String devWebsite = "https://everdro1d.github.io/"; // TODO: update this for github user or pages
    public static final String currentVersion = "1.0.0"; // TODO: update me with each release
    public static final String developerConfigDirectoryName = "dro1dDev"; // TODO: update this for dev

    // NOTE: CommandManager obj for CLI args
    private static final Map<String, CommandInterface> CUSTOM_COMMANDS_MAP = Map.of(
            "-debug", new DebugCommand()
    );
    public static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);

    // NOTE: default locale & LocaleManager to handle I18n
    public static String currentLocale = "eng";
    public static final LocaleManager localeManager = new LocaleManager(MainWorker.class, developerConfigDirectoryName);

    // NOTE: debug logging output all 'sout' statements must be wrapped in 'if (debug)'
    public static boolean debug = false;
    public static DebugConsoleWindow debugConsoleWindow;

    // NOTE: preferences object for saving and loading user settings
    public static final Preferences prefs = Preferences.userNodeForPackage(MainWorker.class);

    // NOTE: default window position
    public static int[] windowPosition = {0, 0, 0};

    // NOTE: instance of MainWindow
    private static MainWindow mainWindow;

    /* NOTE: frame array exists because window title bars don't update with the LaF
     *       add any non-modal frames that exist as windows here to fix it
     *       also dont forget to set the frame here after creating it
     *       (e.g. after creating debug window: windowFrameArray[1] = DebugConsoleWindow.debugFrame;)
     */
    public static JFrame[] windowFrameArray = new JFrame[]{
            mainWindow,
            debugConsoleWindow,
            settingsWindow
    };

    // NOTE: central variables
    /**
     * Valid: "Windows", "macOS", "Unix"
     */
    public static String detectedOS;
    public static boolean darkMode = false; // TODO: only if dark mode is enabled

    // End of variables -----------------------------------------------------------------------------------------------|

    public static void main(String[] args) {
        startUpActions(args);
        startMainWindow();
    }

    private static void startUpActions(String[] args) {
        ApplicationCore.checkCLIArgs(args, commandManager);
        checkOSCompatibility();

        SwingGUI.setupLookAndFeel(true, true); // TODO: if dark mode should be setup

        SwingGUI.uiSetup(MainWindow.fontName, MainWindow.fontSize);

        loadPreferencesAndQueueSave();

        localeManager.loadLocaleFromFile(currentLocale);
        currentLocale = localeManager.getCurrentLocale();

        if (debug) {
            showDebugConsole();
            System.out.println("Loaded locale: " + currentLocale + " at: " + localeManager.getLocaleDirectoryPath());
            System.out.println("Starting " + MainWindow.titleText + " v" + currentVersion + "...");
            System.out.println("Detected OS: " + ApplicationCore.detectOS());
        }

        // checkUpdate(); TODO: enable when ready for release

        // NOTE: this adds the program version to the locale header
        if (!localeManager.getClassesInLocaleMap().contains("!head")) {
            addVersionToLocale();
        }
    }

    /**
     * Adds the application version to the locale.
     */
    private static void addVersionToLocale() {
        Map<String, Map<String, String>> classMap = new TreeMap<>();
        classMap.put("version", new TreeMap<>());
        Map<String, String> mainMap = classMap.get("version");
        mainMap.put("currentVersion", currentVersion);

        localeManager.addClassSpecificMap("!head", classMap);
    }

    /**
     * Detects the OS to determine compat with application and dependencies.
     * @see #executeOSSpecificCode(String)
     */
    public static void checkOSCompatibility() {
        String detectedOS = ApplicationCore.detectOS();
        MainWorker.detectedOS = detectedOS;
        executeOSSpecificCode(detectedOS);
    }

    /**
     * Execute OS specific code.
     * @param detectedOS the detected OS
     * @see #checkOSCompatibility()
     */
    public static void executeOSSpecificCode(String detectedOS) {
        switch (detectedOS) {
            case "Windows" -> {
                // Windows specific code
            }
            case "macOS" -> {
                // MacOS specific code
            }
            case "Unix" -> {
                // Unix specific code
            }
            default -> {
                System.out.println("Unknown OS detected. Cannot guarantee functionality. Exiting.");
                System.exit(1);
            }
        }
    }

    /**
     * Load the user settings from the preferences. And save the settings on exit.
     */
    private static void loadPreferencesAndQueueSave() {
        ApplicationCore.loadConfigFile(MainWorker.class, developerConfigDirectoryName);

        loadWindowPosition();

        currentLocale = prefs.get("currentLocale", "eng");
        darkMode = prefs.getBoolean("darkMode", false);

        savePreferencesOnExit();
    }

    /**
     * Save the user settings on exit.
     */
    private static void savePreferencesOnExit() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveWindowPosition();

            prefs.put("currentLocale", currentLocale);
            prefs.putBoolean("darkMode", darkMode);

            ApplicationCore.saveConfigFile(MainWorker.class, developerConfigDirectoryName, prefs);
        }));
    }

    /**
     * Load the window position from the preferences. And save the position on exit.
     */
    private static void loadWindowPosition() {
        windowPosition[0] = prefs.getInt("framePosX", 0);
        windowPosition[1] = prefs.getInt("framePosY", 0);
        windowPosition[2] = prefs.getInt("activeMonitor", 0);
    }

    /**
     * Save the window position to the preferences.
     */
    private static void saveWindowPosition() {
        prefs.putInt("framePosX", windowPosition[0]);
        prefs.putInt("framePosY", windowPosition[1]);
        prefs.putInt("activeMonitor", windowPosition[2]);
    }

    /**
     * Start the MainWindow.
     */
    private static void startMainWindow() {
        EventQueue.invokeLater(() -> {
            try {
                mainWindow = new MainWindow();
                windowFrameArray[0] = mainWindow;

                SwingGUI.setFramePosition(
                        mainWindow,
                        windowPosition[0],
                        windowPosition[1],
                        windowPosition[2]
                );
                SwingGUI.setFrameIcon(mainWindow, "images/icon32.png", MainWorker.class);

                // NOTE: the following is only if using dark mode
                SwingGUI.switchLightOrDarkMode(darkMode, windowFrameArray);
                mainWindow.customActionsOnDarkModeSwitch();

            } catch (Exception ex) {
                if (debug) ex.printStackTrace(System.err);
                System.err.println("Failed to start MainWindow. Enable debug logging for more information.");
            }
        });
    }

    /**
     * Create or show the debug console window.
     */
    public static void showDebugConsole() {
        if (debugConsoleWindow == null) {
            debugConsoleWindow = new DebugConsoleWindow(
                    MainWindow.topFrame, MainWindow.fontName,
                    (MainWindow.fontSize - 2), prefs,
                    debug, localeManager
            );

            windowFrameArray[1] = debugConsoleWindow;

            if (debug) System.out.println("Debug console created.");

        } else if (!debugConsoleWindow.isVisible()) {
            debugConsoleWindow.setVisible(true);
            if (debug) System.out.println("Debug console shown.");

        } else {
            EventQueue.invokeLater(() -> debugConsoleWindow.toFront());
            if (debug) System.out.println("Debug console already open.");
        }
    }

    /**
     * Checks project GitHub for the latest version at launch.
     * Remember to enable this in startUpActions before release.
     */
    public static void checkUpdate() {
        new Thread(() -> UpdateCheckerDialog.showUpdateCheckerDialog(
                currentVersion, null, debug, githubRepoURL,
                devWebsite + "posts/swing-gui-application-template/", // TODO: update the webpage path
                prefs, localeManager
        )).start();
    }

    public static MainWindow getMainWindow() {
        return mainWindow;
    }
}
