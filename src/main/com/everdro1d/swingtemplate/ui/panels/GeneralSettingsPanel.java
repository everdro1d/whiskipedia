package main.com.everdro1d.swingtemplate.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

import static main.com.everdro1d.swingtemplate.core.MainWorker.*;
import static main.com.everdro1d.swingtemplate.ui.MainWindow.fontName;
import static main.com.everdro1d.swingtemplate.ui.MainWindow.fontSize;

public class GeneralSettingsPanel extends JPanel {
    private final JLabel debugSwitchLabel;
    private String debugSwitchLabelText = "Enable Debug Mode:";
    private final JComboBox<String> debugSwitchComboBox;
    private final JLabel darkModeSwitchLabel;
    private String darkModeSwitchLabelText = "Enable Dark Mode:";
    private final JComboBox<String> darkModeSwitchComboBox;
    private String[] enableDisableSwitchOptions = {"Enabled", "Disabled"};

    public GeneralSettingsPanel() {
        localeCheck();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(4, 4, 4, 4);
        // row 1
        {
            debugSwitchLabel = new JLabel(debugSwitchLabelText);
            debugSwitchLabel.setFont(new Font(fontName, Font.PLAIN, fontSize));
            add(debugSwitchLabel, gbc);

            gbc.gridx++;
            gbc.weightx = 1;
            debugSwitchComboBox = new JComboBox<>(enableDisableSwitchOptions);
            debugSwitchComboBox.setFont(new Font(fontName, Font.PLAIN, fontSize));
            add(debugSwitchComboBox, gbc);

            debugSwitchComboBox.setSelectedIndex(debug ? 0 : 1);

            debugSwitchComboBox.addActionListener(e -> {
                prefs.putBoolean("debug", debugSwitchComboBox.getSelectedIndex() == 0);
            });
        }
        // row 2
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.weighty = 1;
        {
            darkModeSwitchLabel = new JLabel(darkModeSwitchLabelText);
            darkModeSwitchLabel.setFont(new Font(fontName, Font.PLAIN, fontSize));
            add(darkModeSwitchLabel, gbc);

            gbc.gridx++;
            gbc.weightx = 1;
            darkModeSwitchComboBox = new JComboBox<>(enableDisableSwitchOptions);
            darkModeSwitchComboBox.setFont(new Font(fontName, Font.PLAIN, fontSize));
            add(darkModeSwitchComboBox, gbc);

            darkModeSwitchComboBox.setSelectedIndex(darkMode ? 0 : 1);

            darkModeSwitchComboBox.addActionListener(e -> {
                prefs.putBoolean("darkMode", darkModeSwitchComboBox.getSelectedIndex() == 0);
            });
        }
    }

    private void localeCheck() {
        if (!localeManager.getClassesInLocaleMap().contains("BasicSettingsWindow")
                || !localeManager.getComponentsInClassMap("BasicSettingsWindow")
                .contains("GeneralSettingsPanel")
        ) {
            addGeneralSettingsPanelToLocale();
        }
        useLocale();
    }

    private void addGeneralSettingsPanelToLocale() {
        Map<String,String> map = new TreeMap<>();
        map.put("debugSwitchLabelText", debugSwitchLabelText);
        map.put("darkModeSwitchLabelText", darkModeSwitchLabelText);

        for (int i = 0; i < enableDisableSwitchOptions.length; i++) {
            map.put("enableDisableSwitchOptions"+i, enableDisableSwitchOptions[i]);
        }


        if (!localeManager.getClassesInLocaleMap().contains("BasicSettingsWindow")) {
            localeManager.addClassSpecificMap("BasicSettingsWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap(
                "BasicSettingsWindow", "GeneralSettingsPanel", map
        );
    }

    private void useLocale() {
        Map<String,String> varMap = localeManager.getComponentSpecificMap("BasicSettingsWindow", "GeneralSettingsPanel");

        debugSwitchLabelText = varMap.get("debugSwitchLabelText");
        darkModeSwitchLabelText = varMap.get("darkModeSwitchLabelText");

        for (int i = 0; i < enableDisableSwitchOptions.length; i++) {
            enableDisableSwitchOptions[i] = varMap.get("enableDisableSwitchOptions"+i);
        }
    }
}
