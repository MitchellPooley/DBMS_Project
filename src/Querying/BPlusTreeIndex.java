package Querying;

import DataHandling.FileManager;
import DataHandling.Page;
import DataHandling.Schema;
import DataHandling.TableStats;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class BPlusTreeIndex extends ParentIndex {
    private final String INDEXDIR  = "index/";
    private final String BPLUSNAME = "_B_Plus_Index";

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

    @Override
    public QueryResult select(String indexName, String predicate, Boolean inclusive, Object value, Schema schema, String tableName) {
        QueryResult result = new QueryResult(schema);
        String treeDir = FileManager.getCurrentDataBaseDir() + SLASH + tableName + SLASH + INDEXDIR + SLASH + indexName + BPLUSNAME;

        int colIndex = schema.getColumnName().indexOf(indexName);
        Class<?> columnType = schema.getColumnType().get(colIndex);

        BPlusTree bPlusTree;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(treeDir))) {
             bPlusTree = (BPlusTree) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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

    // TODO: Finish BPlusTree Creation and insertion logic
    public void createBPlusTree() {

    }
}
