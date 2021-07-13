import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private ItemMap item_map;
    private Socket s;

    public Server(ItemMap item_map, Socket s) {
        this.item_map = item_map;
        this.s = s;
    }

    public void run() {
        try {
            if (s.getLocalPort() == 2021) {
                handle();
                s.close();
            } else if (s.getLocalPort() == 2022) {
                handle_1();
                s.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle() throws IOException {// Implement methods for Client-Sever

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("==================================================================\n");
            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.print("Please Enter Your Name ID : ");
            out.flush();
            String name;

            //problem here...
            for (name = in.readLine(); Item.nameSet.contains(name) || name.equals("quit"); name = in.readLine()) {
                if (name.equals("quit"))
                    s.close();
                out.println("Name ID is not Unique. Please Try again");
                out.flush();
            }
            Item.nameSet.add(name);
            while (s.isConnected()) {
                if (Main.a.matches("Remaining BID-Time for not extended items 0:0:0")) {
                    out.println("\nRemaining BID-Time for not extended items is Over");
                } else {
                    out.println("\n" + Main.a + "\r");
                }
                out.print("\nOK " + name + ", Please Enter the symbol of the item you want to bid : ");
                out.flush();

                String symbol;
                for (symbol = in.readLine(); !item_map.containsKey(symbol); symbol = in.readLine()) {
                    if (symbol.equals("quit"))
                        s.close();
                    out.println("-1 , Symbol is invalid.Try again");
                    out.flush();
                }

                Item item = item_map.get(symbol);
                out.println("\nPlease Wait.......");
                out.flush();
                synchronized (item) {

                    out.println("\nYes " + name + ", The CURRENT PRICE of the " + symbol + " item is : " + item.get_price());
                    out.print("\nPlease enter your price to bid : ");
                    out.flush();
                    String price = "0";
                    int excessBytesDuringWait = s.getInputStream().available();
                    if (excessBytesDuringWait > 0) {
                        s.getInputStream().skip(excessBytesDuringWait);
                        System.out.println(excessBytesDuringWait);
                    }
                    try {
                        for (price = in.readLine(); !price.equals("quit") && Float.parseFloat(price) <= item.get_price(); price = in.readLine()) {

                            out.print("\nError: Hi " + name + ", The price you entered must be more than the current price of the item. Note that the current price of " + symbol + " is " + item.get_price());
                            out.print("\nPlease re-enter your price to bid : ");
                            out.flush();
                        }
                        if (price.equals("quit"))
                            s.close();
                    } catch (NumberFormatException e) {
                        out.println("\nError You entered an invalid value for price. Exiting the Auction server ....Try again");
                        out.flush();
                        s.close();
                    }


                    out.println("\nOk " + name + ",Your price accepted. Please enter 'confirm' and press enter to confirm bidding.");
                    out.println("Or enter 'quit' and press enter to quit bidding.");
                    out.flush();

                    for (String confirm = in.readLine(); !confirm.equals("confirm"); confirm = in.readLine()) {
                        if (confirm.equals("quit"))
                            s.close();
                        out.println("Error input: Hi " + name + ", Enter 'confirm' and press enter to confirm bidding.");
                        out.println("Or enter 'quit' and press enter to quit bidding.");
                        out.flush();
                    }

                    if (!item_map.get(symbol).timeOut) {
                        item_map.get(symbol).set_Name(name);
                        if (item_map.get(symbol).make_bid(Float.parseFloat(price)) == 0) {
                            out.println("\nCongratulations " + name + ", Your bid saved successfully.");
                            out.println("Current Price in " + symbol + " is " + price + ".");
                            item_map.get(symbol).make_bid(Float.parseFloat(price));
                            sleep(500);
                        } else {
                            out.println(name + ",Your bid is expired. Due to TimeOut ");
                        }
                    } else {
                        out.println(name + ",Your bid is expired. Due to TimeOut");
                    }
                    out.println("\nThank You for using Stock Exchange Server.");
                    out.flush();
                }

                //s.close();
            }
        } catch (IOException | InterruptedException iOException) {
            this.s.close();
        }
    }

    private void handle_1() throws IOException {// Implement methods for Pub-Sub Sever

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("==================================================================\n");
            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.print("Please Enter Your Name ID : ");
            out.flush();
            String name;

            //problem here...
            for (name = in.readLine(); Item.nameSet.contains(name) || name.equals("quit"); name = in.readLine()) {
                if (name.equals("quit"))
                    s.close();
                out.println("Name ID is not Unique. Please Try again");
                out.flush();
            }
            Item.nameSet.add(name);
            //out.flush();

            while (s.isConnected()) {
                if (Main.a.matches("Remaining BID-Time for not extended items 0:0:0")) {
                    out.println("\nRemaining BID-Time for not extended items is Over");
                    out.flush();
                } else {
                    out.println("\n" + Main.a + "\r");
                    out.flush();
                }

                String[] queryTokens;
                String query;
                out.print("\nEnter the Query Format :");
                out.flush();
                for (query = in.readLine(); query.equals("quit"); query = in.readLine()){
                    out.println("quit");
                    out.flush();
                }
                queryTokens = query.split(" ");




                    if(item_map.containsKey(queryTokens[0]) && queryTokens[1].equals(""+item_map.get(queryTokens[0]).getSecurity())) {
                        String sym = queryTokens[0];
                        String securityNumber = queryTokens[1];
                        String profit = queryTokens[2];

                        if(item_map.get(sym).getSecurity() == Integer.parseInt(securityNumber)){
                            item_map.get(sym).setProfit(Float.parseFloat(profit));
                            out.print("0 |");
                            out.print(" Profit Changed");


                        }else{
                            out.print("-1");
                        }

                    }else if (queryTokens[0].equals("PRFT")){
                        String[] profitArray = new String[queryTokens.length];
                        for(int i=1;i< queryTokens.length ;i++) {
                            if ((item_map.containsKey(queryTokens[i]))){
                                profitArray[i] = queryTokens[i];
                                out.print("0 ");
                            }
                            else {
                                out.print("-1");
                            }
                        }

                    }else if ( queryTokens[0].equals("BID")){
                        String[] subscriptionArray = new String[queryTokens.length];
                        for(int i=1;i< queryTokens.length ;i++){
                            subscriptionArray[i] = queryTokens[i];
                        }
                    }else{
                        if(queryTokens[0].toLowerCase().equals("quit")) {
                            s.close();
                            break;
                        }

                    }
                    out.println("\niwarai....");

            }
        } catch (IOException iOException) {

            this.s.close();
        }
    }
}
