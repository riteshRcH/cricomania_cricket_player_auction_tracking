package org.csiVesit.csiVesitExperience;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class XMLParsedDisplayRecord
{
	int teamID, boughtPlayerID;
	double boughtInAmt;
	String boughtInAmtWords, boughtPlayerName;
	Date boughtTimestamp;
	
	static SimpleDateFormat userDisplayTimestampFormat = new SimpleDateFormat("dd MMM yyyy EEE hh:mm:ss a", Locale.ENGLISH);
	static SimpleDateFormat DBStorageTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	public int getTeamID() {
		return teamID;
	}
	public int getBoughtPlayerID() {
		return boughtPlayerID;
	}
	public double getBoughtInAmt() {
		return boughtInAmt;
	}
	public String getBoughtInAmtWords() {
		return boughtInAmtWords;
	}
	public String getBoughtPlayerName() {
		return boughtPlayerName;
	}
	public Date getBoughtTimestamp() {
		return boughtTimestamp;
	}
	public static SimpleDateFormat getUserDisplayTimestampFormat() {
		return userDisplayTimestampFormat;
	}
	public static void setUserDisplayTimestampFormat(
			SimpleDateFormat userDisplayTimestampFormat) {
		XMLParsedDisplayRecord.userDisplayTimestampFormat = userDisplayTimestampFormat;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public void setBoughtPlayerID(int boughtPlayerID) {
		this.boughtPlayerID = boughtPlayerID;
	}
	public void setBoughtInAmt(double boughtInAmt) {
		this.boughtInAmt = boughtInAmt;
	}
	public void setBoughtInAmtWords(String boughtInAmtWords) {
		this.boughtInAmtWords = boughtInAmtWords;
	}
	public void setBoughtPlayerName(String boughtPlayerName) {
		this.boughtPlayerName = boughtPlayerName;
	}
	public void setBoughtTimestamp(Date boughtTimestamp) {
		this.boughtTimestamp = boughtTimestamp;
	}	
}
