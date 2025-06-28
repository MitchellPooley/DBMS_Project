package DataHandling;

import java.io.*;
import java.util.ArrayList;

public class FileManager {
    private static final String dataLocation = "./data/";

    /**
        Create a new database. Database is stored as a folder which stores
     **/
    public static void createDataBase(String dataBaseName) throws IOException {
        if (new File(dataLocation + dataBaseName).mkdirs()) {
            System.out.println("New database created");
        }
        else {
            System.out.println("ERROR: A database with the name '" + dataBaseName + "' already exists");
        }
    }

    public static void createTable(String dataBaseName, String tableName, ArrayList<String> schema) {

        // Check that database exists
        File[] files = new File(dataLocation).listFiles();
        ArrayList<String> dataBases = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    dataBases.add(file.getName());
                }
            }
        }
        DataBase currentDataBase;
        if (dataBases.contains(dataBaseName)) {
            currentDataBase = new DataBase(dataLocation + dataBaseName);
        } else {
            System.out.println("ERROR: No database with the name '" + dataBaseName + "' exists");
            return;
        }

        // Create table if no table of the same name exists
        if (!currentDataBase.getTables().contains(tableName)) {

        }
        else {
            System.out.println("ERROR: Table with the name '" + tableName + "' already exists");
        }
    }

}
