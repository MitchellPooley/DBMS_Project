import DataHandling.FileManager;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Ensure data file exists and create a new file if not
        new File("./data").mkdirs();

        ArrayList<String> schema = new ArrayList<>();

        FileManager.createDataBase("FirstDB");
        FileManager.createTable("FirstDB", "FirstTable", schema);
        FileManager.createRow("FirstDB", "FirstTable", new ArrayList<String>());
    }
}