/*
 * dro1dDev - created: 2025-05-08
 */

package com.everdro1d.whiskipedia.core;

import com.everdro1d.libs.commands.CommandInterface;
import com.everdro1d.libs.commands.CommandManager;
import com.everdro1d.libs.core.*;
import com.everdro1d.libs.locale.*;
import com.everdro1d.libs.swing.*;
import com.everdro1d.libs.swing.dialogs.UpdateCheckerDialog;
import com.everdro1d.libs.swing.windows.DebugConsoleWindow;
import com.everdro1d.whiskipedia.core.commands.GUIDebugCommand;
import com.everdro1d.whiskipedia.ui.MainWindow;
import com.everdro1d.whiskipedia.core.commands.DebugCommand;
import static com.everdro1d.whiskipedia.core.ButtonAction.settingsWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;


public class MainWorker {
    // Variables ------------------------------------------------------------------------------------------------------|
    public static final String githubRepoURL = "https://github.com/everdro1d/whiskipedia/";
    public static final String devWebsite = "https://everdro1d.github.io/";
    public static final String currentVersion = "0.0.1"; // TODO: update me with each release
    public static final String developerConfigDirectoryName = "dro1dDev";
    private static final Map<String, CommandInterface> CUSTOM_COMMANDS_MAP = Map.of(
            "-debug", new DebugCommand(),
            "--gui-debug", new GUIDebugCommand()
    );
    public static CommandManager commandManager = new CommandManager(CUSTOM_COMMANDS_MAP);
    public static String currentLocale = "eng";
    public static final LocaleManager localeManager = new LocaleManager(MainWorker.class, developerConfigDirectoryName);
    public static boolean debug = false;
    public static boolean guiDebugColoring = false;
    public static DebugConsoleWindow debugConsoleWindow;
    public static final Preferences prefs = Preferences.userNodeForPackage(MainWorker.class);
    public static int[] windowPosition = {0, 0, 0};
    public static Dimension windowSize = new Dimension();
    private static MainWindow mainWindow;
    public static JFrame[] windowFrameArray = new JFrame[]{
            mainWindow,
            debugConsoleWindow,
            settingsWindow
    };
    /**
     * Valid: "windows", "mac", "unix"
     */
    public static String detectedOS;
    public static boolean darkMode = false;

    // End of variables -----------------------------------------------------------------------------------------------|

    public static void main(String[] args) {
        startUpActions(args);
        startMainWindow();
    }

    private static void startUpActions(String[] args) {
        ApplicationCore.checkCLIArgs(args, commandManager);
        checkOSCompatibility();

        SwingGUI.setupLookAndFeel(true, true);

        SwingGUI.uiSetup(MainWindow.fontName, MainWindow.fontSize);

        loadPreferencesAndQueueSave();

        localeManager.loadLocaleFromFile(currentLocale);
        currentLocale = localeManager.getCurrentLocale();

        if (debug) {
            SwingUtilities.invokeLater(MainWorker::showDebugConsole);
        }

        // checkUpdate(); TODO: enable when ready for release

        if (!localeManager.getClassesInLocaleMap().contains("!head")) {
            addVersionToLocale();
        }
    }

    private static void addVersionToLocale() {
        Map<String, Map<String, String>> classMap = new TreeMap<>();
        classMap.put("version", new TreeMap<>());
        Map<String, String> mainMap = classMap.get("version");
        mainMap.put("currentVersion", currentVersion);

        localeManager.addClassSpecificMap("!head", classMap);
    }

    public static void checkOSCompatibility() {
        String detectedOS = ApplicationCore.detectOS();
        MainWorker.detectedOS = detectedOS;
        executeOSSpecificCode(detectedOS);
    }

    public static void executeOSSpecificCode(String detectedOS) {
        switch (detectedOS) {
            case "windows" -> {
                // Windows specific code
            }
            case "mac" -> {
                // MacOS specific code

                System.setProperty("apple.awt.application.name", MainWindow.titleText);
                System.setProperty("apple.awt.application.appearance", "system");
            }
            case "unix" -> {
                // Unix specific code
            }
            default -> {
                System.out.println("Unknown OS detected. Cannot guarantee functionality. Exiting.");
                System.exit(1);
            }
        }
    }

    private static void loadPreferencesAndQueueSave() {
        ApplicationCore.loadConfigFile(MainWorker.class, developerConfigDirectoryName);

        loadWindowPosition();

        currentLocale = prefs.get("currentLocale", "eng");
        darkMode = prefs.getBoolean("darkMode", false);

        savePreferencesOnExit();
    }

    private static void savePreferencesOnExit() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveWindowPosition();

            prefs.put("currentLocale", currentLocale);
            prefs.putBoolean("darkMode", darkMode);

            ApplicationCore.saveConfigFile(MainWorker.class, developerConfigDirectoryName, prefs);
        }));
    }

    private static void loadWindowPosition() {
        windowPosition[0] = prefs.getInt("framePosX", 0);
        windowPosition[1] = prefs.getInt("framePosY", 0);
        windowPosition[2] = prefs.getInt("activeMonitor", 0);
    }

    private static void saveWindowPosition() {
        prefs.putInt("framePosX", windowPosition[0]);
        prefs.putInt("framePosY", windowPosition[1]);
        prefs.putInt("activeMonitor", windowPosition[2]);

        prefs.putInt("windowWidth", windowSize.width);
        prefs.putInt("windowHeight", windowSize.height);
    }

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

                windowSize.setSize(
                        prefs.getInt("windowWidth", mainWindow.getMinimumWindowWidth()),
                        prefs.getInt("windowHeight", mainWindow.getMinimumWindowHeight())
                );

                mainWindow.setSize(windowSize);

                ImageUtils.setFrameIcon(mainWindow, "images/logoIcon50.png", MainWorker.class);

                // NOTE: the following is only if using dark mode
                mainWindow.darkModeSwitch();

            } catch (Exception ex) {
                if (debug) ex.printStackTrace(System.err);
                System.err.println("Failed to start MainWindow. Enable debug logging for more information.");
            }
        });
    }

    public static void showDebugConsole() {
        if (debugConsoleWindow == null) {
            debugConsoleWindow = new DebugConsoleWindow(
                    MainWindow.topFrame, MainWindow.fontName,
                    (MainWindow.fontSize - 2), prefs,
                    debug, localeManager
            );

            windowFrameArray[1] = debugConsoleWindow;

            if (debug) System.out.println("Debug console created.");
            System.out.println("Current locale: " + currentLocale + " at: " + localeManager.getLocaleDirectoryPath());
            System.out.println("Application: " + MainWindow.titleText + " v" + currentVersion);
            System.out.println("Detected OS: " + MainWorker.detectedOS);

        } else if (!debugConsoleWindow.isVisible()) {
            debugConsoleWindow.setVisible(true);
            if (debug) System.out.println("Debug console shown.");

        } else {
            EventQueue.invokeLater(() -> debugConsoleWindow.toFront());
            if (debug) System.out.println("Debug console already open.");
        }
    }

    public static void checkUpdate() {
        new Thread(() -> UpdateCheckerDialog.showUpdateCheckerDialog(
                currentVersion, null, debug, githubRepoURL,
                devWebsite + "posts/whiskipedia/",
                prefs, localeManager
        )).start();
    }

    public static MainWindow getMainWindow() {
        return mainWindow;
    }
}
