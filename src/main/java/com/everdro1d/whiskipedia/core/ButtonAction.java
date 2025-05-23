/*
 * dro1dDev - created: 2025-05-08
 */

package com.everdro1d.whiskipedia.core;

import com.everdro1d.libs.swing.windows.settings.BasicSettingsWindow;
import com.everdro1d.whiskipedia.ui.MainWindow;
import com.everdro1d.whiskipedia.ui.panels.GeneralSettingsPanel;

import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.*;
import static com.everdro1d.whiskipedia.ui.MainWindow.topFrame;

public class ButtonAction {

    // Variables ------------------------------------------------------------------------------------------------------|

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
                    currentLocale = localeManager.getCurrentLocale();

                    debug = prefs.getBoolean("debug", debug);
                    darkMode = prefs.getBoolean("darkMode", darkMode);

                    if (debug) showDebugConsole();
                    else if (debugConsoleWindow != null) {
                        debugConsoleWindow.dispose();
                        debugConsoleWindow = null;
                        windowFrameArray[1] = null;
                    }

                    getMainWindow().darkModeSwitch();
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
