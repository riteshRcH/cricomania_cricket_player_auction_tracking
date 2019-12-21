package org.csiVesit.csiVesitExperience.miniEventsScorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.csiVesit.csiVesitExperience.miniEventsScorer.AndroidLikeToast.Style;

@SuppressWarnings("serial")
public class MiniEventsScorerServer extends JFrame
{
	final JButton jbtnStartServer, jbtnShowServerConfig, jbtnSetDB, jbtnGetTeamRanking, jbtnSetMiniEventNames, jbtnCreateXMLOfMiniEventsAndTotalTeamCount;
	
	ClientConnListeningThread t = new ClientConnListeningThread();
	
	JPanel jpRuntimeServerHandlingOperations = new JPanel(new FlowLayout(FlowLayout.CENTER)), jpDBHandlingOperations = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JTabbedPane jtpOperations = new JTabbedPane();
	
	JTextField jtxfDBVendor, jtxfDBServerIPPort, jtxfDBName, jtxfTotalNumTeams, jtxfDBUsername, jtxfDBPassword;
	
	public static SimpleDateFormat logEventTimestampFormat = new SimpleDateFormat("dd MMM(MM) yyyy EEE hh:mm:ss:SSS a");
	final static int SERVER_LISTENING_PORT;
	
	public static File serverDataDir = new File("."+File.separator+"DATA"), serverDataDirSavedMiniEventNames = new File(serverDataDir, "savedMiniEventNames.txt");
	
	ArrayList<String> miniEventNames = new ArrayList<String>();
	int totalNumTeams;
	
	boolean createdXMLAtleastOnceForThisSession = false, DBSet = false;
	
	Connection conn;
	Statement stmt;
	ResultSet rs;
	
	GetNumberInWords getNumberInWords = new GetNumberInWords();
	StringWriter strWriter;
	PrintWriter pw = new PrintWriter(strWriter = new StringWriter());
	
	static
	{
		SERVER_LISTENING_PORT = 1234;
	}
	
	MiniEventsScorerServer()
	{
		super.setLayout(new BorderLayout());
		this.setTitle("Mini Events Scorer Server! v1.0");
		this.setVisible(true);
		this.setSize(1150, 256);
		this.setLocationRelativeTo(null);														//center locn
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);									//dispose all UI windows
		
		File temp[] = {	serverDataDir };
		for(File f:temp)
			if(!f.exists())
				f.mkdirs();
		
		jpRuntimeServerHandlingOperations.setSize(200, 500);
		
		jpRuntimeServerHandlingOperations.add(jbtnStartServer = new JButton("Start Server!"));
		jpRuntimeServerHandlingOperations.add(jbtnShowServerConfig = new JButton("View Server Config"));
		jpRuntimeServerHandlingOperations.add(jbtnGetTeamRanking = new JButton("Get Team Rankings"));
		
		jpDBHandlingOperations.add(new JLabel("jdbc:"));
		jpDBHandlingOperations.add(jtxfDBVendor = new JTextField("mysql", 10));
		jpDBHandlingOperations.add(new JLabel("://"));
		jpDBHandlingOperations.add(jtxfDBServerIPPort = new JTextField("localhost:3306", 20));
		jpDBHandlingOperations.add(new JLabel("/"));
		jpDBHandlingOperations.add(jtxfDBName = new JTextField("csi_mini_events_scorer_db", 20));
		jpDBHandlingOperations.add(jtxfDBUsername = new JTextField("root", 10));
		jpDBHandlingOperations.add(jtxfDBPassword = new JTextField("cooperHawk!1259", 20));
		jpDBHandlingOperations.add(new JLabel("Note: DB Server IP:Port, DB Vendor and DBName will be fixed and disabled once Operations Executor is created"));
		jpDBHandlingOperations.add(jbtnSetDB = new JButton("Set DB"));
		jpRuntimeServerHandlingOperations.add(jbtnCreateXMLOfMiniEventsAndTotalTeamCount = new JButton("Create XML of Mini Events and TeamCount"));
		
