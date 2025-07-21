package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Row implements Serializable {
    private ArrayList<Object> data = new ArrayList<>();

    public Row(ArrayList<Object> data) {
        this.data = data;
    }

    /**
     * Returns the rows data.
     * @return ArrayList of data
     */
    public ArrayList<Object> getData() { return data;}

    /**
     * Returns the row data as String objects.
     * @return Data as an ArrayList of Strings
     */
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
