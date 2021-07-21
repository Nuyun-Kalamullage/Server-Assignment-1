import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server extends Thread {
    private ItemMap item_map;
    private Socket s;

    public Server(ItemMap item_map, Socket s) {//pass the values to class variables.
        this.item_map = item_map;
        this.s = s;
    }

    public void run() {
        try {
            if (s.getLocalPort() == 2021) { // if  port is 2021.
                handle(); // execute the client side.
                s.close();
            } else if (s.getLocalPort() == 2022) { // else if 2022.
                handle_1();// execute the pub sub side.
                s.close();
            }

        } catch (Exception e) {
           e.printStackTrace();// display the error.

        }
    }

    private void handle() throws IOException {// Implement methods for Client-Sever

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); // get inputs from user.
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream())); // print outputs to user.
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("====================== Client-Sever Model ========================");
            out.println("==================================================================\n");
            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.print("Please Enter Your Name ID : ");
            out.flush();
            String name;

            for (name = in.readLine(); Item.nameSet.contains(name) || name.equals("quit"); name = in.readLine()) { //Get the unique Name ID from user
                if (name.equals("quit"))
                    s.close();
                out.println("Name ID is not Unique. Please Try again");//print Name ID is not unique.
                out.flush();
            }
            Item.nameSet.add(name);
            while (s.isConnected()) {
                if (Main.a.matches("Remaining BID-Time for not extended items 0:0:0")) {
                    out.println("\nRemaining BID-Time for not extended items is Over"); // display the remaining time for not extended items.
                } else {
                    out.println("\n" + Main.a + "\r");
                }
                out.print("\nOK " + name + ", Please Enter the symbol of the item that you want to bid : ");
                out.flush();

                String symbol;
                    for (symbol = in.readLine().toUpperCase();  ; symbol = in.readLine().toUpperCase()) { // get symbol from user.
                        if (symbol.equalsIgnoreCase("quit"))
                            s.close();
                        else if(!item_map.containsKey(symbol)) {
                            out.println("-1 , Symbol is invalid.Try again");// Display when symbol is invalid.
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
                                out.println("Enter valid Input!!");// if enter another value display invalid input.
                                out.flush();
                            }
                            if(exist.equalsIgnoreCase("yes")){ // break the loop and go for bidding.
                                break;
                            }
                            out.print("\nOK " + name + ", Please Enter the symbol of the item that you want to bid : ");
                            out.flush();
                        }


                    }

                Item item = item_map.get(symbol);
                out.println("\nPlease Wait.......");
                out.flush();
                synchronized (item) { //wait others when accessing same item.

                    out.print("\nPlease enter your price to bid : ");
                    out.flush();
                    String price = "0";
                    int excessBytesDuringWait = s.getInputStream().available();
                    if (excessBytesDuringWait > 0) {
                        s.getInputStream().skip(excessBytesDuringWait);
                        System.out.println(excessBytesDuringWait);
                    }
                    try {
                        for (price = in.readLine(); !price.equals("quit") && Float.parseFloat(price) <= item.get_price(); price = in.readLine()) {//Check the entered price is higher than current price.

                            out.print("\nError: Hi " + name + ", The price you entered must be more than the current price of the item. Note that the current price of " + symbol + " is " + item.get_price());
                            out.print("\nPlease re-enter your price to bid : ");
                            out.flush();
                        }
                        if (price.equals("quit"))
                            s.close();
                    } catch (NumberFormatException e) {//if there is a number format exception.
                        out.println("\nError You entered an invalid value for price. Exiting the Auction server ....Try again");
                        out.flush();
                        s.close();
                    }

                    out.println("\nOk " + name + ",Your price accepted. Please enter 'confirm' and press enter to confirm bidding.");
                    out.println("Or enter 'quit' and press enter to quit bidding.");
                    out.flush();

                    for (String confirm = in.readLine(); !confirm.equals("confirm"); confirm = in.readLine()) { //get confirmation about bidding from user.
                        if (confirm.equals("quit"))
                            s.close();
                        out.println("Error input: Hi " + name + ", Enter 'confirm' and press enter to confirm bidding.");
                        out.println("Or enter 'quit' and press enter to quit bidding.");
                        out.flush();
                    }

                    if (!item_map.get(symbol).timeOut) {
                        item_map.get(symbol).set_Name(name);
                        if (item_map.get(symbol).make_bid(Float.parseFloat(price)) == 0) {//Display corresponding output according to error code.
                            out.println("\nCongratulations " + name + ", Your bid saved successfully.");
                            out.println("Current Price in " + symbol + " is " + price + ".");
                            item_map.get(symbol).make_bid(Float.parseFloat(price));// do the bidding function.
                            sleep(500);// sleep the thread 0.5 seconds. Then no more users can't made bid within 500ms.
                        } else {
                            out.println(name + ",Your bid is expired. Due to TimeOut ");// Display bid is expired.
                        }
                    } else {
                        out.println(name + ",Your bid is expired. Due to TimeOut");// Display bid is expired.
                    }
                    out.println("\nThank You for using Stock Exchange Server."); // exit the server.
                    out.println("==================================================================");
                    out.flush();
                }

            }
        } catch (IOException | InterruptedException iOException) {
            this.s.close(); //if try block fails close the connection.
        }
    }

    private void handle_1()  {// Implement methods for Pub-Sub Sever

        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));// get inputs from user.
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));// print outputs to user.
            out.println("==================================================================");
            out.println("===========  Welcome to Stock Exchange - Auction Server ==========");
            out.println("================== Publisher-Subscriber Model ====================");
            out.println("==================================================================");
            out.println("Enter 'quit' and press enter any time to quit bidding.");
            out.print("Please Enter Your Name ID : ");
            out.flush();
            String name;

            for (name = in.readLine(); Item.nameSet.contains(name) || name.toLowerCase().equals("quit"); name = in.readLine()) {//Get the unique Name ID from user
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
            Thread profitT = new Thread() { // when user subscribe to items this thread starts.
                public void run() {
                    try {
                        out.flush();
                        while (true) {

                            if (!ItemMap.profitList.isEmpty()) { // If someone change the profit and it contains the profitList then display the symbol and profit price.
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


            Thread bidT = new Thread() {// when user bidding on items this thread starts.
                public void run() {
                    try {
                        out.flush();
                        while (true) {

                            if (!ItemMap.bidList.isEmpty()) {// If someone make bids and it contains the BidList then display the symbol and profit price.
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
                    out.print("\n\rRemaining BID-Time for not extended items is Over"); // display the remaining time for not extended items.
                } else {
                    out.print("\n\r" + Main.a + "");
                }
                out.flush();

                String[] queryTokens; // to store user query.
                String query;
                out.flush();
                out.println("\n\rEnter the Query Format :");
                out.flush();
                for (query = in.readLine().toUpperCase(); query.equals("QUIT"); query = in.readLine().toUpperCase()) { // get the query from user.
                    out.println("Exiting the Auction Sever ......");
                    out.println("Thank you....");
                    out.flush();
                    s.close();
                }
                queryTokens = query.split(" "); // split the query and save on the queryTokens Array.
                boolean isNumber = false; // initiate boolean for check second token is a number or not.
                int count = 0;
                if (queryTokens.length >= 2) {
                    for (char i : queryTokens[1].toCharArray()) {
                        if(Character.isDigit(i)){
                            count++;
                        }
                        if(count == queryTokens[1].length()){
                            isNumber =true; // if all charters are numbers then isNumber is true.
                        }
                    }
                }

                if (item_map.containsKey(queryTokens[0]) && isNumber) {// if this true this is a query format for change profit.
                    String sym = queryTokens[0];
                    int securityNumber = 0;
                    float profit = 0;
                    try {
                        securityNumber = Integer.parseInt(queryTokens[1]);
                        profit = Float.parseFloat(queryTokens[2]);
                    } catch (Exception e) {
                        out.print("\rEntered Number format is invalid!!");// display the message when NumberFormat exception.
                        out.flush();
                    }

                    if (item_map.get(sym).getSecurity() == securityNumber) { // if security number is exist to corresponding item then update the profit.
                        item_map.get(sym).setProfit(profit);
                        out.print("0 |");
                        out.print(" Profit Changed");
                        System.out.println(name + ", Change the Profit in item " + sym + " to : " + profit);
                        ItemMap.profitList.add(sym); // add to profitList.
                    } else {
                        out.print(" | -1");
                    }
                    out.flush();

                } else if (queryTokens[0].equals("PRFT")) {// if this true this is a query format for subscribe profit.
                    HashSet<String> currentProfit = new HashSet<>();// to store symbol.

                    for (int i = 1; i < queryTokens.length; i++) {
                        if (item_map.containsKey(queryTokens[i])) {
                            if (!subscribeMap.contains(queryTokens[i])) {
                                subscribeMap.add(queryTokens[i]);//if symbol is doesn't exist in the subscribeMap then add symbol to subscriberMap.
                            }
                            currentProfit.add(queryTokens[i]);// add symbol to this hashset.
                            System.out.println(name + ", Subscribe to the Profit on " + queryTokens[i] + " item."); // display in the server.
                            out.print("0 ");// display user to return type.
                            if (!profitT.isAlive()) {
                                profitT.start();//If profitT doesn't start then start the Thread.
                            }
                        } else {
                            out.print("-1 ");// display user to return type.
                        }
                        out.flush();
                    }
                    out.println("");
                    for (String i: currentProfit) {//to display current profit for each subscribe items.
                        out.println("\ritem " +i+ " profit is \t:\t " + item_map.get(i).getProfit());
                    }
                    currentProfit.clear();


                } else if (queryTokens[0].equals("BID")) {// if this true this is a query format for subscribe BIDs.

                    for (int i = 1; i < queryTokens.length; i++) {

                        if ((item_map.containsKey(queryTokens[i]))) {
                            if (!subscriptionArray.contains(queryTokens[i])) {
                                subscriptionArray.add(queryTokens[i]);//if symbol is doesn't exist in the subscriptionArray then add symbol to subscriptionArray.
                            }
                            System.out.println(name + ", Subscribe to the BID on " + queryTokens[i] + " item.");
                            out.print("0 ");// display user to return type.
                            if (!bidT.isAlive()) {
                                bidT.start();// if bidT doesn't start then start the Thread.
                            }
                        } else {
                            out.print("-1");// display user to return type.
                        }
                        out.flush();

                    }
                } else {
                    if (queryTokens[0].equals("QUIT")) {//if user type "quit" in server then exit the Auction server.
                        out.println("Exiting the Auction Sever ......");
                        out.println("Thank you....");
                        out.flush();
                        s.close();
                        break;//break the loop.
                    }
                    out.print("\nSorry!!, Couldn't Catch that...");//if user enter nothing or other wise not right query format then display user this message.
                    out.print("| -1");//display the return type.
                    out.print("\n\rTry Again!!\n\n");
                    out.flush();

                }
                out.flush();
                out.println("\r\n==================================================================");
            }
        } catch (IOException iOException) {// if catch Exception then close the socket.
            try {
                this.s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
