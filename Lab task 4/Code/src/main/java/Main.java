
public class Main
{
    public static void main(String args[])
    {
        try
        {
            ConnectionClass connectionClass = new ConnectionClass();
            Item item = connectionClass.getItem(1);
            System.out.println("Current inventory table details");
            item.printItem();

            connectionClass.createOrder();

            Item item2 = connectionClass.getItem(1);
            System.out.println("Inventory details after creating order");
            item2.printItem();

            connectionClass.CreatedOrderDetails("order_info",3);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
