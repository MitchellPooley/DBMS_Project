package DataHandling;

import java.io.*;
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
        for (int i=1; i<columnType.size(); i++) {
            minValue.add(null);
            maxValue.add(null);
            unique.add(new HashSet<>());
        }
    }

    public void addRow() {
        numRows++;
    }

    public void removeRow() {
        numRows--;
    }

    public void addPage() {
        numPages++;
    }

    public void removePage() {
        numPages--;
    }

    public void analyzeTable() {
        numRows = 0;
        numPages = 0;
        String dir = FileManager.DATA_DIR + FileManager.SLASH + tableName;
        for (int i=1; i<FileManager.getFileNames(dir).size()-NUM_NON_PAGES; i++) {
            numPages++;
            Page page;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dir + PAGE_DIR + i))) {
                page = (Page) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            for (Row row: page.getRows()) {
                analyseRow(row.getData());
            }
        }
    }
    
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

    public ArrayList<Object> getMinValue() { return minValue;}

    public ArrayList<Object> getMaxValue() { return maxValue;}

    public ArrayList<Set<Object>> getUnique() { return unique;}

    public int getNumRows() { return numRows;}

    public int getNumPages() { return numPages;}
}
