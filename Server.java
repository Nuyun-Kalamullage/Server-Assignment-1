import com.sun.glass.ui.ClipboardAssistance;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

public class Server extends Thread{
    // TODO : Define all required varible
    private ItemMap item_map;
    private String x= "x";

    private Socket s;

    public Server(ItemMap item_map) {
        this.item_map = item_map;
    }
    public void run(){

    }

    public void start() {
                try
                {
                        ServerSocket ss = new ServerSocket(2021); //declare port number
                        System.out.println("waiting for client to connect..........");

                        Socket s = ss.accept(); //hold server socket till client request
                        System.out.println("Connecting Established");
                        Server server = new Server(item_map);
                        server.handle(s);


                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
        // Create a socket and accept connections
    }
    private void handle(Socket s) throws IOException{

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("==================================================================\n");
            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.println("Please Enter Your Name : ");
            out.flush();
            String name = in.readLine();
            if(name.equals("quit"))
                s.close();

            out.print("\nOK "+name+", Please Enter the symbol of the security you want to bid : " + "\n");
            out.flush();

            String symbol;
            for(symbol = in.readLine(); !item_map.containsKey(symbol) ; symbol = in.readLine()){
                if(symbol.equals("quit"))
                    s.close();
                out.print("\n-1 , Symbol is invalid.Try again\n");
                out.flush();
            }

            Item item = item_map.get(symbol);
            out.print("\nPlease Wait.......\n");
            out.flush();
            synchronized(item){
                double currentPrice = item.get_price();
                out.print("\nYes "+name+", The CURRENT PRICE of the security is : " + currentPrice + "\nPlease enter your price to bid : ");
                out.flush();
                String price="0";
                int excessBytesDuringWait = s.getInputStream().available();
                if(excessBytesDuringWait > 0){
                    s.getInputStream().skip(excessBytesDuringWait);
                    System.out.println(excessBytesDuringWait);
                }
                try{
                    for(price = in.readLine(); !price.equals("quit") && Double.parseDouble(price) <= currentPrice ; price = in.readLine()){
                        out.print("\nError: Hi "+name+", The price you entered must be more than the current price of the security. Note that the current price of " +symbol+ " is "+currentPrice+"\nPlease re-enter your price to bid : ");
                        out.flush();
                    }
                    if(price.equals("quit"))
                        s.close();
                }catch(NumberFormatException e){
                    out.print("\nError You entered an invalid value for price. Exiting the Auction server ....Try again");
                    out.flush();
                    s.close();
                }



                out.print("\nOk "+name+",Your price accepted. Please enter 'confirm' and press enter to confirm bidding. Or enter 'quit' and press enter to quit bidding.\n");
                out.flush();

                for(String confirm = in.readLine(); !confirm.equals("confirm") ; confirm = in.readLine()){
                    if(confirm.equals("quit"))
                        s.close();
                    out.print("\nError input: Hi "+name+", Enter 'confirm' and press enter to confirm bidding. Or enter 'quit' and press enter to quit bidding.\n");
                    out.flush();
                }

//
//                ItemMap item_map =new ItemMap();
//                item.make_bid(Float.parseFloat(price));

                out.print("\nCongratulations "+ name +",Your bid saved successfully. Thank you for using Stock Exchange - Auction Server.\n");
                out.flush();
            }
            s.close();

        } catch (IOException iOException) {
            this.s.close();
        }
    }
}
