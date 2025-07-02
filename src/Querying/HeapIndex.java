package Querying;

import DataHandling.TableStats;

import java.util.Objects;

public class HeapIndex implements IndexStrategy {
    //TODO Implement methods


    @Override
    public int CalculateIO(TableStats stats, String predicate) {
        if (Objects.equals(predicate, "Equal")) {

        }
        else if (predicate.equals("Greater")) {

        }
        else if (predicate.equals("Lesser")) {

        }
        return 0;
    }

    @Override
    public int CalculateSize(TableStats stats, String predicate) {
        return 0;
    }

    @Override
    public int CalculateRFEqual(TableStats stats) {
        return 0;
    }

    @Override
    public int CalculateRFGreater(TableStats stats) {
        return 0;
    }

    @Override
    public int CalculateRFLess(TableStats stats) {
        return 0;
    }
}
