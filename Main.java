import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
        boolean[] alive = {true};

        Timer timer = new Timer();
        System.out.println("timer = " + timer.toString());





        // Milestone 1 : Create the CSV reader and populate data structure

        ItemMap item_map = new ItemMap();   // Data structure
        CSVReader csvreader = new CSVReader("stocks.csv", item_map);    // CSV reader
        csvreader.read();   //item_map gets populated with data

        int x = 0;
        try {
            ServerSocket ss = new ServerSocket(2021);
            while (true){
                System.out.println("Waiting for clients....");
                Socket socket = ss.accept(); // if error must close the socket
                System.out.flush();

                // Milestone 2 : Create a server and accept 1 connection
                Server server = new Server(item_map,socket);   // Server
                server.start();       // Server starts running here.
                //server.sleep(500);
                TimerTask task = new TimerTask() {
                    public void run() {
                        alive[0] = false;
                        System.out.println("bomca");
                        if(alive[0] == false){
                            //server.stop();
                            try {
                                ss.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.err.println("Sever Closed");
                            System.exit(0);
                        }
                    }
                };
                timer.schedule(task, 10_000);

                item_map.values().getClass().getName();
                System.out.println("Client "+(x+1)+ " Connected\n");
                x++;

                }
        } catch (IOException e) {
            e.printStackTrace();

        }






        // Milestone 3 : Modify server to accept multiple connections (multi-threading)

    }
}
