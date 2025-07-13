package Querying;

import java.util.ArrayList;
import java.util.List;

public class BPlusTreeNode {
    boolean isLeaf;

    List<BPlusTreeKey> keys;

    // Children nodes (for internal nodes)
    List<BPlusTreeNode> children;

    // Link to the next leaf node
    BPlusTreeNode next;

    // Constructor to initialize a node
    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.next = null;
    }
}
