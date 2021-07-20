import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

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

        } catch (Exception e) {
           e.printStackTrace();

        }
    }

    private void handle() throws IOException {// Implement methods for Client-Sever

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("====================== Client-Sever Model ========================");
            out.println("==================================================================\n");
            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.print("Please Enter Your Name ID : ");
            out.flush();
            String name;

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
                out.print("\nOK " + name + ", Please Enter the symbol of the item that you want to bid : ");
                out.flush();

                String symbol;
                    for (symbol = in.readLine().toUpperCase();  ; symbol = in.readLine().toUpperCase()) {
                        if (symbol.equalsIgnoreCase("quit"))
                            s.close();
                        else if(!item_map.containsKey(symbol)) {
                            out.println("-1 , Symbol is invalid.Try again");
                            out.flush();
                        }else {
                            out.println("\nYes " + name + ", The CURRENT PRICE of the " + symbol + " item is : " + item_map.get(symbol).get_price()+"\n");
                            out.println(name + ", Do you want to Bid on this item ? (Type with \"yes\" or \"no\")");
                            out.flush();
                            String exist;
                            for (exist = in.readLine(); !exist.equalsIgnoreCase("no") && !exist.equalsIgnoreCase("yes") ; exist = in.readLine()) {
                                if(exist.equalsIgnoreCase("quit")) {
                                    s.close();
                                }
                                out.println("Enter valid Input!!");
                                out.flush();
                            }
                            if(exist.equalsIgnoreCase("yes")){
                                break;
                            }
                            out.print("\nOK " + name + ", Please Enter the symbol of the item that you want to bid : ");
                            out.flush();
                        }


                    }

                Item item = item_map.get(symbol);
                out.println("\nPlease Wait.......");
                out.flush();
                synchronized (item) {

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
                    out.println("==================================================================");
                    out.flush();
                }

            }
        } catch (IOException | InterruptedException iOException) {
            this.s.close();
        }
    }

    private void handle_1()  {// Implement methods for Pub-Sub Sever

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("================== Publisher-Subscriber Model ====================");
            out.println("==================================================================");

            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.print("Please Enter Your Name ID : ");
            out.flush();
            String name;


            for (name = in.readLine(); Item.nameSet.contains(name) || name.toLowerCase().equals("quit"); name = in.readLine()) {
                if (name.toLowerCase().equals("quit")) {
                    out.println("Exiting the Auction Sever ......");
                    out.println("Thank you....");
                    out.flush();
                    s.close();
                }
                out.println("Name ID is not Unique. Please Try again");
                out.flush();
            }
            Item.nameSet.add(name);

            ArrayList<String> subscribeMap = new ArrayList();
            ArrayList<String> subscriptionArray = new ArrayList<>();
            Thread profitT = new Thread() {
                public void run() {
                    try {
                        out.flush();
                        while (true) {

                            if (!ItemMap.profitList.isEmpty()) {
                                if (subscribeMap.contains(ItemMap.profitList.get(ItemMap.profitList.size() - 1)) && !ItemMap.profitList.isEmpty()) {
                                    if (!ItemMap.profitList.isEmpty()) {
                                        out.flush();
                                        out.print("\ritem " + ItemMap.profitList.get(ItemMap.profitList.size() - 1) + " Profit Changed to \t:\t " + item_map.get(ItemMap.profitList.get(ItemMap.profitList.size() - 1)).getProfit() + "\n");
                                        out.flush();
                                    }
                                    if (!ItemMap.profitList.isEmpty())
                                        ItemMap.profitList.remove((ItemMap.profitList.size() - 1));
                                    sleep(1000);

                                } else if (!ItemMap.profitList.isEmpty() && !subscribeMap.contains(ItemMap.profitList.get(ItemMap.profitList.size() - 1))) {
                                    ItemMap.profitList.remove((ItemMap.profitList.size() - 1));
                                }
                            } else
                                out.print("");
                            out.flush();
                        }

                    } catch (Exception e) {

                    }

                }
            };


            Thread bidT = new Thread() {
                public void run() {
                    try {
                        out.flush();
                        while (true) {

                            if (!ItemMap.bidList.isEmpty()) {
                                if (subscriptionArray.contains(ItemMap.bidList.get(ItemMap.bidList.size() - 1)) && !ItemMap.bidList.isEmpty()) {
                                    if (!ItemMap.bidList.isEmpty()) {
                                        out.flush();
                                        out.print("\ritem " + ItemMap.bidList.get(ItemMap.bidList.size() - 1) + " BID Price Changed to \t:\t " + item_map.get(ItemMap.bidList.get(ItemMap.bidList.size() - 1)).get_price() + "\n");
                                        out.flush();
                                    }
                                    if (!ItemMap.bidList.isEmpty())
                                        ItemMap.bidList.remove((ItemMap.bidList.size() - 1));
                                    sleep(1000);

                                } else if (!ItemMap.bidList.isEmpty() && !subscriptionArray.contains(ItemMap.bidList.get(ItemMap.bidList.size() - 1))) {
                                    ItemMap.bidList.remove((ItemMap.bidList.size() - 1));
                                }
                            } else
                                out.print("");
                            out.flush();
                        }

                    } catch (Exception e) {

                    }
                }
            };

            while (s.isConnected()) {
                out.flush();
                if (Main.a.matches("Remaining BID-Time for not extended items 0:0:0")) {
                    out.print("\n\rRemaining BID-Time for not extended items is Over");
                    out.flush();
                } else {
                    out.print("\n\r" + Main.a + "");
                    out.flush();
                }

                String[] queryTokens;
                String query;
                out.flush();
                out.println("\n\rEnter the Query Format :");
                out.flush();
                for (query = in.readLine().toUpperCase(); query.equals("QUIT"); query = in.readLine().toUpperCase()) {
                    out.println("Exiting the Auction Sever ......");
                    out.println("Thank you....");
                    out.flush();
                    s.close();
                }
                queryTokens = query.split(" ");
                boolean isNumber = false;
                isNumber = false;
                int count = 0;
                if (queryTokens.length >= 2) {
                    for (char i : queryTokens[1].toCharArray()) {
                        if(Character.isDigit(i)){
                            count++;
                        }
                        if(count == queryTokens[1].length()){
                            isNumber =true;
                        }
                    }
                }

                if (item_map.containsKey(queryTokens[0]) && isNumber) {
                    String sym = queryTokens[0];
                    int securityNumber = 0;
                    float profit = 0;
                    try {
                        securityNumber = Integer.parseInt(queryTokens[1]);
                        profit = Float.parseFloat(queryTokens[2]);
                    } catch (Exception e) {
                        out.print("\rEntered Number format is invalid!!");
                        out.flush();
                    }

                    if (item_map.get(sym).getSecurity() == securityNumber) {
                        item_map.get(sym).setProfit(profit);
                        out.print("0 |");
                        out.print(" Profit Changed");
                        System.out.println(name + ", Change the Profit in item " + sym + " to : " + profit);
                        ItemMap.profitList.add(sym);
                    } else {
                        out.print(" | -1");
                    }
                    out.flush();

                } else if (queryTokens[0].equals("PRFT")) {
                    HashSet<String> currentProfit = new HashSet<>();

                    for (int i = 1; i < queryTokens.length; i++) {
                        if (item_map.containsKey(queryTokens[i])) {
                            if (!subscribeMap.contains(queryTokens[i])) {
                                subscribeMap.add(queryTokens[i]);
                            }
                            currentProfit.add(queryTokens[i]);
                            System.out.println(name + ", Subscribe to the Profit on " + queryTokens[i] + " item.");
                            out.print("0 ");
                            if (!profitT.isAlive()) {
                                profitT.start();
                            }
                        } else {
                            out.print("-1 ");
                        }
                        out.flush();
                    }
                    out.println("");
                    for (String i: currentProfit) {
                        out.println("\ritem " +i+ " profit is \t:\t " + item_map.get(i).getProfit());
                    }
                    currentProfit.clear();


                } else if (queryTokens[0].equals("BID")) {

                    for (int i = 1; i < queryTokens.length; i++) {

                        if ((item_map.containsKey(queryTokens[i]))) {
                            if (!subscriptionArray.contains(queryTokens[i])) {
                                subscriptionArray.add(queryTokens[i]);
                            }
                            System.out.println(name + ", Subscribe to the BID on " + queryTokens[i] + " item.");
                            out.print("0 ");
                            if (!bidT.isAlive()) {
                                bidT.start();
                            }
                        } else {
                            out.print("-1");
                        }
                        out.flush();

                    }
                } else {
                    if (queryTokens[0].equals("QUIT")) {
                        out.println("Exiting the Auction Sever ......");
                        out.println("Thank you....");
                        out.flush();
                        s.close();
                        break;
                    }
                    out.print("\nSorry!!, Couldn't Catch that...");
                    out.print("| -1");
                    out.print("\n\rTry Again!!\n\n");
                    out.flush();

                }
                out.flush();
                out.println("\r\n==================================================================");
            }
        } catch (IOException iOException) {
            try {
                this.s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
