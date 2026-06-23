package com.everdro1d.whiskipedia.ui.dialogs;

import com.everdro1d.libs.swing.SwingGUI;
import com.everdro1d.whiskipedia.core.RecipeWorker;
import com.everdro1d.whiskipedia.ui.MainWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.everdro1d.libs.swing.ImageUtils.getScaledImage;
import static com.everdro1d.whiskipedia.core.MainWorker.*;
import static com.everdro1d.whiskipedia.ui.MainWindow.EDGE_PADDING;

public class ImageViewerDialog extends JFrame {

    private JFrame topFrame;
    private JPanel topPanel;
        private JScrollPane imageViewScrollPane;
            private JLabel imageView;
        private JScrollPane previewScrollPane;
            private JPanel previewPanel;
                private CardLayout cardLayout;
                private JToolBar imagePreviewBar;
                private JLabel imagesEmptyLabel;
        private JButton leftArrow;
        private JButton rightArrow;

        private JPanel eastPanel, westPanel; // spacing only

    // UI Text Defaults ---
    private String titleText = "Whiskipedia: Image Viewer";
    private String imagesEmptyLabelText = "No Images Found";

    // Image Related ---
    private String imageDir;
    private final Icon placeholderIcon = new MissingIcon();
    private List<JButton> thumbnailButtons = new java.util.ArrayList<>();
    private int currentIndex = -1;

    // Window Related ---
    private static final int MIN_WINDOW_WIDTH = 500;
    private static final int MIN_WINDOW_HEIGHT = 400;
    public static int[] windowPosition = {0, 0, 0};
    public static Dimension windowSize = new Dimension();

