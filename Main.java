import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        ItemMap item_map = new ItemMap();   // Data structure
        CSVReader csvreader = new CSVReader("stocks.csv", item_map);    // CSV reader
        csvreader.read();   //item_map gets populated with data



        // Milestone 2 : Create a server and accept 1 connection
        Server server = new Server(item_map);   // Server
        server.start();       // Server starts running here.


        // Milestone 3 : Modify server to accept multiple connections (multi-threading)

    }
}
