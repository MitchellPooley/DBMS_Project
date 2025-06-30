package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Schema implements Serializable {
    private final ArrayList<String> rowName;
    private final ArrayList<Class<?>> rowType;

    public Schema(ArrayList<String> rowName, ArrayList<Class<?>> rowType) {
        this.rowName = rowName;
        this.rowType = rowType;
    }

    /**
     * Returns the row names of the schema.
     * @return Arraylist containing the names
     */
    public ArrayList<String> getRowName() { return rowName;}

    /**
     * Returns the row types of the schema.
     * @return ArrayList containing the types
     */
    public ArrayList<Class<?>> getRowType() { return rowType;}

    public int size() { return rowName.size();}
}
