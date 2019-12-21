import java.sql.*;
import java.io.*;

class RemoteConnectDB
{
	public static void main(String args[])throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://MySQL5.brinkster.com:3306/mihirsathe", "mihirsathe", "keepitupCSI13");
		ResultSet rs = conn.createStatement().executeQuery("select * from fortondu");
		while(rs.next())
			System.out.println(rs.getString("Column"));
		rs.close();
		conn.close();
	}
}