package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class TableSchema implements Serializable {
    private ArrayList<String> schema = new ArrayList<>();

    public TableSchema(ArrayList<String> schema) {
        this.schema = schema;
    }

    /**
     * Returns a tables Schema
     * @return ArrayList containing the Schema
     */
    public ArrayList<String> getSchema() { return schema;}
}
