package DataHandling;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileManager {
    private static final String DATADIR    = "./data";
    private static final String SLASH      = "/";
    private static final String SCHEMADIR  = "/schema";
    private static final String FIRSTBLOCK = "/page1";
    private static final int    LASTPAGE   = -2;
    private static final int    LASTROW    = -1;

    /**
     * Create a new database, represented by a folder that can contain tables.
     * @param databaseName Name of the database to be created
     */
    public static void createDataBase(String databaseName) {
        if (new File(DATADIR + SLASH + databaseName).mkdirs()) {
            System.out.println("Database '" + databaseName + "' was created");
        }
        else {
            System.out.println("ERROR: A database with the name '" + databaseName + "' already exists");
        }
    }

    /**
     * Creates a new table within a database folder.
     * Represented by a folder that contains a Schema and can contain blocks.
     * @param databaseName Name of the database containing the table
     * @param tableName Name of the table to be created
     * @param schema Table Schema
     * @throws IOException Produced by failed or interrupted I/O operations.
     */
    public static void createTable(String databaseName, String tableName, ArrayList<String> schema) throws IOException {
        // Check the database exists
        String databaseDir = checkForDatabase(databaseName);
        if (databaseDir == null) {
            return;
        }

        // Create table if no table of the same name exists
        if (!getFileNames(databaseDir).contains(tableName)) {
            if (!new File(databaseDir + SLASH + tableName).mkdirs()) {
                System.out.println("ERROR: Table failed to generate, try again please");
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    databaseDir + SLASH + tableName + SCHEMADIR))) {
                out.writeObject(new TableSchema(schema));
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    databaseDir + SLASH + tableName + FIRSTBLOCK))) {
                out.writeObject(new Page());
                System.out.println("Database '" + tableName + "' was created");
            }
        }
        else {
            System.out.println("ERROR: Table with the name '" + tableName + "' already exists");
        }
    }

    /**
     * Adds a new row to a table and gives it a unique id
     * @param databaseName Name of the database being inserted into
     * @param tableName Name of the table being inserted into
     * @param data Data being inserted
     * @throws IOException Produced by failed or interrupted I/O operations.
     * @throws ClassNotFoundException Thrown when no definition for the class with the specified name could be found.
     */
    public static void createRow(String databaseName, String tableName, ArrayList<String> data) throws IOException, ClassNotFoundException {
        // Check the database exists
        String databaseDir = checkForDatabase(databaseName);
        if (databaseDir == null) {
            System.out.println("ERROR: Database with the name '" + databaseName + "' does not exist");
            return;
        }

        // Check that the table exists
        if (!getFileNames(databaseDir).contains(tableName)) {
            System.out.println("ERROR: Table with the name '" + tableName + "' does not exist");
            return;
        }
        String tableDir = databaseDir + SLASH + tableName;

        // Check data fits the table schema
        if (!isValidData(data, tableDir)) {
            System.out.println("ERROR: The provided data does not fit the tables schema");
            return;
        }

        // Add the data to the last page in a table or create a new one if it is full
        ArrayList<String> fileNames = getFileNames(tableDir);
        String lastPageDir = tableDir + SLASH + fileNames.get(LASTPAGE);

        // Get the last page
        Page page;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(lastPageDir))) {
            page = (Page) in.readObject();
        }

        // Get the new rows id
        int id;
        if (page.isEmpty()) {
            id = 0;
        }
        else {
            id = page.getRow(LASTROW).getId() + 1;
        }

        // Add row to a new page, if last page is full
        if (page.isFull()) {
            String newPageDir = lastPageDir.substring(0, lastPageDir.length() - 1) + fileNames.size();
            page = new Page();
            page.addRow(new Row(id, data));

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(newPageDir))) {
                out.writeObject(page);
                System.out.println("Data was successfully added to '" + databaseName + "'");
            }
            return;
        }

        // Add row to the old page, if there is room
        page.addRow(new Row(id, data));
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(lastPageDir))) {
            out.writeObject(page);
            System.out.println("Data was successfully added to '" + databaseName + "'");
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

    /**
     * Checks if the given database exists
     * @param databaseName Database to check
     * @return The directory of said database
     */
    public static String checkForDatabase(String databaseName) {
        // Check that database exists
        if (getFileNames(DATADIR).contains(databaseName)) {
            return DATADIR + SLASH + databaseName;
        } else {
            System.out.println("ERROR: No database with the name '" + databaseName + "' exists");
            return null;
        }
    }

    // TODO Change functionality to allow for non-string data types
    public static Boolean isValidData(ArrayList<String> data, String tableDir) throws IOException, ClassNotFoundException {
        TableSchema schema;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableDir + SCHEMADIR))) {
            schema = (TableSchema) in.readObject();
        }

        return schema.getSchema().size() == data.size();
    }

}
