package Querying;

import DataHandling.Page;
import DataHandling.Row;
import DataHandling.TableStats;

import java.util.Objects;

public abstract class ParentIndex implements IndexStrategy {
    final double RF_MAGIC      = 0.1;
    final int    NUM_NON_PAGES = 3;
    final String PAGE_DIR      = "/page";
    final String EQUAL         = "Equals";
    final String GREATER       = "Greater";
    final String LESSER        = "Lesser";

    /**
     * Calculates the resulting size of a selection.
     * @param stats Tables stats, such as numPages or numUnique
     * @param predicate Predicate of the selection (Equals, Greater, Lesser)
     * @param colIndex Column being selected over
     * @param value Value being selected for
     * @return Calculated size
     */
    @Override
    public int CalculateSize(int colIndex, String predicate, Object value, TableStats stats) {
        double rf = RF_MAGIC;
        if (Objects.equals(predicate, EQUAL)) {
            rf = CalculateRFEqual(stats, colIndex);
        }
        else if (predicate.equals(GREATER)) {
            rf = CalculateRFGreater(stats, colIndex, value);
        }
        else if (predicate.equals(LESSER)) {
            rf = CalculateRFLess(stats, colIndex, value);
        }
        return (int) Math.ceil(rf * stats.getNumRows());
    }

    /**
     * Calculates the reduction factor with an equality predicate.
     * @param stats Tables stats, such as numPages or numUnique
     * @param colIndex Column being selected over
     * @return Reduction value
     */
    public double CalculateRFEqual(TableStats stats, int colIndex) {
        int numUnique = stats.getUnique().get(colIndex).size();
        if (numUnique == 0) {
            return RF_MAGIC;
        }
        return 1.0 / numUnique;
    }

    /**
     * Calculates the reduction factor with a greater than predicate.
     * @param colIndex Column being selected over
     * @param value Value being selected for
     * @return Reduction value
     */
    public double CalculateRFGreater(TableStats stats, int colIndex, Object value) {
        Class<?> colType = stats.getColumnType().get(colIndex);

        if (colType == Integer.class) {
            int iValue = (Integer) value;
            int high = (Integer) stats.getMaxValue().get(colIndex);
            int low = (Integer) stats.getMinValue().get(colIndex);
            return (double) (high - iValue) / (high - low);
        }
        else if (colType == Float.class) {
            float iValue = (Float) value;
            float high = (Float) stats.getMaxValue().get(colIndex);
            float low = (Float) stats.getMinValue().get(colIndex);
            return (high - iValue) / (high - low);
        }
        return RF_MAGIC;
    }

    /**
     * Calculates the reduction factor with a less than predicate.
     * @param stats Tables stats, such as numPages or numUnique
     * @param colIndex Column being selected over
     * @param value Value being selected for
     * @return Reduction value
     */
    public double CalculateRFLess(TableStats stats, int colIndex, Object value) {
        Class<?> colType = stats.getColumnType().get(colIndex);

        if (colType == Integer.class) {
            int iValue = (Integer) value;
            int high = (Integer) stats.getMaxValue().get(colIndex);
            int low = (Integer) stats.getMinValue().get(colIndex);
            return (double) (iValue - low) / (high - low);
        }
        else if (colType == Float.class) {
            float iValue = (Float) value;
            float high = (Float) stats.getMaxValue().get(colIndex);
            float low = (Float) stats.getMinValue().get(colIndex);
            return (iValue - low) / (high - low);
        }
        return RF_MAGIC;
    }

    /**
     * returns true if indexed data in a row is equal/greater/lesser than the given value.
     * @param predicate equal/greater/lesser
     * @param row table row, includes data to be queried
     * @param colIndex column of data to be compared
     * @param value value to be compared against
     * @param columnType type of value in the indexed column
     * @return boolean
     */
    protected boolean compareOnPred(String predicate, Row row, int colIndex, Object value, Class<?> columnType) {
        if (columnType == Integer.class) {
            switch (predicate) {
                case EQUAL -> {
                    if (row.getData().get(colIndex) == null) {
                        return false;
                    }
                    return (Integer) row.getData().get(colIndex) == (Integer) value;
                }
                case GREATER -> {
                    if (row.getData().get(colIndex) == null) {
                        return false;
                    }
                    return (Integer) row.getData().get(colIndex) > (Integer) value;
                }
                case LESSER -> {
                    if (row.getData().get(colIndex) == null) {
                        return false;
                    }
                    return (Integer) row.getData().get(colIndex) < (Integer) value;
                }
            }
        }
        if (columnType == Float.class) {
            switch (predicate) {
                case EQUAL -> {
                    if (row.getData().get(colIndex) == null) {
                        return false;
                    }
                    return (Float) row.getData().get(colIndex) == (Float) value;
                }
                case GREATER -> {
                    if (row.getData().get(colIndex) == null) {
                        return false;
                    }
                    return (Float) row.getData().get(colIndex) > (Float) value;
                }
                case LESSER -> {
                    if (row.getData().get(colIndex) == null) {
                        return false;
                    }
                    return (Float) row.getData().get(colIndex) < (Float) value;
                }
            }
        }
        if (columnType == String.class) {
            if (predicate.equals(EQUAL)) {
                String data = (String) row.getData().get(colIndex);
                return data.equals((String) value);
            }
        }
        if (columnType == Boolean.class) {
            if (predicate.equals(EQUAL)) {
                return (Boolean) row.getData().get(colIndex) == (Boolean) value;
            }
        }
        return false;
    }
}
