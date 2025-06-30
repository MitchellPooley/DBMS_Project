import DataHandling.FileManager;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Ensure data file exists and create a new file if not
        new File("./data").mkdirs();

        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Boolean");
        ArrayList<Class<?>> columnType = new ArrayList<>();
        columnType.add(Boolean.class);
        ArrayList<String> data = new ArrayList<>();
        data.add("false");

//        FileManager.createDataBase("SecondDB");
//        FileManager.createTable( "SecondTable", columnNames, columnType);
//        FileManager.createRow( "SecondTable", data);
    }
}