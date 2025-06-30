package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Schema implements Serializable {
    private final ArrayList<String> columnName;
    private final ArrayList<Class<?>> columnType;

    public Schema(ArrayList<String> columnName, ArrayList<Class<?>> columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    /**
     * Returns the row names of the schema.
     * @return Arraylist containing the names
     */
    public ArrayList<String> getColumnName() { return columnName;}

    /**
     * Returns the row types of the schema.
     * @return ArrayList containing the types
     */
    public ArrayList<Class<?>> getColumnType() { return columnType;}

    public int size() { return columnName.size();}
}
