package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {
    private static final int MAXROWS = 100;

    private final ArrayList<Row> rows = new ArrayList<>();

    /**
     * Adds a new row to the page.
     * @param row added data
     */
    public void addRow(Row row) {
        rows.add(row);
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    /**
     * Checks if a page is full.
     * @return boolean
     */
    public boolean isFull() {
        return rows.size() == MAXROWS;
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

}
