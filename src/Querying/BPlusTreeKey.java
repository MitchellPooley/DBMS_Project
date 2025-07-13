package Querying;

public class BPlusTreeKey implements Comparable<BPlusTreeKey>{
    private final Object key;
    private final String location;
    private final int index;
    private final Class<?> dataType;

    public BPlusTreeKey(Object key, String location, int index, Class<?> dataType) {
        this.key = key;
        this.location = location;
        this.index = index;
        this.dataType = dataType;
    }

    public Object getKey() { return key;}

    public String getLocation() { return location;}

    public int getIndex() { return index;}

    @Override
    public int compareTo(BPlusTreeKey o) {
        if (dataType == Integer.class) {
            return ((Integer) key).compareTo((Integer) o.getKey());
        }
        if (dataType == Float.class) {
            return ((Float) key).compareTo((Float) o.getKey());
        }
        if (dataType == String.class) {
            return ((String) key).compareTo((String) o.getKey());
        }
        if (dataType == Boolean.class) {
            return ((Boolean) key).compareTo((Boolean) o.getKey());
        }
        return 0;
    }
}
