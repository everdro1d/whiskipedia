package com.everdro1d.whiskipedia.core;

import com.everdro1d.libs.structs.Trie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.everdro1d.libs.io.Files.copyDirectory;
import static com.everdro1d.libs.io.Files.deleteDirectory;
import static com.everdro1d.whiskipedia.core.MainWorker.debug;
import static com.everdro1d.whiskipedia.core.MainWorker.recipeRepositoryPath;


// repo
// L recipe-trie.txt
// L <RecipeID>
// | L meta.txt - contains info & refs
// | L content.txt - contains description, ingredients, instructions - separate by "\n§§§\n"
// | L images - contains images
// | L files - contains additional files

public class RecipeWorker {
    public static final String recipeRepositoryName = "recipe-repository";
    public static String[] selectedRecipe = new String[2];

    // TODO: Let user define repo path

    private static final Trie<RecipeObject> recipeIDTrie = new Trie<>();

    public static int setupRecipeRepository() {
        try {
            Files.createDirectory(Path.of(recipeRepositoryPath));
        } catch (FileAlreadyExistsException e) {
            if (debug) System.err.println("Recipe repository already exists!");
            return 1;
        } catch (IOException e1) {
            if (debug) System.err.println("Recipe repository could not be created!");
            return 2;
        }

        return 0;
    }

    public static boolean moveRecipeRepository(String s) {
        String newRecipeRepositoryPath;
        if (!s.endsWith(recipeRepositoryName)) {
            String sep = (s.endsWith(File.separator) ? "" : File.separator);
            newRecipeRepositoryPath = s + sep + recipeRepositoryName;
        } else {
            newRecipeRepositoryPath = s;
        }

        try {
            copyDirectory(Path.of(recipeRepositoryPath), Path.of(newRecipeRepositoryPath));
            deleteDirectory(recipeRepositoryPath);

        } catch (IOException e) {
            if (debug) System.err.println("Recipe repository could not be moved!");
            return false;
        }

        if (debug) System.out.println("Recipe repository moved: \"" + recipeRepositoryPath + "\" -> \"" + newRecipeRepositoryPath + "\"");

        recipeRepositoryPath = newRecipeRepositoryPath;

        return true;
    }

    public static boolean createRecipe(String name) {
        if (Files.notExists(Path.of(recipeRepositoryPath))) {
            if (debug) System.out.println("Recipe repository does not exist. Creating repository in user home directory.");
            setupRecipeRepository(); // TODO: move me to dedicated first start popup & move repo settings
        }

        String recipeID = parseNameToID(name);

        if (recipeIDTrie.contains(recipeID)) {
            if (debug) System.err.println("Recipe already exists with ID: " + recipeID);
            return false;
        }

        // TODO probably good to call up a dialog here
        RecipeObject newRecipe = new RecipeObject();

        recipeIDTrie.insert(recipeID, newRecipe);

        saveRecipe(recipeID, false);

        if (debug) System.out.println("Created recipe with ID: " + recipeID);
        return true;
    }

    public static String parseNameToID(String name) {
        return name.toLowerCase().replaceAll("\\s+", "-");
    }

    private static RecipeObject loadRecipe(String recipeID) {
        Path recipePath = Path.of(recipeRepositoryPath + File.separator + recipeID);
        Path metaFilePath = Path.of(recipePath + File.separator + "meta.txt");
        Path contentFilePath = Path.of(recipePath + File.separator + "content.txt");
        Path imagesDirPath = Path.of(recipePath + File.separator + "images");
        Path filesDirPath = Path.of(recipePath + File.separator + "files");

        if (Files.notExists(recipePath) || Files.notExists(metaFilePath) || Files.notExists(contentFilePath)
                || Files.notExists(imagesDirPath) || Files.notExists(filesDirPath)) {
            if (debug) System.err.println("[loadRecipe]: Recipe " + recipeID + " could not be found.");
            return null;
        }

        RecipeObject r = new RecipeObject();

        loadMetaMapFromFile(metaFilePath, r);

        loadContentFile(contentFilePath, r);

        // load images
        List<Path> images = new ArrayList<>();
        for (String f : com.everdro1d.libs.io.Files.getAllFilesInDirectory(imagesDirPath.toString())) {
            images.add(Path.of(imagesDirPath + File.separator + f));
        }
        r.setImages(images.toArray(new Path[0]));

        // load files
        List<Path> files = new ArrayList<>();
        for (String f : com.everdro1d.libs.io.Files.getAllFilesInDirectory(filesDirPath.toString())) {
            files.add(Path.of(filesDirPath + File.separator + f));
        }

        return r;
    }

