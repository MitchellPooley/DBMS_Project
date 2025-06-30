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

    /**
     * Returns the rows data.
     * @return ArrayList of data
     */
    public ArrayList<Object> getData() { return data;}

    public ArrayList<String> getAsString() {
        ArrayList<String> stringData = new ArrayList<>();
        for (Object value: data) {
            if (value instanceof String) {
                stringData.add((String) value);
            } else {
                stringData.add(String.valueOf(value));
            }
        }
        return stringData;
    }
}