		jtxfDBServerIPPort.addFocusListener(new FocusListener()
		{	
			@Override
			public void focusLost(FocusEvent fe)
			{
				try
				{
					String inputIPPortToValidate = jtxfDBServerIPPort.getText().toString().trim();
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
					JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Invalid IP:Port entry, re-enter!", "Error", JOptionPane.ERROR_MESSAGE);
					jtxfDBServerIPPort.requestFocus();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				//Nothing to code here
			}
		});
		
		jpRuntimeServerHandlingOperations.add(jtxfTotalNumTeams = new JTextField("Enter Total number of teams participating for this session", 32));
		jtxfTotalNumTeams.addFocusListener(new FocusListener()
		{	
			@Override
			public void focusLost(FocusEvent arg0)
			{
				try
				{
					int temp = Integer.parseInt(jtxfTotalNumTeams.getText().toString().trim());
					if(temp<=0)
						throw new Exception("Invalid Team Count");
					else
						totalNumTeams = temp;
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Invalid team count because of: " + e.getMessage()+", Re-enter", "Error", JOptionPane.ERROR_MESSAGE);
					jtxfTotalNumTeams.requestFocus();
				}
			}
			
			@Override
			public void focusGained(FocusEvent arg0)
			{
				if(jtxfTotalNumTeams.getText().trim().equals("Enter Total number of teams participating for this session"))
					jtxfTotalNumTeams.setText("");
			}
		});
		jpRuntimeServerHandlingOperations.add(jbtnSetMiniEventNames = new JButton("Set mini-event names"));
		
		jtxfDBName.setToolTipText("DB Name");
		jtxfDBVendor.setToolTipText("Enter DB Vendor Name specific to jdbc connection URL");
		jtxfDBServerIPPort.setToolTipText("Enter DBServerIP:Port at which DBMS is listening for remote connections, note: format in complaince with jdbc connection URL");
		
		jtpOperations.add("Server", jpRuntimeServerHandlingOperations);
		jtpOperations.add("DB", jpDBHandlingOperations);
		
		this.add(jtpOperations, BorderLayout.CENTER);
		
		ButtonListener bl = new ButtonListener();
		jbtnStartServer.addActionListener(bl);
		jbtnShowServerConfig.addActionListener(bl);
		jbtnGetTeamRanking.addActionListener(bl);
		jbtnSetDB.addActionListener(bl);
		jbtnSetMiniEventNames.addActionListener(bl);
		jbtnCreateXMLOfMiniEventsAndTotalTeamCount.addActionListener(bl);
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer ] Client Connected Sockets were: "+(((t.clientConnectedSockets.size())==0)?"none":t.clientConnectedSockets));
				t.serverRunning = false;
				try
				{
					if(t.serverSocket!=null)
						t.serverSocket.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer ] Cya soon! :)");
				System.gc();									//perform garbage collection
				dispose();										//dispose off UI window
			}
		});
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
						new MiniEventsScorerServer();
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
	class GetMiniEventsNamesJDialog extends JDialog implements ActionListener
	{
		JButton jbtnSet, jbtnCancel, jbtnLoadSavedSessionsMiniEventNames, jbtnSaveCurrentSessionsMiniEventNames;
		JTextArea jtxtareaMiniEventNames;
		int loadedFromFileMiniEventNamesCount;
		
		public GetMiniEventsNamesJDialog(JFrame parent, String title)
		{
			super(parent, title, true);		//true => modal dialog
			this.setSize(new Dimension(400, 512));
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setLayout(new FlowLayout(FlowLayout.CENTER));
			this.setLocationRelativeTo(null);
			
			add(new JLabel("Enter the names of the mini events, 1 mini-event name per line"));
			add(jtxtareaMiniEventNames = new JTextArea(20, 20));
			add(jbtnSet = new JButton("Set mini-event names"));
			add(jbtnCancel = new JButton("Cancel"));
			add(jbtnLoadSavedSessionsMiniEventNames = new JButton("Load mini-event names of a saved session"));
			add(jbtnSaveCurrentSessionsMiniEventNames = new JButton("Save current session's mini-event names"));
			
			jbtnCancel.addActionListener(GetMiniEventsNamesJDialog.this);
			jbtnLoadSavedSessionsMiniEventNames.addActionListener(GetMiniEventsNamesJDialog.this);
			jbtnSaveCurrentSessionsMiniEventNames.addActionListener(GetMiniEventsNamesJDialog.this);
			jbtnSet.addActionListener(GetMiniEventsNamesJDialog.this);
	
			jtxtareaMiniEventNames.setText("");
			for(String miniEventName:miniEventNames)
			{
				jtxtareaMiniEventNames.append(miniEventName);
				jtxtareaMiniEventNames.append(System.getProperty("line.separator"));
			}
			this.setVisible(true);
		}
		public void actionPerformed(ActionEvent ae)
		{
			JButton jbtnEventSrc = (JButton) ae.getSource();
			if(jbtnEventSrc.equals(jbtnCancel))
			{
				GetMiniEventsNamesJDialog.this.dispose();
			}else if(jbtnEventSrc.equals(jbtnSet))
			{
				miniEventNames.clear();
				String[] miniEvents = jtxtareaMiniEventNames.getText().toString().trim().split("["+System.getProperty("line.separator")+"]");
				for(int i=0;i<miniEvents.length;i++)
				{
					miniEvents[i] = miniEvents[i].trim();
					if(miniEvents[i]!=null && !miniEvents[i].equals(""))
						if(!miniEventNames.contains(miniEvents[i]))
							miniEventNames.add(miniEvents[i]);
				}
				GetMiniEventsNamesJDialog.this.dispose();
			}else if(jbtnEventSrc.equals(jbtnLoadSavedSessionsMiniEventNames))
			{
				if(serverDataDirSavedMiniEventNames.exists())
				{
					miniEventNames.clear();
					BufferedReader br;
					try
					{
						br = new BufferedReader(new FileReader(serverDataDirSavedMiniEventNames));
						String temp = new String();
						while((temp = br.readLine())!=null)
							if(!(temp=temp.trim()).equals(""))
								miniEventNames.add(temp);
						br.close();
						
						jtxtareaMiniEventNames.setText("");
						for(String miniEvent:miniEventNames)
						{
							jtxtareaMiniEventNames.append(miniEvent);
							jtxtareaMiniEventNames.append(System.getProperty("line.separator", "\n"));
						}
						
						AndroidLikeToast.makeText(MiniEventsScorerServer.this, "Successfully loaded "+(loadedFromFileMiniEventNamesCount = miniEventNames.size())+" saved mini-event Names!", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
					}catch(Exception e)
					{
						e.printStackTrace();
						AndroidLikeToast.makeText(MiniEventsScorerServer.this, "An error occured reading from saved data, retry!", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					}
				}else
					AndroidLikeToast.makeText(MiniEventsScorerServer.this, "No saved mini-event names found!", AndroidLikeToast.LENGTH_SHORT, Style.NORMAL).display();
			}else if(jbtnEventSrc.equals(jbtnSaveCurrentSessionsMiniEventNames))
			{
				if(miniEventNames.size()==0 && (jtxtareaMiniEventNames.getText().toString().trim().length()==0 || jtxtareaMiniEventNames.getText().toString().equals("")))
					AndroidLikeToast.makeText(MiniEventsScorerServer.this, "Enter atleast 1 mini-event name and then save!", AndroidLikeToast.LENGTH_SHORT, Style.NORMAL).display();
				else
				{
					ArrayList<String> temp = new ArrayList<String>();
					boolean saveUsingTemp = false;
					if(jtxtareaMiniEventNames.getText().toString().trim().length()>0)
					{
						String[] miniEvents = jtxtareaMiniEventNames.getText().toString().trim().split("["+System.getProperty("line.separator")+"]");
						for(int i=0;i<miniEvents.length;i++)
						{
							miniEvents[i] = miniEvents[i].trim();
							if(miniEvents[i]!=null && !miniEvents[i].equals(""))
								if(!temp.contains(miniEvents[i]))
									temp.add(miniEvents[i]);
						}
						saveUsingTemp = true;
					}
					
					if(miniEventNames.size()>0)
					{
						boolean tempAndMiniEventNamesAreDifferent = false;
						if(temp.size()!=miniEventNames.size())
							tempAndMiniEventNamesAreDifferent = true;
						else
						{
							Collections.sort(miniEventNames);
							Collections.sort(temp);
							tempAndMiniEventNamesAreDifferent = miniEventNames.equals(temp);
						}
						if(tempAndMiniEventNamesAreDifferent)
							saveUsingTemp = JOptionPane.showConfirmDialog(MiniEventsScorerServer.this, "Save OLD only?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.NO_OPTION;
						else
							saveUsingTemp = false;			//doesnt matter, save from any1
					}
					
					try
					{
						if(!serverDataDirSavedMiniEventNames.exists())
							serverDataDirSavedMiniEventNames.createNewFile();
						PrintWriter pw = new PrintWriter(new FileWriter(serverDataDirSavedMiniEventNames));
						if(temp!=null && temp.size()>0)
							Collections.sort(temp);
						if(miniEventNames!=null && miniEventNames.size()>0)
							Collections.sort(miniEventNames);
						for(String miniEvent:saveUsingTemp?temp:miniEventNames)
							pw.println(miniEvent);
						pw.close();
						AndroidLikeToast.makeText(MiniEventsScorerServer.this, "Successfully saved mini-event Names!", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
					}catch(Exception e)
					{
						e.printStackTrace();
						AndroidLikeToast.makeText(MiniEventsScorerServer.this, "An error occured reading from saved data, retry!", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					}
				}
			}
		}
	}
	class ButtonListener implements ActionListener
	{	
		public void actionPerformed(ActionEvent ae)
		{
			if(ae.getSource().equals(jbtnStartServer) && !t.serverRunning)
			{
				if(miniEventNames.size()>0)
				{
					if(totalNumTeams==0)
						JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Set the number of team for this session!", "Error", JOptionPane.ERROR_MESSAGE);
					else
					{
						if(DBSet)
						{
							if(createdXMLAtleastOnceForThisSession)
							{
								JTextArea tempJTextArea =new JTextArea("This sessions participating team Count: "+totalNumTeams+System.getProperty("line.separator")+System.getProperty("line.separator")+"Mini events for this session are as follows: "+System.getProperty("line.separator")+miniEventNames.toString().substring(1, miniEventNames.toString().length()-1).trim().replaceAll("[\\s+]", "").replace(",", System.getProperty("line.separator")));
								tempJTextArea.setEditable(false);
								if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(MiniEventsScorerServer.this, new JScrollPane(tempJTextArea), "Proceed Confirmation", JOptionPane.YES_NO_OPTION))
								{
									t.serverRunning = true;
									t.start();
									jbtnStartServer.setEnabled(false);
									jbtnSetMiniEventNames.setEnabled(false);
									jtxfTotalNumTeams.setEnabled(false);
								}
							}else
								JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "XML File must be created atleast once for this session!", "Error", JOptionPane.ERROR_MESSAGE);
						}else
							JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Set DB 1st!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}else
					JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Name of the Mini Events must be prepared 1st in order to start the server!", "Error", JOptionPane.ERROR_MESSAGE);
			}else if(ae.getSource().equals(jbtnShowServerConfig))
					JOptionPane.showMessageDialog(null, getCurrentServerConfiguration(), "Server Config", JOptionPane.INFORMATION_MESSAGE);
			else if(ae.getSource().equals(jbtnSetDB))
			{
				DBSet = true;
				
				jtxfDBServerIPPort.setEnabled(false);
				jtxfDBVendor.setEnabled(false);
				jtxfDBName.setEnabled(false);
				jtxfDBUsername.setEnabled(false);
				jtxfDBPassword.setEnabled(false);
				
				/*if(miniEventsScorerOperationExecutor==null)
					miniEventsScorerOperationExecutor = new org.csiVesit.csiVesitExperience.miniEventsScorer.DBHelper.MiniEventsScorerOperationExecutor(jtxfDBVendor.getText().toString().trim(), jtxfDBServerIPPort.getText().toString().trim(), jtxfDBName.getText().toString().trim(), jtxfDBUsername.getText().toString().trim(), jtxfDBPassword.getText().toString().trim());
				else
					miniEventsScorerOperationExecutor.toggleAcceptMiniEventsScorerOperations();
				
				jbtnSetDB.setText((jbtnSetDB.getText().startsWith("Start") || jbtnSetDB.getText().startsWith("Create"))?"Stop DBFeederMiniEventsScorer Operation Executor":"Start DBFeederMiniEventsScorer Operation Executor");*/				
			}else if(ae.getSource().equals(jbtnGetTeamRanking))
			{
				if(DBSet)
				{
					if(totalNumTeams>0)
					{
						try
						{
							Class.forName("com.mysql.jdbc.Driver");
							if(conn==null)
							{
								conn = DriverManager.getConnection("jdbc:"+jtxfDBVendor.getText().toString().trim()+"://"+jtxfDBServerIPPort.getText().toString().trim()+"/"+jtxfDBName.getText().toString().trim(), jtxfDBUsername.getText().toString().trim(), jtxfDBPassword.getText().toString().trim());
								stmt = conn.createStatement();
							}
							rs = stmt.executeQuery("select team_id, sum(mini_event_amt_won) as mini_event_total_amt_won from mini_events_score_log group by team_id order by mini_event_total_amt_won desc");
							StringBuffer strBuffer = new StringBuffer("Team Rankings: ");
							strBuffer.append(System.getProperty("line.separator"));
							strBuffer.append("TeamID => Total Won Uptil Now").append(System.getProperty("line.separator"));
							double num;
							while(rs.next())
							{
								num = rs.getDouble("mini_event_total_amt_won");
								getNumberInWords.setNumber((long)num);
								strWriter.getBuffer().setLength(0);
								pw.format("%f", num);
								strBuffer.append(rs.getInt("team_id") + " => " + strWriter.getBuffer().toString() + "(" + getNumberInWords.getNumberInWords() + ")").append(System.getProperty("line.separator"));
							}
							JTextArea jtxtareaTemp = new JTextArea(strBuffer.toString());
							jtxtareaTemp.setEditable(false);
							JOptionPane.showMessageDialog(MiniEventsScorerServer.this, jtxtareaTemp, "Team Rankings", JOptionPane.INFORMATION_MESSAGE);
						}catch(Exception e)
						{
							e.printStackTrace();
							AndroidLikeToast.makeText(MiniEventsScorerServer.this, "An error occured while getting team rankings", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
						}
					}else
						JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Set total num of teams 1st", "Error", JOptionPane.ERROR_MESSAGE);
				}else
					JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Set DB 1st", "Error", JOptionPane.ERROR_MESSAGE);
			}else if(ae.getSource().equals(jbtnSetMiniEventNames))
				new GetMiniEventsNamesJDialog(MiniEventsScorerServer.this, "Enter Name of Mini Events");
			else if(ae.getSource().equals(jbtnCreateXMLOfMiniEventsAndTotalTeamCount))
			{
				if(DBSet)
				{
					if(miniEventNames.size()>0)
					{
						if(totalNumTeams==0)
							JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Set the number of team for this session!", "Error", JOptionPane.ERROR_MESSAGE);
						else
						{
							serverDataDirSavedMiniEventNames = new File("MiniEventNamesAndTeamCount.xml");
							try
							{
								PrintWriter pw = new PrintWriter(serverDataDirSavedMiniEventNames);
								pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
								pw.println("<RootTag>");
								pw.println("<TeamCount>"+totalNumTeams+"</TeamCount>");
								for(String miniEventName:miniEventNames)
									pw.println("<MiniEventName>"+miniEventName+"</MiniEventName>");
								pw.println("</RootTag>");
								pw.close();
								createdXMLAtleastOnceForThisSession = true;
								AndroidLikeToast.makeText(MiniEventsScorerServer.this, "Successfuly created XML File", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
							}catch (FileNotFoundException e)
							{
								e.printStackTrace();
								AndroidLikeToast.makeText(MiniEventsScorerServer.this, "Error while creating XML File", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
							}
						}
					}else
						JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Name of the Mini Events must be prepared 1st in order to start the server!", "Error", JOptionPane.ERROR_MESSAGE);
				}else
					JOptionPane.showMessageDialog(MiniEventsScorerServer.this, "Set DB 1st!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	String getCurrentServerConfiguration()
	{
		String configStr = "";
		try
		{
			configStr += "This Machine's Pvt IP: \t\t"+InetAddress.getLocalHost().toString()+"\t"+System.getProperty("line.separator");
			configStr += "Is Server Running: \t\t"+t.serverRunning+"\t"+System.getProperty("line.separator");
			if(t.serverRunning)
			{
				configStr += "Server Running on: \t\t"+InetAddress.getLocalHost().toString()+"\t"+System.getProperty("line.separator");
				configStr += "Server listening (local) port: \t\t"+t.serverSocket.getLocalPort()+"\t"+System.getProperty("line.separator");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return configStr;
	}
	class ClientServicingThread extends Thread
	{
		Socket clientConnectedSocket;
		ObjectInputStream fromClientOIStrm;
		ObjectOutputStream toClientOOStrm;
		org.csiVesit.csiVesitExperience.miniEventsScorer.DBHelper.MiniEventsScorerOperationExecutor miniEventsScorerOperationExecutor = null;
		String forCouncilMemberName = "", myUniqueThreadName, queryToExecute;
		
		ClientServicingThread(Socket clientConnectedSocket, String ClientServicingThreadName)
		{
			super(ClientServicingThreadName);
			myUniqueThreadName = ClientServicingThreadName;
			this.clientConnectedSocket = clientConnectedSocket;
			try
			{
				fromClientOIStrm = new ObjectInputStream(clientConnectedSocket.getInputStream());
				toClientOOStrm = new ObjectOutputStream(clientConnectedSocket.getOutputStream());
				
				miniEventsScorerOperationExecutor = new org.csiVesit.csiVesitExperience.miniEventsScorer.DBHelper.MiniEventsScorerOperationExecutor(jtxfDBVendor.getText().toString().trim(), jtxfDBServerIPPort.getText().toString().trim(), jtxfDBName.getText().toString().trim(), jtxfDBUsername.getText().toString().trim(), jtxfDBPassword.getText().toString().trim());
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] "+ClientServicingThreadName+" has been started and a dedicated DB Connection established for remote Client: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort());
		}
		public void run()
		{
			try
			{
				while(true)
				{
					Object o = fromClientOIStrm.readObject();
					String clientRequest = (o instanceof String)?(String)o:null;
					if(clientRequest!=null)
					{
						if(clientRequest.equals("Client: Hello!"))
						{
							System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => Handshake request from: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" ....");
							toClientOOStrm.writeObject("Server: Hello!");
							toClientOOStrm.flush();
							System.out.println("Handshake success!");
						}else if(clientRequest.startsWith("REG_COUNCIL_MEMBER "))
						{
							//Format:	REG_COUNCIL_MEMBER name
							System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => Council Member Details from: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" ....");
							
							forCouncilMemberName = clientRequest.split("[\\s]")[1];
							
							toClientOOStrm.writeObject("REG_SUCCESS "+forCouncilMemberName);
							toClientOOStrm.flush();
							System.out.println("Reg_Success!");
						}else if(clientRequest.equals("GET_MINI_EVENT_NAMES_AND_TEAM_COUNT"))
						{
							System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => Mini-event names and team counts request from: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort());
							
							try
							{
								String md5;
								toClientOOStrm.writeObject("md5:"+(md5 = MiniEventsScorerServer.getMD5OfParam(serverDataDirSavedMiniEventNames)));			//send md5 to client to check if it needs updation of mini-Event Names or not
								toClientOOStrm.flush();
								System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Sent md5: "+md5);
								
								Object obj = fromClientOIStrm.readObject();
								boolean clientsNeedUpdation = obj instanceof Boolean && ((Boolean)obj).booleanValue();
								System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] updation needed: "+clientsNeedUpdation);
								if(clientsNeedUpdation)		//client will send back true if client needs updation(didnt match md5) else false
								{	
									byte[] byteArray = new byte[1024];
									FileInputStream fin = new FileInputStream(serverDataDirSavedMiniEventNames);
									int len;
									while((len=fin.read(byteArray))>0)
									{
										toClientOOStrm.writeObject(Arrays.copyOf(byteArray, len));
										toClientOOStrm.flush();
									}
									
									fin.close();
									
									md5 = MiniEventsScorerServer.getMD5OfParam(serverDataDirSavedMiniEventNames);
									
									toClientOOStrm.writeObject("FINISHED_SENDING_XML md5:"+md5);
									toClientOOStrm.flush();
									
									System.out.println("Sending XML File Success!");
								}
							}catch(Exception e)
							{
								toClientOOStrm.writeObject(e);
								toClientOOStrm.flush();
								System.out.println("Sending Failed!");
							}
						}
						else if(clientRequest.startsWith("MINI_EVENTS_SCORE_LOG_OPERATION~View LatestEntry~"))
						{
							System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => new view operation request from "+forCouncilMemberName+": "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort());
							String miniEventsScoreLogOperationComponents[] = clientRequest.split("[~]");
							//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName
							
							try
							{
								queryToExecute = "select * from mini_events_score_log where team_id="+miniEventsScoreLogOperationComponents[2]+" and mini_event_name='"+miniEventsScoreLogOperationComponents[3]+"' and timestamp_of_entry=(select max(timestamp_of_entry) as latestTSForTeamIDEventName from mini_events_score_log where team_id="+miniEventsScoreLogOperationComponents[2]+" and mini_event_name='"+miniEventsScoreLogOperationComponents[3]+"')";
								
								System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Execution of query("+queryToExecute+") to insert a new record initiated for "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" .. ");
								
								org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord desiredEventLogRecord = miniEventsScorerOperationExecutor.executeMiniEventsScorerOperation(myUniqueThreadName, queryToExecute, miniEventsScoreLogOperationComponents[1]);
								toClientOOStrm.writeObject(desiredEventLogRecord);
								toClientOOStrm.flush();
								
								System.out.println("Execution Success!");
							}catch(Exception e)
							{
								e.printStackTrace();
								toClientOOStrm.writeObject(e);
								toClientOOStrm.flush();
								System.out.println("Execution Failed!");
							}
							miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.remove(myUniqueThreadName);
						}else if(clientRequest.startsWith("MINI_EVENTS_SCORE_LOG_OPERATION~Delete LatestEntry~"))
						{
							System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => new delete latest entry request from "+forCouncilMemberName+": "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort());
							String miniEventsScoreLogOperationComponents[] = clientRequest.split("[~]");
							//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName
							
							try
							{
								queryToExecute = "select * from mini_events_score_log where team_id="+miniEventsScoreLogOperationComponents[2]+" and mini_event_name='"+miniEventsScoreLogOperationComponents[3]+"' and timestamp_of_entry=(select max(timestamp_of_entry) as latestTSForTeamIDEventName from mini_events_score_log where team_id="+miniEventsScoreLogOperationComponents[2]+" and mini_event_name='"+miniEventsScoreLogOperationComponents[3]+"')";
								
								System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Execution of query("+queryToExecute+") to select old record (i.e to be deleted) initiated for "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" .. ");
								
								org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord desiredEventLogRecord = miniEventsScorerOperationExecutor.executeMiniEventsScorerOperation(myUniqueThreadName, queryToExecute, miniEventsScoreLogOperationComponents[1]);
								toClientOOStrm.writeObject(desiredEventLogRecord);
								toClientOOStrm.flush();
								
								System.out.println("Execution Success .. Sent Intermediate Results .. Awating Confirmation!");
								
								/*****************************************************************************************************************/
								
								if(desiredEventLogRecord==null)				//there is a record to delete
									System.out.println("No record to delete");
								else
								{
									Boolean deleteConfirmation = (Boolean)fromClientOIStrm.readObject();
									
									if(deleteConfirmation)		//Yes, delete it = user confirmation
									{
										queryToExecute = "delete from mini_events_score_log where team_id="+desiredEventLogRecord.getTeamID()+" and mini_event_name='"+desiredEventLogRecord.getMiniEventName()+"' and entry_by_council_member_name='"+desiredEventLogRecord.getEntryByCouncilMemberName()+"' and entry_by_imei_num='"+desiredEventLogRecord.getEntryByIMEINum()+"' and timestamp_of_entry=(select latestTSForTeamIDEventName from (select max(timestamp_of_entry) as latestTSForTeamIDEventName from mini_events_score_log where team_id="+desiredEventLogRecord.getTeamID()+" and mini_event_name='"+desiredEventLogRecord.getMiniEventName()+"') as tempTable)";
										
										System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Execution of query("+queryToExecute+") to update a new record initiated for "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" .. ");
										
										org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord selectionAfterDeletionEventLogRecord = miniEventsScorerOperationExecutor.executeMiniEventsScorerOperation(myUniqueThreadName, queryToExecute, miniEventsScoreLogOperationComponents[1]);
										if(selectionAfterDeletionEventLogRecord==null && miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.get(myUniqueThreadName).endsWith(Boolean.toString(true)))
										{
											toClientOOStrm.writeObject("Deletion Success!");
											toClientOOStrm.flush();
											System.out.println("Deletion Success!");
										}else
										{
											toClientOOStrm.writeObject("Deletion Failed!");
											toClientOOStrm.flush();
											System.out.println("Deletion Failed!");
										}
									}else
										System.out.println("User canceled Deletion operation!");
								}
							}catch(Exception e)
							{
								e.printStackTrace();
								toClientOOStrm.writeObject(e);
								toClientOOStrm.flush();
								System.out.println("Execution Failed!");
							}
							miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.remove(myUniqueThreadName);
						}else if(clientRequest.startsWith("MINI_EVENTS_SCORE_LOG_OPERATION~Insert LatestEntry~"))
						{
							System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => new mini-event score Record entry from: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort());
							String miniEventsScoreLogOperationComponents[] = clientRequest.split("[~]");
							//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName~Amt Won~Entry_by_council_member_name~entry_by_imei_num
							
							try
							{
								StringBuffer queryToExecuteStrBuffer = new StringBuffer("insert into mini_events_score_log(team_id, mini_event_name, mini_event_amt_won, entry_by_council_member_name, entry_by_imei_num) values(");
								queryToExecuteStrBuffer.append(miniEventsScoreLogOperationComponents[2]).append(", '").append(miniEventsScoreLogOperationComponents[3]).append("', ").append(miniEventsScoreLogOperationComponents[4]).append(", '").append(miniEventsScoreLogOperationComponents[5]).append("', '").append(miniEventsScoreLogOperationComponents[6]).append("')");
								queryToExecute = queryToExecuteStrBuffer.toString();
								
								System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Execution of query("+queryToExecute+") to insert a new record initiated for "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" .. ");
								
								org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord selectionAfterInsertionEventLogRecord = miniEventsScorerOperationExecutor.executeMiniEventsScorerOperation(myUniqueThreadName, queryToExecute, miniEventsScoreLogOperationComponents[1]);
								if(selectionAfterInsertionEventLogRecord==null && miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.get(myUniqueThreadName).endsWith(Boolean.toString(true)))	//checking for null return record as cant select after insert due to different timestamps
								{
									toClientOOStrm.writeObject("Insertion Success!");
									toClientOOStrm.flush();
									System.out.println("Insertion Success!");
								}else
								{
									toClientOOStrm.writeObject("Insertion Failed!");
									toClientOOStrm.flush();
									System.out.println("Insertion Failed!");
								}
							}catch(Exception e)
							{
								e.printStackTrace();
								toClientOOStrm.writeObject(e);
								toClientOOStrm.flush();
								System.out.println("Execution Failed!");
							}
							miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.remove(myUniqueThreadName);
						}else if(clientRequest.startsWith("MINI_EVENTS_SCORE_LOG_OPERATION~Update LatestEntry~"))
						{
							System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Message Received: \""+clientRequest+"\" => update latest's timestamps mini-event score Record entry from: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort());
							String miniEventsScoreLogOperationComponents[] = clientRequest.split("[~]");
							//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName~Amt Won~Entry_by_council_member_name~entry_by_imei_num
							
							try
							{
								queryToExecute = "select * from mini_events_score_log where team_id="+miniEventsScoreLogOperationComponents[2]+" and mini_event_name='"+miniEventsScoreLogOperationComponents[3]+"' and entry_by_council_member_name='"+miniEventsScoreLogOperationComponents[5]+"' and entry_by_imei_num='"+miniEventsScoreLogOperationComponents[6]+"' and timestamp_of_entry=(select max(timestamp_of_entry) as latestTSForTeamIDEventName from mini_events_score_log where team_id="+miniEventsScoreLogOperationComponents[2]+" and mini_event_name='"+miniEventsScoreLogOperationComponents[3]+"')";
								
								System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Execution of query("+queryToExecute+") to select old record (i.e to be updated) initiated for "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" .. ");
								
								org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord desiredEventLogRecord = miniEventsScorerOperationExecutor.executeMiniEventsScorerOperation(myUniqueThreadName, queryToExecute, miniEventsScoreLogOperationComponents[1]);
								toClientOOStrm.writeObject(desiredEventLogRecord);
								toClientOOStrm.flush();
								
								System.out.println("Execution Success .. Sent Intermediate Results .. Awating Confirmation!");
								
								/*****************************************************************************************************************/
								
								if(desiredEventLogRecord==null)
									System.out.println("No record to update");
								else
								{
									Boolean updateConfirmation = (Boolean)fromClientOIStrm.readObject();
									
									if(updateConfirmation)			//Yes, update it
									{
										queryToExecute = "update mini_events_score_log set mini_event_amt_won="+miniEventsScoreLogOperationComponents[4]+", timestamp_of_entry=CURRENT_TIMESTAMP, entry_by_council_member_name='"+miniEventsScoreLogOperationComponents[5]+"', entry_by_imei_num='"+miniEventsScoreLogOperationComponents[6]+"' where team_id="+desiredEventLogRecord.getTeamID()+" and mini_event_name='"+desiredEventLogRecord.getMiniEventName()+"' and timestamp_of_entry=(select latestTSForTeamIDEventName from (select max(timestamp_of_entry) as latestTSForTeamIDEventName from mini_events_score_log where team_id="+desiredEventLogRecord.getTeamID()+" and mini_event_name='"+desiredEventLogRecord.getMiniEventName()+"') as tempTable)";
										//Update latest timestamp entry's amt to new amt  for that team id and event name
										
										System.out.print("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Execution of query("+queryToExecute+") to update old record initiated for "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" .. ");
										
										org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord selectionAfterUpdateEventLogRecord = miniEventsScorerOperationExecutor.executeMiniEventsScorerOperation(myUniqueThreadName, queryToExecute, miniEventsScoreLogOperationComponents[1]);
										if(selectionAfterUpdateEventLogRecord==null && miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.get(myUniqueThreadName).endsWith(Boolean.toString(true)))	//checking for null return record as cant select after insert due to different timestamps
										{
											toClientOOStrm.writeObject("Update Success!");
											toClientOOStrm.flush();
											System.out.println("Update Success!");
										}else
										{
											toClientOOStrm.writeObject("Update Failed!");
											toClientOOStrm.flush();
											System.out.println("Update Failed!");
										}
									}else							//No , dont update it
										System.out.println("User Canceled Update Operation!");
								}
							}catch(Exception e)
							{
								e.printStackTrace();
								toClientOOStrm.writeObject(e);
								toClientOOStrm.flush();
								System.out.println("Execution Failed!");
							}
							miniEventsScorerOperationExecutor.currentSessionOperationConfirmations.remove(myUniqueThreadName);
						}else if(clientRequest.startsWith("LOGOUT_DONE_WITH_CURRENT_MINI_EVENTS_SESSION "))
						{
							toClientOOStrm.writeObject("OK! Stopping your servicing thread.");
							toClientOOStrm.flush();
							break;
						}
					}
				}
				clientConnectedSocket.close();
				toClientOOStrm.close();
				fromClientOIStrm.close();
				System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientServicingThread ] Remote Client's: "+clientConnectedSocket.getInetAddress()+":"+clientConnectedSocket.getPort()+" {for Council Member: "+forCouncilMemberName+"} socket closed (its "+this.getName()+" has been stopped) .. has finished!");
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Execution Failed!");
			}
		}
	}
	class ClientConnListeningThread extends Thread
	{
		volatile boolean serverRunning;					//avoid caching of true value and indicates other threads can change it too
		ServerSocket serverSocket;
		Socket clientConnectedSocket;
		ArrayList<Socket> clientConnectedSockets = new ArrayList<Socket>();
		int clientsConnectedSoFarCnter;
		
		public void run()
		{
			while(serverRunning)
			{
				try
				{
					if(serverSocket==null)
					{
						serverSocket = new ServerSocket(SERVER_LISTENING_PORT);
						serverSocket.setReuseAddress(true);
						System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientConnListeningThread ] Server started listening on port number: "+SERVER_LISTENING_PORT+" ....");
						System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientConnListeningThread ] Waiting for client requests .... ");
					}
					clientConnectedSocket = serverSocket.accept();
					clientConnectedSockets.add(clientConnectedSocket);
					new ClientServicingThread(clientConnectedSocket, "ClientServicingThread"+(++clientsConnectedSoFarCnter)).start();
				}catch(SocketException se)
				{
					if(se.getMessage().equals("socket closed"))
						System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - MiniEventsScorerServer.ClientConnListeningThread ] Connection Listening Server stopped!");
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	static public String getMD5OfParam(File serverDataZippedFile)
	{
		try
		{
			FileInputStream fin = new FileInputStream(serverDataZippedFile);
			MessageDigest mdEncoder = MessageDigest.getInstance("MD5");
			int len;
			byte[] byteArray = new byte[1024];
			while((len=fin.read(byteArray))>0)
				mdEncoder.update(byteArray, 0, len);
			String md5 = new BigInteger(1, mdEncoder.digest()).toString(16);
			fin.close();
			return md5;
			
		}catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}