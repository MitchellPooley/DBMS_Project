import DataHandling.FileManager;
import DataHandling.TableStats;
import Querying.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Test {
    public boolean RunTest() throws IOException, ClassNotFoundException {
        boolean passed = true;
        passed = passed && CreateDBTest();
        passed = passed && HeapIndexTest();
        passed = passed && BPlusTreeIndexTest();


        return true;
    }

    // Create a database, table and add a row to the table
    private boolean CreateDBTest() throws IOException, ClassNotFoundException {
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("String");
        ArrayList<Class<?>> columnType = new ArrayList<>();
        columnType.add(String.class);
        ArrayList<String> data = new ArrayList<>();
        data.add("Hello");

        FileManager.createDataBase("DB");
        FileManager.setCurrentDataBaseDir("DB");
        FileManager.createTable( "Table", columnNames, columnType);
        FileManager.addRow( "Table", data);

        return true;
    }

    private boolean HeapIndexTest() throws IOException {
        TableStats tableStats;
        String dir = FileManager.getCurrentDataBaseDir() + "/Table/tableStats";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dir))) {
            tableStats = (TableStats) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        tableStats.analyzeTable();

        int io = HeapIndex.getInstance().CalculateIO(0, "Equals", false, tableStats);
        System.out.println(io);

        double rf = HeapIndex.getInstance().CalculateRFEqual(tableStats, 0);
        System.out.println(rf);

        return true;
    }

    private boolean BPlusTreeIndexTest() {
        BPlusTreeIndex.getInstance().createBPlusTree("String", "Table");

        QueryResult queryResult = BPlusTreeIndex.getInstance().select("String", "Equals", false, "Hello", "Table");
        queryResult.displayResult();
        return true;
    }
}
