import DataHandling.FileManager;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Ensure data file exists and create a new file if not
        new File("./data").mkdirs();

        Test test = new Test();
        if (test.RunTest()) {
            System.out.println("All tests pass");
        }
    }
}