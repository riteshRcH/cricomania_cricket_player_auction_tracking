package org.csiVesit.csiVesitExperience;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.csiVesit.csiVesitExperience.AndroidLikeToast.Style;

@SuppressWarnings("serial")
public class UpdateMiniEventTotalToServerCricoMania extends JFrame
{
	final JButton jbtnAddMiniEventTotalAmtToServer, jbtnSetDB;
	JTextField jtxfDBVendorLocalDB, jtxfDBServerIPPortLocalDB, jtxfDBNameLocalDB, jtxfDBUsernameLocalDB, jtxfDBPasswordLocalDB;
	JTextField jtxfDBVendorWebServerDB, jtxfDBServerIPPortWebServerDB, jtxfDBNameWebServerDB, jtxfDBUsernameWebServerDB, jtxfDBPasswordWebServerDB;
	
	Connection conn;
	Statement stmt;
	ResultSet rs;
	PreparedStatement ps;
	
	public static SimpleDateFormat logEventTimestampFormat = new SimpleDateFormat("dd MMM(MM) yyyy EEE hh:mm:ss:SSS a");
	
	LinkedHashMap<Integer, Double> perTeamTotal;
	
	GetNumberInWords getNumberInWords = new GetNumberInWords();
	StringWriter strWriter;
	PrintWriter pw = new PrintWriter(strWriter = new StringWriter());
	
	 boolean DBSet = false;
	
	UpdateMiniEventTotalToServerCricoMania()
	{
		super.setLayout(new FlowLayout());
		this.setTitle("Update per team mini Event amt total to Server");
		this.setVisible(true);
		this.setSize(1150, 256);
		this.setLocationRelativeTo(null);														//center locn
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);									//dispose all UI windows
		
		this.add(new JLabel("Local DB: "));
		this.add(new JLabel("jdbc:"));
		this.add(jtxfDBVendorLocalDB = new JTextField("mysql", 10));
		this.add(new JLabel("://"));
		this.add(jtxfDBServerIPPortLocalDB = new JTextField("localhost:3306", 20));
		this.add(new JLabel("/"));
		this.add(jtxfDBNameLocalDB = new JTextField("csi_mini_events_scorer_db", 20));
		this.add(jtxfDBUsernameLocalDB = new JTextField("root", 10));
		this.add(jtxfDBPasswordLocalDB = new JTextField("cooperHawk!1259", 20));
		
		this.add(new JLabel("Web server DB: "));
		this.add(new JLabel("jdbc:"));
		this.add(jtxfDBVendorWebServerDB = new JTextField("mysql", 10));
		this.add(new JLabel("://"));
		this.add(jtxfDBServerIPPortWebServerDB = new JTextField("MySQL5.brinkster.com:3306", 20));
		this.add(new JLabel("/"));
		this.add(jtxfDBNameWebServerDB = new JTextField("mihirsathe", 20));
		this.add(jtxfDBUsernameWebServerDB = new JTextField("mihirsathe", 10));
		this.add(jtxfDBPasswordWebServerDB = new JTextField("keepitupCSI13", 20));
		
		this.add(jbtnSetDB = new JButton("Set DB"));
		this.add(jbtnAddMiniEventTotalAmtToServer = new JButton("Get Team Rankings and add total to Server"));
		
