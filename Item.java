import java.util.HashSet;

public class Item extends Thread { //extend the item class from Thread class to run the thread.
    private String symbol;
    private float price;
    private int security;
    private float profit;
    private String name; //name of user.
    public static HashSet<String> nameSet = new HashSet<>(); // to store unique Name IDs.

    boolean timeOut; // boolean that shows bidding timeout is true or false.
    private static long biddingTime; // store the time of the bidding time of an item.
    long start = System.currentTimeMillis(); // start time.
    long end = start + biddingTime; // actual ending time
    long extendTime = end - 60_000; // Time that extended function begin execute.

    public void run() { //thread start function.

        while (!timeOut) { // execute the statements until timeout is true.
            if (end <= System.currentTimeMillis()) { //checks the current time is exceed to end time.
                System.out.println("Extend Time Over for Bidding " + symbol + " now!"); // display  to the sever.
                timeOut = true; // make timeout true.
                break;//exit from the loop.
            }
            timeOut = false;// otherwise timeout will false.
        }
    }

    public Item(String symbol, String name, float price, int security, float profit) { //pass the values to class variables.
        this.symbol = symbol;
        this.price = price;
        this.security = security;
        this.profit = profit;
    }

    // add getters and setters to change private variables.
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
    } // make function to update price in the item class.


    public float make_bid(float new_price) {
        int errorCode= -2;// return -2 error code if try block fail.
        try {
            if (!timeOut) { // if time out is false.
                if (end <= System.currentTimeMillis()) {//check the current time exceed the ending time and it is true.
                    System.out.println("Bidding Time is Over for "+get_symbol()+" item.");// display this in server.
                    timeOut = true; // make timeout boolean true.
                    errorCode = -2; // make return code -2 to the server.

                } else if (extendTime < System.currentTimeMillis() && get_price() < new_price) {//check the current time exceed the extend time and user price is lager than current price and only these two true.

                    System.out.println("Time Extended for " + get_symbol() + " 1 Minute Additional\n");// display server to corresponding item ending time is extend.
                    end = end + 60_000; // add one minute to the end time.
                    System.out.println(get_name() + " Make a bid in " + get_symbol() + " for $" + new_price + ". \n");// display server to user make bid on corresponding item.

                    update_price(new_price); // update the price that entered user.
                    ItemMap.bidList.add(symbol); // add the symbol to bid list for subscribers purpose.
                    if(!isAlive()) {
                        start();// if thread is not started yet start the thread.
                    }
                    errorCode = 0;// return the error code to zero.
                    timeOut =false; // make timeout false.

                } else {
                    if (get_price() < new_price) {//user price is lager than current price and it is true.
                        System.out.println(get_name() + " Make a bid in " + get_symbol() + " for $" + new_price + ". \n");// display server to user make bid on corresponding item.
                        update_price(new_price); // update the price that entered user.
                        ItemMap.bidList.add(symbol); // add the symbol to bid list for subscribers purpose.

                        errorCode = 0;// return the error code to zero.
                    } else// otherwise.
                        errorCode = -2;// make return code -2 to the server.
                    timeOut = false;// make timeout false.
                }
            }
            System.out.flush();

        } catch (NumberFormatException e) {
            System.err.println("Invalid Number Format");// display error if exception in number format.
        }
        return errorCode; // return the return type of this function.
    }

}
