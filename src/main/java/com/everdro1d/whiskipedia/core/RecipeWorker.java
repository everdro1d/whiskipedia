package com.everdro1d.whiskipedia.core;

import com.everdro1d.libs.structs.Trie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static com.everdro1d.libs.io.Files.copyDirectory;
import static com.everdro1d.libs.io.Files.deleteDirectory;
import static com.everdro1d.whiskipedia.core.MainWorker.debug;
import static com.everdro1d.whiskipedia.core.MainWorker.recipeRepositoryPath;


// repo
// L recipe-trie.txt
// L RecipeID
// | L directory.txt - contains info & refs
// | L contents.md - contains description, ingredients, instructions - separate by "§§§"
// | L images - contains images
// | L files - contains additional files

public class RecipeWorker {
    static final String recipeRepositoryName = "recipe-repository";

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

    public static int moveRecipeRepository(String newRecipeRepositoryPath) {
        try {
            copyDirectory(Path.of(recipeRepositoryPath), Path.of(newRecipeRepositoryPath));
            deleteDirectory(recipeRepositoryPath);

        } catch (IOException e) {
            if (debug) System.err.println("Recipe repository could not be moved!");
            return 1;
        }

        recipeRepositoryPath = newRecipeRepositoryPath;

        return 0;
    }

    public static int createRecipe(String name) {
        if (Files.notExists(Path.of(recipeRepositoryPath))) {
            if (debug) System.err.println("Recipe repository does not exist. Creating repository in user home directory.");
            setupRecipeRepository(); // TODO: move me to dedicated first start popup & move repo settings
        }

        String recipeID = parseNameToID(name);

        if (recipeIDTrie.contains(recipeID)) {
            if (debug) System.err.println("Recipe already exists with ID: " + recipeID);
            return 1;
        }

        RecipeObject newRecipe = new RecipeObject();

        recipeIDTrie.insert(recipeID, newRecipe);

        saveRecipe(recipeID, false);

        if (debug) System.out.println("Created recipe with ID: " + recipeID);
        return 0;
    }

    private static String parseNameToID(String name) {
        return name.toLowerCase().replaceAll("\\s+", "-");
    }

        // repo
        // L recipe-trie.txt
        // L RecipeID
        // | L directory.txt - contains info & refs
        // | L contents.md - contains description, ingredients, instructions - separate by "§§§"
        // | L images - contains images
        // | L files - contains additional files

    private static int saveRecipe(String recipeID, boolean overwrite) {
        Path recipePath = Path.of(recipeRepositoryPath + File.separator + recipeID);
        Path recipeTriePath = Path.of(recipeRepositoryPath + File.separator + "recipeIDTrie.txt");
        Path directoryFilePath = Path.of(recipePath + File.separator + "directory.txt");
        Path contentsFilePath = Path.of(recipePath + File.separator + "contents.md");
        Path imagesDirPath = Path.of(recipePath + File.separator + "images");
        Path filesDirPath = Path.of(recipePath + File.separator + "files");

        // check if file already exists
        if (Files.exists(recipePath)) {
            boolean overwrite = false;
            // confirm overwrite

            if (overwrite) {
                // delete the dir
            } else {
                return 1;
            }
        }

        try {
            Files.createDirectory(recipePath);

            Files.createFile(directoryFilePath);
            Files.createFile(contentsFilePath);

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

        // TODO: update trie file

        com.everdro1d.libs.io.Files.saveMapToFile(directoryFilePath, populateDirectoryMap(r), true);

        try (FileWriter wr = new FileWriter(contentsFilePath.toString())) {
            wr.write(r.getDescription() + "§§§" + r.getInstructions() + "§§§" + r.getIngredients());
            wr.flush();
        } catch (IOException e) {
            if (debug) System.err.println("[saveRecipe]: Could not save recipe contents file.");
            return 5;
        }

        return 0;
    }

    private static Map<String, String> populateDirectoryMap(RecipeObject r) {
        Map<String, String> map = new HashMap<>();

        map.put("name", r.getName());
        map.put("contentsFilePath", "contents.md");

        map.put("imagesDirPath", "images");
        map.put("filesDirPath", "files");

        map.put("servingSize", r.getServingSize());
        map.put("notes", r.getNotes());
        map.put("source", r.getSource());

        map.put("tags", Arrays.toString(r.getTags()));
        map.put("categories", Arrays.toString(r.getCategories()));

        return map;
    }

    public static int loadRecipeTrie() {
        // TODO

        return 0;
    }

    public static int saveRecipeTrie() { //TODO
        if (Files.exists(Path.of(recipeRepositoryPath + File.separator + "recipeIDTrie.txt"))) return 1;

        List<String> keys = recipeIDTrie.listKeys();

        try (FileWriter wr = new FileWriter(recipeRepositoryPath + File.separator + "recipeIDTrie.txt")) {
            for (String key : keys) {
                wr.write(key + System.lineSeparator());
            }
            wr.flush();
        } catch (IOException e) {
            if (debug) System.err.println("[saveRecipeTrie]: Could not save recipe trie file.");
            return 2;
        }

        if (debug) System.out.println("[saveRecipeTrie]: Recipe trie saved.");
        return 0;
    }
}
