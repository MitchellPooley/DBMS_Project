package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Row implements Serializable {
    public int id;
    public ArrayList<Object> data = new ArrayList<>();

    public Row(int id, ArrayList<Object> data) {
        this.id = id;
        this.data = data;
    }

    /**
     * Returns a rows id.
     * @return integer row id
     */
    public int getId() { return id;}
    public ArrayList<Object> getData() { return data;}
}
