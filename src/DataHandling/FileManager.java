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
    private static final int    ISVALID    = 0;
    private static final int    NEWDATA    = 1;

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
     * @param rowName Table row names
     * @param rowType Table row types
     * @throws IOException Produced by failed or interrupted I/O operations.
     */
    public static void createTable(String databaseName, String tableName, ArrayList<String> rowName, ArrayList<Class<?>> rowType) throws IOException {
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
                out.writeObject(new TableSchema(rowName, rowType));
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
        ArrayList<Object> result = convertData(data, tableDir);
        ArrayList<Object> convertedData;
        if (! (Boolean) result.get(ISVALID)) {
            System.out.println("ERROR: The provided data does not fit the tables schema");
            return;
        }
        else {
            convertedData = (ArrayList<Object>) result.get(NEWDATA);
        }

        // Add the data to the last page in a table or create a new one if it is full
        ArrayList<String> fileNames = getFileNames(tableDir);
        String lastPageDir = tableDir + SLASH + fileNames.get(fileNames.size() + LASTPAGE);

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
            id = page.getLastRow().getId() + 1;
        }

        // Add row to a new page, if last page is full
        if (page.isFull()) {
            String newPageDir = lastPageDir.substring(0, lastPageDir.length() - 1) + fileNames.size();
            page = new Page();
            page.addRow(new Row(id, convertedData));

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(newPageDir))) {
                out.writeObject(page);
                System.out.println("Data was successfully added to '" + databaseName + "'");
            }
            return;
        }

        // Add row to the old page, if there is room
        page.addRow(new Row(id, convertedData));
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

    /**
     * Checks if the data fits the schema and converts it to the data types given in the schema.
     * @param data Data to check
     * @param tableDir Directory of the table being inserted into
     * @return ArrayList containing a Boolean value (If the data is valid) and the converted data
     * @throws IOException Produced by failed or interrupted I/O operations.
     * @throws ClassNotFoundException Thrown when no definition for the class with the specified name could be found.
     */
    public static ArrayList<Object> convertData(ArrayList<String> data, String tableDir) throws IOException, ClassNotFoundException {
        ArrayList<Object> result = new ArrayList<>();
        TableSchema schema;

        // Read the schema
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableDir + SCHEMADIR))) {
            schema = (TableSchema) in.readObject();
        }

        // Check data length matches schema length
        ArrayList<Class<?>> types = schema.getRowType();
        if (types.size() != data.size()) {
            result.add(false);
            return result;
        }
        result.add(true);
        result.add(new ArrayList<Object>());
        ArrayList<Object> updatedData = (ArrayList<Object>) result.get(NEWDATA);

        // Converts data to the correct type if possible, returns false if not
        // Gross wall of if statements, but I don't know how to do this better
        for (int i=0; i<data.size(); i++) {
            if (types.get(i) == String.class) {
                updatedData.add(data.get(i));
            }
            else if (types.get(i) == Integer.class) {
                try {
                    updatedData.add(Integer.parseInt(data.get(i)));
                }
                catch (NumberFormatException e) {
                    result.set(ISVALID, false);
                    return result;
                }
            }
            else if (types.get(i) == Boolean.class) {
                if (canConvertToBoolean(data.get(i))) {
                    updatedData.add(Boolean.parseBoolean(data.get(i)));
                }
                else {
                    result.set(ISVALID, false);
                    return result;
                }
            }
            else if (types.get(i) == Float.class) {
                try {
                    updatedData.add(Float.parseFloat(data.get(i)));
                }
                catch (NumberFormatException e) {
                    result.set(ISVALID, false);
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * Checks if a string can be converted to a Boolean.
     * @param str String to check.
     * @return Boolean value
     */
    public static boolean canConvertToBoolean(String str) {
        return "true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str);
    }
}