    private static int saveRecipe(String recipeID, boolean overwrite) {
        Path recipePath = Path.of(recipeRepositoryPath + File.separator + recipeID);
        Path metaFilePath = Path.of(recipePath + File.separator + "meta.txt");
        Path contentFilePath = Path.of(recipePath + File.separator + "content.txt");
        Path imagesDirPath = Path.of(recipePath + File.separator + "images");
        Path filesDirPath = Path.of(recipePath + File.separator + "files");

        // check if file already exists
        if (Files.exists(recipePath) && !overwrite) {
            if (debug) System.err.println("[saveRecipe]: Recipe already exists. No overwrite.");
            return 1;
        } else if (Files.exists(recipePath)) {
            if (debug) System.out.println("[saveRecipe]: Recipe already exists. Overwriting.");
            deleteRecipe(recipeID);
        }

        try {
            Files.createDirectory(recipePath);

            Files.createFile(metaFilePath);
            Files.createFile(contentFilePath);

            Files.createDirectory(imagesDirPath);
            Files.createDirectory(filesDirPath);

        } catch (IOException e) {
            if (debug) System.err.println("[saveRecipe]: Could not create directory.");
            return 3;
        }

        RecipeObject r = recipeIDTrie.get(recipeID);
        if (r == null) {
            if (debug) System.err.println("[saveRecipe]: RecipeObject is null.");
            return 4;
        }

        saveRecipeTrie(true);

        // save meta file
        com.everdro1d.libs.io.Files.saveMapToFile(metaFilePath, populateMetaMap(r), true);

        // save content file
        try (FileWriter wr = new FileWriter(contentFilePath.toString())) {
            wr.write(r.getDescription() + "\n§§§\n" + r.getInstructions() + "\n§§§\n" + parseWritableIngredients(r.getIngredients()));
            wr.flush();
        } catch (IOException e) {
            if (debug) System.err.println("[saveRecipe]: Could not save recipe contents file.");
            return 5;
        }

        // copy images to dir
        if (r.getImages() != null) for (Path image : r.getImages()) {
            // skip if already in images folder
            if (image.getParent().getFileName().toString().equals("images")) {
                continue;
            }

            try {
                Files.copy(image, imagesDirPath.resolve(image.getFileName()));
            } catch (IOException e) {
                if (debug) System.err.println("[saveRecipe]: Could not copy image file.");
                return 6;
            }
        }

        // copy files to dir
        if (r.getAdditionalFiles() != null) for (Path file : r.getAdditionalFiles()) {
            // skip if already in files folder
            if (file.getParent().getFileName().toString().equals("files")) {
                continue;
            }

            try {
                Files.copy(file, filesDirPath.resolve(file.getFileName()));
            } catch (IOException e) {
                if (debug) System.err.println("[saveRecipe]: Could not copy additional file.");
                return 7;
            }
        }

        if (debug) System.out.println("[saveRecipe]: Recipe saved.");
        return 0;
    }

    public static boolean deleteRecipe(String recipeID) {
        String recipePath = recipeRepositoryPath + File.separator + recipeID;

        deleteDirectory(recipePath);

        recipeIDTrie.remove(recipeID);
        saveRecipeTrie(true);

        if (debug) System.out.println("[deleteRecipe]: Recipe deleted: " + recipeID);
        return true;
    }

    private static Map<String, String> populateMetaMap(RecipeObject r) {
        Map<String, String> map = new HashMap<>();

        map.put("name", r.getName());

        map.put("servingSize", r.getServingSize());
        map.put("notes", r.getNotes());
        map.put("source", r.getSource());

        map.put("tags", Arrays.toString(r.getTags()));
        map.put("categories", Arrays.toString(r.getCategories()));

        return map;
    }

