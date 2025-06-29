import DataHandling.FileManager;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Ensure data file exists and create a new file if not
        new File("./data").mkdirs();

        ArrayList<String> rowName = new ArrayList<>();
        rowName.add("Boolean");
        ArrayList<Class<?>> rowType = new ArrayList<>();
        rowType.add(Boolean.class);
        ArrayList<String> data = new ArrayList<>();
        data.add("false");

        FileManager.createDataBase("SecondDB");
        FileManager.createTable("SecondDB", "SecondTable", rowName, rowType);
        FileManager.createRow("SecondDB", "SecondTable", data);
    }
}