    // Other ---
    private final Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 4, 0, 4),
            BorderFactory.createBevelBorder(BevelBorder.LOWERED)
    );

    private final Border selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 2, 0, 2),
            BorderFactory.createLineBorder(UIManager.getColor("Accent.color") != null ?
                    UIManager.getColor("Accent.color") : Color.BLUE, 4)
    );

    public ImageViewerDialog() {
        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")
                || !localeManager.getComponentsInClassMap("MainWindow")
                .contains("ImageViewerDialog")
        ) {
            //addComponentToLocale(); TODO: re-enable when built
        }
        useLocale();

        initializeWindowProperties();

        initializeGUIComponents();
    }

    private void addComponentToLocale() {
        Map<String, String> map = new TreeMap<>();
        map.put("titleText", titleText);
        map.put("imagesEmptyLabelText", imagesEmptyLabelText);

        if (!localeManager.getClassesInLocaleMap().contains("MainWindow")) {
            localeManager.addClassSpecificMap("MainWindow", new TreeMap<>());
        }

        localeManager.addComponentSpecificMap("MainWindow", "ImageViewerDialog", map);
    }

    private void useLocale() {
        Map<String, String> varMap = localeManager.getComponentSpecificMap("MainWindow", "ImageViewerDialog");
        titleText = varMap.getOrDefault("titleText", titleText);
        imagesEmptyLabelText = varMap.getOrDefault("imagesEmptyLabelText", imagesEmptyLabelText);
    }

    private void initializeWindowProperties() {
        topFrame = this;
        topFrame.setTitle(titleText); // TODO append filename
        topFrame.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));
        topFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        topFrame.setResizable(true);
        topFrame.setLocationRelativeTo(getMainWindow());

        topFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                ImageViewerDialog.windowPosition = SwingGUI.getFramePositionOnScreen(topFrame);
            }
            @Override
            public void componentResized(ComponentEvent e) {
                ImageViewerDialog.windowSize = topFrame.getSize();
            }
        });
    }

    private void showWindow() {
        topFrame.revalidate();
        topFrame.repaint();
        topFrame.setVisible(true);

        SwingGUI.setFramePosition(
                topFrame,
                ImageViewerDialog.windowPosition[0],
                ImageViewerDialog.windowPosition[1],
                ImageViewerDialog.windowPosition[2]
        );

        topFrame.setSize(
                prefs.getInt("imageViewerWindowWidth", MIN_WINDOW_WIDTH),
                prefs.getInt("imageViewerWindowHeight", MIN_WINDOW_HEIGHT)
        );
    }

    private void initializeGUIComponents() {

        topPanel = new JPanel(new BorderLayout());
        this.add(topPanel);
        {
            // --- CENTER ---
            imageViewScrollPane = new JScrollPane();
            imageViewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            imageViewScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            imageViewScrollPane.getVerticalScrollBar().setUnitIncrement(16);
            imageViewScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
            imageViewScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,EDGE_PADDING,EDGE_PADDING,EDGE_PADDING), BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
            topPanel.add(imageViewScrollPane, BorderLayout.CENTER);

            imageView = new JLabel(placeholderIcon);
            imageViewScrollPane.setViewportView(imageView);

            // --- BOTTOM ---
            previewScrollPane = new JScrollPane();
            previewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            previewScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            previewScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
            previewScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,((EDGE_PADDING*3)-5),EDGE_PADDING,((EDGE_PADDING*3)-5)), BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

            topPanel.add(previewScrollPane, BorderLayout.SOUTH);

            cardLayout = new CardLayout();
            previewPanel = new JPanel(cardLayout);
            previewPanel.setBorder(BorderFactory.createEmptyBorder(0,0,EDGE_PADDING,0));
            previewScrollPane.setViewportView(previewPanel);
            {
                imagePreviewBar = new JToolBar();
                previewPanel.add(imagePreviewBar, "IMAGES");
                {
                    imagePreviewBar.add(Box.createGlue());
                    imagePreviewBar.add(Box.createGlue());
                }

                imagesEmptyLabel = new JLabel(imagesEmptyLabelText);
                imagesEmptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imagesEmptyLabel.setVerticalAlignment(SwingConstants.CENTER);
                imagesEmptyLabel.setFont(MainWindow.FONT);
                previewPanel.add(imagesEmptyLabel, "EMPTY");

            }

            // --- SIDES ---
            westPanel = new JPanel();
            westPanel.setLayout(new GridBagLayout());
            topPanel.add(westPanel, BorderLayout.WEST);

            eastPanel = new JPanel();
            eastPanel.setLayout(new GridBagLayout());
            topPanel.add(eastPanel, BorderLayout.EAST);
            {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.weightx = 1;
                c.anchor = GridBagConstraints.CENTER;
                c.fill = GridBagConstraints.BOTH;

                // --- Sides ---
                leftArrow = new JButton("<");
                leftArrow.addActionListener(e -> navigateImage(-1));
                leftArrow.setFont(MainWindow.BOLD_FONT);
                leftArrow.setMinimumSize(new Dimension(EDGE_PADDING, 10));
                c.gridy = 0;
                c.weighty = 0.25;
                westPanel.add(new JPanel(), c);
                c.gridy++;
                c.weighty = 0.5;
                westPanel.add(leftArrow, c);
                c.gridy++;
                c.weighty = 0.25;
                westPanel.add(new JPanel(), c);

                c.gridy = 0;

                rightArrow = new JButton(">");
                rightArrow.addActionListener(e -> navigateImage(1));
                rightArrow.setFont(MainWindow.BOLD_FONT);
                rightArrow.setMinimumSize(new Dimension(EDGE_PADDING, 10));
                c.gridy = 0;
                c.weighty = 0.25;
                eastPanel.add(new JPanel(), c);
                c.gridy++;
                c.weighty = 0.5;
                eastPanel.add(rightArrow, c);
                c.gridy++;
                c.weighty = 0.25;
                eastPanel.add(new JPanel(), c);
            }
        }

        loadImages.execute();
    }

    private void navigateImage(int direction) {
        if (thumbnailButtons.isEmpty()) return;

        int newIndex = currentIndex + direction;
        if (newIndex >= 0 && newIndex < thumbnailButtons.size()) {
            thumbnailButtons.get(newIndex).doClick();
        }
    }

    private void updateSelection() {
        leftArrow.setEnabled(currentIndex > 0);
        rightArrow.setEnabled(currentIndex < thumbnailButtons.size() - 1);

        for (int i = 0; i < thumbnailButtons.size(); i++) {
            if (i == currentIndex) {
                thumbnailButtons.get(i).setBorder(selectedBorder);
            } else {
                thumbnailButtons.get(i).setBorder(defaultBorder);
            }
        }
    }

    @Override
    public void dispose() {
        prefs.putInt("imageViewerFramePosX", ImageViewerDialog.windowPosition[0]);
        prefs.putInt("imageViewerFramePosY", ImageViewerDialog.windowPosition[1]);
        prefs.putInt("imageViewerActiveMonitor", ImageViewerDialog.windowPosition[2]);

        prefs.putInt("imageViewerWindowWidth", ImageViewerDialog.windowSize.width);
        prefs.putInt("imageViewerWindowHeight", ImageViewerDialog.windowSize.height);

        super.dispose();
    }

    /**
     * SwingWorker class that loads the images a background thread and calls publish
     * when a new one is ready to be displayed.
     * <p>
     * We use Void as the first SwingWroker param as we do not need to return
     * anything from doInBackground().
     */
    private SwingWorker<Void, ThumbnailAction> loadImages = new SwingWorker<Void, ThumbnailAction>() {

        /**
         * Creates full size and thumbnail versions of the target image files.
         */
        @Override
        protected Void doInBackground() {
            // Image related ---
            List<String> imageFileNames = populateImageFileList();

            if (imageFileNames.isEmpty()) {
                cardLayout.show(previewPanel, "EMPTY");
                updateSelection();
                showWindow();

                return null;
            } else {
                cardLayout.show(previewPanel, "IMAGES");
            }

            for (int i = 0; i < imageFileNames.size(); i++) {
                String imageFileName = imageFileNames.get(i);
                ImageIcon icon;
                icon = createImageIconFromFileSystem(imageDir + File.separator + imageFileName);

                ThumbnailAction thumbAction;
                if (icon != null) {

                    ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 48, 48));

                    thumbAction = new ThumbnailAction(icon, thumbnailIcon, imageFileName, i);

                } else {
                    // the image failed to load for some reason
                    // so load a placeholder instead
                    thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, imageFileName, i);
                }
                publish(thumbAction);
            }

            if (debug) System.out.println("Loaded images:" + "\n" + imageFileNames);

            return null;
        }

        /**
         * Process all loaded images.
         */
        @Override
        protected void process(List<ThumbnailAction> chunks) {
            for (ThumbnailAction thumbAction : chunks) {
                JButton thumbButton = new JButton(thumbAction);
                thumbButton.setBorder(defaultBorder);
                // add the new button BEFORE the last glue
                // this centers the buttons in the toolbar
                imagePreviewBar.add(thumbButton, imagePreviewBar.getComponentCount() - 1);
                thumbnailButtons.add(thumbButton);
            }

            if (currentIndex == -1 && !thumbnailButtons.isEmpty()) {
                thumbnailButtons.getFirst().doClick();
            }

            showWindow();
        }
    };

    private ImageIcon createImageIconFromFileSystem(String path) {
        ImageIcon icon = null;
        try (InputStream iconStream = Files.newInputStream(Path.of(path))) {
            icon = new ImageIcon(ImageIO.read(iconStream));
        } catch (Exception e) {
            System.err.println("[ERROR] Could not read icon file from file system at: " + path);
            e.printStackTrace(System.err);
        }
        return icon;
    }

    private String findImageDir() {
        String s = File.separator;
        String path = recipeRepositoryPath + s + RecipeWorker.selectedRecipe[0] + s + "images";

        return path;
    }

    //TODO tie in with RecipeObject images listing
    // currently this class scans the image directory
    // we should be asking the recipe file for the images,
    // checking if they exist, and prompting to update the
    // list if some are removed or extra exist
    private List<String> populateImageFileList() {
        imageDir = findImageDir();

        List<String> files = com.everdro1d.libs.io.Files.getAllFilesInDirectory(imageDir).stream().filter(file -> {
            try {
                String mimeType = java.nio.file.Files.probeContentType(java.nio.file.Path.of(file));
                return mimeType != null && mimeType.startsWith("image/");
            } catch (java.io.IOException e) {
                return false;
            }
        }).toList();

        return files;
    }

    /**
     * Action class that shows the image specified in its constructor.
     */
    private class ThumbnailAction extends AbstractAction{

        private Icon displayPhoto;
        private int index;

        public ThumbnailAction(Icon photo, Icon thumb, String name, int index) {
            displayPhoto = photo;
            this.index = index;

            putValue(SHORT_DESCRIPTION, name);
            putValue(LARGE_ICON_KEY, thumb);
        }

        public void actionPerformed(ActionEvent e) {
            imageView.setIcon(displayPhoto);
            setTitle(titleText + " - " + getValue(SHORT_DESCRIPTION).toString());

            currentIndex = this.index;
            updateSelection();

            previewPanel.scrollRectToVisible(thumbnailButtons.get(currentIndex).getBounds());
        }
    }

    /**
     * The "missing icon" is a white box with a black border and a red x.
     * It's used to display something when there are issues loading an
     * icon from an external location.
     *
     * @author Collin Fagan
     */
    private static class MissingIcon implements Icon {

        private final int width = 32;
        private final int height = 32;

        private final BasicStroke stroke = new BasicStroke(4);

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(Color.WHITE);
            g2d.fillRect(x +1 ,y + 1,width -2 ,height -2);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x +1 ,y + 1,width -2 ,height -2);

            g2d.setColor(Color.RED);

            g2d.setStroke(stroke);
            g2d.drawLine(x +10, y + 10, x + width -10, y + height -10);
            g2d.drawLine(x +10, y + height -10, x + width -10, y + 10);

            g2d.dispose();
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }
    }
}
