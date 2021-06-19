import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    public Socket s;

    // TODO : Define all required varible
    private ItemMap item_map;

    public Server(ItemMap item_map, Socket socket) {
        this.item_map = item_map;
        this.s = socket;
    }

    public void run() {
        try {
            handle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle() throws IOException {
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
            if (name.equals("quit"))
                s.close();

            out.println("\nOK " + name + ", Please Enter the symbol of the security you want to bid : " );
            out.flush();

            String symbol;
            for (symbol = in.readLine(); !item_map.containsKey(symbol); symbol = in.readLine()) {
                if (symbol.equals("quit"))
                    s.close();
                out.println("\n-1 , Symbol is invalid.Try again\n");
                out.flush();
            }

            Item item = item_map.get(symbol);
            out.println("\nPlease Wait.......");
            out.flush();
            synchronized (item) {
                double currentPrice = item.get_price();
                out.println("Yes " + name + ", The CURRENT PRICE of the security is : " + currentPrice + "\nPlease enter your price to bid : ");
                out.flush();
                String price = "0";
                int excessBytesDuringWait = s.getInputStream().available();
                if (excessBytesDuringWait > 0) {
                    s.getInputStream().skip(excessBytesDuringWait);
                    System.out.println(excessBytesDuringWait);
                }
                try {
                    for (price = in.readLine(); !price.equals("quit") && Double.parseDouble(price) <= currentPrice; price = in.readLine()) {
                        out.println("Error: Hi " + name + ", The price you entered must be more than the current price of the security. Note that the current price of " + symbol + " is " + currentPrice + "\nPlease re-enter your price to bid : ");
                        out.flush();
                    }
                    if (price.equals("quit"))
                        s.close();
                } catch (NumberFormatException e) {
                    out.println("\nError You entered an invalid value for price. Exiting the Auction server ....Try again");
                    out.flush();
                    s.close();
                }


                out.println("\nOk " + name + ",Your price accepted. Please enter 'confirm' and press enter to confirm bidding. Or enter 'quit' and press enter to quit bidding.");
                out.flush();

                for (String confirm = in.readLine(); !confirm.equals("confirm"); confirm = in.readLine()) {
                    if (confirm.equals("quit"))
                        s.close();
                    out.println("\nError input: Hi " + name + ", Enter 'confirm' and press enter to confirm bidding. Or enter 'quit' and press enter to quit bidding.");
                    out.flush();
                }

                item_map.get(symbol).make_bid(Float.parseFloat(price));

                out.println("\nCongratulations " + name + ",Your bid saved successfully. Thank you for using Stock Exchange - Auction Server.");
                out.flush();
            }
            s.close();
        } catch (IOException iOException) {
            System.out.println("iOException.getMessage() = " + iOException.getMessage());
            s.close();
        }
    }

}
