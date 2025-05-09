package main.com.everdro1d.swingtemplate.core;

import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.libs.swing.windows.settings.BasicSettingsWindow;
import main.com.everdro1d.swingtemplate.ui.MainWindow;
import main.com.everdro1d.swingtemplate.ui.panels.GeneralSettingsPanel;

import java.util.Map;
import java.util.TreeMap;

import static main.com.everdro1d.swingtemplate.core.MainWorker.*;
import static main.com.everdro1d.swingtemplate.ui.MainWindow.topFrame;

public class ButtonAction {

    // Variables ------------------------------------------------------------------------------------------------------|

    // NOTE: settings window for locale changes & basic settings
    public static BasicSettingsWindow settingsWindow;

    public static void showSettingsWindow() {
        if (debug) System.out.println("Showing settings window.");
        if (settingsWindow == null ||  !settingsWindow.isVisible()) {
            settingsWindow = new BasicSettingsWindow(
                    topFrame, MainWindow.fontName, MainWindow.fontSize,
                    prefs, debug, localeManager, new GeneralSettingsPanel(),
                    githubRepoURL + "tree/master/locale/", devWebsite
            ) {
                @Override
                public void applySettings() {
                    localeManager.reloadLocaleInProgram(prefs.get("currentLocale", localeManager.getCurrentLocale()));
                    currentLocale = localeManager.getCurrentLocale();

                    debug = prefs.getBoolean("debug", debug);
                    darkMode = prefs.getBoolean("darkMode", darkMode);

                    if (debug) {
                        showDebugConsole();
                        System.out.println("Active locale: " + currentLocale);
                        System.out.println("Active: " + MainWindow.titleText + " v" + currentVersion);
                        System.out.println("Detected OS: " + MainWorker.detectedOS);
                    } else if (debugConsoleWindow != null) {
                        debugConsoleWindow.dispose();
                        debugConsoleWindow = null;
                        windowFrameArray[1] = null;
                    }

                    SwingGUI.switchLightOrDarkMode(darkMode, windowFrameArray);
                    getMainWindow().customActionsOnDarkModeSwitch();
                }

                @Override
                public Map<String, String> setOriginalSettingsMap() {
                    Map<String, String> originalSettingsMap = new TreeMap<>();

                    originalSettingsMap.put("debug", String.valueOf(debug));
                    originalSettingsMap.put("darkMode", String.valueOf(darkMode));

                    return originalSettingsMap;
                }

                @Override
                public Map<String, Boolean> setRestartRequiredSettingsMap() {
                    Map<String, Boolean> restartRequiredSettingsMap = new TreeMap<>();

                    restartRequiredSettingsMap.put("debug", false);
                    restartRequiredSettingsMap.put("darkMode", false);

                    return restartRequiredSettingsMap;
                }
            };
            windowFrameArray[2] = settingsWindow;
        } else {
            settingsWindow.requestFocus();
            settingsWindow.toFront();
        }

    }
}
