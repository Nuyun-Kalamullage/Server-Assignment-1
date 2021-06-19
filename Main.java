import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        // Milestone 1 : Create the CSV reader and populate data structure

        ItemMap item_map = new ItemMap();   // Data structure
        CSVReader csvreader = new CSVReader("stocks.csv", item_map);    // CSV reader
        csvreader.read();   //item_map gets populated with data

        int x = 0;
        try {
            ServerSocket ss = new ServerSocket(2021);
            while (x != 4){
                Socket socket = ss.accept(); // if error must close the socket
                Server server = new Server(item_map, socket);
                server.start();
                System.out.println(x);
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Milestone 3 : Modify server to accept multiple connections (multi-threading)

    }
}
