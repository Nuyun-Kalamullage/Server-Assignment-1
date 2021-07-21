import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static String a;//make this String a static for pass the remaining time to not extended items to client who join to server.

    public static void main(String[] args) {

        final int[] givenTime = new int[2];//make given time array to store user inputs.
        try {
            givenTime[1] = Integer.parseInt(args[0]) * 60_000; //get the user input argument as minutes and store in given time array.
            Item.setBiddingTime(Integer.parseInt(args[0]) * 60_000); // pass the value to item map bidding time variable.
        } catch (NumberFormatException e) {
            e.printStackTrace();// if there is a number format issue print the error.
            System.err.println("Problem in input Arguments");// print some error to sever.
        }

        // Milestone 1 : Create the CSV reader and populate data structure

        ItemMap item_map = new ItemMap();   // Data structure
        CSVReader csvreader = new CSVReader("stocks.csv", item_map);    // CSV reader
        csvreader.read();   //item_map gets populated with data
        Thread t1 = new Thread() { //add t1 thread to do timing mechanism to not extend items in the sever.

            public void run() {
                long end = System.currentTimeMillis() + givenTime[1];

                try {
                    while (end > System.currentTimeMillis()) {
                        int second = 0;
                        try {
                            String str = Long.toString(end - System.currentTimeMillis()).substring(0, Long.toString(end - System.currentTimeMillis()).length()-3);//calculate remain time for not extend items.
                            second = Integer.parseInt(str); // string convert to integer
                        } catch (Exception e) {
                            second =0; // if there is a exception in the try block second going to zero.
                        }
                        //Seconds convert to hours minutes and seconds format.
                        int h = second / 3600;
                        int m = second / 60;
                        int s = second % 60;
                        System.out.print("Remaining Time to BID is "+h+":"+m+":"+s+"\r");// prints the remain time in server and this will nicely countdown.
                        a="Remaining BID-Time for not extended items "+h+":"+m+":"+s; // make a this time.

                        sleep(1000);// sleep this thread in one second after start.
                    }
                    System.out.print("\r");
                    System.out.println("Bidding Time is Over for Not extended items "); // after reaching the end time show this message to sever.
                } catch (InterruptedException e) {
                    e.printStackTrace(); // if there is a exception print the error.
                }
            }
        };
        t1.start();


        Thread t2 = new Thread() { // make this thread for publisher-subscriber model.

            public void run() {
                int y = 0;// initiate this variable for count the how many pub or sub join the server.
                try {
                    ServerSocket ps = new ServerSocket(2022); // make 2022 port visible for users.
                    while (true) { // do infinite loop for gathering users to server.
                        System.out.println("Waiting for Pub & Sub....\n");
                        Socket socket = ps.accept(); //if user connect to sever using this port accept the user. if error must close the socket.
                        System.out.flush();
                        // Milestone 2 : Create a server and accept 1 connection
                        Server server = new Server(item_map, socket);   // Create a Server class and pass the values to constructor.
                        server.start();    // Server thread starts running here.
                        if (!ps.isBound() && ps.isClosed()) {
                            break;
                        }
                        item_map.values().getClass().getName();
                        System.out.println("Pub-Sub " + (y + 1) + " Connected\n"); // print in the server that user is connect to the server now.
                        y++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();// if there was a error in try block print the error.

                }
            }
        };
        System.out.println("==================================================================");//Display that sever was began.
        System.out.println("===================== Auction Server Starts ======================");
        System.out.println("==================================================================");
        t2.start();//Start the thread for Publishers and Subscribers.

        // Milestone 3 : Modify server to accept multiple connections (multi-threading)
        int x = 0;// initiate this variable for count the how many Clients join the server.
        try {
            ServerSocket ss = new ServerSocket(2021);// make 2022 port visible for users.
            while (true) { // do infinite loop for gathering users to server.
                System.out.println("Waiting for clients....\n");
                Socket socket = ss.accept(); //if user connect to sever using this port accept the user. if error must close the socket.
                System.out.flush();
                // Milestone 2 : Create a server and accept 1 connection
                Server server = new Server(item_map, socket);   // Create a Server class and pass the values to constructor.
                server.start();    // Server starts running here.
                if (!ss.isBound() && ss.isClosed()) {
                    break;
                }
                item_map.values().getClass().getName();
                System.out.println("Client " + (x + 1) + " Connected\n"); // print in the server that user is connect to the server now.
                x++;
            }
        } catch (IOException e) {
            e.printStackTrace();// if there was a error in try block print the error.

        }
    }
}
