package datasite;

import java.sql.*;

public class DataManager 
{	
	public static int create() {
		int create_table = 0;
		Connection sqlconnect = null;
		try {
			Class.forName("org.sqlite.JDBC");
			sqlconnect = DriverManager.getConnection("jdbc:sqlite:c2pl.db");
			Statement st = sqlconnect.createStatement();
			st.setQueryTimeout(30);
			st.executeUpdate("drop table if exists datastore");
			create_table = st.executeUpdate("create table datastore (item string, value integer)");
			st.close(); sqlconnect.close();
		}
		catch(SQLException e) { System.err.println(" SQL Exception: " + e.getMessage()); }
		catch(ClassNotFoundException e) { System.err.println( " Class Not Found Exception: " + e.getMessage()); }
		return create_table;
	}
	public static int write(String item, int value)
	{
		int output = 0;
		Connection sqlconnect = null;
		try {
			Class.forName("org.sqlite.JDBC");
			sqlconnect = DriverManager.getConnection("jdbc:sqlite:c2pl.db");
			Statement st = sqlconnect.createStatement();
			st.setQueryTimeout(30);
			String query = "select value from datastore where item = '" + item + "'";
			ResultSet resultset = st.executeQuery(query);
			if(resultset.next()) { query = "update datastore set value = " + value + " where item = '" + item + "'"; }
			else { query = "insert into datastore values ('" + item + "', " + value + ")"; }
			output = st.executeUpdate(query);
			st.close(); sqlconnect.close();
		}
		catch(SQLException e) { System.err.println( " SQL Exception: " + e.getMessage());	}
		catch(ClassNotFoundException e) { System.err.println(" Class Not Found Exception: " + e.getMessage()); }
		return output;
	}
	
	public static int read(String item)
	{
		int updated_val = 0;
		Connection sqlconnect = null;
		try {
			Class.forName("org.sqlite.JDBC");
			sqlconnect = DriverManager.getConnection("jdbc:sqlite:c2pl.db");
			Statement st = sqlconnect.createStatement();
			st.setQueryTimeout(30);
			ResultSet output = st.executeQuery("select value from datastore where item = '" + item + "'");
			if(output.next()) {	updated_val = output.getInt("value");}
			output.close(); st.close(); sqlconnect.close();	
		}
		catch(SQLException e) { System.err.println( " SQL Exception: " + e.getMessage()); }
		catch(ClassNotFoundException e) { System.err.println( " Class Not Found Exception: " + e.getMessage()); }	
		return updated_val;
	}
}
