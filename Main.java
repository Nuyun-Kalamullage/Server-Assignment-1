import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static String a;

    public static void main(String[] args) {

        final int[] givenTime = new int[2];
        try {
            givenTime[1] = Integer.parseInt(args[0]) * 60_000;
            Item.setBiddingTime(Integer.parseInt(args[0]) * 60_000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Milestone 1 : Create the CSV reader and populate data structure

        ItemMap item_map = new ItemMap();   // Data structure
        CSVReader csvreader = new CSVReader("stocks.csv", item_map);    // CSV reader
        csvreader.read();   //item_map gets populated with data
        Thread t1 = new Thread() {

            public void run() {
                long end = System.currentTimeMillis() + givenTime[1];

                try {
                    while (end > System.currentTimeMillis()) {
                        int second = 0;
                        try {
                            String str = Long.toString(end - System.currentTimeMillis()).substring(0, Long.toString(end - System.currentTimeMillis()).length()-3);
                            second = Integer.parseInt(str);
                        } catch (Exception e) {
                            second =0;
                        }
                        int h = second / 3600;
                        int m = second / 60;
                        int s = second % 60;
                        System.out.print("Remaining Time to BID is "+h+":"+m+":"+s+"\r");
                        a="Remaining BID-Time for not extended items "+h+":"+m+":"+s;

                        sleep(1000);
                    }
                    System.out.print("\r");
                    System.out.println("Bidding Time is Over for Not extended items ");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();


        Thread t2 = new Thread() {

            public void run() {
                int y = 0;
                try {
                    ServerSocket ps = new ServerSocket(2022);
                    while (true) {
                        System.out.println("Waiting for Pub & Sub....\n");
                        Socket socket = ps.accept(); // if error must close the socket
                        System.out.flush();
                        // Milestone 2 : Create a server and accept 1 connection
                        Server server = new Server(item_map, socket);   // Server
                        server.start();    // Server starts running here.


                        if (!ps.isBound() && ps.isClosed()) {
                            break;
                        }
                        item_map.values().getClass().getName();
                        System.out.println("Pub-Sub " + (y + 1) + " Connected\n");
                        y++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        };
        t2.start();

        // Milestone 3 : Modify server to accept multiple connections (multi-threading)
        int x = 0;
        try {
            ServerSocket ss = new ServerSocket(2021);
            while (true) {
                System.out.println("Waiting for clients....\n");
                Socket socket = ss.accept(); // if error must close the socket
                System.out.flush();
                // Milestone 2 : Create a server and accept 1 connection
                Server server = new Server(item_map, socket);   // Server
                server.start();    // Server starts running here.


                if (!ss.isBound() && ss.isClosed()) {
                    break;
                }
                item_map.values().getClass().getName();
                System.out.println("Client " + (x + 1) + " Connected\n");
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
