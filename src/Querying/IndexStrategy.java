package Querying;

import DataHandling.TableStats;

public interface IndexStrategy {
    public int CalculateIO(TableStats stats, String predicate);
    public int CalculateSize(TableStats stats, String predicate);

    public int CalculateRFEqual(TableStats stats);
    public int CalculateRFGreater(TableStats stats);
    public int CalculateRFLess(TableStats stats);
}