    private static boolean loadMetaMapFromFile(Path metaFilePath, RecipeObject r) {
        Map<String, String> map = com.everdro1d.libs.io.Files.loadMapFromFile(metaFilePath);
        if (map == null) {
            if (debug) System.err.println("[loadMetaMapFromFile]: Could not load metadata map from file.");
            return false;
        }

        r.setName(map.get("name"));

        r.setServingSize(map.get("servingSize"));
        r.setNotes(map.get("notes"));
        r.setSource(map.get("source"));

        r.setTags(map.get("tags").replaceAll("[\\[\\]\\s]", "").split(","));
        r.setCategories(map.get("categories").replaceAll("[\\[\\]\\s]", "").split(","));

        return true;
    }

    private static boolean loadContentFile(Path contentsFilePath, RecipeObject r) {
        List<String> lines;
        String recipeID = parseNameToID(r.getName());

        try {
            lines = Files.readAllLines(contentsFilePath);
        } catch (IOException e) {
            if (debug) System.err.println("[loadContentFile]: " + recipeID + " Could not load content file.");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(System.lineSeparator());
        }

        String[] parts = sb.toString().split("§§§", -1);
        if (parts.length != 3) {
            if (debug) System.err.println("[loadContentFile]: " + recipeID + " Contents file number of parts invalid: " + parts.length);
            return false;
        }

        r.setDescription(parts[0].trim());
        r.setInstructions(parts[1].trim());
        r.setIngredients(parseIngredientsList(parts[2].trim()));

        if (debug)  System.out.println("[loadContentFile]: " + recipeID + " Contents file loaded.");
        return true;
    }

    private static List<RecipeObject.Ingredient> parseIngredientsList(String s) {
        List<RecipeObject.Ingredient> ingredients = new ArrayList<RecipeObject.Ingredient>();
        // ingredient strings should be formatted "%s: %s %s\n" (name, amount, unit)

        String[] ingr = s.split(System.lineSeparator());
        for (String i : ingr) {
            String[] parts = i.split(" ");

            String name = parts[0].split(":")[0];
            double amount = Double.parseDouble(parts[1]);
            String unit = parts[2]; //TODO handle "count" and null state

            ingredients.add(new RecipeObject.Ingredient(name, amount, unit));
        }

        return ingredients;
    }

    public static String parseWritableIngredients(List<RecipeObject.Ingredient> list) {
        StringBuilder sb = new StringBuilder();

        for (RecipeObject.Ingredient i : list) {
            sb.append(i.toString()).append(System.lineSeparator());
        }

        return sb.toString();
    }

    public static boolean loadRecipeTrie() {
        if (Files.notExists(Path.of(recipeRepositoryPath + File.separator + "recipeIDTrie.txt"))) {
            if (debug) System.err.println("[loadRecipeTrie]: Recipe trie file does not exist.");
            return false;
        }

        List<String> keys;

        try {
            keys = Files.readAllLines(Path.of(recipeRepositoryPath + File.separator + "recipeIDTrie.txt"));
        } catch (IOException e) {
            if (debug) System.err.println("[loadRecipeTrie]: Could not load recipe trie file.");
            return false;
        }

        for (String key : keys) {
            recipeIDTrie.insert(key, loadRecipe(key));
            if (debug && recipeIDTrie.get(key) != null) recipeIDTrie.get(key).print();
        }

        if (debug) System.out.println("[loadRecipeTrie]: Recipe trie loaded.");
        if (debug) System.out.println("Current recipe repository path: " + recipeRepositoryPath);
        return true;
    }

    public static boolean saveRecipeTrie(boolean overwrite) {
        if (!overwrite && Files.exists(Path.of(recipeRepositoryPath + File.separator + "recipeIDTrie.txt"))) {
            if (debug) System.err.println("[saveRecipeTrie]: Recipe trie file exists and overwrite is disabled.");
            return false;
        }

        List<String> keys = recipeIDTrie.listKeys();

        try (FileWriter wr = new FileWriter(recipeRepositoryPath + File.separator + "recipeIDTrie.txt")) {
            for (String key : keys) {
                wr.write(key + System.lineSeparator());
            }
            wr.flush();
        } catch (IOException e) {
            if (debug) System.err.println("[saveRecipeTrie]: Could not save recipe trie file.");
            return false;
        }

        if (debug) System.out.println("[saveRecipeTrie]: Recipe trie saved.");
        return true;
    }

    // --- Getters & Setters ---


    public static Trie<RecipeObject> getRecipeIDTrie() {
        return recipeIDTrie;
    }
}
