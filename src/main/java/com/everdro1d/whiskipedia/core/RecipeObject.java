package com.everdro1d.whiskipedia.core;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RecipeObject {
    private String id;
    private String name;
    private String description;

    private String instructions;

    static final DecimalFormat df = new DecimalFormat("#.####");

    public enum UnitCategory {
        WEIGHT, VOLUME
    }

    public enum IngredientUnit {
        // Weight Units (Base: Gram)
        GRAM(UnitCategory.WEIGHT, 1.0),
        DEKAGRAM(UnitCategory.WEIGHT, 10.0),
        KILOGRAM(UnitCategory.WEIGHT, 1000.0),
        OUNCE(UnitCategory.WEIGHT, 28.3495),
        POUND(UnitCategory.WEIGHT, 453.592),

        // Volume Units (Base: Milliliter)
        ML(UnitCategory.VOLUME, 1.0),
        LITER(UnitCategory.VOLUME, 1000.0),
        TSP(UnitCategory.VOLUME, 4.92892),
        TBSP(UnitCategory.VOLUME, 14.7868),
        FLOZ(UnitCategory.VOLUME, 29.5735),
        CUP(UnitCategory.VOLUME, 240.0);

        private final UnitCategory category;
        private final double baseFactor;

        IngredientUnit(UnitCategory category, double baseFactor) {
            this.category = category;
            this.baseFactor = baseFactor;
        }

        public UnitCategory getCategory() { return category; }

        public double convertTo(double amount, IngredientUnit targetUnit) {
            if (this.category != targetUnit.category) {
                throw new IllegalArgumentException("Cannot convert " + this.category + " to " + targetUnit.category);
            }

            return (amount * this.baseFactor) / targetUnit.baseFactor;
        }

        public static Optional<IngredientUnit> fromString(String name) {
            if (name == null || name.isBlank()) {
                return Optional.empty();
            }

            String cleanName = name.trim();
            if (cleanName.length() > 1 && cleanName.toLowerCase().endsWith("s")) {
                cleanName = cleanName.substring(0, cleanName.length() - 1);
            }

            final String finalName = cleanName;
            return Arrays.stream(values())
                    .filter(u -> u.name().equalsIgnoreCase(finalName))
                    .findFirst();
        }
    }

    private List<Ingredient> ingredients;
    public record Ingredient(String name, double amount, String unit) {
        @Override
        public String toString() {
            return String.format("%s: %s %s", name, df.format(amount), unit);
        }

        /**
         * Accepts any of:
         * <pre>
         * Weight: gram, kilogram, dekagram, ounce, pound.
         * Volume: mL, liter, tsp, tbsp, flOz, cups.
         * </pre>
         */
        public Ingredient convertTo(String targetUnitStr) {
            IngredientUnit source = IngredientUnit.fromString(this.unit)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid source unit: " + this.unit));
            IngredientUnit target = IngredientUnit.fromString(targetUnitStr)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid target unit: " + targetUnitStr));

            double newAmount = source.convertTo(this.amount, target);
            return new Ingredient(this.name, newAmount, target.name());
        }
    }

    private String numServings;

    private String notes;
    private String source;

    private Path previewImagePath;
    private Path[] images;
    private Path[] additionalFiles;

    private String[] tags;
    private String[] categories;

    // ------------------------------------------------------------------------|
    public RecipeObject(
            String name, // text TODO
            String description, // text

            String instructions, // text
            List<Ingredient> ingredients,
            String numServings, // number

            String notes, // text
            String source, // text

            Path previewImagePath, // path in images
            Path[] images, // path in images
            Path[] additionalFiles, // path in files

            String[] tags,
            String[] categories
    ) {
        this.id = RecipeWorker.parseNameToID(name);
        this.name = name;
        this.description = description;

        this.instructions = instructions;
        this.ingredients = ingredients;
        this.numServings = numServings;

        this.notes = notes;
        this.source = source;

        this.previewImagePath = previewImagePath;
        this.images = images;
        this.additionalFiles = additionalFiles;

        this.tags = tags;
        this.categories = categories;
    }

    public RecipeObject() {}

    public void print() {
        System.out.printf(
                """
                ---%s---
                --Desc--
                %s
                --instructions--
                %s
                --ingredients--
                %s
                --servings--
                %s
                --notes--
                %s
                --source--
                %s
                --preview--
                %s
                --images--
                %s
                --files--
                %s
                --tags--
                %s
                --categories--
                %s
                ---end recipe---
                """,
                this.name,
                this.description,
                this.instructions,
                this.ingredients.toString(),
                this.numServings,
                this.notes,
                this.source,
                this.previewImagePath,
                Arrays.toString(this.images),
                Arrays.toString(this.additionalFiles),
                Arrays.toString(this.tags),
                Arrays.toString(this.categories)
        );
    }

    // Getters and Setters ----------------------------------------------------|
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * !!DOES NOT WRITE FILES!!
     * <br>Sets name and updates ID.
     * @return false if new ID already exists in trie
     */
    public boolean setName(String name) {
        //TODO if name changes, so should ID. should options be allowed to overwrite existing?
        String newID = RecipeWorker.parseNameToID(name);
        if (RecipeWorker.getRecipeIDTrie().contains(newID)) return false;

        this.id = newID;
        this.name = name;
        return true;
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

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getNumServings() {
        return numServings;
    }

    public void setNumServings(String numServings) {
        this.numServings = numServings;
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
