package snmp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test {

	 public static void create_connection(){
	        try{
	           //STEP 2: Register JDBC driver
	           Class.forName("com.mysql.cj.jdbc.Driver");

	           //STEP 3: Open a connection
	           System.out.println("Connecting to database...");
	           conn = DriverManager.getConnection(DB_URL,USER,PASS);

	           /*//STEP 4: Execute a query
	           System.out.println("Creating statement...");
	           stmt = conn.createStatement();
	           String sql;
	           sql = "SELECT * FROM emp";
	           ResultSet rs = stmt.executeQuery(sql);
	           
	           //STEP 5: Extract data from result set
	           while(rs.next()){
	           //Retrieve by column name
	           int id  = rs.getInt("id");
	           Object o = rs.getObject("name");
	           
	           //Display values
	           System.out.println("ID: " + id);
	           System.out.print("Name: " + o.toString());
	           }
	           //STEP 6: Clean-up environment
	           rs.close();
	           stmt.close();
	           conn.close();
	           */
	        }catch(SQLException se){
	           //Handle errors for JDBC
	           se.printStackTrace();
	        }catch(Exception e){
	           //Handle errors for Class.forName
	           e.printStackTrace();
	        }
	        /*        finally
	        {
	        //finally block used to close resources
	        try{
	        if(stmt!=null)
	        stmt.close();
	        }catch(SQLException se2){
	        }// nothing we can do
	        try{
	        if(conn!=null)
	        conn.close();
	        }catch(SQLException se){
	        se.printStackTrace();
	        }//end finally try
	        }//end try*/
	        System.out.println("END");
	    }
	 
	public static String getCommunityByIP(String ip) {
    	String community = "";
    	try {
    		create_connection();
            stmt = conn.createStatement();
            //start
            String sql="select community from "+database_name+"."+tablename_devices+" where device_ip = '"+ip+"'";
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            community = rs.getString(1);
        	rs.close();
        	stmt.close();
        	conn.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return community;
    }
    
    static String database_name="network_devices_manager";
    
    // by default localhost
    static String db_ip="localhost";
    
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://"+db_ip+"/"+database_name;

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "@Hoang04122002";
    
    static public String tablename_records = "recorded_data";
    static public String tablename_interfaces = "interface_data";
    static public String tablename_devices = "device_data";
    
    static Connection conn;
    static Statement stmt;
    
	public static void main(String[] args) {
		String a = getCommunityByIP("192.168.1.66");
		System.out.println(a);
	}

}
