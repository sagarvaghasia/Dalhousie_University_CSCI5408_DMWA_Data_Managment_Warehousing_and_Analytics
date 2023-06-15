import java.security.spec.RSAOtherPrimeInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.PreparedStatement;


public class ConnectionClass
{
    private static Connection localConnection = null;
    private static Connection dockerConnection = null;

    public ConnectionClass() throws SQLException
    {
        localConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/localdb", "root", "root");
        dockerConnection = DriverManager.getConnection("jdbc:mysql://localhost:3000/remotedb", "root", "my-secret-pw");
    }

    private static Connection getConnectionForTable(String tableName)
    {
        if(tableName.equals("user") || tableName.equals("order_info")) {
            System.out.println("the table "+tableName+" exists in local instance");
            return localConnection;
        }

        else if(tableName.equals("inventory")) {
            System.out.println("the table" +tableName+ "exists in docker instance");
            return dockerConnection;
        }

        else {
            return null;
        }
    }



    public List<Item> getItems(String tableName, String query)throws SQLException{
        List<Item> items = new ArrayList<Item>();
        Connection connection = getConnectionForTable(tableName);
        if(connection == null){
            return null;
        }

        ResultSet resultSet = connection.createStatement().executeQuery(query);

        while(resultSet.next()){
            Item item = new Item(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
            items.add(item);
        }
        return items;
    }

    public Item getItem(int id)throws SQLException {
        ResultSet resultSet = dockerConnection.createStatement().executeQuery("select * from inventory where item_id = " + id + ";");
        while (resultSet.next()) {
            Item item = new Item(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
            return item;
        }
        return null;
    }

    public void CreatedOrderDetails(String tableName, int id) throws SQLException{

        ResultSet resultSet= localConnection.createStatement().executeQuery("select * from order_info where order_id = " + id + ";");
        System.out.println("Created Order is :");
        while (resultSet.next()){
            System.out.println(resultSet.getInt(1)+" "+ resultSet.getInt(2)+" "+resultSet.getString(3)+" "+resultSet.getString(4)+" "+resultSet.getDate(5));
        }
    }



    public void createOrder() throws SQLException{

        String query1 = "insert into order_info (order_id, user_id, item_name, quantity, order_date) values (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement =localConnection.prepareStatement(query1);
        preparedStatement.setInt(1,3);
        preparedStatement.setInt(2,1);
        preparedStatement.setString(3,"water bottle");
        preparedStatement.setInt(4,4);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
        String currentDateTime = format.format(date);
        preparedStatement.setString(5,currentDateTime);
        preparedStatement.executeUpdate();



        String query2 = "update inventory set available_quantity = available_quantity - ? where item_id = ?";
        PreparedStatement preparedStatement2 = dockerConnection.prepareStatement(query2);
        preparedStatement2.setInt(1,4);
        preparedStatement2.setInt(2,1);
        preparedStatement2.executeUpdate();


    }

}
