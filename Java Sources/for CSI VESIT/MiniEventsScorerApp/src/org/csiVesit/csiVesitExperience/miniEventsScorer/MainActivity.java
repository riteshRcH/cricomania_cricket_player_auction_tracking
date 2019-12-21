package org.csiVesit.csiVesitExperience.miniEventsScorer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
    private EditText editTextServerIP, editTextPW, editTextCouncilMemberName;
	private CheckBox chkBoxShowPassword;
	private Button btnEstablishConn, btnRegCouncilMember, btnStart;
	
	private Socket clientSocket;
	static ObjectOutputStream toServerOOStrm;
	static ObjectInputStream fromServerOIS;
	private boolean serverConnEstablished;
	protected String councilMemberName;
	
	static File appDir = new File(Environment.getExternalStorageDirectory(), ".CSI VESIT Council");
	static File appDirCricoManiaDir = new File(appDir, "CricoMania");

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		File files[] = { appDir, appDirCricoManiaDir };
		for(File f:files)
			if(!f.exists())
				f.mkdirs();
		
		/*************************************************************************************************************************************/
		
		initViews();
		
		/*************************************************************************************************************************************/
		
		InputFilter IpAddrRegexfilter = new InputFilter()
		{
			@Override
		    public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend) 
		    {
				if (end > start) 
		        {
					String destTxt = dest.toString();
		            String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
		            if(!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?"))
		            	return "";
		            else
		            {
		            	String[] splits = resultingTxt.split("\\.");
		                for(int i=0; i<splits.length; i++)
		                	if(Integer.valueOf(splits[i]) > 255)
		                		return "";
		            }
		        }
				return null;
			}
		};
		editTextServerIP.setFilters(new InputFilter[]{IpAddrRegexfilter});
		    
		/*************************************************************************************************************************************/
		
		chkBoxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{	

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				editTextPW.setTransformationMethod(isChecked?null:new PasswordTransformationMethod());
			}
		});
		
		/*************************************************************************************************************************************/
		    
	    btnEstablishConn.setOnClickListener(new View.OnClickListener()
	    {
			@Override
			public void onClick(View v) 
			{
				ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			    if (mWifi.isConnected())
			    {
			    	AsyncTask<Void, Void, Exception> bgTaskEstablishConnToServer = new AsyncTask<Void, Void, Exception>()
			    	{
			    		ProgressDialog progressDialog = null;
			    	    	       				
			    	    @Override
			    	    protected void onPreExecute()
			    	    {
			    	    	progressDialog = new ProgressDialog(MainActivity.this);
			    	    	progressDialog.setTitle("Processing ..");
			    	    	progressDialog.setMessage("Please wait.");
			    	    	progressDialog.setCancelable(false);
			    	    	progressDialog.setIndeterminate(true);
			    	    	progressDialog.show();
			    	    }
			    	    	    	    							
			    	    @Override
			    	    protected Exception doInBackground(Void ...params)
			    	    {
			    	    	try
			    	    	{
			    	    		clientSocket = new Socket();
			    	    		clientSocket.connect(new InetSocketAddress(editTextServerIP.getText().toString(), 1234), 2000);	//2secs connection timeout
			    	    		clientSocket.setSoTimeout(5000);			//5secs read timeout
			    	    		toServerOOStrm = new ObjectOutputStream(clientSocket.getOutputStream());
			    	    		fromServerOIS = new ObjectInputStream(clientSocket.getInputStream());
			    	    		toServerOOStrm.writeObject("Client: Hello!");
			    	    		toServerOOStrm.flush();
			    	    		Object serverReply = (String)fromServerOIS.readObject();
			    	    		if(serverReply!=null)
			    	    			serverConnEstablished = serverReply.equals("Server: Hello!");
			    	    	}catch (Exception e) 
			    	    	{
			    	    		return e;
			    	    	}
							return null;
			    	    }
			    	    
			    	    @Override
			    	    protected void onPostExecute(Exception exceptionOccured)
						{
							if(progressDialog!=null)
								progressDialog.dismiss();
							
							if(exceptionOccured==null)
							{
								if(serverConnEstablished)
								{
									Toast.makeText(getBaseContext(), "Server-Client Handshake successfull!", Toast.LENGTH_SHORT).show();
									btnEstablishConn.setEnabled(false);
		    	    				editTextServerIP.setEnabled(false);
		    	    				btnRegCouncilMember.setEnabled(true);
								}else
									Toast.makeText(getBaseContext(), "An Error occured establishing connection to server .. please re-try!", Toast.LENGTH_SHORT).show();
							}else if(exceptionOccured instanceof UnknownHostException)
								Toast.makeText(getBaseContext(), "Cant find server .. contact your class CSI co-ords!", Toast.LENGTH_SHORT).show();
							else if(exceptionOccured instanceof IOException)
								Toast.makeText(getBaseContext(), "Exception occured while connecting .. please retry.\nException Details: "+exceptionOccured.getMessage(), Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(getBaseContext(), "Exception occured while connecting .. please retry.\nException Details: "+exceptionOccured.getMessage(), Toast.LENGTH_LONG).show();
						}
			    	};
			    	bgTaskEstablishConnToServer.execute();
			    }else
			    	Toast.makeText(getBaseContext(), "Please connect to a CSI-VESIT hosted WiFi network!", Toast.LENGTH_SHORT).show();
			}
		});
		/*************************************************************************************************************************************/
	    btnRegCouncilMember.setOnClickListener(new View.OnClickListener()
	    {
			@Override
			public void onClick(View v)
			{
				if(editTextCouncilMemberName.getText().toString().trim().equals(""))
					Toast.makeText(getBaseContext(), "Please enter your name.", Toast.LENGTH_SHORT).show();
				else
					councilMemberName = editTextCouncilMemberName.getText().toString();
				
				if(councilMemberName!=null && !councilMemberName.trim().equals(""))
				{
					if(editTextPW.getText().toString().trim().equals(""))
						Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_SHORT).show();
					else if(editTextPW.getText().toString().equals("CSI-VESIT"))
					{
						ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
					    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					    
					    if(mWifi.isConnected())
					    {
					    	AsyncTask<Void, Void, Exception> bgTaskRegCouncilMember = new AsyncTask<Void, Void, Exception>()
							{
					    		ProgressDialog progressDialog = null;
							    	    	       				
							    @Override
							    protected void onPreExecute()
							    {
							    	progressDialog = new ProgressDialog(MainActivity.this);
							    	progressDialog.setTitle("Processing ..");
							    	progressDialog.setMessage("Please wait.");
							    	progressDialog.setCancelable(false);
							    	progressDialog.setIndeterminate(true);
							    	progressDialog.show();
							    }
							    	    	    	    							
							    @Override
							    protected Exception doInBackground(Void ...params)
							    {
							    	try
							    	{
							    		toServerOOStrm.writeObject("REG_COUNCIL_MEMBER "+editTextCouncilMemberName.getText().toString());
							    		toServerOOStrm.flush();
							    		String serverReply = (String)fromServerOIS.readObject();
							    		if(serverReply!=null)
							    			councilMemberName = serverReply.equals("REG_SUCCESS "+editTextCouncilMemberName.getText().toString())?editTextCouncilMemberName.getText().toString():"";
							    	}catch (Exception e) 
							    	{
							    		return e;
							    	}
									return null;
							    }
							    
							    @Override
					    	    protected void onPostExecute(Exception exceptionOccured)
								{
									if(progressDialog!=null)
										progressDialog.dismiss();
									
									if(exceptionOccured==null)
									{
										if(councilMemberName.equals(""))
											Toast.makeText(getBaseContext(), "Unable to register .. make sure its typed correctly!", Toast.LENGTH_SHORT).show();
										else
										{
											Toast.makeText(getBaseContext(), "Council Member: "+councilMemberName+" successfully registered at Server!", Toast.LENGTH_SHORT).show();
						    				btnRegCouncilMember.setEnabled(false);
						    				editTextPW.setEnabled(false);
						    				editTextCouncilMemberName.setEnabled(false);
						    				btnStart.setEnabled(true);
										}
									}else
										Toast.makeText(getBaseContext(), "Exception occured while connecting .. please retry.\nException Details: "+exceptionOccured.getMessage(), Toast.LENGTH_SHORT).show();
								}
							};
						bgTaskRegCouncilMember.execute();
					    }
					}else
						Toast.makeText(MainActivity.this, "Enter correct password", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    /*************************************************************************************************************************************/
	    btnStart.setOnClickListener(new View.OnClickListener()
	    {
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent("org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventScoresFeederActivity");
				
				Bundle extras = new Bundle();
				extras.putString("councilMemberName", editTextCouncilMemberName.getText().toString());
				i.putExtras(extras);
				
				startActivity(i);
			}
		});
    }

	private void initViews()
	{
		btnEstablishConn = (Button)findViewById(R.id.btnEstablishConn);
		btnRegCouncilMember = (Button)findViewById(R.id.btnReg);
		btnStart = (Button)findViewById(R.id.btnStart);
		
		editTextCouncilMemberName = (EditText)findViewById(R.id.EditTextCouncilMemberName);
		editTextPW = (EditText)findViewById(R.id.editTextPW);
		editTextServerIP = (EditText)findViewById(R.id.editTextServerIP);
		
		chkBoxShowPassword = (CheckBox)findViewById(R.id.chkBoxShowPassword);
	}
}
