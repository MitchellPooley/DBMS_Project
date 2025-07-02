package Querying;

import DataHandling.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class HeapIndex extends ParentIndex{
    private static HeapIndex heapIndex;

    /**
     * Singleton getter method.
     * @return Singleton instance of HeapIndex class
     */
    public static HeapIndex getInstance() {
        if (heapIndex == null) {
            return new HeapIndex();
        }
        return heapIndex;
    }

    /**
     * Calculates the expected IO cost of selecting using a heap index.
     * @param colIndex Column being selected over
     * @param predicate Predicate of the selection (Equals, Greater, Lesser)
     * @param value Value being selected for
     * @param stats Tables stats, such as numPages or numUnique
     * @return Expected number of IO operations
     */
    @Override
    public int CalculateIO(int colIndex, String predicate, Object value, TableStats stats) {
        return stats.getNumRows();
    }

    /**
     * Select rows from a table based on some predicate.
     * @param colIndex Column being selected over
     * @param predicate Predicate of the selection (Equals, Greater, Lesser)
     * @param inclusive If the predicate is inclusive (Greater/Lesser Than or Equal)
     * @param value Value being selected for
     * @param schema Schema of the table before selection
     * @param tableName Name of the table being selected over
     * @return QueryResult
     */
    public QueryResult select(int colIndex, String predicate, Boolean inclusive, Object value, Schema schema, String tableName) {
        String dir = FileManager.DATA_DIR + FileManager.SLASH + tableName;
        QueryResult result = new QueryResult(schema);

        for (int i=1; i<FileManager.getFileNames(dir).size()-NUM_NON_PAGES; i++) {
            Page page;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dir + PAGE_DIR + i))) {
                page = (Page) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Class<?> columnType = schema.getColumnType().get(colIndex);
            for (Row row: page.getRows()) {
                // Checks equality if a predicate is greater than or equal / less than or equal
                boolean included = false;
                if (inclusive) {
                    included = compareOnPred(EQUAL, row, colIndex, value, columnType);
                }
                if (compareOnPred(predicate, row, colIndex, value, columnType) || included) {
                    result.addRow(row);
                }
            }
        }
        return result;
    }
}
