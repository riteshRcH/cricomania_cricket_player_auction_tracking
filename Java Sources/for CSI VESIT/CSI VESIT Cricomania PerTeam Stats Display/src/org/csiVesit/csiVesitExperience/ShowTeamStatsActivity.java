package org.csiVesit.csiVesitExperience;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowTeamStatsActivity extends Activity
{
	int teamID;
	String receivedXMLFromServer, serverIPPortToUse, earlierParsedXMLMD5 = "noMD5";
	double teamBalanceLeft;
	boolean logoutSuccess = false;
	
	Button btnRefresh;
	TextView txtviewShowCurrentBalance;
	
	ArrayList<XMLParsedDisplayRecord> allBoughtPlayers = new ArrayList<XMLParsedDisplayRecord>();
	GetNumberInWords getNumberInWords = new GetNumberInWords();
	StringWriter strWriter;
	PrintWriter pw = new PrintWriter(strWriter = new StringWriter());
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_team_stats);
        
        /********************************************************************************************************/
        
        initViews();

		/********************************************************************************************************/
		
		Bundle receivedData = getIntent().getExtras();
		teamID = receivedData.getInt("org.csiVesit.csiVesitExperience.LoginActivity.team_id");
		serverIPPortToUse = receivedData.getString("org.csiVesit.csiVesitExperience.LoginActivity.serverIPPortToUse");
		setTitle("Team: "+teamID+" Current stats");
		
		/********************************************************************************************************/
		
		btnRefresh.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				allBoughtPlayers.clear();
				new bgTaskGetTeamCurrentStatsFromServerAsXML().execute();
			}
		});
		btnRefresh.performClick();
    }

	private void initViews()
	{
		btnRefresh = (Button)findViewById(R.id.btnRefreshStats);
		txtviewShowCurrentBalance = (TextView)findViewById(R.id.txtviewShowCurrentBalance);
	}
	class bgTaskGetTeamCurrentStatsFromServerAsXML extends AsyncTask<Void, String, Exception>
	{
		ProgressDialog progressDialog = null;
		boolean receivedStatsAsXMLFromServerSuccessIndicator = false;
		StringBuffer responseBody = new StringBuffer();
		
		@Override
	    protected void onPreExecute()
	    {
	    	progressDialog = new ProgressDialog(ShowTeamStatsActivity.this);
	    	progressDialog.setTitle("Processing ..");
	    	progressDialog.setMessage("Please wait.");
	    	progressDialog.setCancelable(true);
	    	progressDialog.setIndeterminate(true);
	    	progressDialog.show();
	    }
		
		@Override
		protected Exception doInBackground(Void... params)
		{
		    HttpPost httppost = new HttpPost("http://"+serverIPPortToUse+"/cricomania/cricomania_get_team_stats.php");

		    try
		    {
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("team_id", Integer.toString(teamID)));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        HttpResponse response = LoginActivity.httpclient.execute(httppost);
		        
		        responseBody = new StringBuffer();
		        String s = new String();
		        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        while((s=br.readLine())!=null)
		        	responseBody.append(s).append(System.getProperty("line.separator"));
		        br.close();
		        
		        receivedXMLFromServer = responseBody.toString().trim();
		        receivedStatsAsXMLFromServerSuccessIndicator = receivedXMLFromServer.length()>0;

		    }catch(Exception e)
		    {
		    	Log.e("Exception", e.getMessage()==null?"null":e.getMessage());
	    		e.printStackTrace();
	    		return e;
		    }
			return null;
		} 
		
		@Override
		protected void onProgressUpdate(String ...progressParams)
		{
			progressDialog.setMessage(progressParams[0]);
		}
		
		@Override
		protected void onPostExecute(Exception exceptionOccured)
		{
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(exceptionOccured==null)
			{
				if(receivedStatsAsXMLFromServerSuccessIndicator)
				{
					Toast.makeText(ShowTeamStatsActivity.this, "Successfully received your stats from server!", Toast.LENGTH_SHORT).show();
					parseXMLAndDisplayCurrentTeamStats();
				}else
					Toast.makeText(ShowTeamStatsActivity.this, "There was an error while receiving your stats from server .. retry", Toast.LENGTH_SHORT).show();
			}else
				Toast.makeText(ShowTeamStatsActivity.this, "Exception occured: "+(exceptionOccured.getMessage()==null?"No exception description found!":exceptionOccured.getMessage()), Toast.LENGTH_SHORT).show();
		}
	}
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ShowTeamStatsActivity.this);
		builder.setTitle("Logout confirm?");
		builder.setMessage("Are you sure you want to logout?");
		builder.setCancelable(true);
		builder.setNegativeButton("No", new DialogInterface.OnClickListener()
		{	
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				new bgTaskInformLogoutToServer().execute();
			}
		});
		builder.create().show();
	}
	public void parseXMLAndDisplayCurrentTeamStats()
	{
		try
		{
			if(!earlierParsedXMLMD5.equals(getMD5OfParam(receivedXMLFromServer)))
			{
				//Parse XML	using XMLPUllParser(Android provided is more efficient as after few insertions of start times DOM parsing would be mem inefficient) as XML String has been changed since last parsing
				XmlPullParser xpp = Xml.newPullParser();
				xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			    xpp.setInput(new StringReader(receivedXMLFromServer));
			
			    int eventType = xpp.getEventType();
			    XMLParsedDisplayRecord record = null;
			    while(eventType != XmlPullParser.END_DOCUMENT)
			    {
			        if (eventType == XmlPullParser.START_TAG)
			        {
			            if (xpp.getName().equals("team_balance"))
			            	teamBalanceLeft = Double.parseDouble(xpp.nextText());
			            else if(xpp.getName().equals("team_auction_details"))
			            	record = new XMLParsedDisplayRecord();
			            else if(xpp.getName().equals("team_id") && record!=null)
			            	record.setTeamID(Integer.parseInt(xpp.nextText()));
			            else if(xpp.getName().equals("bought_player_id") && record!=null)
			            	record.setBoughtPlayerID(Integer.parseInt(xpp.nextText()));
			            else if(xpp.getName().equals("bought_player_name") && record!=null)
			            	record.setBoughtPlayerName(xpp.nextText());
			            else if(xpp.getName().equals("bought_in_amt") && record!=null)
			            	record.setBoughtInAmt(Double.parseDouble(xpp.nextText()));
			            else if(xpp.getName().equals("bought_in_amt_words") && record!=null)
			            	record.setBoughtInAmtWords(xpp.nextText());
			            else if(xpp.getName().equals("bought_timestamp") && record!=null)
			            	record.setBoughtTimestamp(XMLParsedDisplayRecord.DBStorageTimestampFormat.parse(xpp.nextText()));
			        }else if(eventType == XmlPullParser.END_TAG && xpp.getName().equals("team_auction_details"))
			        {
			        	allBoughtPlayers.add(record);
			        	record = null;
			        }
			        eventType = xpp.next(); //move to next element
			    }
			    
			    earlierParsedXMLMD5 = getMD5OfParam(receivedXMLFromServer);
			    
			    updateDisplayWRTRecords();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(ShowTeamStatsActivity.this, "Error while interpreting server sent data", Toast.LENGTH_SHORT).show();
		}
	}
	private void updateDisplayWRTRecords()
	{
		try
		{
			getNumberInWords.setNumber((long)teamBalanceLeft);
			pw.format("%f", teamBalanceLeft);
			txtviewShowCurrentBalance.setText("Balance Left: " + System.getProperty("line.separator") + "\t" + strWriter.getBuffer().toString() + System.getProperty("line.separator") + "\t" + getNumberInWords.getNumberInWords());
			txtviewShowCurrentBalance.setTextSize(14);
			txtviewShowCurrentBalance.setTypeface(null, Typeface.BOLD);
			strWriter.getBuffer().setLength(0);
			
			TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
			tableLayout.removeAllViews();
			for(XMLParsedDisplayRecord record:allBoughtPlayers)
			{
				TableRow row = new TableRow(ShowTeamStatsActivity.this);
				TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 20, 0, 20);
		        row.setLayoutParams(lp);
		        
		        TextView txtviewShowBoughtPlayerDetails = new TextView(ShowTeamStatsActivity.this);
		        StringBuffer strBufferBoughtPlayerDetails = new StringBuffer("Bought Player Details: "+System.getProperty("line.separator"));
		        strBufferBoughtPlayerDetails.append("\tPlayer ID: ").append(Integer.toString(record.getBoughtPlayerID())).append(System.getProperty("line.separator"));
		        strBufferBoughtPlayerDetails.append("\tPlayer Name: ").append(record.getBoughtPlayerName()).append(System.getProperty("line.separator"));
		        pw.format("%f", record.getBoughtInAmt());
		        strBufferBoughtPlayerDetails.append("\tBuying Amount: ").append(strWriter.getBuffer().toString()).append(System.getProperty("line.separator"));
		        strBufferBoughtPlayerDetails.append("\tBuying Amount Words: ").append(record.getBoughtInAmtWords()).append(System.getProperty("line.separator"));
		        strBufferBoughtPlayerDetails.append("\tBought at: ").append(XMLParsedDisplayRecord.userDisplayTimestampFormat.format(record.getBoughtTimestamp())).append(System.getProperty("line.separator"));
		        txtviewShowBoughtPlayerDetails.setText(strBufferBoughtPlayerDetails.toString());
		        txtviewShowBoughtPlayerDetails.setTextSize(14);
		        row.addView(txtviewShowBoughtPlayerDetails);
		        strWriter.getBuffer().setLength(0);
		        
		        tableLayout.addView(row, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	static public String getMD5OfParam(String s)
	{
		try
		{
			MessageDigest mdEncoder = MessageDigest.getInstance("MD5");
			mdEncoder.update(s.getBytes());
			String md5 = new BigInteger(1, mdEncoder.digest()).toString(16);
			return md5;
			
		}catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	class bgTaskInformLogoutToServer extends AsyncTask<Void, String, Exception> 
	{
		ProgressDialog progressDialog = null;
		StringBuffer responseBody = new StringBuffer();
			
		@Override
	    protected void onPreExecute()
	    {
			progressDialog = new ProgressDialog(ShowTeamStatsActivity.this);
			progressDialog.setTitle("Processing ..");
			progressDialog.setMessage("Please wait.");
			progressDialog.setCancelable(true);
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}
			
		@Override
		protected Exception doInBackground(Void... params)
		{
		    HttpPost httppost = new HttpPost("http://"+serverIPPortToUse+"/cricomania/LogoutHandler.php");

		    try
		    {
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		        nameValuePairs.add(new BasicNameValuePair("team_id", Integer.toString(teamID)));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        HttpResponse response = LoginActivity.httpclient.execute(httppost);
		        
		        String s = new String();
		        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        while((s=br.readLine())!=null)
		        	responseBody.append(s).append(System.getProperty("line.separator"));
		        br.close();
		        
		        logoutSuccess = responseBody.toString().trim().equals("LogoutSuccess");
		        
		    }catch(Exception e)
		    {
		    	Log.e("Exception", e.getMessage()==null?"null":e.getMessage());
	    		e.printStackTrace();
	    		return e;
		    }
			return null;
		} 
		
		@Override
		protected void onProgressUpdate(String ...progressParams)
		{
			progressDialog.setMessage(progressParams[0]);
		}
		
		@Override
		protected void onPostExecute(Exception exceptionOccured)
		{
			if(progressDialog!=null)
				progressDialog.dismiss();
			
			if(exceptionOccured==null)
			{
				if(logoutSuccess)
				{
					Toast.makeText(ShowTeamStatsActivity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
					ShowTeamStatsActivity.this.finish();
				}else
					Toast.makeText(ShowTeamStatsActivity.this, "There was an error while logging out .. retry", Toast.LENGTH_SHORT).show();
			}else
				Toast.makeText(ShowTeamStatsActivity.this, "Exception occured: "+(exceptionOccured.getMessage()==null?"No exception description found!":exceptionOccured.getMessage()), Toast.LENGTH_SHORT).show();
		}
	};
}