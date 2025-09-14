package com.everdro1d.whiskipedia.core;

import java.nio.file.Path;

public class RecipeObject {
    private String name;
    private String description;

    private String instructions;
    private String ingredients;
    private String servingSize;

    private String notes;
    private String source;

    private Path previewImagePath;
    private Path[] images;
    private Path[] additionalFiles;

    private String[] tags;
    private String[] categories;

    // ------------------------------------------------------------------------|
    public RecipeObject(
            String name, // text
            String description, // markdown

            String instructions, // markdown
            String ingredients, // markdown
            String servingSize, // number

            String notes, // text
            String source, // text

            Path previewImagePath, // path in images
            Path[] images, // path in images
            Path[] additionalFiles, // path in files

            String[] tags,
            String[] categories
    ) {
        this.name = name;
        this.description = description;

        this.instructions = instructions;
        this.ingredients = ingredients;
        this.servingSize = servingSize;

        this.notes = notes;
        this.source = source;

        this.previewImagePath = previewImagePath;
        this.images = images;
        this.additionalFiles = additionalFiles;

        this.tags = tags;
        this.categories = categories;
    }

    /**
     * Test Recipe Object TODO: remove me
     */
    public RecipeObject() {
        this.name = "New Recipe";
        this.description = "Description\nA new line\n kachow";

        this.instructions = "Instructions\n1. a\n2. b\n3. c";
        this.ingredients = "Ingredients\nLightning\nMcQueen";
        this.servingSize = "3";

        this.notes = "Notes";
        this.source = "Source";

        this.previewImagePath = null;
        this.images = null;
        this.additionalFiles = null;

        this.tags = new String[]{"a", "b", "c"};
        this.categories = new String[]{"d", "e", "f"};
    }

    // Getters and Setters ----------------------------------------------------|
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public Path getPreviewImagePath() {
        return previewImagePath;
    }

    public void setPreviewImagePath(Path previewImagePath) {
        this.previewImagePath = previewImagePath;
    }

    public Path[] getImages() {
        return images;
    }

    public void setImages(Path[] images) {
        this.images = images;
    }

    public Path[] getAdditionalFiles() {
        return additionalFiles;
    }

    public void setAdditionalFiles(Path[] additionalFiles) {
        this.additionalFiles = additionalFiles;
    }


    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    // End of Getters and Setters ---------------------------------------------|
}
