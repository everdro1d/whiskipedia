package com.everdro1d.whiskipedia.ui.dialogs;

import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.whiskipedia.core.MainWorker.localeManager;

public class ImageViewerDialog {

    // UI Text Defaults ---
    private String titleText = "Image Viewer";

    // Other ---
    private final int MIN_WINDOW_WIDTH = 400;
    private final int MIN_WINDOW_HEIGHT = 300;

    public ImageViewerDialog() {
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                .contains("ImageViewerDialog")
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

        localeManager.addComponentSpecificMap("MainWindow", "ImageViewerDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "ImageViewerDialog");
        titleText = varMap.getOrDefault("titleText", titleText);
    }

    private void initializeGUIComponents() {

    }
}
