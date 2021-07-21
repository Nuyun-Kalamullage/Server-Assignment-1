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
        this.filename = filename;   //pass the file name to class variable.
        this.item_map = item_map;   //reference to the data structure containing all the items.

    }

    public void read() {

        try {
            fileRd = new FileReader(filename); // to read file name of the file.
            reader = new BufferedReader(fileRd); // to read the text line in the csv.
            String[] tokens; // make String array for the store String line.
            reader.readLine(); // read the headings.
            for (String line = reader.readLine(); line != null; line = reader.readLine()) { // for loop to read the all the lines in the stocks.csv.

                tokens = line.split(","); // Split the line with "," and add to the tokens array.
                Item item = new Item(tokens[0], "", Float.parseFloat(tokens[1]), Integer.parseInt(tokens[2]), Float.parseFloat(tokens[3])); // pass the separated token value to item class.
                item_map.put(tokens[0], item); //then item objet add to hashmap child class called item_map.

            }
            fileRd.close();//close the readers
            reader.close();

        } catch (FileNotFoundException ex) {
            System.err.println("Stock.csv file not in the Location"); // if there are no csv file called stock.csv display this error.
        } catch (IOException ex) {
            ex.printStackTrace();//print the error.
        }

    }
}
