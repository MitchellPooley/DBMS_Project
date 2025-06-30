package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {
    private static final int MAX_ROWS = 5;
    private static final int LAST_ROW = -1;

    private final ArrayList<Row> rows = new ArrayList<>();

    /**
     * Adds a new row to the page.
     * @param row added data
     */
    public void addRow(Row row) {
        rows.add(row);
    }

    /**
     * Returns all rows.
     * @return All rows in the page
     */
    public ArrayList<Row> getRows() { return rows;}

    /**
     * Gets a row from the page.
     * @param index Row index
     * @return Returns the row
     */
    public Row getRow(int index) {
        return rows.get(index);
    }

    /**
     * Gets the last row from the page.
     * @return Returns the row
     */
    public Row getLastRow() {
        return rows.get(rows.size() + LAST_ROW);
    }

    /**
     * Checks if a page is full.
     * @return boolean value
     */
    public boolean isFull() {
        return rows.size() >= MAX_ROWS;
    }

    /**
     * Checks if a page is empty.
     * @return boolean value
     */
    public boolean isEmpty() {
        return rows.isEmpty();
    }

}
