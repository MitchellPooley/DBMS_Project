package DataHandling;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileManager {
    private static final String DATALOCATION = "./data";
    private static final String SLASH = "/";

    /**
     * Create a new database, represented by a folder that can contain tables.
     * @param dataBaseName Name of the database to be created
     */
    public static void createDataBase(String dataBaseName) {
        if (new File(DATALOCATION + SLASH + dataBaseName).mkdirs()) {
            System.out.println("Database '" + dataBaseName + "' was created");
        }
        else {
            System.out.println("ERROR: A database with the name '" + dataBaseName + "' already exists");
        }
    }

    /**
     * Creates a new table within a database folder.
     * Represented by a folder that contains a Schema and can contain blocks.
     * @param dataBaseName Name of the database containing the table
     * @param tableName Name of the table to be created
     * @param schema Table Schema
     * @throws IOException Produced by failed or interrupted I/O operations.
     */
    public static void createTable(String dataBaseName, String tableName, ArrayList<String> schema) throws IOException {
        String dataBaseDir;

        // Check that database exists
        if (getFileNames(DATALOCATION).contains(dataBaseName)) {
            dataBaseDir = DATALOCATION + SLASH + dataBaseName;
        } else {
            System.out.println("ERROR: No database with the name '" + dataBaseName + "' exists");
            return;
        }

        // Create table if no table of the same name exists
        if (!getFileNames(dataBaseDir).contains(tableName)) {
            if (!new File(DATALOCATION + SLASH + dataBaseName + SLASH + tableName).mkdirs()) {
                System.out.println("ERROR: Table failed to generate, try again please");
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    DATALOCATION + SLASH + dataBaseName + SLASH + tableName + "/schema"))) {
                out.writeObject(new TableSchema(schema));
                System.out.println("Database '" + tableName + "' was created");            }
        }
        else {
            System.out.println("ERROR: Table with the name '" + tableName + "' already exists");
        }
    }

    /**
     * Gets an ArrayList of files names within a given directory.
     * @param dir Directory to search
     * @return ArrayList of file names
     */
    public static ArrayList<String> getFileNames(String dir) {
        String[] files = new File(dir).list();
        ArrayList<String> fileList;

        if (files != null) {
            fileList = new ArrayList<>(Arrays.asList(files));
        }
        else {
            fileList = new ArrayList<>();
        }
        return fileList;
    }

}
