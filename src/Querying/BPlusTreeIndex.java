package Querying;

import DataHandling.Schema;
import DataHandling.TableStats;

public class BPlusTreeIndex extends ParentIndex {
    private static BPlusTreeIndex bPlusTreeIndex;

    /**
     * Singleton getter method.
     * @return Singleton instance of BPlusTreeIndex class
     */
    public static BPlusTreeIndex getInstance() {
        if (bPlusTreeIndex == null) {
            return new BPlusTreeIndex();
        }
        return bPlusTreeIndex;
    }

    @Override
    public int CalculateIO(int colIndex, String predicate, Object value, TableStats stats) {
        double rf = RF_MAGIC;
        rf = switch (predicate) {
            case EQUAL -> CalculateRFEqual(stats, colIndex);
            case GREATER -> CalculateRFGreater(stats, colIndex, value);
            case LESSER -> CalculateRFLess(stats, colIndex, value);
            default -> rf;
        };
        return (int) Math.ceil(stats.getNumRows() * rf);
    }

    @Override
    public QueryResult select(int colIndex, String predicate, Boolean inclusive, Object value, Schema schema, String tableName) {


        return null;
    }
}
