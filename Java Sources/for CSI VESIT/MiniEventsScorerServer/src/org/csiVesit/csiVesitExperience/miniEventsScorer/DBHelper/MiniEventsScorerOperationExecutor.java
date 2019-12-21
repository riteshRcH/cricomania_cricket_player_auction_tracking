package org.csiVesit.csiVesitExperience.miniEventsScorer.DBHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

import org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord;

public class MiniEventsScorerOperationExecutor
{
	boolean acceptMiniEventsScorerOperations;
	
	private Connection conn;
	private Statement stmt;
	private ResultSet rs;
	
	public LinkedHashMap<String, String> currentSessionOperationConfirmations = new LinkedHashMap<String, String>(100, 0.75F, true);	//(initialCapacity, float loadFactor, boolean accessOrder)
	//Key(String) = Client Threads unique Name => Value(String) = Client Threads requested operation identified by chosenMode~interMediateResultsSent(true/false)~opeartionSuccessIndicator(true for successful deletion/insertion/update else false)
	
	public MiniEventsScorerOperationExecutor(String DBVendor,String DBServerIPPort, String DBName, String DBUsername, String DBPassword)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:"+DBVendor+"://"+DBServerIPPort+"/"+DBName, DBUsername, DBPassword);
			stmt = conn.createStatement();
			System.out.println("[ "+org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScorerServer.logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerOperationExceutor] Connection for mini-Events Scorer Operation Execution established, ready to accept mini-EventsScorer Operations");
		}catch(SQLException e)
		{
			e.printStackTrace();
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		acceptMiniEventsScorerOperations = true;
	}

	public void toggleAcceptMiniEventsScorerOperations()
	{
		this.acceptMiniEventsScorerOperations = !this.acceptMiniEventsScorerOperations;
		System.out.println("[ "+org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScorerServer.logEventTimestampFormat.format(System.currentTimeMillis())+" - AMSDBDataHelper.AttendanceRecord] "+(this.acceptMiniEventsScorerOperations?"":"mini-events scorer Operations Execution Disabled, NOT ")+"Ready to accept mini-event scorer operations!");
	}
	
	public MiniEventsScoreLogRecord executeMiniEventsScorerOperation(String clientsUniqueThreadName, String queryToExecute, String chosenMode)throws Exception
	{
		//returns/inserts latest entry for that teamID
		if(acceptMiniEventsScorerOperations)
		{
			if(chosenMode.equals("Insert LatestEntry"))
			{
				if(stmt.executeUpdate(queryToExecute)>0)
				{
					currentSessionOperationConfirmations.put(clientsUniqueThreadName, chosenMode+"~"+Boolean.toString(true)+"~"+Boolean.toString(true));
					return null;
				}
			}else if(chosenMode.equals("Delete LatestEntry") || chosenMode.equals("Update LatestEntry"))
			{
				if(currentSessionOperationConfirmations.containsKey(clientsUniqueThreadName) && currentSessionOperationConfirmations.get(clientsUniqueThreadName).equals(chosenMode+"~"+Boolean.toString(true)+"~"+Boolean.toString(false)))						
				{
					if(stmt.executeUpdate(queryToExecute)>0)
					{
						currentSessionOperationConfirmations.put(clientsUniqueThreadName, chosenMode+"~"+Boolean.toString(true)+"~"+Boolean.toString(true));
						return null;
					}
				}else
				{
					currentSessionOperationConfirmations.put(clientsUniqueThreadName, chosenMode+"~"+Boolean.toString(true)+"~"+Boolean.toString(false));
					return popualateIntermediateResult(queryToExecute);
				}
			}else if(chosenMode.equals("View LatestEntry"))
				return popualateIntermediateResult(queryToExecute);
		}else
			return null;
		return null;
	}
	private MiniEventsScoreLogRecord popualateIntermediateResult(String queryToExecute)throws Exception
	{
		rs = stmt.executeQuery(queryToExecute);
		
		MiniEventsScoreLogRecord miniEventsScoreLogRecord = null;			//returns last record of that team for any event
		while(rs.next())
			miniEventsScoreLogRecord = new MiniEventsScoreLogRecord(rs.getInt("team_id"), rs.getString("mini_event_name"), rs.getLong("mini_event_amt_won"), rs.getString("entry_by_council_member_name"), rs.getString("entry_by_imei_num"), rs.getTimestamp("timestamp_of_entry")); 
		rs.close();
		return miniEventsScoreLogRecord;
	}
}