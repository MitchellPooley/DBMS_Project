package Querying;

public interface IndexStrategy {
    public int CalculateIO();
    public int CalculateSize();

    public int CalculateRFEqual();
    public int CalculateRFGreater();
    public int CalculateRFLess();
}
