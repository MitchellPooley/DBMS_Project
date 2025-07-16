package Querying;

import DataHandling.Schema;
import DataHandling.TableStats;

public interface IndexStrategy {
    public int CalculateIO(int colIndex, String predicate, Object value, TableStats stats);
    public int CalculateSize(int colIndex, String predicate, Object value, TableStats stats);

    public QueryResult select(String indexName, String predicate, Boolean inclusive, Object value, Schema schema, String tableName);
}
