package Querying;

import DataHandling.*;

import java.io.*;
import java.util.ArrayList;

public class BPlusTreeIndex extends ParentIndex {
    private final String INDEXDIR  = "index/";
    private final String BPLUSNAME = "_B_Plus_Index";
    private final String SCHEMA    = "schema";

    private static BPlusTreeIndex bPlusTreeIndex;

    /**
     * Singleton getter method.
     * @return Singleton instance of BPlusTreeIndex class
     */
    public static BPlusTreeIndex getInstance() {
        if (bPlusTreeIndex == null) {
            return new BPlusTreeIndex();
        }
        return bPlusTreeIndex;
    }
    /**
     * Calculates the expected IO cost of selecting using an Un-Clustered BPlusTree index.
     * @param colIndex Column being selected over
     * @param predicate Predicate of the selection (Equals, Greater, Lesser)
     * @param value Value being selected for
     * @param stats Tables stats, such as numPages or numUnique
     * @return Expected number of IO operations
     */
    @Override
    public int CalculateIO(int colIndex, String predicate, Object value, TableStats stats) {
        double rf = RF_MAGIC;
        rf = switch (predicate) {
            case EQUAL -> CalculateRFEqual(stats, colIndex);
            case GREATER -> CalculateRFGreater(stats, colIndex, value);
            case LESSER -> CalculateRFLess(stats, colIndex, value);
            default -> rf;
        };
        return (int) Math.ceil(stats.getNumRows() * rf);
    }

    /**
     * Select rows from a table based on some predicate.
     * @param indexName Column being selected over
     * @param predicate Predicate of the selection (Equals, Greater, Lesser)
     * @param inclusive If the predicate is inclusive (Greater/Lesser Than or Equal)
     * @param value Value being selected for
     * @param schema Schema of the table before selection
     * @param tableName Name of the table being selected over
     * @return QueryResult
     */
    @Override
    public QueryResult select(String indexName, String predicate, Boolean inclusive, Object value, Schema schema, String tableName) {
        QueryResult result = new QueryResult(schema);

        int colIndex = schema.getColumnName().indexOf(indexName);
        Class<?> columnType = schema.getColumnType().get(colIndex);

        BPlusTree bPlusTree = getTree(indexName, tableName);

        BPlusTreeNode node;
        int i = 0;

        if (predicate.equals(LESSER)) {
            node = bPlusTree.getFirstNode();
        }
        else {
            // Uses mostly null valued BPlusTreeKey as only the key value is required for finding the correct leaf
            node = bPlusTree.findLeaf(new BPlusTreeKey(value, null, null, null));

            while(!compareOnPred(predicate, node.getKeys().get(i).getKey(), value, columnType) &&
                    (!inclusive || !compareOnPred(EQUAL, node.getKeys().get(i).getKey(), value, columnType))) {
                i++;
                if (i == node.getKeys().size()) {
                    node = node.next;
                    if (node == null) {
                        return result;
                    }
                    i=0;
                }
            }
        }

        while(compareOnPred(predicate, node.getKeys().get(i).getKey(), value, columnType) ||
                (inclusive && compareOnPred(EQUAL, node.getKeys().get(i).getKey(), value, columnType))) {

            BPlusTreeKey key = node.getKeys().get(i);
            String pageDir = FileManager.getCurrentDataBaseDir() + SLASH + tableName + SLASH + key.getPageName();

            Page page;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pageDir))) {
                page = (Page) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            result.addRow(page.getRow(key.getIndex()));

            i++;
            if (i == node.getKeys().size()) {
                node = node.next;
                if (node == null) {
                    return result;
                }
                i=0;
            }
        }
        return result;
    }

    /**
     * Create a BPlusTree index over a single column in a table.
     * @param indexName Name of the column being indexed.
     * @param tableName Name of the table being indexed.
     */
    public void createBPlusTree(String indexName, String tableName) {
        String tableDir = FileManager.getCurrentDataBaseDir() + SLASH + tableName;
        String treeDir = tableDir + SLASH + INDEXDIR + SLASH + indexName + BPLUSNAME;

        Schema schema;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableDir + SLASH + SCHEMA))) {
            schema = (Schema) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        int colIndex = schema.getColumnName().indexOf(indexName);
        Class<?> dataType = schema.getColumnType().get(colIndex);
        BPlusTree bPlusTree = new BPlusTree(dataType);

        // Insert keys into the tree
        for (int i=1; i<FileManager.getFileNames(tableDir).size()-NUM_NON_PAGES; i++) {
            Page page;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableDir + PAGE_DIR + i))) {
                page = (Page) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (int j = 0; j < Page.MAX_ROWS; j++) {
                Row row = page.getRow(j);
                bPlusTree.insert(new BPlusTreeKey(row.getData().get(colIndex), PAGE_DIR + i, j, dataType));
            }
        }

        // Save tree to memory
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(treeDir + SLASH + INDEXDIR +
                SLASH + indexName + BPLUSNAME))) {
            out.writeObject(bPlusTree);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert a new key into the index.
     * @param indexName Name of the column being indexed
     * @param tableName Name of the table being indexed
     * @param key Key being inserted
     */
    public void insertKey(String indexName, String tableName, BPlusTreeKey key) {
        BPlusTree bPlusTree = getTree(indexName, tableName);

        bPlusTree.insert(key);
    }

    /**
     * Helper method to get a BPlusTree from memory
     * @param indexName Name of the column being indexed
     * @param tableName Name of the table being indexed
     * @return BPlusTree retrieved from memory
     */
    private BPlusTree getTree(String indexName, String tableName) {
        String treeDir = FileManager.getCurrentDataBaseDir() + SLASH + tableName + SLASH + INDEXDIR + SLASH + indexName + BPLUSNAME;

        BPlusTree bPlusTree;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(treeDir))) {
            bPlusTree = (BPlusTree) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bPlusTree;
    }
}