		jtxfDBServerIPPortLocalDB.addFocusListener(new FocusListener()
		{	
			@Override
			public void focusLost(FocusEvent fe)
			{
				try
				{
					String inputIPPortToValidate = jtxfDBServerIPPortLocalDB.getText().toString().trim();
					for(int i=0;i<inputIPPortToValidate.length();i++)
					{
						char c = inputIPPortToValidate.charAt(i);
						if(!(Character.isDigit(c) || c=='.' || c==':' || c=='l' || c=='o' || c=='c' || c=='a' || c=='l' || c=='h' || c=='o' || c=='s' || c=='t'))
							throw new Exception("Invalid IP:Port entered, please re-enter");
					}
					if(!inputIPPortToValidate.split("[:]")[0].equals("localhost"))
					{
						Pattern IPRegexPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
						Matcher matcher = IPRegexPattern.matcher(inputIPPortToValidate.split("[:]")[0]);
						if(!matcher.matches())
							throw new Exception("Invalid IP address");
					}
					
					int portNum = Integer.parseInt(inputIPPortToValidate.split("[:]")[1].trim());
					if(!(portNum>=1024 && portNum<=65535))
						throw new Exception("Invalid port number");
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(UpdateMiniEventTotalToServerCricoMania.this, "Invalid IP:Port entry, re-enter!", "Error", JOptionPane.ERROR_MESSAGE);
					jtxfDBServerIPPortLocalDB.requestFocus();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				//Nothing to code here
			}
		});
		
		jbtnSetDB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				DBSet = true;
				
				jtxfDBNameLocalDB.setEnabled(false);
				jtxfDBPasswordLocalDB.setEnabled(false);
				jtxfDBServerIPPortLocalDB.setEnabled(false);
				jtxfDBUsernameLocalDB.setEnabled(false);
				jtxfDBVendorLocalDB.setEnabled(false);
				
				jtxfDBNameWebServerDB.setEnabled(false);
				jtxfDBPasswordWebServerDB.setEnabled(false);
				jtxfDBServerIPPortWebServerDB.setEnabled(false);
				jtxfDBUsernameWebServerDB.setEnabled(false);
				jtxfDBVendorWebServerDB.setEnabled(false);
			}
		});
		
		jtxfDBNameLocalDB.setToolTipText("DB Name");
		jtxfDBVendorLocalDB.setToolTipText("Enter DB Vendor Name specific to jdbc connection URL");
		jtxfDBServerIPPortLocalDB.setToolTipText("Enter DBServerIP:Port at which DBMS is listening for remote connections, note: format in complaince with jdbc connection URL");
		
		jbtnAddMiniEventTotalAmtToServer.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(DBSet)
				{
					try
					{
						Class.forName("com.mysql.jdbc.Driver");
						if(conn==null)
						{
							conn = DriverManager.getConnection("jdbc:"+jtxfDBVendorLocalDB.getText().toString().trim()+"://"+jtxfDBServerIPPortLocalDB.getText().toString().trim()+"/"+jtxfDBNameLocalDB.getText().toString().trim(), jtxfDBUsernameLocalDB.getText().toString().trim(), jtxfDBPasswordLocalDB.getText().toString().trim());
							stmt = conn.createStatement();
						}
						rs = stmt.executeQuery("select team_id, sum(mini_event_amt_won) as mini_event_total_amt_won from mini_events_score_log group by team_id order by mini_event_total_amt_won desc");
						StringBuffer strBuffer = new StringBuffer("Team Rankings: ");
						strBuffer.append(System.getProperty("line.separator"));
						strBuffer.append("TeamID => Total Won Uptil Now").append(System.getProperty("line.separator"));
						perTeamTotal = new LinkedHashMap<Integer, Double>();
						double num;
						while(rs.next())
						{
							int teamID;
							num = rs.getDouble("mini_event_total_amt_won");
							getNumberInWords.setNumber((long)num);
							strWriter.getBuffer().setLength(0);
							pw.format("%f", num);
							strBuffer.append((teamID = rs.getInt("team_id")) + " => " + strWriter.getBuffer().toString() + "(" + getNumberInWords.getNumberInWords() + ")").append(System.getProperty("line.separator"));
							perTeamTotal.put(teamID, num);
						}
						rs.close();
						stmt.close();
						JTextArea jtxtareaTemp = new JTextArea(strBuffer.toString());
						jtxtareaTemp.setEditable(false);
						if(JOptionPane.showConfirmDialog(UpdateMiniEventTotalToServerCricoMania.this, jtxtareaTemp, "Team Rankings, Sure to add values to server", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
						{
							conn.close();
							conn = null;
							addMiniEventTotalPerTeamToWebServer();
						}
					}catch(Exception e)
					{
						e.printStackTrace();
						AndroidLikeToast.makeText(UpdateMiniEventTotalToServerCricoMania.this, "An error occured while getting team rankings", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					}
				}else 
					JOptionPane.showMessageDialog(UpdateMiniEventTotalToServerCricoMania.this, "Set DB 1st", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	protected void addMiniEventTotalPerTeamToWebServer()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			if(conn==null)
			{
				conn = DriverManager.getConnection("jdbc:"+jtxfDBVendorWebServerDB.getText().toString().trim()+"://"+jtxfDBServerIPPortWebServerDB.getText().toString().trim()+"/"+jtxfDBNameWebServerDB.getText().toString().trim(), jtxfDBUsernameWebServerDB.getText().toString().trim(), jtxfDBPasswordWebServerDB.getText().toString().trim());
				stmt = conn.createStatement();
				ps = conn.prepareStatement("update cricomania_teams set team_balance = team_balance + ? where team_id = ?");
			}
			int cnter = 0;
			for(Map.Entry<Integer, Double> entry:perTeamTotal.entrySet())
			{
				ps.setDouble(1, entry.getValue());
				ps.setInt(2, entry.getKey());
				if(ps.executeUpdate()==1)
				{
					System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] Team Balance Update for team_id: "+entry.getKey()+" Success!");
					cnter++;
				}
			}
			if(cnter==perTeamTotal.size())
				AndroidLikeToast.makeText(UpdateMiniEventTotalToServerCricoMania.this, "Successfully added total to cricomania's per team balance", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();				
		}catch(Exception e)
		{
			e.printStackTrace();
			AndroidLikeToast.makeText(UpdateMiniEventTotalToServerCricoMania.this, "An error occured while getting team rankings", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
		}
	}
	public static void main(String args[])
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()							//make GUI on event dispatching thread
			{
					public void run()
					{
						try
						{
							UIManager.setLookAndFeel(new NimbusLookAndFeel());
						}catch (UnsupportedLookAndFeelException e)
						{
							e.printStackTrace();
						}
						new UpdateMiniEventTotalToServerCricoMania();
					}
			});
		}catch(InterruptedException intre)
		{
			intre.printStackTrace();
		}catch(InvocationTargetException invocationTargetExcep)
		{
			invocationTargetExcep.printStackTrace();
		}
	}
}