package org.csiVesit.cricomania.feeder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.csiVesit.cricomania.feeder.AndroidLikeToast.Style;

public class MainFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 4385205301092956068L;
	
	JLabel jlblShowBoughtAtAmtInWords, jlblShowPerTeamStartingPriceInWords, jlblAboutDevs;
	JTextField jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance, jtxfGetTotalNumTeams, jtxfGetPerTeamStartingPrice;
	JButton jbtnInsertIntoServer, jbtnSetTotalNumTeams, jbtnGetTeamRanking, jbtnSetCurrentPoolAndPlayer, jbtnSetSelectedPlayerAvailability, jbtnAddAmountToParticularTeamsBalance, jbtnMulAmountToParticularTeamsBalance, jbtnSubAmountToParticularTeamsBalance, jbtnDivideAmountToParticularTeamsBalance, jbtnGetSelectedTeamsCurrentStats, jbtnGetCurrentPoolAndPlayer, jbtnSetDBAndInit, jbtnGetAllTeamsBalance, jbtnGetTeamEliminations, jbtnUndoPlayerPurchaseForSelectedTeam;
	JCheckBox jchkBoxIsAvailable, jchkboxShowDBLoginPassword;
	
	public static SimpleDateFormat logEventTimestampFormat = new SimpleDateFormat("dd MMM(MM) yyyy EEE hh:mm:ss:SSS a");
	
	JComboBox<Integer> jcomboBoxChooseTeamID;
	JComboBox<String> jcomboBoxChooseBoughtPlayersPool, jcomboBoxChooseBoughtPlayer;
	JComboBox<String> jcomboBoxChooseCurrentPlayer, jcomboBoxChooseCurrentPool;
	
	JTabbedPane jtpOperations = new JTabbedPane();
	JPanel jpTabSetDBAndInit = new JPanel(new FlowLayout(FlowLayout.CENTER)), jpTabRuntimeAuctionOperation = new JPanel(new FlowLayout(FlowLayout.CENTER)), jpTabRuntimSetCurrentPoolPlayer = new JPanel(new FlowLayout()), jpTabShowTeamRanking = new JPanel(new FlowLayout());
	
	JTextField jtxfDBVendor, jtxfDBServerIPPort, jtxfDBName, jtxfTotalNumTeams, jtxfDBUsername;
	JPasswordField jpwFieldDBPassword;
	
	JPanel jpGetTeamPlayerInfo = new JPanel(new GridLayout(0, 2));
	
	JPanel jpAmtOperations = new JPanel(new BorderLayout());
	JPanel jpAmtModGrid = new JPanel(new GridLayout(0, 2));
	GetNumberInWords getNumberInWords = new GetNumberInWords();
	JButton jbtnClearAmount;
	JButton jbtnMulAmtBy10, jbtnMulAmtBy100, jbtnMulAmtBy1k, jbtnMulAmtBy10k, jbtnMulAmtBy1Lac, jbtnMulAmtBy10Lac, jbtnMulAmtBy1Cr, jbtnMulAmtBy5Cr, jbtnMulAmtBy10Cr;
	JButton jbtnAddToAmt10, jbtnAddToAmt100, jbtnAddToAmt1k, jbtnAddToAmt10k, jbtnAddToAmt1Lac, jbtnAddToAmt10Lac, jbtnAddToAmt1Cr, jbtnAddToAmt5Cr, jbtnAddToAmt10Cr;
	double playerBoughtAtAmt, perTeamStartingPrice = 200000000;
	boolean enableShowAmtInWordsDueToTextFieldModifications = true;
	
	int totalNumTeams;
	
	//String DBConnURL = "jdbc:mysql://MySQL5.brinkster.com:3306/mihirsathe", DBLoginUsername = "mihirsathe", DBLoginPassword = "keepitupCSI13";
	//String DBConnURL = "jdbc:mysql://localhost:3306/csi_cricomania", DBLoginUsername = "root", DBLoginPassword = "cooperHawk!1259";
	Connection conn;
	Statement stmt;
	PreparedStatement ps, psUpdateBalanceAfterPlayerPurchase;
	ResultSet rs;
	ArrayList<CricoManiaPlayer> allPlayers = new ArrayList<CricoManiaPlayer>();
	
	SimpleDateFormat userDisplayTimestampFormat = new SimpleDateFormat("dd MMM yyyy EEE hh:mm:ss a", Locale.ENGLISH);
	SimpleDateFormat DBStorageTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	
	boolean DBSetAndInitSuccess = false;
	
	MainFrame()
	{
		/**********************************************************UI Code Starts here**********************************************************/
		
		super.setLayout(new BorderLayout());
		this.setTitle("CSI VESIT CricoMania Feeder! v1.0");
		this.setSize(1200, 550);
		this.setLocationRelativeTo(null);														//center locn
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);									//dispose all UI windows
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent we)
			{
				jpwFieldDBPassword.requestFocus();
			}
			public void windowClosing(WindowEvent we)
			{
				if(JOptionPane.showConfirmDialog(MainFrame.this, "You sure you want to quit?", "Confirmation", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
				{
					try
					{
						if(rs!=null)
							rs.close();
						if(stmt!=null)
							stmt.close();
						if(conn!=null)
						{
							conn.close();
							System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] Closed DB Connecction GraceFully!");
						}
						
						System.gc();
						System.exit(0);
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		
		JPanel temp = new JPanel(new GridLayout(0, 1, 10, 10));
		temp.add(new JLabel("DB Connection URL"));
		JPanel temp1 = new JPanel(new FlowLayout());
		temp1.add(new JLabel("jdbc:"));
		temp1.add(jtxfDBVendor = new JTextField("mysql", 10));
		jtxfDBVendor.setToolTipText("DB Vendor name");
		temp1.add(new JLabel("://"));
		temp1.add(jtxfDBServerIPPort = new JTextField("localhost:3306", 20));
		jtxfDBServerIPPort.setToolTipText("DB Server IP:Port");
		temp1.add(new JLabel("/"));
		temp1.add(jtxfDBName = new JTextField("csi_cricomania", 20));
		jtxfDBName.setToolTipText("DB Name");
		JPanel temp2 = new JPanel(new GridLayout(0, 2));
		temp2.add(new JLabel("DB Login Username: "));
		temp2.add(jtxfDBUsername = new JTextField("CSICricomaniaDBA", 10));
		jtxfDBUsername.setToolTipText("Default Username: CSICricomaniaDBA");
		temp2.add(new JLabel("DB Login Password: "));
		temp2.add(jpwFieldDBPassword = new JPasswordField(20));
		jpwFieldDBPassword.setToolTipText("Default Password: keepitupCSIVESIT");
		jpwFieldDBPassword.setEchoChar('#');
		temp2.add(new JLabel());
		temp2.add(jchkboxShowDBLoginPassword = new JCheckBox("Show Password"));
		jchkboxShowDBLoginPassword.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent ie)
			{
				if(ie.getStateChange()==ItemEvent.DESELECTED)
					jpwFieldDBPassword.setEchoChar('#');
				else
					jpwFieldDBPassword.setEchoChar((char)0);
			}
		});
		jpTabSetDBAndInit.add(temp);
		temp.add(temp1);
		temp.add(temp2);
		temp.add(jbtnSetDBAndInit = new JButton("Set DB and Init"));
		jbtnSetDBAndInit.addActionListener(this);
		String s = "";
		for(int i=0;i<arrChars.length;i++)
			s += (char)Integer.parseInt(arrChars[i], 16);
		temp.add(jlblAboutDevs = new JLabel(s, SwingConstants.CENTER));
		jpTabSetDBAndInit.add(temp);
		
		jpGetTeamPlayerInfo.add(new JLabel("Per Team Starting Price in words: "));
		jpGetTeamPlayerInfo.add(jlblShowPerTeamStartingPriceInWords = new JLabel("Enter Per Team Starting Price 1st"));
		jpGetTeamPlayerInfo.add(new JLabel("Enter Per Team Starting Price(used while init): "));
		jpGetTeamPlayerInfo.add(jtxfGetPerTeamStartingPrice = new JTextField(20));
		jtxfGetPerTeamStartingPrice.getDocument().addDocumentListener(new DocumentListener()
		{	
			@Override
			public void removeUpdate(DocumentEvent arg0)
			{
				updatePerTeamStartingPrice();
			}
			@Override
			public void insertUpdate(DocumentEvent arg0)
			{
				updatePerTeamStartingPrice();
			}
			@Override
			public void changedUpdate(DocumentEvent arg0)
			{
				updatePerTeamStartingPrice();
			}
			private void updatePerTeamStartingPrice()
			{
				try
				{
					double temp = Double.parseDouble(jtxfGetPerTeamStartingPrice.getText().toString().trim().replaceAll("[,+]", ""));
					if(temp<0)
						throw new Exception("Invalid per team starting price");
					else
					{
						perTeamStartingPrice = temp;
						getNumberInWords.setNumber((long)perTeamStartingPrice);
						jlblShowPerTeamStartingPriceInWords.setText(getNumberInWords.getNumberInWords());
					}
				}catch(Exception e)
				{
					AndroidLikeToast.makeText(MainFrame.this, "Invalid amt in per team starting price", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					jtxfGetPerTeamStartingPrice.requestFocus();
				}
			}
		});
		jtxfGetPerTeamStartingPrice.setText("200000000");
		jpGetTeamPlayerInfo.add(jtxfGetTotalNumTeams = new JTextField("Enter Total number of teams", 22));
		jtxfGetTotalNumTeams.addFocusListener(new FocusListener()
		{	
			@Override
			public void focusLost(FocusEvent arg0)
			{
				try
				{
					int temp = Integer.parseInt(jtxfGetTotalNumTeams.getText().toString().trim());
					if(temp<=0)
						throw new Exception("Invalid Team Count");
				}catch(Exception e)
				{
					AndroidLikeToast.makeText(MainFrame.this, "Invalid team count because of: " + e.getMessage()+", Re-enter", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					jtxfGetTotalNumTeams.requestFocus();
				}
			}
			@Override
			public void focusGained(FocusEvent arg0)
			{
				if(jtxfGetTotalNumTeams.getText().trim().equals("Enter Total number of teams"))
					jtxfGetTotalNumTeams.setText("");
			}
		});
		jpGetTeamPlayerInfo.add(jbtnSetTotalNumTeams = new JButton("Set total num of teams"));
		jbtnSetTotalNumTeams.addActionListener(this);
		jpGetTeamPlayerInfo.add(new JLabel("Choose team ID: "));
		jpGetTeamPlayerInfo.add(jcomboBoxChooseTeamID = new JComboBox<Integer>());
		jpGetTeamPlayerInfo.add(new JLabel("Filter players by pool: "));
		jpGetTeamPlayerInfo.add(jcomboBoxChooseBoughtPlayersPool = new JComboBox<String>());
		jcomboBoxChooseBoughtPlayersPool.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				jcomboBoxChooseBoughtPlayer.removeAllItems();
				for(CricoManiaPlayer player:allPlayers)
					if(player.getPoolID()==Integer.parseInt(jcomboBoxChooseBoughtPlayersPool.getSelectedItem().toString().split("=>")[1].trim()))
						jcomboBoxChooseBoughtPlayer.addItem(player.getPlayerName()+" | ID => "+player.getPlayerID());
			}
		});
		jpGetTeamPlayerInfo.add(new JLabel("Choose player bought by team: "));
		jpGetTeamPlayerInfo.add(jcomboBoxChooseBoughtPlayer = new JComboBox<String>());
		jpGetTeamPlayerInfo.add(jbtnGetSelectedTeamsCurrentStats = new JButton("Get Selected Team's current stats"));
		jbtnGetSelectedTeamsCurrentStats.addActionListener(this);
		jbtnGetSelectedTeamsCurrentStats.setEnabled(false);
		jpGetTeamPlayerInfo.add(jbtnGetCurrentPoolAndPlayer = new JButton("Get Current Pool and Player"));
		jbtnGetCurrentPoolAndPlayer.addActionListener(this);
		jpGetTeamPlayerInfo.add(jbtnUndoPlayerPurchaseForSelectedTeam = new JButton("Undo Player purchases for selected team's ID"));
		jbtnUndoPlayerPurchaseForSelectedTeam.addActionListener(this);
		jbtnUndoPlayerPurchaseForSelectedTeam.setEnabled(false);
		
		jpTabRuntimeAuctionOperation.add(jpGetTeamPlayerInfo);
		
		temp = new JPanel(new GridLayout(0, 2));
		temp.add(new JLabel("Amt in words: "));
		temp.add(jlblShowBoughtAtAmtInWords = new JLabel("Enter amt 1st"));
		temp.add(new JLabel("Enter buying amount"));
		temp.add(jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance = new JTextField(20));
		jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getDocument().addDocumentListener(new DocumentListener()
		{	
			@Override
			public void removeUpdate(DocumentEvent arg0)
			{
				if(enableShowAmtInWordsDueToTextFieldModifications)
					updatePlayerBoughtAtAmt();
			}
			@Override
			public void insertUpdate(DocumentEvent arg0)
			{
				if(enableShowAmtInWordsDueToTextFieldModifications)
					updatePlayerBoughtAtAmt();
			}
			@Override
			public void changedUpdate(DocumentEvent arg0)
			{
				if(enableShowAmtInWordsDueToTextFieldModifications)
					updatePlayerBoughtAtAmt();
			}
			private void updatePlayerBoughtAtAmt()
			{
				try
				{
					double temp = Double.parseDouble(jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().trim().replaceAll("[,+]", ""));
					if(temp<0)
						throw new Exception("Invalid amt");
					else
					{
						playerBoughtAtAmt = temp;
						getNumberInWords.setNumber((long)playerBoughtAtAmt);
						jlblShowBoughtAtAmtInWords.setText(getNumberInWords.getNumberInWords());
					}
				}catch(Exception e)
				{
					AndroidLikeToast.makeText(MainFrame.this, "Invalid amt", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.requestFocus();
				}
			}
		});
		jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent arg0)
			{
				try
				{
					double temp = Double.parseDouble(jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().trim().replaceAll(",+", ""));
					if(temp<=0)
						throw new Exception("Invalid amt");
					else
					{
						playerBoughtAtAmt = temp;
						getNumberInWords.setNumber((long)playerBoughtAtAmt);
						jlblShowBoughtAtAmtInWords.setText(getNumberInWords.getNumberInWords());
					}
				}catch(Exception e)
				{
					AndroidLikeToast.makeText(MainFrame.this, "Invalid amt", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}
			}
			@Override
			public void focusGained(FocusEvent arg0)
			{
				//Nothing to code here
			}
		});
		jpAmtOperations.add(temp, BorderLayout.NORTH);
		
		jpAmtModGrid.add(new JLabel(""));										jpAmtModGrid.add(new JLabel(""));
		jpAmtModGrid.add(new JLabel("Click button to clear amount to Rs. 0"));	jpAmtModGrid.add(jbtnClearAmount = new JButton("Clear Amount"));
		jpAmtModGrid.add(jbtnAddToAmt10 = new JButton("+10"));					jpAmtModGrid.add(jbtnMulAmtBy10 = new JButton("x10"));
		jpAmtModGrid.add(jbtnAddToAmt100 = new JButton("+100"));				jpAmtModGrid.add(jbtnMulAmtBy100 = new JButton("x100"));
		jpAmtModGrid.add(jbtnAddToAmt1k = new JButton("+1 k"));					jpAmtModGrid.add(jbtnMulAmtBy1k = new JButton("x1k"));
		jpAmtModGrid.add(jbtnAddToAmt10k = new JButton("+10 k"));				jpAmtModGrid.add(jbtnMulAmtBy10k = new JButton("x10k"));
		jpAmtModGrid.add(jbtnAddToAmt1Lac = new JButton("+1 Lac"));				jpAmtModGrid.add(jbtnMulAmtBy1Lac = new JButton("x1Lac"));
		jpAmtModGrid.add(jbtnAddToAmt10Lac = new JButton("+10Lac"));			jpAmtModGrid.add(jbtnMulAmtBy10Lac = new JButton("x10Lac"));
		jpAmtModGrid.add(jbtnAddToAmt1Cr = new JButton("+1 Cr"));				jpAmtModGrid.add(jbtnMulAmtBy1Cr = new JButton("x1Cr"));
		jpAmtModGrid.add(jbtnAddToAmt5Cr = new JButton("+5 Cr"));				jpAmtModGrid.add(jbtnMulAmtBy5Cr = new JButton("x5Cr"));
		jpAmtModGrid.add(jbtnAddToAmt10Cr = new JButton("+10 Cr"));				jpAmtModGrid.add(jbtnMulAmtBy10Cr = new JButton("x10Cr"));
		jpAmtModGrid.add(new JLabel(""));										jpAmtModGrid.add(new JLabel(""));
		jpAmtModGrid.add(jbtnAddAmountToParticularTeamsBalance = new JButton("Add amount to teams balance"));
		jbtnAddAmountToParticularTeamsBalance.setEnabled(false);
		jpAmtModGrid.add(jbtnSubAmountToParticularTeamsBalance = new JButton("Sub amount from teams balance"));
		jbtnSubAmountToParticularTeamsBalance.setEnabled(false);
		jpAmtModGrid.add(jbtnMulAmountToParticularTeamsBalance = new JButton("Multiply amount to teams balance"));
		jbtnMulAmountToParticularTeamsBalance.setEnabled(false);
		jpAmtModGrid.add(jbtnDivideAmountToParticularTeamsBalance = new JButton("Divide teams balance by amount"));
		jbtnDivideAmountToParticularTeamsBalance.setEnabled(false);
		
		jpAmtOperations.add(jpAmtModGrid, BorderLayout.CENTER);
		jpTabRuntimeAuctionOperation.add(jpAmtOperations);
		
		AmtModificationListener amtModificationListener = new AmtModificationListener();
		
		jbtnClearAmount.addActionListener(amtModificationListener);
		
		jbtnAddToAmt10.addActionListener(amtModificationListener);
		jbtnAddToAmt100.addActionListener(amtModificationListener);
		jbtnAddToAmt1k.addActionListener(amtModificationListener);
		jbtnAddToAmt10k.addActionListener(amtModificationListener);
		jbtnAddToAmt1Lac.addActionListener(amtModificationListener);
		jbtnAddToAmt10Lac.addActionListener(amtModificationListener);
		jbtnAddToAmt1Cr.addActionListener(amtModificationListener);
		jbtnAddToAmt5Cr.addActionListener(amtModificationListener);
		jbtnAddToAmt10Cr.addActionListener(amtModificationListener);
		
		jbtnMulAmtBy10.addActionListener(amtModificationListener);
		jbtnMulAmtBy100.addActionListener(amtModificationListener);
		jbtnMulAmtBy1k.addActionListener(amtModificationListener);
		jbtnMulAmtBy10k.addActionListener(amtModificationListener);
		jbtnMulAmtBy1Lac.addActionListener(amtModificationListener);
		jbtnMulAmtBy10Lac.addActionListener(amtModificationListener);
		jbtnMulAmtBy1Cr.addActionListener(amtModificationListener);
		jbtnMulAmtBy5Cr.addActionListener(amtModificationListener);
		jbtnMulAmtBy10Cr.addActionListener(amtModificationListener);
		
		jbtnAddAmountToParticularTeamsBalance.addActionListener(this);
		jbtnMulAmountToParticularTeamsBalance.addActionListener(this);
		jbtnDivideAmountToParticularTeamsBalance.addActionListener(this);
		jbtnSubAmountToParticularTeamsBalance.addActionListener(this);
		
		jbtnAddAmountToParticularTeamsBalance.setEnabled(false);
		jbtnSubAmountToParticularTeamsBalance.setEnabled(false);
		jbtnDivideAmountToParticularTeamsBalance.setEnabled(false);
		jbtnMulAmountToParticularTeamsBalance.setEnabled(false);
		
		jpTabRuntimeAuctionOperation.add(jbtnInsertIntoServer = new JButton("Insert into Server"));
		jbtnInsertIntoServer.addActionListener(this);
		jbtnInsertIntoServer.setEnabled(false);
		
		jpTabShowTeamRanking.add(jbtnGetTeamRanking = new JButton("Get Team Ranking"));
		jbtnGetTeamRanking.setEnabled(false);
		jbtnGetTeamRanking.addActionListener(this);
		jpTabShowTeamRanking.add(jbtnGetAllTeamsBalance = new JButton("Get Balance of all teams"));
		jbtnGetAllTeamsBalance.setEnabled(false);
		jbtnGetAllTeamsBalance.addActionListener(this);
		jpTabShowTeamRanking.add(jbtnGetTeamEliminations = new JButton("Get Eliminated Teams (Experimental/Buggy)"));
		jbtnGetTeamEliminations.setEnabled(false);
		jbtnGetTeamEliminations.addActionListener(this);
		
		jpTabRuntimSetCurrentPoolPlayer.add(jcomboBoxChooseCurrentPool = new JComboBox<String>());
		jcomboBoxChooseCurrentPool.addActionListener(new ActionListener()				//item selection changed => update jcomboBox of players accordingly
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				jcomboBoxChooseCurrentPlayer.removeAllItems();
				for(CricoManiaPlayer player:allPlayers)
					if(player.getPoolID()==Integer.parseInt(jcomboBoxChooseCurrentPool.getSelectedItem().toString().split("=>")[0].trim()))
						jcomboBoxChooseCurrentPlayer.addItem(player.getPlayerID() + " => " + player.getPlayerName());
			}
		});
		jpTabRuntimSetCurrentPoolPlayer.add(jcomboBoxChooseCurrentPlayer = new JComboBox<String>());
		jpTabRuntimSetCurrentPoolPlayer.add(jbtnSetCurrentPoolAndPlayer = new JButton("Set current pool and player"));
		jpTabRuntimSetCurrentPoolPlayer.add(jchkBoxIsAvailable = new JCheckBox("Is Available", true));
		jpTabRuntimSetCurrentPoolPlayer.add(jbtnSetSelectedPlayerAvailability = new JButton("Set selected player availability"));
		jbtnSetSelectedPlayerAvailability.addActionListener(this);
		jbtnSetCurrentPoolAndPlayer.addActionListener(this);
		
		jtpOperations.add("Set DB and Init", jpTabSetDBAndInit);
		jtpOperations.add("Auction", jpTabRuntimeAuctionOperation);
		jtpOperations.add("Set Current Pool, Player", jpTabRuntimSetCurrentPoolPlayer);
		jtpOperations.add("Get Team Ranking according to Total Brand Value", jpTabShowTeamRanking);
		jtpOperations.setSelectedIndex(0);
		jtpOperations.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent ce)
			{
				if(!DBSetAndInitSuccess)
				{
					JTabbedPane sourceTabbedPane = (JTabbedPane) ce.getSource();
					if(sourceTabbedPane.getSelectedIndex()>0)
					{
						AndroidLikeToast.makeText(MainFrame.this, "DB must be set and Init must be performed BEFORE changing to other tabs").display();
						sourceTabbedPane.setSelectedIndex(0);
					}
				}
			}
		});		
		this.add(jtpOperations, BorderLayout.CENTER);
		
		/**********************************************************UI Code Finished here**********************************************************/
		this.setVisible(true);
	}
	String arrChars[] = "003c~0068~0074~006d~006c~003e~003c~0066~006f~006e~0074~0020~0066~0061~0063~0065~003d~0022~0050~0061~006c~0061~0074~0069~006e~006f~0022~0020~0073~0069~007a~0065~003d~0022~0034~0022~003e~003c~0062~003e~0044~0065~0076~0065~006c~006f~0070~0065~0072~0073~003a~003c~0062~0072~0020~002f~003e~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0020~0052~0069~0074~0065~0073~0068~0020~0054~0061~006c~0072~0065~006a~0061~0020~0028~004d~006f~0062~003a~0020~0039~0039~0032~0030~0030~0037~0035~0039~0033~0039~0029~003c~0062~0072~0020~002f~003e~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0026~006e~0062~0073~0070~003b~0020~0052~006f~0068~0061~006e~0020~0054~006f~006e~0064~0075~006c~006b~0061~0072~0020~0028~004d~006f~0062~003a~0020~0039~0038~0036~0039~0036~0039~0033~0035~0035~0035~0029~003c~0062~0072~0020~002f~003e~0042~0061~0074~0063~0068~003a~0020~0032~0030~0031~0031~002d~0032~0030~0031~0035~002c~0020~0054~0045~0020~002d~0020~0043~004d~0050~004e~003c~002f~0062~003e~003c~002f~0066~006f~006e~0074~003e~003c~002f~0068~0074~006d~006c~003e".trim().split("~");
	void setDBAndinit()
	{
		/**********************************************************UI Elements Init from DB Code Starts here**********************************************************/
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:"+jtxfDBVendor.getText().toString().trim()+"://"+jtxfDBServerIPPort.getText().toString().trim()+"/"+jtxfDBName.getText().toString().trim(), jtxfDBUsername.getText().toString().trim(), new String(jpwFieldDBPassword.getPassword()).toString().trim());
			stmt = conn.createStatement();
			
			System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] DB Connection Success!");
			
			rs = stmt.executeQuery("select * from cricomania_pools order by pool_id");
			while(rs.next())
				jcomboBoxChooseBoughtPlayersPool.addItem(rs.getString("pool_name")+" | ID => "+rs.getInt("pool_id"));
			rs.close();
			
			rs = stmt.executeQuery("select * from cricomania_players");
			while(rs.next())
			{
				int playerID;
				String playerName;
				CricoManiaPlayer player;
				allPlayers.add(player = new CricoManiaPlayer(playerID = rs.getInt("player_id"), rs.getInt("pool_id"), rs.getInt("age"), rs.getInt("brand_value"), playerName = rs.getString("player_name"), rs.getString("country"), rs.getString("type_of_player"), rs.getString("photo_file_path"), rs.getString("is_available")=="yes", rs.getString("is_sold")=="yes", rs.getDouble("base_price")));
				if(player.getPoolID()==Integer.parseInt(jcomboBoxChooseBoughtPlayersPool.getSelectedItem().toString().split("=>")[1].trim()))
					jcomboBoxChooseBoughtPlayer.addItem(playerName+" | ID => "+playerID);
			}
			rs.close();
			
			ps = conn.prepareStatement("insert into cricomania_team_auction_details(team_id, bought_player_id, bought_in_amt, bought_in_amt_words) values(?, ?, ?, ?)");
			psUpdateBalanceAfterPlayerPurchase = conn.prepareStatement("update cricomania_teams set team_balance = team_balance - ? where team_id = ?");
			
			System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] Populated All Players for local use from DB Server!");
			
			rs = stmt.executeQuery("select * from cricomania_pools order by pool_id");
			while(rs.next())
				jcomboBoxChooseCurrentPool.addItem(rs.getInt("pool_id")+" => "+rs.getString("pool_name"));
			rs.close();
			
			System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] Populated All Pools for selection of current one!");
			AndroidLikeToast.makeText(MainFrame.this, "Successflly set the DB and init success!", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
			
			for(Component c:getAllComponents(jpTabSetDBAndInit))
				if(!c.equals(jlblAboutDevs))
					c.setEnabled(false);
			
			DBSetAndInitSuccess = true;
			jtpOperations.setSelectedIndex(1);
			jtxfGetTotalNumTeams.requestFocus();
		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(MainFrame.this, "Error while connecting to DB/Init from DB", "Error", JOptionPane.ERROR_MESSAGE);
		}
		System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] Init Success!");
		/**********************************************************UI Elements Init from DB Code Finished here**********************************************************/
	}
	
	public static List<Component> getAllComponents(final Container c)
	{
		//recursively gets all components of given Container c
	    Component[] comps = c.getComponents();
	    List<Component> compList = new ArrayList<Component>();
	    for (Component comp : comps)
	    {
	        compList.add(comp);
	        if (comp instanceof Container)
	            compList.addAll(getAllComponents((Container) comp));
	    }
	    return compList;
	}
	
	class AmtModificationListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			enableShowAmtInWordsDueToTextFieldModifications = false;
			
			String alreadyExistingText = jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().trim();
			JButton eventSrc = (JButton) ae.getSource();
			StringWriter strWriter;
			PrintWriter pw = new PrintWriter(strWriter = new StringWriter());
			
			if(playerBoughtAtAmt==0.0 && jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().trim().length()>0)
				playerBoughtAtAmt = Double.parseDouble(jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().trim().replaceAll("[,+]", ""));
			
			if(eventSrc.equals(jbtnAddToAmt10))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					playerBoughtAtAmt = 10;
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10");
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 10));
			}else if(eventSrc.equals(jbtnAddToAmt100))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("100");
					playerBoughtAtAmt = 100;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 100));
			}else if(eventSrc.equals(jbtnAddToAmt1k))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("1,000");
					playerBoughtAtAmt = 1000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 1000));
			}else if(eventSrc.equals(jbtnAddToAmt10k))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10,000");
					playerBoughtAtAmt = 10000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 10000));
			}else if(eventSrc.equals(jbtnAddToAmt1Lac))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("1,00,000");
					playerBoughtAtAmt = 100000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 100000));
			}else if(eventSrc.equals(jbtnAddToAmt10Lac))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10,00,000");
					playerBoughtAtAmt = 1000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 1000000));
			}else if(eventSrc.equals(jbtnAddToAmt1Cr))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("1,00,00,000");
					playerBoughtAtAmt = 10000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 10000000));
			}else if(eventSrc.equals(jbtnAddToAmt5Cr))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("5,00,00,000");
					playerBoughtAtAmt = 50000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 50000000));
			}else if(eventSrc.equals(jbtnAddToAmt10Cr))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10,00,00,000");
					playerBoughtAtAmt = 100000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt + 100000000));
			}else if(eventSrc.equals(jbtnMulAmtBy10))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10");
					playerBoughtAtAmt = 10;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 10));
			}else if(eventSrc.equals(jbtnMulAmtBy100))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("100");
					playerBoughtAtAmt = 100;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 100));
			}else if(eventSrc.equals(jbtnMulAmtBy1k))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("1,000");
					playerBoughtAtAmt = 1000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 1000));
			}else if(eventSrc.equals(jbtnMulAmtBy10k))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10,000");
					playerBoughtAtAmt = 10000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 10000));
			}else if(eventSrc.equals(jbtnMulAmtBy1Lac))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("1,00,000");
					playerBoughtAtAmt = 100000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 100000));
			}else if(eventSrc.equals(jbtnMulAmtBy10Lac))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10,00,000");
					playerBoughtAtAmt = 1000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 1000000));
			}else if(eventSrc.equals(jbtnMulAmtBy1Cr))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("1,00,00,000");
					playerBoughtAtAmt = 10000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 10000000));
			}else if(eventSrc.equals(jbtnMulAmtBy5Cr))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("5,00,00,000");
					playerBoughtAtAmt = 50000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 50000000));
			}else if(eventSrc.equals(jbtnMulAmtBy10Cr))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("10,00,00,000");
					playerBoughtAtAmt = 100000000;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = playerBoughtAtAmt * 100000000));
			}else if(eventSrc.equals(jbtnClearAmount))
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText("0");
					playerBoughtAtAmt = 0;
				}else
					jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(Double.toString(playerBoughtAtAmt = 0));
			}
			pw.format("%f", playerBoughtAtAmt);
			jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.setText(strWriter.getBuffer().toString());
			getNumberInWords.setNumber((long)playerBoughtAtAmt);
			jlblShowBoughtAtAmtInWords.setText(getNumberInWords.getNumberInWords());
			
			enableShowAmtInWordsDueToTextFieldModifications = true;
		}
	}
	
	public static void main(String[] args)
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
						new MainFrame();
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
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		JButton eventSrc = (JButton) ae.getSource();
		if(eventSrc.equals(jbtnSetTotalNumTeams))
		{
			try
			{
				if(jtxfGetPerTeamStartingPrice.getText().toString().trim().length()==0 || jtxfGetPerTeamStartingPrice.getText().toString().equals(""))
					AndroidLikeToast.makeText(MainFrame.this, "Set the per team starting price 1st", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				else
				{
					totalNumTeams = Integer.parseInt(jtxfGetTotalNumTeams.getText().toString().trim());
					jcomboBoxChooseTeamID.removeAllItems();
					for(int i=1;i<=totalNumTeams;i++)
						jcomboBoxChooseTeamID.addItem(i);
					
					//Inserting initial starting price per team in DB along with randomly generated password
					rs = stmt.executeQuery("select count(*) as total_rows from cricomania_teams");
					int currentNumRows = 0;
					while(rs.next())
						currentNumRows = rs.getInt("total_rows");
					rs.close();
					
					if(currentNumRows>0)
						AndroidLikeToast.makeText(MainFrame.this, "Already data present per team in DB, and team count set successfully", AndroidLikeToast.LENGTH_SHORT, Style.NORMAL).display();
					else					
					{
						if(stmt.executeUpdate("delete from cricomania_teams")>0 || currentNumRows==0)		// delete all old rows and put new ones, also put new ones when table is fully empty
						{
							int SuccessCnter = 0;
							for(int i=1;i<=totalNumTeams;i++)
								if(stmt.executeUpdate("insert into cricomania_teams(team_id, team_balance, password_to_view_own_team_stats) values("+i+", "+perTeamStartingPrice+", '"+new BigInteger(130, new java.security.SecureRandom()).toString(32).substring(0, 5)+"')")==1)
								{
									System.out.println("[ "+logEventTimestampFormat.format(System.currentTimeMillis())+" - org.csiVesit.cricomania.feeder.MainFrame ] Successfully inserted init data for teamID: "+i+"!");
									SuccessCnter++;
								}
							if(SuccessCnter==totalNumTeams)
								AndroidLikeToast.makeText(MainFrame.this, "Successfully team count set and wrote init data per team in DB!", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
						}
					}
					jbtnInsertIntoServer.setEnabled(true);
					jbtnAddAmountToParticularTeamsBalance.setEnabled(true);
					jbtnMulAmountToParticularTeamsBalance.setEnabled(true);
					jbtnDivideAmountToParticularTeamsBalance.setEnabled(true);
					jbtnSubAmountToParticularTeamsBalance.setEnabled(true);
					jbtnGetTeamRanking.setEnabled(true);
					jbtnGetSelectedTeamsCurrentStats.setEnabled(true);
					jbtnGetAllTeamsBalance.setEnabled(true);
					jbtnGetTeamEliminations.setEnabled(true);
					jbtnUndoPlayerPurchaseForSelectedTeam.setEnabled(true);
				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "Invalid team count", AndroidLikeToast.LENGTH_SHORT, Style.ERROR);
				jtxfGetTotalNumTeams.requestFocus();
			}
		}else if(eventSrc.equals(jbtnInsertIntoServer))
		{
			if(jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().length()==0 || jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString().equals(""))
				AndroidLikeToast.makeText(MainFrame.this, "Enter buying amount 1st", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			else
			{
				if(JOptionPane.showConfirmDialog(MainFrame.this, new JTextArea("Review information: "+System.getProperty("line.separator")+System.getProperty("line.separator")+"Team ID: "+jcomboBoxChooseTeamID.getSelectedItem().toString()+System.getProperty("line.separator")+" has bought: "+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString()+System.getProperty("line.separator")+" for Amount: "+playerBoughtAtAmt+"("+jlblShowBoughtAtAmtInWords.getText()+")"), "Review and Submit to server", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
				{
					try
					{
						rs = stmt.executeQuery("select is_available, is_sold from cricomania_players where player_id="+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split("=>")[1].trim());
						boolean isAvailable = false, isAlreadySold = false;
						while(rs.next())
						{
							isAvailable = rs.getString("is_available").equals("yes");
							isAlreadySold = rs.getString("is_sold").equals("yes");
						}
						rs.close();
						
						if(!isAvailable)
							AndroidLikeToast.makeText(MainFrame.this, "PlayerID: "+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split("=>")[1].trim()+", Player Name: "+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split(" ID ")[0].trim()+" isnt available for auction", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
						else
						{
							if(isAlreadySold)
							{
								rs = stmt.executeQuery("select team_id from cricomania_team_auction_details where bought_player_id = "+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split("=>")[1].trim());
								int soldToTeamID = 0; 
								while(rs.next())
									soldToTeamID = Integer.parseInt(rs.getString("team_id"));
								rs.close();
								
								AndroidLikeToast.makeText(MainFrame.this, "PlayerID: "+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split("=>")[1].trim()+", Player Name: "+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split(" ID ")[0].trim()+" has been already bought by teamID: "+soldToTeamID, AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
							}else
							{
								AndroidLikeToast.makeText(MainFrame.this, "Submit to server initiated ..", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
								
								rs = stmt.executeQuery("select team_balance from cricomania_teams where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString());
								double currentTeamBalance = 0;
								while(rs.next())
									currentTeamBalance = rs.getDouble("team_balance");
								rs.close();
								
								if(currentTeamBalance>=playerBoughtAtAmt)
								{
									ps.setInt(1, Integer.parseInt(jcomboBoxChooseTeamID.getSelectedItem().toString()));
									ps.setInt(2, Integer.parseInt(jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split("=>")[1].trim()));
									ps.setDouble(3, playerBoughtAtAmt);
									ps.setString(4, jlblShowBoughtAtAmtInWords.getText());
									
									if(ps.executeUpdate()==1)
									{
										psUpdateBalanceAfterPlayerPurchase.setDouble(1, playerBoughtAtAmt);
										psUpdateBalanceAfterPlayerPurchase.setInt(2, Integer.parseInt(jcomboBoxChooseTeamID.getSelectedItem().toString()));
										
										if(psUpdateBalanceAfterPlayerPurchase.executeUpdate()==1)
											if(stmt.executeUpdate("update cricomania_players set is_sold='yes' where player_id="+jcomboBoxChooseBoughtPlayer.getSelectedItem().toString().split("=>")[1].trim())==1)
												AndroidLikeToast.makeText(MainFrame.this, "Insertion success, subtracted purchase amount from balance accordingly and successfully set is_sold", AndroidLikeToast.LENGTH_LONG, Style.SUCCESS).display();
									}
								}else
									AndroidLikeToast.makeText(MainFrame.this, "Team has insufficient balance", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
						}
						}
					}catch (SQLException e)
					{
						e.printStackTrace();
						AndroidLikeToast.makeText(MainFrame.this, "An error occured while insertion into DB Server", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
					}
			}
			}
		}else if(eventSrc.equals(jbtnSetCurrentPoolAndPlayer))
		{
			try
			{
				int currentNumRows = 0;
				
				rs = stmt.executeQuery("select count(*) as total_rows from cricomania_current_pool_player");
				while(rs.next())
					currentNumRows = rs.getInt("total_rows");
				rs.close();
				
				if(currentNumRows==0)				//no row already present so insert it and reflect in main buying UI the current auction player 
				{
					if(stmt.executeUpdate("insert into cricomania_current_pool_player values("+jcomboBoxChooseCurrentPool.getSelectedItem().toString().split("=>")[0].trim()+", "+jcomboBoxChooseCurrentPlayer.getSelectedItem().toString().split("=>")[0].trim()+")")>0)
					{
						AndroidLikeToast.makeText(MainFrame.this, "Current pool and player set successfully", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
						updateCurrentPlayerAndPoolInUIJComboBoxes(Integer.parseInt(jcomboBoxChooseCurrentPool.getSelectedItem().toString().split("=>")[0].trim()), Integer.parseInt(jcomboBoxChooseCurrentPlayer.getSelectedItem().toString().split("=>")[0].trim()));
					}
				}else								//row already present so just update it and reflect in main buying UI the current auction player
				{
					if(stmt.executeUpdate("update cricomania_current_pool_player set pool_id="+jcomboBoxChooseCurrentPool.getSelectedItem().toString().split("=>")[0].trim()+", player_id="+jcomboBoxChooseCurrentPlayer.getSelectedItem().toString().split("=>")[0].trim())>0)
					{
						AndroidLikeToast.makeText(MainFrame.this, "Current pool and player set successfully", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
						updateCurrentPlayerAndPoolInUIJComboBoxes(Integer.parseInt(jcomboBoxChooseCurrentPool.getSelectedItem().toString().split("=>")[0].trim()), Integer.parseInt(jcomboBoxChooseCurrentPlayer.getSelectedItem().toString().split("=>")[0].trim()));
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "Error occured while setting current pool and player", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			}
		}else if(eventSrc.equals(jbtnSetSelectedPlayerAvailability))
		{
			try
			{
				if(stmt.executeUpdate("update cricomania_players set is_available='"+(jchkBoxIsAvailable.isSelected()?"yes":"no")+"' where player_id='"+jcomboBoxChooseCurrentPlayer.getSelectedItem().toString().split("=>")[0].trim()+"'")==1)
					AndroidLikeToast.makeText(MainFrame.this, "Current player: "+jcomboBoxChooseCurrentPlayer.getSelectedItem().toString()+" of pool: "+jcomboBoxChooseCurrentPool.getSelectedItem().toString()+" has been set as "+(jchkBoxIsAvailable.isSelected()?"":"un")+"available!").display();
			}catch(Exception e)
			{
				e.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "Exception while setting "+jcomboBoxChooseCurrentPlayer.getSelectedItem()+" as unavailable!", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			}
		}else if(eventSrc.equals(jbtnGetTeamRanking))
			new JDialogShowCurrentTeamRanking(MainFrame.this, "Team Ranking");
		else if(eventSrc.equals(jbtnGetTeamEliminations))
		{
			try
			{
				Vector<String> ColumnNames = new Vector<String>(); 
				ColumnNames.add("TeamID vs Pools/Pools Group");
				rs = stmt.executeQuery("select * from cricomania_elimination_criteria");
				final LinkedHashMap<ArrayList<Integer>, Integer> eliminationCritera = new LinkedHashMap<ArrayList<Integer>, Integer>();
				while(rs.next())
				{
					ArrayList<Integer> temp = new ArrayList<Integer>();
					String[] nums = rs.getString("pool_ids").replaceAll("[\\s+]", "").split("[,]");
					for(String s:nums)
					{
						int pool_id = Integer.parseInt(s);
						temp.add(pool_id);
					}
					eliminationCritera.put(temp, rs.getInt("min_must_be_bought_count"));
				}
				rs.close();

				for(Map.Entry<ArrayList<Integer>, Integer> entry:eliminationCritera.entrySet())
				{
					StringBuffer sb = new StringBuffer();
					for(int pool_id:entry.getKey())
					{
						rs = stmt.executeQuery("select * from cricomania_pools where pool_id="+pool_id);
						while(rs.next())
							sb.append(pool_id+"("+rs.getString("pool_name")+") + ");
					}
					ColumnNames.add(sb.substring(0, sb.length()-3)+" Min: "+entry.getValue());		//remove off last " + "
				}
				
				Vector<Vector<String>> data = new Vector<Vector<String>>();
				for(int i=1;i<=totalNumTeams;i++)
				{
					StringBuffer poolIDCondition = new StringBuffer("IN (");
					Vector<String> row = new Vector<String>();
					row.add(Integer.toString(i));
					for(Map.Entry<ArrayList<Integer>, Integer> entry:eliminationCritera.entrySet())
					{
						for(int pool_id:entry.getKey())
							poolIDCondition.append(pool_id+", ");
						//remove off last space and comma and put a closing round bracket of "IN" SQL clause in poolIDCondition
						rs = stmt.executeQuery("select count(*) as final_count from cricomania_players where player_id IN (select bought_player_id from cricomania_team_auction_details where team_id="+i+") and pool_id "+poolIDCondition.substring(0, poolIDCondition.length()-2).concat(")"));
						//get count of bought players of particular poolID/poolGroup for each team ID
						while(rs.next())
						{
							//String s;
							//System.out.println("TeamID: "+i+" has bought "+(s=rs.getObject("final_count").toString())+" players of poolID/poolGroup: "+poolIDCondition);
							row.add(rs.getObject("final_count").toString());
						}
						rs.close();
					}
					data.add(row);
				}
				
				JTable jtable = new JTable(data, ColumnNames);
				JScrollPane jscrollPane = new JScrollPane(jtable);
				jscrollPane.setPreferredSize(new Dimension(1200, 450));
				JOptionPane.showMessageDialog(MainFrame.this, jscrollPane, "Team Elimination Sheet", JOptionPane.INFORMATION_MESSAGE);
			}catch (SQLException e)
			{
				e.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "An error occured while retrieving eliminated teams", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			}
		}else if(eventSrc.equals(jbtnGetAllTeamsBalance))
		{
			try
			{
				StringBuffer strBuffer = new StringBuffer();
				StringWriter sw = new StringWriter();
				PrintWriter tempPw = new PrintWriter(sw);
				GetNumberInWords getNumberInWords = new GetNumberInWords();
				Vector<Vector<String>> data = new Vector<Vector<String>>();
				Vector<String> colNames = new Vector<String>();
				strBuffer.append("TEAM ID\tCURRENT BALANCE\tPassword to view stats").append(System.getProperty("line.separator"));
				rs = stmt.executeQuery("select * from cricomania_teams");
				ResultSetMetaData rsmd = rs.getMetaData();
				for(int i=1;i<=rsmd.getColumnCount();i++)
					colNames.add(rsmd.getColumnName(i));
				while(rs.next())
				{
					double balance = rs.getDouble("team_balance");
					getNumberInWords.setNumber((long)balance);
					tempPw.format("%f", balance);
					Vector<String> row = new Vector<String>();
					row.add(rs.getString("team_id"));
					row.add(sw.getBuffer().toString()+"("+getNumberInWords.getNumberInWords()+")");
					row.add(rs.getString("password_to_view_own_team_stats"));
					data.add(row);
					sw.getBuffer().setLength(0);
				}
				rs.close();
				
				JScrollPane jscrollPane = new JScrollPane(new JTable(data, colNames));
				jscrollPane.setPreferredSize(new Dimension(800, 450));
				JOptionPane.showMessageDialog(MainFrame.this, jscrollPane, "All teams Balance", JOptionPane.INFORMATION_MESSAGE);
			}catch (SQLException e)
			{
				e.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "An error occured while retrieving all team's balance", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			}
		}else if(eventSrc.equals(jbtnGetCurrentPoolAndPlayer))
		{
			try
			{
				rs = stmt.executeQuery("select * from cricomania_current_pool_player");
				int currentPoolID = 1, currentPlayerID = 1;
				while(rs.next())
				{
					currentPlayerID = rs.getInt("player_id");
					currentPoolID = rs.getInt("pool_id");
				}
				rs.close();
				
				updateCurrentPlayerAndPoolInUIJComboBoxes(currentPoolID, currentPlayerID);
				
				AndroidLikeToast.makeText(MainFrame.this, "Successfully retrieved current pool and player", AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
			}catch (SQLException e)
			{
				AndroidLikeToast.makeText(MainFrame.this, "There was an error while getting current pool and player .. retry", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				e.printStackTrace();
			}
		}else if(eventSrc.equals(jbtnAddAmountToParticularTeamsBalance))
		{
			if(JOptionPane.showConfirmDialog(MainFrame.this, "You sure you want to ADD: Rs. "+jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString()+"("+jlblShowBoughtAtAmtInWords.getText().toString()+") to Current Balance of TeamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString())==JOptionPane.YES_OPTION)
			{
				try
				{
					if(stmt.executeUpdate("update cricomania_teams set team_balance = team_balance + "+playerBoughtAtAmt+" where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString())==1)
						AndroidLikeToast.makeText(MainFrame.this, "Successfully ADDed amount to teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
					else
						AndroidLikeToast.makeText(MainFrame.this, "There was a error while ADDing amount to teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}catch(Exception e)
				{
					e.printStackTrace();
					AndroidLikeToast.makeText(MainFrame.this, "There was a error while ADDing amount to teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}
			}
		}else if(eventSrc.equals(jbtnSubAmountToParticularTeamsBalance))
		{
			if(JOptionPane.showConfirmDialog(MainFrame.this, "You sure you want to SUBTRACT: Rs. "+jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString()+"("+jlblShowBoughtAtAmtInWords.getText().toString()+") to Current Balance of TeamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString())==JOptionPane.YES_OPTION)
			{
				try
				{
					if(stmt.executeUpdate("update cricomania_teams set team_balance = team_balance - "+playerBoughtAtAmt+" where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString())==1)
						AndroidLikeToast.makeText(MainFrame.this, "Successfully SUBTRACTEDed amount for teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
					else
						AndroidLikeToast.makeText(MainFrame.this, "There was a error while SUBTRACTing amount for teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}catch(Exception e)
				{
					e.printStackTrace();
					AndroidLikeToast.makeText(MainFrame.this, "There was a error while SUBTRACTing amount for teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}
			}
		}else if(eventSrc.equals(jbtnDivideAmountToParticularTeamsBalance))
		{
			if(JOptionPane.showConfirmDialog(MainFrame.this, "You sure you want to DIVIDE: Rs. "+jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString()+"("+jlblShowBoughtAtAmtInWords.getText().toString()+") to Current Balance of TeamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString())==JOptionPane.YES_OPTION)
			{
				try
				{
					if(stmt.executeUpdate("update cricomania_teams set team_balance = team_balance / "+playerBoughtAtAmt+" where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString())==1)
						AndroidLikeToast.makeText(MainFrame.this, "Successfully DIVIDEDed amount of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
					else
						AndroidLikeToast.makeText(MainFrame.this, "There was a error while DIVIDEDing amount of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}catch(Exception e)
				{
					e.printStackTrace();
					AndroidLikeToast.makeText(MainFrame.this, "There was a error while DIVIDEDing amount of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}
			}
		}else if(eventSrc.equals(jbtnMulAmountToParticularTeamsBalance))
		{
			if(JOptionPane.showConfirmDialog(MainFrame.this, "You sure you want to Multiply: Rs. "+jtxfGetAmtAtWhichPlayerIsBoughtOrGetAmtToModInTeamBalance.getText().toString()+"("+jlblShowBoughtAtAmtInWords.getText().toString()+") to Current Balance of TeamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString())==JOptionPane.YES_OPTION)
			{
				try
				{
					if(stmt.executeUpdate("update cricomania_teams set team_balance = team_balance * "+playerBoughtAtAmt+" where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString())==1)
						AndroidLikeToast.makeText(MainFrame.this, "Successfully MULTIPLIED amount of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.SUCCESS).display();
					else
						AndroidLikeToast.makeText(MainFrame.this, "There was a error while MULTIPLYing amount of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}catch(Exception e)
				{
					e.printStackTrace();
					AndroidLikeToast.makeText(MainFrame.this, "There was a error while MULTIPLYing amount of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				}
			}
		}else if(eventSrc.equals(jbtnGetSelectedTeamsCurrentStats))
		{
			try
			{
				StringBuffer strBufferTeamStatsContent = new StringBuffer("Current Stats of TeamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString());
				strBufferTeamStatsContent.append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
				strBufferTeamStatsContent.append("Team Details: ").append(System.getProperty("line.separator"));

				StringWriter strWriter = new StringWriter();
				PrintWriter printWriterObj = new PrintWriter(strWriter);
				GetNumberInWords gniw = new GetNumberInWords();
				rs = stmt.executeQuery("select * from cricomania_teams where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString());
				while(rs.next())
				{
					double balance = rs.getDouble("team_balance");
					printWriterObj.format("%f", balance);
					gniw.setNumber((long)balance);
					strBufferTeamStatsContent.append("\tTeamID: "+rs.getInt("team_id")).append(System.getProperty("line.separator")).append("\tTeam's Current Balance: "+strWriter.getBuffer().toString()+"("+gniw.getNumberInWords()+")").append(System.getProperty("line.separator")).append("\tTeam Password to view own stats in Android App: "+rs.getString("password_to_view_own_team_stats")).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
					strWriter.getBuffer().setLength(0);
				}
				rs.close();
				
				strBufferTeamStatsContent.append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
				strBufferTeamStatsContent.append("Players bought by team ID: "+jcomboBoxChooseTeamID.getSelectedItem().toString()+" are as follows(LATEST to OLDEST purchase): ").append(System.getProperty("line.separator"));
				
				rs = stmt.executeQuery("select * from cricomania_team_auction_details where team_id="+jcomboBoxChooseTeamID.getSelectedItem().toString()+" order by bought_timestamp desc");
				StringWriter stringWriter;
				PrintWriter printWriter = new PrintWriter(stringWriter = new StringWriter());
				ResultSet rs1;
				Statement stmt1 = conn.createStatement();
				while(rs.next())
				{
					int playerID;
					strBufferTeamStatsContent.append("\t"+rs.getRow()+"] Player ID: "+(playerID = rs.getInt("bought_player_id"))).append(System.getProperty("line.separator"));
					
					int boughtPlayersPoolID = 1;
					rs1 = stmt1.executeQuery("select player_name, pool_id from cricomania_players where player_id="+playerID);
					while(rs1.next())
					{
						strBufferTeamStatsContent.append("\t Player Name: "+rs1.getString("player_name")).append(System.getProperty("line.separator"));
						boughtPlayersPoolID = rs1.getInt("pool_id");
					}
					rs1.close();
					
					rs1 = stmt1.executeQuery("select pool_name from cricomania_pools where pool_id="+boughtPlayersPoolID);
					while(rs1.next())
						strBufferTeamStatsContent.append("\t Player of Pool: "+rs1.getString("pool_name")).append(System.getProperty("line.separator"));
					rs1.close();
					
					printWriter.format("%f", rs.getDouble("bought_in_amt"));
					strBufferTeamStatsContent.append("\t Buying Amount: Rs. "+stringWriter.getBuffer().toString()).append(System.getProperty("line.separator"));
					stringWriter.getBuffer().setLength(0);
					strBufferTeamStatsContent.append("\t Buying Amount Words: Rs. "+rs.getString("bought_in_amt_words")).append(System.getProperty("line.separator"));
					strBufferTeamStatsContent.append("\t Bought Timestamp: "+userDisplayTimestampFormat.format(DBStorageTimestampFormat.parse(rs.getTimestamp("bought_timestamp").toString()))).append(System.getProperty("line.separator"));
					
					strBufferTeamStatsContent.append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
				}
				stringWriter.getBuffer().setLength(0);
				stringWriter = null;
				printWriter = null;
				rs.close();
				
				JTextArea jtxtarea = new JTextArea(strBufferTeamStatsContent.toString());
				jtxtarea.setEditable(false);
				JScrollPane jscrollPane = new JScrollPane(jtxtarea);
				jscrollPane.setPreferredSize(new Dimension(800, 400));
				JOptionPane.showMessageDialog(MainFrame.this, jscrollPane, "Current Stats of TeamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), JOptionPane.INFORMATION_MESSAGE);
			}catch(Exception e)
			{
				e.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "There was a error while retrieving current stats of teamID: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			}
		}else if(eventSrc.equals(jbtnSetDBAndInit))
		{
			if((jtxfDBName.getText().toString().trim().length()==0 || jpwFieldDBPassword.getPassword().length==0 || jtxfDBServerIPPort.getText().toString().trim().length()==0 || jtxfDBUsername.getText().toString().trim().length()==0 || jtxfDBVendor.getText().toString().trim().length()==0) || (jtxfDBName.getText().toString().trim().equals("") || new String(jpwFieldDBPassword.getPassword()).toString().trim().equals("") || jtxfDBServerIPPort.getText().toString().trim().equals("") || jtxfDBUsername.getText().toString().trim().equals("") || jtxfDBVendor.getText().toString().trim().equals("")))
				AndroidLikeToast.makeText(MainFrame.this, "None of the field can be left empty on this tab", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			else
				setDBAndinit();
		}else if(eventSrc.equals(jbtnUndoPlayerPurchaseForSelectedTeam))
		{
			try
			{
				JDialogShowUndoPlayerPurchaseForSelectedTeamID jdialogUndoPlayerPurchase = new JDialogShowUndoPlayerPurchaseForSelectedTeamID(MainFrame.this, Integer.parseInt(jcomboBoxChooseTeamID.getSelectedItem().toString()));
				jdialogUndoPlayerPurchase.setUndoUI();
				jdialogUndoPlayerPurchase.setVisible(true);
			}catch(Exception e)
			{
				AndroidLikeToast.makeText(MainFrame.this, "An exception occured while undo-ing purchase of players for team: "+jcomboBoxChooseTeamID.getSelectedItem().toString(), AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				e.printStackTrace();
			}
		}
	}
	void updateCurrentPlayerAndPoolInUIJComboBoxes(int currentPoolID, int currentPlayerID)
	{
		for(int i=0;i<jcomboBoxChooseBoughtPlayersPool.getItemCount();i++)
			if(jcomboBoxChooseBoughtPlayersPool.getItemAt(i).toString().contains("ID => "+currentPoolID))
			{
				jcomboBoxChooseBoughtPlayersPool.setSelectedIndex(i);
				break;
			}
		
		for(int i=0;i<jcomboBoxChooseBoughtPlayer.getItemCount();i++)
			if(jcomboBoxChooseBoughtPlayer.getItemAt(i).toString().contains("ID => "+currentPlayerID))
			{
				jcomboBoxChooseBoughtPlayer.setSelectedIndex(i);
				break;
			}
	}
	class JDialogShowUndoPlayerPurchaseForSelectedTeamID extends JDialog implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 662250327938238984L;
		int forTeamID = 0;
		Vector<Vector<String>> teamAuctionDetailsRows = new Vector<Vector<String>>();
		Vector<String> columnNames = new Vector<String>();
		JButton jbtnUndoActionCmd[];
		
		public JDialogShowUndoPlayerPurchaseForSelectedTeamID(JFrame parentFrame, Integer teamID)
		{
			super(parentFrame, "Undo Player Purchases for TeamID: "+teamID, true);				//Modal dialog
			this.setLayout(new BorderLayout());
			this.setSize(1100, 600);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(null);
			
			this.forTeamID = teamID;
		}
		void setUndoUI()throws Exception
		{
			rs = stmt.executeQuery("select * from cricomania_team_auction_details where team_id="+forTeamID+" order by bought_timestamp");
			ResultSetMetaData rsmd = rs.getMetaData();
			columnNames.add("Player purchase Transaction Number");
			columnNames.add("bought_player_name");
			for(int i=1;i<rsmd.getColumnCount();i++)
				columnNames.add(rsmd.getColumnName(i));
			ResultSet rs1;
			int index = 0;
			while(rs.next())
			{
				int bought_player_id = rs.getInt("bought_player_id");
				String bought_player_name = "";
				rs1 = conn.createStatement().executeQuery("select player_name from cricomania_players where player_id="+bought_player_id);
				while(rs1.next())
					bought_player_name = rs1.getString("player_name");
				rs1.close();
				
				Vector<String> row = new Vector<String>();			//ORDER of putting it is important as while UNDOing the same order is taken
				row.add(Integer.toString(++index));
				row.add(bought_player_name);
				row.add(Integer.toString(rs.getInt("team_id")));
				row.add(Integer.toString(rs.getInt("bought_player_id")));
				row.add(Double.toString(rs.getDouble("bought_in_amt")));
				row.add(rs.getString("bought_in_amt_words"));
				row.add(rs.getObject("bought_timestamp").toString());
				teamAuctionDetailsRows.add(row);
			}
			JScrollPane jscrollPane = new JScrollPane(new JTable(teamAuctionDetailsRows, columnNames));
			jscrollPane.setPreferredSize(new Dimension(1100, 450));
			this.add(jscrollPane, BorderLayout.CENTER);
			
			JPanel temp = new JPanel(new GridLayout(0, 4));
			jbtnUndoActionCmd = new JButton[index];
			for(int i=0;i<jbtnUndoActionCmd.length;i++)
			{
				temp.add(jbtnUndoActionCmd[i] = new JButton("Undo Purchase Transaction Number "+(i+1)));
				jbtnUndoActionCmd[i].setToolTipText("Undo Purchase Transaction Number "+(i+1));
				jbtnUndoActionCmd[i].addActionListener(JDialogShowUndoPlayerPurchaseForSelectedTeamID.this);
			}
			this.add(temp, BorderLayout.SOUTH);
		}
		@Override
		public void actionPerformed(ActionEvent ae)
		{
			int undoTransactionNumber = Integer.parseInt(ae.getActionCommand().replace("Undo Purchase Transaction Number", "").trim());
			try
			{
				Vector<String> rowOfTeamActionDetailsToUndo = teamAuctionDetailsRows.get(undoTransactionNumber-1);
				//taking string data and parsing it into apt data in order it was put inside vector above
				int bought_player_id_toUndo = Integer.parseInt(rowOfTeamActionDetailsToUndo.get(3));
				String bought_player_name_toUndo = rowOfTeamActionDetailsToUndo.get(1);
				double bought_in_amt_toUndo = Double.parseDouble(rowOfTeamActionDetailsToUndo.get(4));
				
				if(stmt.executeUpdate("update cricomania_players set is_sold='no' where player_id="+bought_player_id_toUndo)==1)
					if(stmt.executeUpdate("update cricomania_teams set team_balance=team_balance+"+bought_in_amt_toUndo+" where team_id="+forTeamID)==1)
						if(stmt.executeUpdate("delete from cricomania_team_auction_details where team_id="+forTeamID+" and bought_player_id="+bought_player_id_toUndo)==1)
						{
							GetNumberInWords gniw = new GetNumberInWords();
							gniw.setNumber((long)bought_in_amt_toUndo);
							AndroidLikeToast.makeText(MainFrame.this, "Successfully undone playerID: "+bought_player_id_toUndo+" and name: "+bought_player_name_toUndo+" purchase of teamID: "+forTeamID+", added back amount of: Rs. "+bought_in_amt_toUndo+" ("+gniw.getNumberInWords()+") and made is_sold=\"no\" AND deleted record from cricomania_team_action_details", AndroidLikeToast.LENGTH_LONG, Style.SUCCESS).display();
							JDialogShowUndoPlayerPurchaseForSelectedTeamID.this.dispose();
							JDialogShowUndoPlayerPurchaseForSelectedTeamID jdialog = new JDialogShowUndoPlayerPurchaseForSelectedTeamID(MainFrame.this, Integer.parseInt(jcomboBoxChooseTeamID.getSelectedItem().toString()));
							jdialog.setUndoUI();
							jdialog.setVisible(true);
						}
			}catch(Exception e)
			{
				AndroidLikeToast.makeText(MainFrame.this, "An exception occured, see console for details!", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
				e.printStackTrace();
			}
		}
	}
	class JDialogShowCurrentTeamRanking extends JDialog
	{
		private static final long serialVersionUID = 7326478912635767383L;

		public JDialogShowCurrentTeamRanking(JFrame parentFrame, String title)
		{
			super(parentFrame, title, true);				//Modal dialog
			this.setLayout(new FlowLayout());
			this.setTitle("Team Ranking according to sum(Brand Value)");
			this.setSize(200, 200);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(null);
			
				/*LinkedHashMap<Integer, Integer> perTeamBrandValueSum = new LinkedHashMap<Integer, Integer>();
				LinkedHashMap<Integer, ArrayList<Integer>> perTeamBoughtPlayerIDs = new LinkedHashMap<Integer, ArrayList<Integer>>();
				for(int i=0;i<totalNumTeams;i++)
				{
					int teamID = i+1;
					ArrayList<Integer> temp = new ArrayList<Integer>();
					rs = stmt.executeQuery("select team_id, bought_player_id, bought_in_amt from cricomania_team_auction_details where team_id="+teamID);
					while(rs.next())
						temp.add(rs.getInt("bought_player_id"));
					rs.close();
					perTeamBoughtPlayerIDs.put(teamID, temp);
				}
				LinkedHashMap<ArrayList<Integer>, Integer> maxBoughtPlayersConsiderationForBrandValueCalc = new LinkedHashMap<ArrayList<Integer>, Integer>();
				rs = stmt.executeQuery("select * from cricomania_pools_bought_consideration_limit");
				while(rs.next())
				{
					ArrayList<Integer> temp = new ArrayList<Integer>();
					String tempStr[] = rs.getString("pools_id").trim().replaceAll("\\s+", "").split(",");
					for(String s:tempStr)
						temp.add(Integer.parseInt(s));
					maxBoughtPlayersConsiderationForBrandValueCalc.put(temp, rs.getInt("num_players_bought_consideration_limit"));
				}*/
				/*									OLD CODE no consideration per pool
				 * Calculates addition of all brand values of all bought players, no custom consideration
				 * for(Map.Entry<Integer, ArrayList<Integer>> entry:perTeamBoughtPlayerIDs.entrySet())
				{
					int teamID = entry.getKey();
					perTeamBrandValueSum.put(teamID, 0);
					int sum = 0;
					for(Integer bought_player_id:entry.getValue())
					{
						rs = stmt.executeQuery("select brand_value from cricomania_players where player_id="+bought_player_id);
						while(rs.next())
							sum += rs.getInt("brand_value");
						perTeamBrandValueSum.put(teamID, sum);
						rs.close();
					}
				}*/
				/*for(Map.Entry<Integer, ArrayList<Integer>> entry:perTeamBoughtPlayerIDs.entrySet())
				{
					int teamID = entry.getKey();
					perTeamBrandValueSum.put(teamID, 0);
					int sum = 0;
					
					String INClause = "IN (";
					for(Integer bought_player_id:entry.getValue())
						INClause += (bought_player_id + ", ");
					INClause.substring(0, INClause.length()-2).concat(")");		//remove off last space and comma and put a brakcet
					
					for(Integer bought_player_id:entry.getValue())
					{
						rs = stmt.executeQuery("select brand_value from cricomania_players where player_id="+bought_player_id);
						while(rs.next())
							sum += rs.getInt("brand_value");
						perTeamBrandValueSum.put(teamID, sum);
						rs.close();
					}
				}
				System.out.println(maxBoughtPlayersConsiderationForBrandValueCalc);
				
				*
				*
				*					FOLLOWING CODE BY ROHAN TONDULKAR
				*
				*/
				
				int player_count=0;
				int []player_ids=new int[20];
				int [][]pool_n_brand=new int[20][20];
				int [] total_brand=new int[totalNumTeams+1];
				int []pool_limit=new int[6];
				int []type1=new int[10];
				int []type2=new int[10];
				int []type3=new int[10];
				int []type4=new int[10];
				int []type5=new int[10];
				int [][]final_total=new int [totalNumTeams+1][2];
				try
				{
					PreparedStatement ps1;
					ResultSet rs1;
					//get the limit for each type of players
					for(int j=1;j<=5;j++)
					{
						ps1=conn.prepareStatement("select * from cricomania_pool_limit where player_type='"+getTypeName(j)+"'");
						rs1=ps1.executeQuery();
						while(rs1.next())
						{
							pool_limit[j]=rs1.getInt("limit");
						}
					}
					for(int i=1;i<=totalNumTeams;i++)
					{	
						final_total[i][0]=i;
						final_total[i][1]=0;
						for(int j=0;j<10;j++)
						{
							type1[j]=0;
							type2[j]=0;
							type3[j]=0;
							type4[j]=0;
							type5[j]=0;
						}
						for(int j=0;j<20;j++)
						{
							player_ids[j]=0;
							pool_n_brand[j][0]=0;
							pool_n_brand[j][1]=0;
						}
						player_count=0;
						ps1=conn.prepareStatement("select bought_player_id from cricomania_team_auction_details where team_id="+i);
						rs1=ps1.executeQuery();
						rs1.last();
						player_count=rs1.getRow();
						rs1.beforeFirst();
						//Stores all the player ids of a team
						for(int j=0;j<player_count;j++)
						{
							rs1.next();
							player_ids[j+1]=rs1.getInt("bought_player_id");
						}
						rs1.close();
						//stores the pool_ids and brand_value of each player of a team
						for(int j=0;j<player_count;j++)
						{
							ps1=conn.prepareStatement("select pool_id, brand_value from cricomania_players where player_id="+player_ids[j+1]);
							rs1=ps1.executeQuery();
							rs1.next();
							pool_n_brand[j+1][0]=rs1.getInt("pool_id");
							pool_n_brand[j+1][1]=rs1.getInt("brand_value");
						}
						rs1.close();
						/*for(int j=1;j<=player_count;j++)
						{
							total_brand[i]+=pool_n_brand[j][1];
						}*/
						int temp1=0, temp2=0,temp3=0,temp4=0,temp5=0;
						//System.out.println(total_brand[i]);
						for(int j=1;j<=player_count;j++)
						{
							if(getTypeNumber(pool_n_brand[j][0])==1)
							{
								temp1++;
								type1[temp1]=pool_n_brand[j][1];
								//System.out.println(temp1+" "+i);
							}
							if(getTypeNumber(pool_n_brand[j][0])==2)
							{
								temp2++;
								type2[temp2]=pool_n_brand[j][1];
								//System.out.println(temp2+" "+i);
							}
							if(getTypeNumber(pool_n_brand[j][0])==3)
							{
								temp3++;
								type3[temp3]=pool_n_brand[j][1];
								//System.out.println(temp3+" "+i);
							}
							if(getTypeNumber(pool_n_brand[j][0])==4)
							{
								temp4++;
								type4[temp4]=pool_n_brand[j][1];
								//System.out.println(temp4+" "+i);
							}
							if(getTypeNumber(pool_n_brand[j][0])==5)
							{
								temp5++;
								type5[temp5]=pool_n_brand[j][1];
								//System.out.println(temp5+" "+i);
							}
							
						}
						if(temp1>pool_limit[1])
						{
							//System.out.println("More than expected bowlers");
							int temp_total=getMaxTotal(type1,pool_limit[1]);
							total_brand[i]+=temp_total;
							//System.out.println(temp_total);
						}
						else
						{
							total_brand[i]+=getTotal(type1);
						}
						if(temp2>pool_limit[2])
						{
							//System.out.println("More than expected batsmen");
							int temp_total=getMaxTotal(type2,pool_limit[2]);
							total_brand[i]+=temp_total;
							//System.out.println(temp_total);
						}
						else
						{
							total_brand[i]+=getTotal(type2);
						}
						if(temp3>pool_limit[3])
						{
							//System.out.println("More than expected wicketkeepers");
							int temp_total=getMaxTotal(type3,pool_limit[3]);
							total_brand[i]+=temp_total;
							//System.out.println(temp_total);
						}
						else
						{
							total_brand[i]+=getTotal(type3);
						}
						if(temp4>pool_limit[4])
						{
							//System.out.println("More than expected allrounders");
							int temp_total=getMaxTotal(type4,pool_limit[4]);
							total_brand[i]+=temp_total;
							//System.out.println(temp_total);
						}
						else
						{
							total_brand[i]+=getTotal(type4);
						}
						if(temp5>pool_limit[5])
						{
							//System.out.println("More than expected legends");
							int temp_total=getMaxTotal(type5,pool_limit[5]);
							total_brand[i]+=temp_total;
							//System.out.println(temp_total);
						}
						else
						{
							total_brand[i]+=getTotal(type5);
						}
						final_total[i][1]=total_brand[i];
						//System.out.println("Total brand Value of team "+i+" is :"+final_total[i][1]);
						
					}
			}catch (SQLException e)
			{
				e.printStackTrace();
				AndroidLikeToast.makeText(MainFrame.this, "An error occured while calculating team ranking", AndroidLikeToast.LENGTH_SHORT, Style.ERROR).display();
			}
			getResult(final_total);
			
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("RANK");
			columnNames.add("TEAM ID");
			columnNames.add("BRAND VALUE TOTAL");
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			for(int k=1;k<=totalNumTeams;k++)
			{
				Vector<String> row = new Vector<String>();
				row.add(Integer.toString(k));
				row.add(Integer.toString(final_total[k][0]));
				row.add(Integer.toString(final_total[k][1]));
				data.add(row);
			}
			
			JScrollPane jScrollPane = new JScrollPane(new JTable(data, columnNames));
			jScrollPane.setPreferredSize(new Dimension(400, 450));
			JOptionPane.showMessageDialog(MainFrame.this, jScrollPane, "Final Winners", JOptionPane.INFORMATION_MESSAGE);
			
			this.setVisible(false);
		}
		void getResult(int [][]a)
		{
			for (int i=1;i<totalNumTeams+1;i++)
			{
				for(int j=i+1;j<totalNumTeams+1;j++)
				{
					if(a[i][1]<a[j][1])
					{
						int t=a[i][1];
						a[i][1]=a[j][1];
						a[j][1]=t;
						int t1=a[i][0];
						a[i][0]=a[j][0];
						a[j][0]=t1;
					}
				}
			}
		}
		int getTotal(int []x)
		{
			int total=0;
			for(int i=0;i<x.length;i++)
			{
				total+=x[i];
			}
			return total;
		}
		
		int getMaxTotal(int[]a,int limit)
		{
			int total=0;
			for (int i=0;i<a.length;i++)
			{
				for(int j=i+1;j<a.length;j++)
				{
					if(a[i]<a[j])
					{
						int t=a[i];
						a[i]=a[j];
						a[j]=t;
					}
				}
			}
			for(int i=0;i<limit;i++)
			{
				total+=a[i];
			}
			return total;
		}
		String getTypeName(int i)
		{
			String type;
			switch(i)
			{
			case 1:type= "Bowlers";
				break;
			case 2: type= "Batsmen";
				break;
			case 3: type= "Wicketkeepers";
				break;
			case 4: type= "Allrounders";
				break;
			case 5: type= "Legends";
				break;
			default: type= "Invalid type";
			}
			return type;
		}
		
		int getTypeNumber(int poolNo)
		{
			int type=0;
			switch(poolNo)
			{
			case 1:type=2;
			break;
			case 2:type=1;
			break;
			case 3:type=1;
			break;
			case 4:type=3;
			break;
			case 5:type=4;
			break;
			case 6:type=2;
			break;
			case 7:type=5;
			break;
				default:break;
			}
			return type;
		}
	}
}