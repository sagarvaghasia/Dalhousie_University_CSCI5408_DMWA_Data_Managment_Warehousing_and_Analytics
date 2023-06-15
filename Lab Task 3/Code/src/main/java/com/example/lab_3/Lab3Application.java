package com.example.lab_3;

public class Lab3Application {

	public static void main(String[] args) {
		try{
			DBConnection dbConn = new DBConnection();
			Item item = dbConn.fetchItem(1);
			item.print();

			dbConn.createOrder();

			Item item2 = dbConn.fetchItem(1);
			item2.print();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}


	}

}
