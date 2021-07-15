import java.util.HashSet;

public class Item extends Thread {
    private String symbol;
    private float price;
    private int security;
    private float profit;
    private String name;
    public static HashSet<String> nameSet = new HashSet<>();


    boolean timeOut;
    private static long biddingTime;
    long start = System.currentTimeMillis();
    long end = start + biddingTime;
    long extendTime = end - 60_000;

    public void run() {

        while (!timeOut) {
            if (end <= System.currentTimeMillis()) {
                System.out.println("Extend Time Over for Bidding " + symbol + " now!");
                timeOut = true;
                break;
            }
            timeOut = false;
        }
    }

    public Item(String symbol, String name, float price, int security, float profit) {
        this.symbol = symbol;
        this.price = price;
        this.security = security;
        this.profit = profit;
    }


    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public int getSecurity() {
        return security;
    }

    public static void setBiddingTime(long biddingTime) {
        Item.biddingTime = biddingTime;
    }

    public void set_Name(String name) {
        this.name = name;
    }

    public String get_symbol() {
        return this.symbol;
    }

    public String get_name() {
        return this.name;
    }

    public float get_price() {
        return this.price;
    }

    public void update_price(float price) {
        this.price = price;
    }


    public float make_bid(float new_price) {
        // TODO: Implement this.
        int errorCode= -2;
        try {

            if (!timeOut) {
                if (end <= System.currentTimeMillis()) {
                    System.out.println("Bidding Time is Over for "+get_symbol()+" item.");
                    timeOut = true;
                    errorCode = -2;

                } else if (extendTime < System.currentTimeMillis() && get_price() < new_price) {

                    System.out.println("Time Extended for " + get_symbol() + " 1 Minute Additional\n");
                    end = end + 60_000;
                    System.out.println(get_name() + " Make a bid in " + get_symbol() + " for $" + new_price + ". \n");

                    update_price(new_price);
                    ItemMap.bidList.add(symbol);
                    if(!isAlive()) {
                        start();
                    }
                    errorCode = 0;
                    timeOut =false;

                } else {
                    if (get_price() < new_price) {
                        System.out.println(get_name() + " Make a bid in " + get_symbol() + " for $" + new_price + ". \n");
                        update_price(new_price);
                        ItemMap.bidList.add(symbol);

                        errorCode = 0;
                    } else
                        errorCode = -2;
                    timeOut = false;
                }
            }
            System.out.flush();

        } catch (NumberFormatException e) {
            System.err.println("Invalid Number Format");
        }
        return errorCode;
    }

}
