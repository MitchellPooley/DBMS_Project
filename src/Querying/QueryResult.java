package Querying;

import DataHandling.Page;
import DataHandling.Row;
import DataHandling.Schema;

import java.util.ArrayList;

public class QueryResult {
    private final int DEFAULT_START_INDEX = 0;
    private final int DEFAULT_END_INDEX   = 10;

    Schema schema;
    ArrayList<Page> pages = new ArrayList<>();


    public QueryResult(Schema schema) {
        this.schema = schema;
        this.pages.add(new Page());
    }

    /**
     * Add a row to the query result.
     * @param row Added row.
     */
    public void addRow(Row row) {
        if (pages.get(pages.size()-1).isFull()) {
            pages.add(new Page());
        }
        pages.get(pages.size()-1).addRow(row);
    }

    /**
     * Display the query result using default length and index (0-10).
     */
    public void displayResult() {
        displayResult(DEFAULT_START_INDEX, DEFAULT_END_INDEX);
    }

    /**
     * Display the query result.
     * @param startIndex Start index of displayed rows
     * @param endIndex End index of displayed rows
     */
    public void displayResult(int startIndex, int endIndex) {
        int columns = schema.size();
        int[] colWidths = new int[columns];

        // Determine the maximum string length
        for (int col=0; col< schema.size(); col++) {
            colWidths[col] = Math.max(colWidths[col], schema.getColumnName().get(col).length());
        }
        int curRow = -1;
        outerLoop:
        for (Page page: pages) {
            for (Row row: page.getRows()) {
                curRow++;
                if (curRow == endIndex) {
                    break outerLoop;
                }
                else if (curRow < startIndex) {
                    continue;
                }
                ArrayList<String> rowData = row.getAsString();
                for (int col=0; col<row.getData().size(); col++) {
                    colWidths[col] = Math.max(colWidths[col], rowData.get(col).length());
                }
            }
        }

        // Print the schema
        for (int col=0; col< schema.size(); col++) {
            // left-align with padding
            String format = "%-" + (colWidths[col] + 2) + "s";
            System.out.printf(format, schema.getColumnName().get(col));
            System.out.println();
        }
        // Print the rows
        curRow = -1;
        outerLoop:
        for (Page page: pages) {
            for (Row row: page.getRows()) {
                curRow++;
                if (curRow == endIndex) {
                    break outerLoop;
                }
                else if (curRow < startIndex) {
                    continue;
                }
                ArrayList<String> rowData = row.getAsString();
                for (int col=0; col<row.getData().size(); col++) {
                    // left-align with padding
                    String format = "%-" + (colWidths[col] + 2) + "s";
                    System.out.printf(format, rowData.get(col));
                }
                System.out.println();
            }
        }
    }
}
