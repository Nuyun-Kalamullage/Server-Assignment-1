import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    private String filename;
    public ItemMap item_map;
    private FileReader fileRd = null;
    private BufferedReader reader = null;

    public CSVReader(String filename, ItemMap item_map) {
        this.filename = filename;
        this.item_map = item_map;   //reference to the data structure containing all the items.

    }

    public void read() {

        // TODO : Implement method to read CSV file (this.filename) and populat the datastructure (this.item_map)
        try {
            fileRd = new FileReader(filename);
            reader = new BufferedReader(fileRd);
            String[] tokens;
            reader.readLine();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {

                tokens = line.split(",");


                Item item = new Item(tokens[0], "", Float.parseFloat(tokens[1]), Integer.parseInt(tokens[2]), Float.parseFloat(tokens[3]));
                item_map.put(tokens[0], item);

            }
            fileRd.close();
            reader.close();

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }
}
