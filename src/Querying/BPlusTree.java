/**
 * Credit: This class uses and builds upon code taken from GeeksforGeeks
 * URL: https://www.geeksforgeeks.org/java/java-program-to-implement-b-tree/
 */

package Querying;

import java.util.Collections;

public class BPlusTree {
    private final String EQUAL   = "Equal";
    private final String GREATER = "Greater";
    private final String LESSER  = "Lesser";

    private BPlusTreeNode root;
    private final Class<?> keyType;

    private final int ORDER = 3;

    public BPlusTree(Class<?> keyType) {
        this.root = new BPlusTreeNode(true);
        this.keyType = keyType;
    }

    /**
     * Finds the leaf node that a given key belongs too.
     * @param key key value referencing a row in a table
     * @return leaf node
     */
    public BPlusTreeNode findLeaf(BPlusTreeKey key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && (BPlusTreeIndex.getInstance().compareOnPred(GREATER, key.getKey(), node.keys.get(i), keyType) ||
                    BPlusTreeIndex.getInstance().compareOnPred(EQUAL, key.getKey(), node.keys.get(i), keyType))) {
                i++;
            }
            node = node.children.get(i);
        }
        return node;
    }

    public BPlusTreeNode getFirstNode() {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            node = node.children.get(0);
        }
        return node;
    }

    /**
     * Insert a new key into the tree.
     * @param key key to be inserted
     */
    public void insert(BPlusTreeKey key) {
        BPlusTreeNode leaf = findLeaf(key);
        insertIntoLeaf(leaf, key);

        // Split the leaf node if it exceeds the order
        if (leaf.keys.size() > ORDER - 1) {
            splitLeaf(leaf);
        }
    }

    /**
     * Insert the key into a leaf node.
     * @param leaf Leaf node being inserted into
     * @param key Key being inserted
     */
    private void insertIntoLeaf(BPlusTreeNode leaf, BPlusTreeKey key) {
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos < 0) {
            pos = -(pos + 1);
        }
        leaf.keys.add(pos, key);
    }

    /**
     * Split  a leaf node and update its parents.
     * @param leaf Leaf node to be split
     */
    private void splitLeaf(BPlusTreeNode leaf) {
        int mid = (ORDER + 1) / 2;
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);

        // Move half the keys to the new leaf node
        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        // If the root splits, create a new root
        if (leaf == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(newLeaf.keys.get(0));
            newRoot.children.add(leaf);
            newRoot.children.add(newLeaf);
            root = newRoot;
        } else {
            insertIntoParent(leaf, newLeaf, newLeaf.keys.get(0));
        }
    }

    /**
     * Insert a key into a parent nodes when splitting the root.
     * @param left Left node
     * @param right Right node
     * @param key Key to be inserted
     */
    private void insertIntoParent(BPlusTreeNode left, BPlusTreeNode right, BPlusTreeKey key) {
        BPlusTreeNode parent = findParent(root, left);

        if (parent == null) {
            throw new RuntimeException("Parent node not found for insertion");
        }

        int pos = Collections.binarySearch(parent.keys, key);
        if (pos < 0) {
            pos = -(pos + 1);
        }

        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);

        // Split the internal node if it exceeds the order
        if (parent.keys.size() > ORDER - 1) {
            splitInternal(parent);
        }
    }

    /**
     * Split an internal node.
     * @param internal Node to be split
     */
    private void splitInternal(BPlusTreeNode internal) {
        int mid = (ORDER + 1) / 2;
        BPlusTreeNode newInternal = new BPlusTreeNode(false);

        // Move half the keys to the new internal node
        newInternal.keys.addAll(internal.keys.subList(mid + 1, internal.keys.size()));
        internal.keys.subList(mid, internal.keys.size()).clear();

        // Move half the children to the new internal node
        newInternal.children.addAll(internal.children.subList(mid + 1, internal.children.size()));
        internal.children.subList(mid + 1, internal.children.size()).clear();

        // If the root splits, create a new root
        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(internal.keys.get(mid));
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            insertIntoParent(internal, newInternal, internal.keys.remove(mid));
        }
    }

    /**
     * Find the parent node of a given node.
     * @param current Current node being checked
     * @param target Child node whose parent is being searched for
     * @return parent node.
     */
    private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode target) {
        if (current.isLeaf || current.children.isEmpty()) {
            return null;
        }

        for (int i = 0; i < current.children.size(); i++) {
            BPlusTreeNode child = current.children.get(i);

            if (child == target) {
                // Parent found
                return current;
            }

            BPlusTreeNode possibleParent = findParent(child, target);
            if (possibleParent != null) {
                return possibleParent;
            }
        }

        // Parent not found
        return null;
    }

    /**
     * Checks if a key exists in the tree
     * @param key Key being searched for
     * @return Boolean
     */
    public boolean search(BPlusTreeKey key) {
        BPlusTreeNode node = findLeaf(key);
        int pos = Collections.binarySearch(node.keys, key);
        return pos >= 0;
    }
}
