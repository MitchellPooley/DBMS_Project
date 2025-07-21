package DataHandling;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileManager {
    public  static final String DATA_DIR    = "./data";
    public  static final String SLASH       = "/";
    private static final String SCHEMA_DIR  = "/schema";
    private static final String FIRST_BLOCK = "/page1";
    private static final String INDEX_FILE  = "/index";
    private static final String TABLE_STATS = "/tableStats";
    private static final int    LAST_PAGE   = -3;
    private static final int    IS_VALID    = 0;
    private static final int    NEW_DATA    = 1;
    private static final int    PAGE_OFFSET = -2;

    private static String currentDataBaseDir;

    //TODO Separate into cohesive classes
    //TODO Add primary key functionality

    /**
     * Set the current database being handled
     * @param databaseName name of said database
     */
    public static void setCurrentDataBaseDir(String databaseName) {

        // Check that database exists
        if (getFileNames(DATA_DIR).contains(databaseName)) {
            FileManager.currentDataBaseDir = DATA_DIR + SLASH + databaseName;
        } else {
            System.out.println("ERROR: No database with the name '" + databaseName + "' exists");
        }
    }

    /**
     * Get the current database directory
     * @return String containing the path
     */
    public static String getCurrentDataBaseDir() {
        return currentDataBaseDir;
    }

    /**
     * Create a new database, represented by a folder that can contain tables.
     * @param databaseName Name of the database to be created
     */
    public static void createDataBase(String databaseName) {
        if (!new File(DATA_DIR + SLASH + databaseName).mkdirs()) {
            System.out.println("ERROR: A database with the name '" + databaseName + "' already exists");
        }
    }

    /**
     * Creates a new table within a database folder.
     * Represented by a folder that contains a Schema and can contain blocks.
     * @param tableName Name of the table to be created
     * @param columnNames Table row names
     * @param columnType Table row types
     * @throws IOException Produced by failed or interrupted I/O operations.
     */
    public static void createTable(String tableName, ArrayList<String> columnNames, ArrayList<Class<?>> columnType) throws IOException {
        // Check the database exists
        String databaseDir = getCurrentDataBaseDir();

        // Create table if no table of the same name exists
        if (!getFileNames(databaseDir).contains(tableName)) {
            new File(databaseDir + SLASH + tableName).mkdirs();
            new File(databaseDir + SLASH + tableName + INDEX_FILE).mkdirs();

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    databaseDir + SLASH + tableName + SCHEMA_DIR))) {
                out.writeObject(new Schema(columnNames, columnType));
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    databaseDir + SLASH + tableName + FIRST_BLOCK))) {
                out.writeObject(new Page());
            }
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    databaseDir + SLASH + tableName + TABLE_STATS))) {
                out.writeObject(new TableStats(columnType, tableName));
            }
        }
        else {
            System.out.println("ERROR: Table with the name '" + tableName + "' already exists");
        }
    }

    /**
     * Adds a new row to a table and gives it a unique id.
     * @param tableName Name of the table being inserted into
     * @param data Data being inserted
     * @throws IOException Produced by failed or interrupted I/O operations.
     * @throws ClassNotFoundException Thrown when no definition for the class with the specified name could be found.
     */
    public static void addRow(String tableName, ArrayList<String> data) throws IOException, ClassNotFoundException {
        // Check the database exists
        String databaseDir = getCurrentDataBaseDir();

        // Check that the table exists
        if (!getFileNames(databaseDir).contains(tableName)) {
            System.out.println("ERROR: Table with the name '" + tableName + "' does not exist");
            return;
        }
        String tableDir = databaseDir + SLASH + tableName;

        // Check data fits the table schema
        ArrayList<Object> result = convertData(data, tableDir);
        ArrayList<Object> convertedData;
        if (! (Boolean) result.get(IS_VALID)) {
            System.out.println("ERROR: The provided data does not fit the tables schema");
            return;
        }
        else {
            convertedData = (ArrayList<Object>) result.get(NEW_DATA);
        }

        TableStats tableStats;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(databaseDir + SLASH + tableName
                + SLASH + TABLE_STATS))) {
            tableStats = (TableStats) in.readObject();
            tableStats.addRow();
        }

        // Add the data to the last page in a table or create a new one if it is full
        ArrayList<String> fileNames = getFileNames(tableDir);
        String lastPageDir = tableDir + SLASH + fileNames.get(fileNames.size() + LAST_PAGE);

        // Get the last page
        Page page;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(lastPageDir))) {
            page = (Page) in.readObject();
        }


        // Add row to a new page, if last page is full
        if (page.isFull()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(databaseDir + SLASH + tableName + TABLE_STATS))) {
                tableStats = (TableStats) in.readObject();
                tableStats.addPage();
            }

            String newPageDir = lastPageDir.substring(0, lastPageDir.length() - 1) + (fileNames.size() + PAGE_OFFSET);
            page = new Page();
            page.addRow(new Row(convertedData));

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(newPageDir))) {
                out.writeObject(page);
                System.out.println("Data was successfully added to '" + tableName + "'");
            }
            return;
        }

        // Add row to the old page, if there is room
        page.addRow(new Row(convertedData));
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(lastPageDir))) {
            out.writeObject(page);
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
     * Checks if the data fits the schema and converts it to the data types given in the schema.
     * @param data Data to check
     * @param tableDir Directory of the table being inserted into
     * @return ArrayList containing a Boolean value (If the data is valid) and the converted data
     * @throws IOException Produced by failed or interrupted I/O operations.
     * @throws ClassNotFoundException Thrown when no definition for the class with the specified name could be found.
     */
    public static ArrayList<Object> convertData(ArrayList<String> data, String tableDir) throws IOException, ClassNotFoundException {
        ArrayList<Object> result = new ArrayList<>();
        Schema schema;

        // Read the schema
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableDir + SCHEMA_DIR))) {
            schema = (Schema) in.readObject();
        }

        // Check data length matches schema length
        ArrayList<Class<?>> types = schema.getColumnType();
        if (types.size() != data.size()) {
            result.add(false);
            return result;
        }
        result.add(true);
        result.add(new ArrayList<Object>());
        ArrayList<Object> updatedData = (ArrayList<Object>) result.get(NEW_DATA);

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
                    result.set(IS_VALID, false);
                    return result;
                }
            }
            else if (types.get(i) == Boolean.class) {
                if (canConvertToBoolean(data.get(i))) {
                    updatedData.add(Boolean.parseBoolean(data.get(i)));
                }
                else {
                    result.set(IS_VALID, false);
                    return result;
                }
            }
            else if (types.get(i) == Float.class) {
                try {
                    updatedData.add(Float.parseFloat(data.get(i)));
                }
                catch (NumberFormatException e) {
                    result.set(IS_VALID, false);
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

    private static Page getPage(String pageName) throws FileNotFoundException {
        String databaseDir = getCurrentDataBaseDir();
        String pageDir = databaseDir + SLASH + pageName;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pageDir))) {
            return (Page) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Row getRow(String pageName, int rowIndex) throws FileNotFoundException {
        return getPage(pageName).getRow(rowIndex);
    }
}
