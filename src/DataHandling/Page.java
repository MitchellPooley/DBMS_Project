package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {
    private static final int MAXROWS = 5;
    private static final int LASTROW = -1;

    private final ArrayList<Row> rows = new ArrayList<>();

    /**
     * Adds a new row to the page.
     * @param row added data
     */
    public void addRow(Row row) {
        rows.add(row);
    }

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
        return rows.get(rows.size() + LASTROW);
    }

    /**
     * Checks if a page is full.
     * @return boolean value
     */
    public boolean isFull() {
        return rows.size() >= MAXROWS;
    }

    /**
     * Checks if a page is empty.
     * @return boolean value
     */
    public boolean isEmpty() {
        return rows.isEmpty();
    }

}
