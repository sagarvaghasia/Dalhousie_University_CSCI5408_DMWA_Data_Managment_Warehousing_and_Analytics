public class Item {
    public int id;
    public String itemName;
    public int available_quantity;

    public Item(int id, String itemName, int available_quantity){
        this.id = id;
        this.itemName = itemName;
        this.available_quantity = available_quantity;
    }

    public void printItem(){
        System.out.println( this.id + " " + this.itemName + " " + this.available_quantity);
    }




}
