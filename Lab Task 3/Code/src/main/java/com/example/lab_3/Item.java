package com.example.lab_3;

public class Item {
    public int id;
    public String itemName;
    public int availableQuantity;

    public Item(int id, String itemName, int availableQuantity){
        this.id = id;
        this.itemName = itemName;
        this.availableQuantity = availableQuantity;
    }

    public void print(){
        System.out.println(this.id + " " + this.itemName + " " + this.availableQuantity);
    }
}
