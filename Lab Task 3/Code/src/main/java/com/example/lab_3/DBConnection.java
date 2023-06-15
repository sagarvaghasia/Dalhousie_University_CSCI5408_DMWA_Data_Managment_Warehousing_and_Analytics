package com.example.lab_3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.PreparedStatement;

public class DBConnection {

    private static Connection localConnection = null;
    private static Connection remoteConnection = null;

    public DBConnection() throws SQLException {
        localConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab_3_local", "root", "root");
        remoteConnection = DriverManager.getConnection("jdbc:mysql://localhost:3000/lab_3_remote", "root", "root");

    }

    private static Connection getTableConnection(String tableName){
        if(tableName.equals("user") || tableName.equals("or der_Info")){
            return localConnection;
        }
        else if(tableName.equals("Inventory_Table")){
            return remoteConnection;
        }
        else{
            return null;
        }
    }

    public List<Item> fetchItems(String tableName, String query) throws SQLException {
        List<Item> items = new ArrayList<Item>();
        Connection conn = getTableConnection(tableName);
        if(conn == null){
            return null;
        }

        ResultSet resultSet = conn.createStatement().executeQuery(query);

        while(resultSet.next()){
            Item item = new Item(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
            items.add(item);
        }

        return items;
    }

    public Item fetchItem(int id) throws SQLException {
//        List<Item> items = new ArrayList<Item>();
//        Connection conn = getTableConnection(tableName);
//        if(conn == null){
//            return null;
//        }

        ResultSet resultSet = remoteConnection.createStatement().executeQuery("select * from Inventory_Table where item_id = " + id + ";");

        while(resultSet.next()){
            Item item = new Item(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
            return item;
        }
        return null;
    }

    public void createOrder() throws SQLException {
        String query = "INSERT INTO Order_Info (order_id, user_id, item_name, quantity, order_date) values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = localConnection.prepareStatement(query);
        preparedStmt.setInt(1, 3);
        preparedStmt.setInt(2, 1);
        preparedStmt.setString(3, "laptop");
        preparedStmt.setInt(4, 4);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(date);
        preparedStmt.setString(5, currentDateTime);
        preparedStmt.executeUpdate();



        String query2 = "update Inventory_Table set available_quantity = available_quantity - ? where item_id = ?";
        PreparedStatement preparedStmt2 = remoteConnection.prepareStatement(query2);
        preparedStmt2.setInt(1, 4);
        preparedStmt2.setInt(2, 1);
        preparedStmt2.executeUpdate();

    }

}
