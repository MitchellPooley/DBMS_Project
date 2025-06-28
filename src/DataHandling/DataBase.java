package DataHandling;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class DataBase implements Serializable {
    private ArrayList<String> tables = new ArrayList<>();
    public DataBase(String dbDirectory) {
        File[] files = new File(dbDirectory).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                tables.add(file.getName());
            }
        }
    }

    public void addTable(String name) {
        tables.add(name);
    }

    public ArrayList<String> getTables() { return tables; }
}
