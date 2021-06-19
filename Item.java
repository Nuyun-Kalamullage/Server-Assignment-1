
public class Item {
    private String symbol;
    private float price;
    private int security;
    private float profit;
    private String name;

    public Item(String symbol, String name, float price){
        this.symbol = symbol;
        this.price = price;
        this.security = security;
        this.profit = profit;
    }

    public String get_symbol(){
        return this.symbol;
    }

    public String get_name(){
        return this.name;
    }

    public float get_price() {
    	return this.price;
    }

    public void update_price(float price){
        this.price = price;
    }

    public float make_bid(float new_price){
    	// TODO: Implement this.
        try{
            if(get_price() >= new_price ){
                System.out.print("\nError: Hi "+get_name()+", The price you entered must be more than the current price of the security. Note that the current price of " +get_symbol()+ " is "+get_price()+"\nPlease re-enter your price to bid : ");
                System.out.flush();
                return -2;
            }
            if(get_price()< new_price){
                System.out.print("\nOk "+get_name()+",Your price accepted. Please enter 'confirm' and press enter to confirm bidding. Or enter 'quit' and press enter to quit bidding.\n");
                update_price(new_price);
                System.out.flush();

            }

        }catch(NumberFormatException e){
            System.out.print("\nError You entered an invalid value for price. Exiting the Auction server ....Try again");
            System.out.flush();
            return -2;
        }
        return get_price();
    }

}
