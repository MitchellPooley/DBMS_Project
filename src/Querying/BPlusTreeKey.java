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

    /**
     * Gets the keys value.
     * @return String, Integer or Float value (Cannot create a BPlusTree with boolean value as it would be pointless)
     */
    public Object getKey() { return key;}

    /**
     * Get the page directory of the row referenced to by the key.
     * @return String
     */
    public String getLocation() { return location;}

    /**
     * Get the index within the page, of the row referenced by the key.
     * @return Integer index
     */
    public int getIndex() { return index;}

    /**
     * Compare to method included in Comparable interface.
     * @param treeKey the object to be compared.
     * @return -1, 0, 1 for values less than, equal to and greater than, respectively
     */
    @Override
    public int compareTo(BPlusTreeKey treeKey) {
        if (dataType == Integer.class) {
            return ((Integer) key).compareTo((Integer) treeKey.getKey());
        }
        if (dataType == Float.class) {
            return ((Float) key).compareTo((Float) treeKey.getKey());
        }
        if (dataType == String.class) {
            return ((String) key).compareTo((String) treeKey.getKey());
        }
        return 0;
    }
}
