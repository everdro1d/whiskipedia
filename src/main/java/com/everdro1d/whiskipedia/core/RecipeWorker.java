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

    public static String parseNameToID(String name) {
        return name.toLowerCase().replaceAll("\\s+", "-");
    }

    private static RecipeObject loadRecipe(String recipeID) {
        Path recipePath = Path.of(recipeRepositoryPath + File.separator + recipeID);
        Path directoryFilePath = Path.of(recipePath + File.separator + "directory.txt");
        Path contentsFilePath = Path.of(recipePath + File.separator + "contents.md");
        Path imagesDirPath = Path.of(recipePath + File.separator + "images");
        Path filesDirPath = Path.of(recipePath + File.separator + "files");

        if (Files.notExists(recipePath) || Files.notExists(directoryFilePath) || Files.notExists(contentsFilePath)
                || Files.notExists(imagesDirPath) || Files.notExists(filesDirPath)) {
            if (debug) System.err.println("[loadRecipe]: Recipe could not be found.");
            return null;
        }

        RecipeObject r = new RecipeObject();

        loadDirectoryMap(directoryFilePath, r);

        loadContentsFile(contentsFilePath, r);

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
        Path directoryFilePath = Path.of(recipePath + File.separator + "directory.txt");
        Path contentsFilePath = Path.of(recipePath + File.separator + "contents.md");
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

        saveRecipeTrie(true);

        // save directory file
        com.everdro1d.libs.io.Files.saveMapToFile(directoryFilePath, populateDirectoryMap(r), true);

        // save contents file
        try (FileWriter wr = new FileWriter(contentsFilePath.toString())) {
            wr.write(r.getDescription() + "§§§" + r.getInstructions() + "§§§" + r.getIngredients());
            wr.flush();
        } catch (IOException e) {
            if (debug) System.err.println("[saveRecipe]: Could not save recipe contents file.");
            return 5;
        }

        // copy images to dir
        for (Path image : r.getImages()) {
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
        for (Path file : r.getAdditionalFiles()) {
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

    private static Map<String, String> populateDirectoryMap(RecipeObject r) {
        Map<String, String> map = new HashMap<>();

        map.put("name", r.getName());

        map.put("servingSize", r.getServingSize());
        map.put("notes", r.getNotes());
        map.put("source", r.getSource());

        map.put("tags", Arrays.toString(r.getTags()));
        map.put("categories", Arrays.toString(r.getCategories()));

        return map;
    }

    private static int loadDirectoryMap(Path directoryFilePath, RecipeObject r) {
        Map<String, String> map = com.everdro1d.libs.io.Files.loadMapFromFile(directoryFilePath);
        if (map == null) {
            if (debug) System.err.println("[loadDirectoryMap]: Could not load directory map from file.");
            return 1;
        }

        r.setName(map.get("name"));

        r.setServingSize(map.get("servingSize"));
        r.setNotes(map.get("notes"));
        r.setSource(map.get("source"));

        r.setTags(map.get("tags").replaceAll("[\\[\\]\\s]", "").split(","));
        r.setCategories(map.get("categories").replaceAll("[\\[\\]\\s]", "").split(","));

        return 0;
    }

    private static int loadContentsFile(Path contentsFilePath, RecipeObject r) {
        List<String> lines;
        try {
            lines = Files.readAllLines(contentsFilePath);
        } catch (IOException e) {
            if (debug) System.err.println("[loadContentsFile]: Could not load contents file.");
            return 1;
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(System.lineSeparator());
        }

        String[] parts = sb.toString().split("§§§", -1);
        if (parts.length != 3) {
            if (debug) System.err.println("[loadContentsFile]: Contents file number of parts invalid: " + parts.length);
            return 2;
        }

        r.setDescription(parts[0].trim());
        r.setInstructions(parts[1].trim());
        r.setIngredients(parts[2].trim());

        if (debug)  System.out.println("[loadContentsFile]: Contents file loaded.");
        return 0;
    }

    public static int loadRecipeTrie() {
        // TODO

        return 0;
    }

    public static int saveRecipeTrie(boolean overwrite) {
        if (!overwrite && Files.exists(Path.of(recipeRepositoryPath + File.separator + "recipeIDTrie.txt"))) {
            if (debug) System.err.println("[saveRecipeTrie]: Recipe trie file exists and overwrite is disabled.");
            return 1;
        }

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
