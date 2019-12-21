package org.csiVesit.csiVesitExperience;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
	EditText editTextGetLocalWebServerIP, editTextGetTeamID, editTextGetPassword, editTextGetWebServerPortNumber; 
	CheckBox chkBoxUseDifferentLocalServer, chkBoxShowPassword;
	Button btnLogin, btnGotoNextActivity;
	static HttpClient httpclient = new DefaultHttpClient();
	
	int teamID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		setTitle("Cricomania Login");
		
		CookieHandler.setDefault(new CookieManager());
		
		/********************************************************************************************************/
		
		initViews();
		
		/********************************************************************************************************/
		
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
		editTextGetLocalWebServerIP.setFilters(new InputFilter[]{IpAddrRegexfilter});
		
		chkBoxUseDifferentLocalServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{	
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked)
			{
				editTextGetLocalWebServerIP.setEnabled(isChecked);
				editTextGetWebServerPortNumber.setEnabled(isChecked);
				if(isChecked)
					editTextGetLocalWebServerIP.requestFocus();
			}
		});
		
		chkBoxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{	
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				editTextGetPassword.setTransformationMethod(isChecked?null:new PasswordTransformationMethod());
			}
		});
		
		editTextGetWebServerPortNumber.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
				{
					try
					{
						int temp = Integer.parseInt(editTextGetWebServerPortNumber.getText().toString().trim());
						if(temp<0 || temp>65535)
							throw new Exception();
					}catch(Exception e)
					{
						Toast.makeText(LoginActivity.this, "Invalid port number", Toast.LENGTH_SHORT).show();
						editTextGetWebServerPortNumber.requestFocus();
					}
				}
			}
		});
		
		/********************************************************************************************************/
		
		btnGotoNextActivity.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent i = new Intent();
				i.setAction("org.csiVesit.csiVesitExperience.ShowTeamStatsActivity");
				Bundle extras = new Bundle();
				extras.putInt("org.csiVesit.csiVesitExperience.LoginActivity.team_id", teamID);
				extras.putString("org.csiVesit.csiVesitExperience.LoginActivity.serverIPPortToUse", chkBoxUseDifferentLocalServer.isChecked()?(editTextGetLocalWebServerIP.getText().toString()+":"+editTextGetWebServerPortNumber.getText().toString()):"csi-vesit.org");
				i.putExtras(extras);
				startActivity(i);
			}
		});
		
		/********************************************************************************************************/
		
		btnLogin.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View arg0)
			{
				if(editTextGetTeamID.getText().toString().trim().equals("") || editTextGetTeamID.getText().toString().trim().length()==0)
					Toast.makeText(LoginActivity.this, "Enter your team ID!", Toast.LENGTH_SHORT).show();
				else
				{
					try
					{
						int temp = Integer.parseInt(editTextGetTeamID.getText().toString().trim());
						if(temp<=0)
							throw new Exception("Invalid team ID");
						else
							teamID = temp;
					}catch(Exception e)
					{
						Toast.makeText(LoginActivity.this, "Invalid team ID", Toast.LENGTH_SHORT).show();
						editTextGetTeamID.requestFocus();
					}
				}
				
				if(teamID>0)
				{
					if(chkBoxUseDifferentLocalServer.isChecked() && (editTextGetLocalWebServerIP.getText().toString().trim().equals("") || editTextGetLocalWebServerIP.getText().toString().trim().length()==0))
						Toast.makeText(LoginActivity.this, "Enter local web server IP:Port OR deselect use different local web server!", Toast.LENGTH_SHORT).show();
					else
					{
						if(editTextGetPassword.getText().toString().trim().equals("") || editTextGetPassword.getText().toString().trim().length()==0)
							Toast.makeText(LoginActivity.this, "Enter your password!", Toast.LENGTH_SHORT).show();
						else
							new bgTaskSubmitLoginCredentials().execute();
					}
				}
			}
		});
	}
	private void initViews()
	{
		editTextGetLocalWebServerIP = (EditText)findViewById(R.id.editTextGetLocalWebServerIP);
		editTextGetPassword = (EditText)findViewById(R.id.editTextGetPassword);
		editTextGetTeamID = (EditText)findViewById(R.id.editTextGetTeamID);
		editTextGetWebServerPortNumber = (EditText)findViewById(R.id.editTextGetWebServerPortNumber);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnGotoNextActivity = (Button)findViewById(R.id.btnGotoNextActivity);
		
		chkBoxUseDifferentLocalServer = (CheckBox)findViewById(R.id.chkBoxUseDifferentLocalServer);
		chkBoxShowPassword = (CheckBox)findViewById(R.id.chkBoxShowPassword);
	}
	class bgTaskSubmitLoginCredentials extends AsyncTask<Void, String, Exception>
	{
		ProgressDialog progressDialog = null;
		boolean loginSuccess = false;
		StringBuffer responseBody = new StringBuffer();
		
		@Override
	    protected void onPreExecute()
	    {
	    	progressDialog = new ProgressDialog(LoginActivity.this);
	    	progressDialog.setTitle("Processing ..");
	    	progressDialog.setMessage("Please wait.");
	    	progressDialog.setCancelable(true);
	    	progressDialog.setIndeterminate(true);
	    	progressDialog.show();
	    }
		
		@Override
		protected Exception doInBackground(Void... params)
		{
		    HttpPost httppost = new HttpPost("http://"+(chkBoxUseDifferentLocalServer.isChecked()?(editTextGetLocalWebServerIP.getText().toString()+":"+editTextGetWebServerPortNumber.getText().toString()):"csi-vesit.org")+"/cricomania/LoginHandler.php");

		    try
		    {
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("team_id", editTextGetTeamID.getText().toString()));
		        nameValuePairs.add(new BasicNameValuePair("password", editTextGetPassword.getText().toString()));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        HttpResponse response = httpclient.execute(httppost);
		        
		        responseBody = new StringBuffer();
		        String s = new String();
		        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        while((s=br.readLine())!=null)
		        	responseBody.append(s).append(System.getProperty("line.separator"));
		        br.close();
		        
		        loginSuccess = responseBody.toString().trim().equals("LoginSuccess");	//trimming response body is important! because server returns LoginSuccess\n

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
				if(loginSuccess)
				{
					Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
					editTextGetWebServerPortNumber.setEnabled(false);
					editTextGetTeamID.setEnabled(false);
					editTextGetPassword.setEnabled(false);
					editTextGetLocalWebServerIP.setEnabled(false);
					btnLogin.setEnabled(false);
					chkBoxUseDifferentLocalServer.setEnabled(false);
					btnGotoNextActivity.setEnabled(true);
				}else
					Toast.makeText(LoginActivity.this, "There was an error while logging in .. retry", Toast.LENGTH_SHORT).show();
			}else
				Toast.makeText(LoginActivity.this, "Exception occured: "+(exceptionOccured.getMessage()==null?"No exception description found!":exceptionOccured.getMessage()), Toast.LENGTH_SHORT).show();
		}
	}
}
