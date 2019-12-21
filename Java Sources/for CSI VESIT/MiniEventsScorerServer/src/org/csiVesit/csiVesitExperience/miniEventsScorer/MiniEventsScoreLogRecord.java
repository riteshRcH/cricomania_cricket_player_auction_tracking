package org.csiVesit.csiVesitExperience.miniEventsScorer;

import java.io.Serializable;
import java.sql.Timestamp;

public class MiniEventsScoreLogRecord implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6972803237023693661L;
	int teamID;
	String miniEventName, entryByCouncilMemberName, entryByIMEINum;
	Timestamp entryTimestamp;
	long miniEventAmtWon;
	
	public MiniEventsScoreLogRecord(int teamID, String miniEventName, long miniEventAmtWon, String entryByCouncilMemberName, String entryByIMEINum, Timestamp entryTimestamp)
	{
		this.teamID = teamID;
		this.miniEventName = miniEventName;
		this.entryByCouncilMemberName = entryByCouncilMemberName;
		this.entryByIMEINum = entryByIMEINum;
		this.entryTimestamp = entryTimestamp;
		this.miniEventAmtWon = miniEventAmtWon;
	}

	public int getTeamID() {
		return teamID;
	}

	public String getMiniEventName() {
		return miniEventName;
	}

	public String getEntryByCouncilMemberName() {
		return entryByCouncilMemberName;
	}

	public String getEntryByIMEINum() {
		return entryByIMEINum;
	}

	public Timestamp getEntryTimestamp() {
		return entryTimestamp;
	}

	public long getMiniEventAmtWon() {
		return miniEventAmtWon;
	}
	
	public String toString()
	{
		StringBuffer strBufferToReturn = new StringBuffer();
		strBufferToReturn.append("Team ID:	"+teamID).append(System.getProperty("line.separator"));
		strBufferToReturn.append("Mini Event Name:	"+miniEventName).append(System.getProperty("line.separator"));
		strBufferToReturn.append("Entry By Council Member:	"+entryByCouncilMemberName).append(System.getProperty("line.separator"));
		strBufferToReturn.append("Entry By IMEI Num:	"+entryByIMEINum).append(System.getProperty("line.separator"));
		strBufferToReturn.append("Entry done at:	"+entryTimestamp.toString()).append(System.getProperty("line.separator"));
		strBufferToReturn.append("Mini Event Amt Won:	"+miniEventAmtWon).append(System.getProperty("line.separator"));
		
		return strBufferToReturn.toString();
	}
}
