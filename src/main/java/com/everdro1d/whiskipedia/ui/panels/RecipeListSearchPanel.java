package com.everdro1d.whiskipedia.ui.panels;

import com.everdro1d.libs.structs.Trie;
import com.everdro1d.libs.swing.components.LabeledTextField;
import com.everdro1d.whiskipedia.core.RecipeObject;
import com.everdro1d.whiskipedia.core.RecipeWorker;
import com.everdro1d.whiskipedia.ui.MainWindow;

import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import static com.everdro1d.whiskipedia.core.MainWorker.*;

public class RecipeListSearchPanel extends JPanel {
    private JPanel listUtilsPanel;
        private JPanel listUtilsMenuBar;
        private LabeledTextField searchBar;
    private JPanel listCardContainer;
        private CardLayout listCardLayout;
        private JScrollPane listScrollPane;
            private DefaultListModel<String> recipeListModel;
            private JList<String> recipeDisplayList;
        private JLabel listEmptyLabel;

    private final int MIN_PANEL_WIDTH = 180;

    // UI Text Defaults ---
    private String searchBarDefaultText = "Search Recipes";
    private String recipeListTitleText = "Recipe List";
    private String listEmptyLabelText = "No Recipes Found";

    public RecipeListSearchPanel() {
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                    .contains("RecipeListSearchPanel")
        ) {
            //addComponentToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeGUIComponents();
    }

    private void addComponentToLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("searchBarDefaultText", searchBarDefaultText);
        map.put("recipeListTitleText", recipeListTitleText);
        map.put("listEmptyLabelText", listEmptyLabelText);


        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            localeManager.addClassSpecificMap("MainWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("MainWindow", "RecipeListSearchPanel", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "RecipeListSearchPanel");
        searchBarDefaultText = varMap.getOrDefault("searchBarDefaultText", searchBarDefaultText);
        recipeListTitleText = varMap.getOrDefault("recipeListTitleText", recipeListTitleText);
        listEmptyLabelText = varMap.getOrDefault("listEmptyLabelText", listEmptyLabelText);

    }

    private void initializeGUIComponents() {
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, 100));
        this.setBorder(BorderFactory.createTitledBorder(recipeListTitleText));

        listUtilsPanel = new JPanel();
        listUtilsPanel.setLayout(new BoxLayout(listUtilsPanel, BoxLayout.Y_AXIS));
        this.add(listUtilsPanel, BorderLayout.NORTH);
        {
            listUtilsMenuBar = new JPanel();
            listUtilsMenuBar.setLayout(new GridBagLayout());
            listUtilsMenuBar.setBorder(BorderFactory.createEmptyBorder());
            listUtilsMenuBar.setPreferredSize(new Dimension(MIN_PANEL_WIDTH, 25));
            if (guiDebugColoring) listUtilsMenuBar.setBackground(Color.GREEN);
            listUtilsPanel.add(listUtilsMenuBar);
            {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                // TODO finish gbc

                JButton placeholder = new JButton();
                placeholder.setPreferredSize(new Dimension(20, 20));
                listUtilsMenuBar.add(placeholder);

                // TODO add buttons for sorting and filtering (asc, desc..., type of dish, etc.)
            }

            searchBar = new LabeledTextField(searchBarDefaultText);
            searchBar.setFont(MainWindow.FONT);
            listUtilsPanel.add(searchBar);
            // TODO update search for desc, tag, category, etc search
            searchBar.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String s = RecipeWorker.parseNameToID(searchBar.getText());
                    List<String> matches;
                    System.out.println(s);

                    if (!s.isEmpty()) {
                        matches = RecipeWorker.getRecipeIDTrie().listKeysMatching(s);
                        displayRecipeList(matches);
                        System.out.println(matches);
                    } else {
                        displayRecipeList();
                    }

                }
            });

            JPanel spacer = new JPanel();
            if (guiDebugColoring) spacer.setBackground(Color.GREEN);
            listUtilsPanel.add(spacer);
        }

        recipeListModel = new DefaultListModel<String>();

        listScrollPane = new JScrollPane(recipeDisplayList = new JList<String>(recipeListModel));
        listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        //TODO icon on list items
        // see https://stackoverflow.com/a/5807585
        // see https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#renderer

        recipeDisplayList.setFont(MainWindow.FONT);
        recipeDisplayList.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, 25));
        recipeDisplayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeDisplayList.setLayoutOrientation(JList.VERTICAL);
        recipeDisplayList.setVisibleRowCount(-1);
        recipeDisplayList.setSelectedIndex(0);
        recipeDisplayList.ensureIndexIsVisible(0);
        if (guiDebugColoring) recipeDisplayList.setBackground(Color.ORANGE);

        listEmptyLabel = new JLabel(listEmptyLabelText);
        listEmptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        listEmptyLabel.setVerticalAlignment(SwingConstants.TOP);
        listEmptyLabel.setFont(MainWindow.FONT);

        listCardLayout = new CardLayout();
        listCardContainer = new JPanel(listCardLayout);
        listCardContainer.add(listScrollPane, "LIST");
        listCardContainer.add(listEmptyLabel, "EMPTY");

        this.add(listCardContainer, BorderLayout.CENTER);
        {
            displayRecipeList();

            recipeDisplayList.getSelectionModel().addListSelectionListener(e -> {
                int i = recipeDisplayList.getSelectedIndex();
                if (i < 0) return;

                String name = recipeListModel.get(i);
                String key = RecipeWorker.parseNameToID(name);
                RecipeWorker.selectedRecipe[0] = key;
                RecipeWorker.selectedRecipe[1] = name;
                if (debug) System.out.println("Selected: " + key + "/\"" + name + "\"");
            });

            // https://docs.oracle.com/javase/tutorial/uiswing/components/list.html
        }
    }

    private void displayRecipeList() {
        displayRecipeList(null);
    }

    private void displayRecipeList(List<String> matches) {
        Trie<RecipeObject> recipes = RecipeWorker.getRecipeIDTrie();

        if (matches == null) {
            matches = recipes.listKeys();
        }

        if (matches.isEmpty()) {
            listCardLayout.show(listCardContainer, "EMPTY");
            return;
        } else {
            listCardLayout.show(listCardContainer, "LIST");
        }

        recipeListModel.removeAllElements();

        int invalid = 0;
        for (String match : matches) {
            RecipeObject r = recipes.get(match);
            if (r == null) {
                invalid++;
                continue;
            }
            String n = r.getName();
            recipeListModel.addElement(n.isEmpty() ? match : n);
        }

        if (debug && invalid > 0) System.err.println("Detected " + invalid + " invalid recipes. Please verify correct file structure");

        String name = RecipeWorker.selectedRecipe[1];
        int i = recipeDisplayList.getNextMatch((name == null ? "" : name), 0, Position.Bias.Forward);
        if (i >= 0) {
            recipeDisplayList.setSelectedIndex(i);
            recipeDisplayList.ensureIndexIsVisible(i);
        }

        recipeDisplayList.revalidate();
        recipeDisplayList.repaint();
    }

    // --- Get & Set ---

    public LabeledTextField getSearchBar() {
        return searchBar;
    }

    public JList<String> getRecipeDisplayList() {
        return recipeDisplayList;
    }

    public DefaultListModel<String> getRecipeListModel() {
        return recipeListModel;
    }
}
