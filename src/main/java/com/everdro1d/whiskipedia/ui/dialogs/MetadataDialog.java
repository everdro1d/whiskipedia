package com.everdro1d.whiskipedia.ui.dialogs;

import com.everdro1d.libs.swing.components.TrackingFrame;
import com.everdro1d.whiskipedia.core.MainWorker;

import javax.swing.*;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.localeManager;

public class MetadataDialog extends TrackingFrame {

    // UI Text Defaults ---
    private String titleText = "Metadata Viewer";

    // Other ---
    private final int MIN_WINDOW_WIDTH = 180;
    private final int MIN_WINDOW_HEIGHT = 360;

    public MetadataDialog() {
        super(MainWorker.prefs, "metadataViewer");
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                .contains("MetadataDialog")
        ) {
            //addComponentToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeGUIComponents();
    }

    private void addComponentToLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("titleText", titleText);


        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            localeManager.addClassSpecificMap("MainWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("MainWindow", "MetadataDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "MetadataDialog");
        titleText = varMap.getOrDefault("titleText", titleText);
    }

    private void initializeGUIComponents() {

    }
}
