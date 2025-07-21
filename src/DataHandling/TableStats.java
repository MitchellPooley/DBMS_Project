package DataHandling;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TableStats implements Serializable {
    private final int    NUM_NON_PAGES = 3;
    private final String PAGE_DIR      = "/page";

    private int numRows = 0;
    private int numPages = 1;
    private final ArrayList<Class<?>> columnType;
    private final ArrayList<Object> minValue = new ArrayList<>();
    private final ArrayList<Object> maxValue = new ArrayList<>();
    private final ArrayList<Set<Object>> unique = new ArrayList<>();
    private final String tableName;


    public TableStats(ArrayList<Class<?>> columnType, String tableName) {
        this.columnType = columnType;
        this.tableName = tableName;
        for (int i=0; i<columnType.size(); i++) {
            minValue.add(null);
            maxValue.add(null);
            unique.add(new HashSet<>());
        }
    }

    /**
     * Increases the count of rows
     */
    public void addRow() {
        numRows++;
    }

    /**
     * Decreases the count of rows
     */
    public void removeRow() {
        numRows--;
    }

    /**
     * Increases the count of pages
     */
    public void addPage() {
        numPages++;
    }

    /**
     * Decreases the count of pages
     */
    public void removePage() {
        numPages--;
    }

    /**
     * Analyses the table and updates the table stats
     */
    public void analyzeTable() throws IOException {
        numRows = 0;
        numPages = 0;
        String tableDir = FileManager.getCurrentDataBaseDir() + FileManager.SLASH + tableName;

        for (int i=1; i<=FileManager.getFileNames(tableDir).size()-NUM_NON_PAGES; i++) {
            numPages++;
            Page page;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableDir + PAGE_DIR + i))) {
                page = (Page) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            for (Row row: page.getRows()) {
                analyseRow(row.getData());
            }
        }
        String statsDir = FileManager.getCurrentDataBaseDir() + FileManager.SLASH + tableName + "/tableStats";
        Path filePath = Paths.get(statsDir);
        Files.deleteIfExists(filePath);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                statsDir))) {
            out.writeObject(this);
        }
    }

    /**
     * Updates table stats for a single row.
     * @param data Data from a single row
     */
    private void analyseRow(ArrayList<Object> data) {
        numRows++;
        for (int i=0; i<data.size(); i++) {
            unique.get(i).add(data.get(i));
            if (columnType.get(i) == Integer.class) {
                if (minValue.get(i) == null) {
                    minValue.set(i, (Integer) data.get(i));
                }
                else if ((Integer) minValue.get(i) > (Integer) data.get(i)) {
                    minValue.set(i, (Integer) data.get(i));
                }
                if (maxValue.get(i) == null) {
                    maxValue.set(i, (Integer) data.get(i));
                }
                else if ((Integer) maxValue.get(i) < (Integer) data.get(i)) {
                    maxValue.set(i, (Integer) data.get(i));
                }
            }
            else if (columnType.get(i) == Float.class) {
                if (minValue.get(i) == null) {
                    minValue.set(i, (Float) data.get(i));
                }
                else if ((Float) minValue.get(i) > (Float) data.get(i)) {
                    minValue.set(i, (Float) data.get(i));
                }
                if (maxValue.get(i) == null) {
                    maxValue.set(i, (Float) data.get(i));
                }
                else if ((Float) maxValue.get(i) < (Float) data.get(i)) {
                    maxValue.set(i, (Integer) data.get(i));
                }
            }
        }
    }

    /**
     * Gets the minValue in each column.
     * @return Arraylist of values
     */
    public ArrayList<Object> getMinValue() { return minValue;}

    /**
     * Gets the maxValue in each column.
     * @return Arraylist of values
     */
    public ArrayList<Object> getMaxValue() { return maxValue;}

    /**
     * Gets the unique values in the table.
     * @return ArrayList of sets containing each value.
     */
    public ArrayList<Set<Object>> getUnique() { return unique;}

    /**
     * Gets the number of rows in the table,
     * @return int number of rows
     */
    public int getNumRows() { return numRows;}

    /**
     * Gets the number of pages in the table,
     * @return int number of pages
     */
    public int getNumPages() { return numPages;}

    /**
     * Gets the tables column types,
     * @return ArrayList of Classes
     */
    public ArrayList<Class<?>> getColumnType() { return columnType;}
}
