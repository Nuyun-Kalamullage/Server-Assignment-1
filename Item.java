import java.util.HashSet;

public class Item extends Thread {
    private String symbol;
    private float price;
    private int security;
    private float profit;
    private String name;
    public static HashSet<String> nameSet = new HashSet<>();
    private boolean isMadeBid;
    boolean timeOut;
    private static long biddingTime;
    long start = System.currentTimeMillis();
    long end = start + 60_000;
    long extendTime = System.currentTimeMillis() + 30_000;

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

    public Item(String symbol, String name, float price) {
        this.symbol = symbol;
        this.price = price;
        this.security = security;
        this.profit = profit;
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
        int errorCode = 0;
        try {

            if (!isAlive()) {
                if (end <= System.currentTimeMillis()) {
                    System.out.println("Bidding Time is Over for "+get_symbol()+" item.");
                    timeOut = true;
                    errorCode = -2;

                } else if (extendTime < System.currentTimeMillis() && get_price() < new_price) {
                    isMadeBid = false;
                    System.out.println("Time Extended for " + get_symbol() + " 1 Minute Additional\n");
                    end = end + 10_000;
                    System.out.println(get_name() + " Make a bid in " + get_symbol() + " for $" + new_price + ". \n");
                    isMadeBid = true;
                    update_price(new_price);
                    start();
                    errorCode = 0;

                } else {
                    if (get_price() < new_price) {
                        System.out.println(get_name() + " Make a bid in " + get_symbol() + " for $" + new_price + ". \n");
                        isMadeBid = true;
                        update_price(new_price);
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
