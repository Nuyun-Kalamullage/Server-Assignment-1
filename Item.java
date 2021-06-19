
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

    public void setName(String name) {
        this.name = name;
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
            if(get_price()< new_price){
                update_price(new_price);
                System.out.println(get_name()+" Make a bid in "+get_symbol()+" for $"+get_price()+". \n");
                System.out.flush();
            }

        }catch(NumberFormatException e){
        }
        return get_price();
    }

}
