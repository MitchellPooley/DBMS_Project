/**
 * Credit: This class uses and builds upon code taken from GeeksforGeeks
 * URL: https://www.geeksforgeeks.org/java/java-program-to-implement-b-tree/
 */

package Querying;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BPlusTreeNode implements Serializable {
    boolean isLeaf;

    List<BPlusTreeKey> keys;
    List<BPlusTreeNode> children;

    BPlusTreeNode next;

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.next = null;
    }

    public List<BPlusTreeKey> getKeys() {return keys; }

    public BPlusTreeNode getNext() {return next; }
}
