package DataHandling;

import java.io.Serializable;
import java.util.ArrayList;

public class Row implements Serializable {
    public int id;
    public ArrayList<String> data = new ArrayList<>();

    public Row(int id, ArrayList<String> data) {
        this.id = id;
        this.data = data;
    }

    public int getId() { return id;}
    public ArrayList<String> getData() { return data;}
}
