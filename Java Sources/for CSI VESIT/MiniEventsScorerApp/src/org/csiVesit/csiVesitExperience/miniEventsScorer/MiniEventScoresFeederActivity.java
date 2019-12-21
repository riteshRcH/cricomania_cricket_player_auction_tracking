package org.csiVesit.csiVesitExperience.miniEventsScorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MiniEventScoresFeederActivity extends Activity
{
	Button btnUpdateFetchMiniEventDetails, btnInsertAsLatestEntry, btnUpdateLatestEntry, btnViewLatestEntry, btnDeleteLatestEntry, btnChooseMiniEventName, btnClearOffMiniEventAmtWon;
	Button btnMulAmtBy10, btnMulAmtBy100, btnMulAmtBy1k, btnMulAmtBy10k, btnMulAmtBy1Lac, btnMulAmtBy10Lac, btnMulAmtBy1Cr, btnMulAmtBy5Cr, btnMulAmtBy10Cr;
	Button btnAddToAmt10, btnAddToAmt100, btnAddToAmt1k, btnAddToAmt10k, btnAddToAmt1Lac, btnAddToAmt10Lac, btnAddToAmt1Cr, btnAddToAmt5Cr, btnAddToAmt10Cr;
	Button btnMulByMinus1;
	
	EditText editTextMiniEventAmtWonLost;
	TextView txtviewShowMiniEventAmtWonInWords;
	
	Spinner spinnerChooseTeam, spinnerChooseMiniEventName;
	ArrayAdapter<String> miniEventNameDataAdapter, teamIDAdapter;
	int totalTeamCount;
	
	double miniEventAmtWon = 0;
	boolean canFinishActivity = false;
	
	GetNumberInWords getNumberInWords = new GetNumberInWords();
	
	File xmlFileOfMiniEventNamesAndTeamCount = new File(MainActivity.appDirCricoManiaDir, "xmlFileMiniEventNamesAndTeamCount.xml");
	
	String earlierXMLMD5 = "noMD5", councilMemberName;
	StringWriter strWriter;
	PrintWriter pw = new PrintWriter(strWriter = new StringWriter());
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mini_event_scores_feeder);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		miniEventNameDataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item);
		miniEventNameDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		teamIDAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item);
		teamIDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		councilMemberName = getIntent().getExtras().getString("councilMemberName");
		
		/*********************************************************************************************************/
		
		initViews();
		
		/*********************************************************************************************************/
		
		MiniEventAmtWonModificationButtonListener miniEventAmtWonModificationButtonListener = new MiniEventAmtWonModificationButtonListener();
		btnAddToAmt10.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt100.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt1k.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt10k.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt1Lac.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt10Lac.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt1Cr.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt5Cr.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnAddToAmt10Cr.setOnClickListener(miniEventAmtWonModificationButtonListener);
		
		btnMulAmtBy10.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy100.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy1k.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy10k.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy1Lac.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy10Lac.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy1Cr.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy5Cr.setOnClickListener(miniEventAmtWonModificationButtonListener);
		btnMulAmtBy10Cr.setOnClickListener(miniEventAmtWonModificationButtonListener);
		
		btnClearOffMiniEventAmtWon.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View arg0)
			{
				editTextMiniEventAmtWonLost.setText("");
				miniEventAmtWon = 0;
				strWriter.getBuffer().setLength(0);
				pw.format("%f", miniEventAmtWon);
				editTextMiniEventAmtWonLost.setText(strWriter.getBuffer().toString());
				getNumberInWords.setNumber((long)miniEventAmtWon);
				txtviewShowMiniEventAmtWonInWords.setText(getNumberInWords.getNumberInWords());
			}
		});
		
		btnMulByMinus1.setOnClickListener(new View.OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				miniEventAmtWon *= -1;
				strWriter.getBuffer().setLength(0);
				pw.format("%f", miniEventAmtWon);
				editTextMiniEventAmtWonLost.setText(strWriter.getBuffer().toString());
				getNumberInWords.setNumber((long)Math.abs(miniEventAmtWon));
				String s = (miniEventAmtWon<0?"Minus ":"")+getNumberInWords.getNumberInWords();
				txtviewShowMiniEventAmtWonInWords.setText(s);
			}
		});
		
		/*********************************************************************************************************/
		
		editTextMiniEventAmtWonLost.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				try
				{
					if(!hasFocus)
					{
						int indexOf1stDecimalPoint; 
						if(editTextMiniEventAmtWonLost.getText().toString().trim().contains("."))		//if it has a decimal point it must be only once in text
							if(!((indexOf1stDecimalPoint = editTextMiniEventAmtWonLost.getText().toString().trim().indexOf("."))!=-1 && editTextMiniEventAmtWonLost.getText().toString().trim().substring(indexOf1stDecimalPoint+1).indexOf(".")==-1))
								throw new Exception();
						miniEventAmtWon = Double.parseDouble(editTextMiniEventAmtWonLost.getText().toString().trim().replaceAll("[,+]", ""));
						
						getNumberInWords.setNumber((long)miniEventAmtWon);
						txtviewShowMiniEventAmtWonInWords.setText(getNumberInWords.getNumberInWords());
					}
				}catch(Exception e)
				{
					Toast.makeText(getBaseContext(), "Invalid Amt Entered", Toast.LENGTH_SHORT).show();
					editTextMiniEventAmtWonLost.requestFocus();
				}
			}
		});
		
		/*********************************************************************************************************/
		
		btnInsertAsLatestEntry.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(btnChooseMiniEventName.getText().toString().equals("Choose Mini Event"))
					Toast.makeText(getBaseContext(), "MiniEvent Must be chosen 1st", Toast.LENGTH_SHORT).show();
				else if(editTextMiniEventAmtWonLost.getText().toString().trim().length()==0)
					Toast.makeText(getBaseContext(), "MiniEvent amt won/lost is to be entered", Toast.LENGTH_SHORT).show();
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
					builder.setTitle("Review and Submit");
					StringBuffer reviewStrBuffer = new StringBuffer();
					reviewStrBuffer.append("Mode:	Insert Latest Entry").append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Mini Event Name:	"+btnChooseMiniEventName.getText()).append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Team ID:	"+spinnerChooseTeam.getSelectedItem().toString()).append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Amount Won/Lost:	"+editTextMiniEventAmtWonLost.getText().toString()).append(System.getProperty("line.separator"));
					builder.setMessage(reviewStrBuffer);
					builder.setPositiveButton("Submit to Server", new DialogInterface.OnClickListener()
					{	
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							AsyncTask<Void,String,Exception> bgTaskSubmitInsertOperation = new AsyncTask<Void, String, Exception>()
							{
								ProgressDialog progressDialog = null;
								String operationCommandStringToSend;
								boolean insertOperationSuccess = false;
								
								@Override
							    protected void onPreExecute()
							    {
							    	progressDialog = new ProgressDialog(MiniEventScoresFeederActivity.this);
							    	progressDialog.setTitle("Processing ..");
							    	progressDialog.setMessage("Please wait.");
							    	progressDialog.setCancelable(false);
							    	progressDialog.setIndeterminate(true);
							    	progressDialog.show();
							    }
								
								@Override
								protected Exception doInBackground(Void... arg0)
								{
									try
									{
										//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName~Amt Won~Entry_by_council_member_name~entry_by_imei_num
										
										TelephonyManager telephonyManager = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE));
										publishProgress("Formulating Operation command .. ");
							    		operationCommandStringToSend = "MINI_EVENTS_SCORE_LOG_OPERATION~Insert LatestEntry~"+spinnerChooseTeam.getSelectedItem().toString().split(" ")[1].trim()+"~"+btnChooseMiniEventName.getText()+"~"+editTextMiniEventAmtWonLost.getText()+"~"+councilMemberName+"~"+telephonyManager.getDeviceId();
							    		publishProgress("Formulated Operation Command!");
							    		
							    		MainActivity.toServerOOStrm.writeObject(operationCommandStringToSend);
							    		MainActivity.toServerOOStrm.flush();
							    		
							    		Object obj = MainActivity.fromServerOIS.readObject();

							    		if(obj instanceof Exception)
							    			throw (Exception)obj;
							    		else if(obj instanceof String)
							    			insertOperationSuccess = ((String)obj).equals("Insertion Success!");
							    		
									}catch (Exception e) 
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
										if(insertOperationSuccess)
											Toast.makeText(getBaseContext(), "Successful insert operation!", Toast.LENGTH_SHORT).show();
										else
											Toast.makeText(getBaseContext(), "There was an error in insert operation .. please retry", Toast.LENGTH_SHORT).show();
									}else
										Toast.makeText(getBaseContext(), "Exception occured while insert operation .. please retry.\nException Details: "+((exceptionOccured.getMessage()==null || exceptionOccured.getMessage().equals(""))?"No error Description Found":exceptionOccured.getMessage()), Toast.LENGTH_LONG).show();
								}
							};
							bgTaskSubmitInsertOperation.execute();

						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		});
		btnUpdateLatestEntry.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(btnChooseMiniEventName.getText().toString().equals("Choose Mini Event"))
					Toast.makeText(getBaseContext(), "MiniEvent Must be chosen 1st", Toast.LENGTH_SHORT).show();
				else if(editTextMiniEventAmtWonLost.getText().toString().trim().length()==0)
					Toast.makeText(getBaseContext(), "MiniEvent amt won/lost is to be entered", Toast.LENGTH_SHORT).show();
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
					builder.setTitle("Review and Submit");
					StringBuffer reviewStrBuffer = new StringBuffer();
					reviewStrBuffer.append("Mode:	Update Latest Entry").append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Mini Event Name:	"+btnChooseMiniEventName.getText()).append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Team ID:	"+spinnerChooseTeam.getSelectedItem().toString()).append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Update to Won/Lost Amount:	"+editTextMiniEventAmtWonLost.getText().toString()).append(System.getProperty("line.separator"));
					builder.setMessage(reviewStrBuffer);
					builder.setPositiveButton("Submit to Server", new DialogInterface.OnClickListener()
					{	
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							AsyncTask<Void,String,Exception> bgTaskSubmitUpdateOperation = new AsyncTask<Void, String, Exception>()
							{
								ProgressDialog progressDialog = null;
								String operationCommandStringToSend;
								MiniEventsScoreLogRecord desiredLogRecord;
								boolean updateOperationSuccess = false, canSendConfirmationToServer = false, updateConfirmation = false, userCanceledOperation = false;
								
								@Override
							    protected void onPreExecute()
							    {
							    	progressDialog = new ProgressDialog(MiniEventScoresFeederActivity.this);
							    	progressDialog.setTitle("Processing ..");
							    	progressDialog.setMessage("Please wait.");
							    	progressDialog.setCancelable(false);
							    	progressDialog.setIndeterminate(true);
							    	progressDialog.show();
							    }
								
								@Override
								protected Exception doInBackground(Void... arg0)
								{
									try
									{
										//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName~Amt Won~Entry_by_council_member_name~entry_by_imei_num
										
										TelephonyManager telephonyManager = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE));
										publishProgress("Formulating Operation command .. ");
							    		operationCommandStringToSend = "MINI_EVENTS_SCORE_LOG_OPERATION~Update LatestEntry~"+spinnerChooseTeam.getSelectedItem().toString().split(" ")[1].trim()+"~"+btnChooseMiniEventName.getText()+"~"+editTextMiniEventAmtWonLost.getText()+"~"+councilMemberName+"~"+telephonyManager.getDeviceId();
							    		publishProgress("Formulated Operation Command!");
							    		
							    		MainActivity.toServerOOStrm.writeObject(operationCommandStringToSend);
							    		MainActivity.toServerOOStrm.flush();
							    		
							    		Object obj = MainActivity.fromServerOIS.readObject();

							    		if(obj instanceof Exception)
							    			throw (Exception)obj;
							    		
							    		if(obj==null)
							    		{
							    			updateOperationSuccess = true;
							    			publishProgress("Cmd: Display to Update Record");
							    		}else
							    		{
								    		desiredLogRecord = (org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord) obj;
								    		
								    		publishProgress("Cmd: Display to Update Record");
								    		
								    		while(!canSendConfirmationToServer);
								    		
								    		MainActivity.toServerOOStrm.writeObject(Boolean.valueOf(updateConfirmation));
								    		MainActivity.toServerOOStrm.flush();
								    		
								    		if(updateConfirmation)
								    		{
									    		obj = MainActivity.fromServerOIS.readObject();
		
									    		if(obj instanceof Exception)
									    			throw (Exception)obj;
									    		
									    		if(obj!=null)
									    			updateOperationSuccess = ((String)obj).equals("Update Success!");
								    		}else
								    			userCanceledOperation = true;
							    		}
							    		
									}catch (Exception e) 
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
									if(progressParams[0].equals("Cmd: Display to Update Record"))
									{
										if(progressDialog!=null)
											progressDialog.dismiss();
										AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
										builder.setTitle("Update Confirmation");
										if(desiredLogRecord==null)
										{
											builder.setMessage("No matching record found in DB");
											builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
											{												
												@Override
												public void onClick(DialogInterface dialog, int which)
												{
													dialog.dismiss();
												}
											});
										}else
										{
											String updateInfoStr = desiredLogRecord.toString();
											updateInfoStr += System.getProperty("line.separator") + "After Update New Event Amt Won: "+editTextMiniEventAmtWonLost.getText() + System.getProperty("line.separator");
											updateInfoStr += "Timestamp, IMEI Num and Council Member would be updated as was shown while review" + System.getProperty("line.separator");
											builder.setMessage(updateInfoStr);
											builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
											{	
												@Override
												public void onClick(DialogInterface dialog, int arg1)
												{
													updateConfirmation = true;
													dialog.dismiss();
													progressDialog.show();
													canSendConfirmationToServer = true;
												}
											});
											builder.setNegativeButton("Dont Update", new DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface dialog, int which)
												{
													updateConfirmation = false;
													dialog.dismiss();
													progressDialog.show();
													canSendConfirmationToServer = true;
												}
											});
										}
										builder.create().show();
									}else
										progressDialog.setMessage(progressParams[0]);
								}
								
								@Override
								protected void onPostExecute(Exception exceptionOccured)
								{
									if(progressDialog!=null)
										progressDialog.dismiss();
									
									if(exceptionOccured==null)
									{
										if(updateOperationSuccess)
											Toast.makeText(getBaseContext(), "Successful update operation!", Toast.LENGTH_SHORT).show();
										else
										{
											if(userCanceledOperation)
												Toast.makeText(getBaseContext(), "User canceled update Operation", Toast.LENGTH_SHORT).show();
											else
												Toast.makeText(getBaseContext(), "There was an error in update operation .. please retry", Toast.LENGTH_SHORT).show();
										}
									}else
										Toast.makeText(getBaseContext(), "Exception occured while update operation .. please retry.\nException Details: "+((exceptionOccured.getMessage()==null || exceptionOccured.getMessage().equals(""))?"No error Description Found":exceptionOccured.getMessage()), Toast.LENGTH_LONG).show();
								}
							};
							bgTaskSubmitUpdateOperation.execute();
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		});
		btnDeleteLatestEntry.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View arg0)
			{
				if(btnChooseMiniEventName.getText().toString().equals("Choose Mini Event"))
					Toast.makeText(getBaseContext(), "MiniEvent Must be chosen 1st", Toast.LENGTH_SHORT).show();
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
					builder.setTitle("Review and Submit");
					StringBuffer reviewStrBuffer = new StringBuffer();
					reviewStrBuffer.append("Mode:	Delete Latest Entry").append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Mini Event Name:	"+btnChooseMiniEventName.getText()).append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Team ID:	"+spinnerChooseTeam.getSelectedItem().toString()).append(System.getProperty("line.separator"));
					builder.setMessage(reviewStrBuffer);
					builder.setPositiveButton("Submit to Server", new DialogInterface.OnClickListener()
					{	
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							AsyncTask<Void,String,Exception> bgTaskSubmitDeleteOperation = new AsyncTask<Void, String, Exception>()
							{
								ProgressDialog progressDialog = null;
								String operationCommandStringToSend;
								MiniEventsScoreLogRecord desiredLogRecord;
								boolean deleteOperationSuccess = false, canSendConfirmationToServer = false, deleteConfirmation = false, userCanceledOperation = false;
								
								@Override
							    protected void onPreExecute()
							    {
							    	progressDialog = new ProgressDialog(MiniEventScoresFeederActivity.this);
							    	progressDialog.setTitle("Processing ..");
							    	progressDialog.setMessage("Please wait.");
							    	progressDialog.setCancelable(false);
							    	progressDialog.setIndeterminate(true);
							    	progressDialog.show();
							    }
								
								@Override
								protected Exception doInBackground(Void... arg0)
								{
									try
									{
										//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName
										
										publishProgress("Formulating Operation command .. ");
							    		operationCommandStringToSend = "MINI_EVENTS_SCORE_LOG_OPERATION~Delete LatestEntry~"+spinnerChooseTeam.getSelectedItem().toString().split(" ")[1].trim()+"~"+btnChooseMiniEventName.getText();
							    		publishProgress("Formulated Operation Command!");
							    		
							    		MainActivity.toServerOOStrm.writeObject(operationCommandStringToSend);
							    		MainActivity.toServerOOStrm.flush();
							    		
							    		Object obj = MainActivity.fromServerOIS.readObject();

							    		if(obj instanceof Exception)
							    			throw (Exception)obj;
							    		
							    		if(obj==null)
							    		{
							    			deleteOperationSuccess = true;
							    			publishProgress("Cmd: Display to Delete Record");
							    		}else
							    		{
								    		desiredLogRecord = (org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord) obj;
								    		
								    		publishProgress("Cmd: Display to Delete Record");
								    		
								    		while(!canSendConfirmationToServer);
								    		
								    		MainActivity.toServerOOStrm.writeObject(Boolean.valueOf(deleteConfirmation));
								    		MainActivity.toServerOOStrm.flush();
								    		
								    		if(deleteConfirmation)
								    		{
									    		obj = MainActivity.fromServerOIS.readObject();
		
									    		if(obj instanceof Exception)
									    			throw (Exception)obj;
									    		
									    		if(obj!=null)
									    			deleteOperationSuccess = ((String)obj).equals("Deletion Success!");
								    		}else
								    			userCanceledOperation = true;
							    		}
							    		
									}catch (Exception e) 
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
									if(progressParams[0].equals("Cmd: Display to Delete Record"))
									{
										if(progressDialog!=null)
											progressDialog.dismiss();
										AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
										builder.setTitle("Delete Confirmation");
										if(desiredLogRecord==null)
										{
											builder.setMessage("No matching record found in DB");
											builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
											{												
												@Override
												public void onClick(DialogInterface dialog, int which)
												{
													dialog.dismiss();
												}
											});
										}else
										{
											builder.setMessage(desiredLogRecord.toString());
											builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
											{	
												@Override
												public void onClick(DialogInterface dialog, int arg1)
												{
													deleteConfirmation = true;
													dialog.dismiss();
													progressDialog.show();
													canSendConfirmationToServer = true;
												}
											});
											builder.setNegativeButton("Dont Delete", new DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface dialog, int which)
												{
													deleteConfirmation = false;
													dialog.dismiss();
													progressDialog.show();
													canSendConfirmationToServer = true;
												}
											});
										}
										builder.create().show();
									}else
										progressDialog.setMessage(progressParams[0]);
								}
								
								@Override
								protected void onPostExecute(Exception exceptionOccured)
								{
									if(progressDialog!=null)
										progressDialog.dismiss();
									
									if(exceptionOccured==null)
									{
										if(deleteOperationSuccess)
											Toast.makeText(getBaseContext(), "Successful delete operation!", Toast.LENGTH_SHORT).show();
										else
										{
											if(userCanceledOperation)
												Toast.makeText(getBaseContext(), "User canceled delete Operation", Toast.LENGTH_SHORT).show();
											else
												Toast.makeText(getBaseContext(), "There was an error in delete operation .. please retry", Toast.LENGTH_SHORT).show();
										}
									}else
										Toast.makeText(getBaseContext(), "Exception occured while delete operation .. please retry.\nException Details: "+((exceptionOccured.getMessage()==null || exceptionOccured.getMessage().equals(""))?"No error Description Found":exceptionOccured.getMessage()), Toast.LENGTH_LONG).show();
								}
							};
							bgTaskSubmitDeleteOperation.execute();
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		});
		btnViewLatestEntry.setOnClickListener(new View.OnClickListener()
		{			
			@Override
			public void onClick(View arg0)
			{
				if(btnChooseMiniEventName.getText().toString().equals("Choose Mini Event"))
					Toast.makeText(getBaseContext(), "MiniEvent Must be chosen 1st", Toast.LENGTH_SHORT).show();
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
					builder.setCancelable(true);
					builder.setTitle("Review and Submit");
					StringBuffer reviewStrBuffer = new StringBuffer();
					reviewStrBuffer.append("Mode:	View Latest Entry").append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Mini Event Name:	"+btnChooseMiniEventName.getText()).append(System.getProperty("line.separator"));
					reviewStrBuffer.append("Team ID:	"+spinnerChooseTeam.getSelectedItem().toString()).append(System.getProperty("line.separator"));
					builder.setMessage(reviewStrBuffer);
					builder.setPositiveButton("Submit to Server", new DialogInterface.OnClickListener()
					{	
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							AsyncTask<Void,String,Exception> bgTaskSubmitViewOperation = new AsyncTask<Void, String, Exception>()
							{
								ProgressDialog progressDialog = null;
								String operationCommandStringToSend;
								MiniEventsScoreLogRecord desiredLogRecord;
								boolean viewOperationSuccess = false;
								
								@Override
							    protected void onPreExecute()
							    {
							    	progressDialog = new ProgressDialog(MiniEventScoresFeederActivity.this);
							    	progressDialog.setTitle("Processing ..");
							    	progressDialog.setMessage("Please wait.");
							    	progressDialog.setCancelable(false);
							    	progressDialog.setIndeterminate(true);
							    	progressDialog.show();
							    }
								
								@Override
								protected Exception doInBackground(Void... arg0)
								{
									try
									{
										//Format:	MINI_EVENTS_SCORE_LOG_OPERATION~chosenMode(wrt button click)~teamID~miniEventName
										
										publishProgress("Formulating Operation command .. ");
							    		operationCommandStringToSend = "MINI_EVENTS_SCORE_LOG_OPERATION~View LatestEntry~"+spinnerChooseTeam.getSelectedItem().toString().split(" ")[1].trim()+"~"+btnChooseMiniEventName.getText();
							    		publishProgress("Formulated Operation Command!");
							    		
							    		MainActivity.toServerOOStrm.writeObject(operationCommandStringToSend);
							    		MainActivity.toServerOOStrm.flush();
							    		
							    		Object obj = MainActivity.fromServerOIS.readObject();

							    		if(obj instanceof Exception)
							    			throw (Exception)obj;
							    		
							    		if(obj!=null)
							    			desiredLogRecord = (org.csiVesit.csiVesitExperience.miniEventsScorer.MiniEventsScoreLogRecord) obj;
							    		
							    		publishProgress("Cmd: Display View Results");
							    		viewOperationSuccess = true;
							    		
									}catch (Exception e) 
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
									if(progressParams[0].equals("Cmd: Display View Results"))
									{
										if(progressDialog!=null)
											progressDialog.dismiss();
										AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
										builder.setTitle("View Results");
										if(desiredLogRecord==null)
											builder.setMessage("No matching record found in DB");
										else
											builder.setMessage(desiredLogRecord.toString());
										builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
										{	
											@Override
											public void onClick(DialogInterface dialog, int arg1)
											{
												dialog.dismiss();
											}
										});
										builder.create().show();
									}else
										progressDialog.setMessage(progressParams[0]);
								}
								
								@Override
								protected void onPostExecute(Exception exceptionOccured)
								{
									if(progressDialog!=null)
										progressDialog.dismiss();
									
									if(exceptionOccured==null)
									{
										if(viewOperationSuccess)
											Toast.makeText(getBaseContext(), "Successful view operation!", Toast.LENGTH_SHORT).show();
										else
											Toast.makeText(getBaseContext(), "There was an error in view operation .. please retry", Toast.LENGTH_SHORT).show();
									}else
										Toast.makeText(getBaseContext(), "Exception occured while view operation .. please retry.\nException Details: "+((exceptionOccured.getMessage()==null || exceptionOccured.getMessage().equals(""))?"No error Description Found":exceptionOccured.getMessage()), Toast.LENGTH_LONG).show();
								}
							};
							bgTaskSubmitViewOperation.execute();
						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
			}
		});
		
		/*********************************************************************************************************/
		
		btnUpdateFetchMiniEventDetails.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				AsyncTask<Void, String, Exception> bgTaskFetchUpdatePresets = new AsyncTask<Void, String, Exception>()
		    	{
		    		ProgressDialog progressDialog = null;
		    		boolean receivedServerXMLFileSuccess = false, needUpdation = true;
		    	    	       				
		    	    @Override
		    	    protected void onPreExecute()
		    	    {
		    	    	progressDialog = new ProgressDialog(MiniEventScoresFeederActivity.this);
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
		    	    		MainActivity.toServerOOStrm.writeObject("GET_MINI_EVENT_NAMES_AND_TEAM_COUNT");
		    	    		MainActivity.toServerOOStrm.flush();
		    	    		
		    	    		needUpdation = true;
		    	    		
		    	    		Object serverReply = MainActivity.fromServerOIS.readObject();
		    	    		Log.d("received md5", (String)serverReply);
		    	    		if(serverReply instanceof String)
		    	    		{
		    	    			if(!xmlFileOfMiniEventNamesAndTeamCount.exists())
		    	    			{
		    	    				Log.d("zip doesnt exist, and hence need updation:", Boolean.valueOf(needUpdation).toString());
		    	    				needUpdation = true;
		    	    			}else
		    	    			{
		    	    				needUpdation = !(((String)serverReply).split("[:]")[1]).toString().trim().equals(getMD5OfParam(xmlFileOfMiniEventNamesAndTeamCount));
		    	    				Log.d("checking", ((String)serverReply).split("[:]")[1]+" and "+getMD5OfParam(xmlFileOfMiniEventNamesAndTeamCount));
		    	    				Log.d("zip exists, and need updation: ", Boolean.valueOf(needUpdation).toString());
		    	    			}
		    	    		}
		    	    		if(needUpdation)
		    	    		{
		    	    			MainActivity.toServerOOStrm.writeObject(Boolean.valueOf(needUpdation));		//inform server whether client needs updation of server data or not
		    	    			
		    	    			deleteFolderContents(MainActivity.appDirCricoManiaDir);
		    	    			
		    	    			xmlFileOfMiniEventNamesAndTeamCount.getParentFile().mkdirs();
			    	    		FileOutputStream fos = new FileOutputStream(xmlFileOfMiniEventNamesAndTeamCount);
			    	    		while(true)
			    	    		{
			    	    			serverReply = MainActivity.fromServerOIS.readObject();
			    	    			MainActivity.appDirCricoManiaDir.mkdirs();
			    	    			if(serverReply!=null)
			    	    				if(serverReply instanceof byte[])
			    	    					fos.write((byte[])serverReply);
		    	    				else if(serverReply instanceof String && ((String)serverReply).startsWith("FINISHED_SENDING_XML"))
		    	    				{
		    	    					fos.flush();
		    	    					fos.close();
		    	    					publishProgress("Verifying with md5..");
		    	    					String receivedServerReply = (String)serverReply; 
		    	    					String calculatedMD5 = getMD5OfParam(xmlFileOfMiniEventNamesAndTeamCount);
		    	    					String receivedMD5 = receivedServerReply.split("[:]")[1];
		    	    					receivedServerXMLFileSuccess = calculatedMD5.equals(receivedMD5);
		    	    					break;
		    	    				}
		    	    			}
		    	    		}else
		    	    		{
		    	    			receivedServerXMLFileSuccess = true;
		    	    			MainActivity.toServerOOStrm.writeObject(Boolean.valueOf(needUpdation));		//inform server whether client needs updation of server data or not
		    	    			publishProgress("Display Toast .. already up to date server Data");
		    	    		}
		    	    	}catch (Exception e) 
		    	    	{
		    	    		Log.e("Exception", e.getMessage()==null?"null":e.getMessage());
		    	    		return e;
		    	    	}
						return null;
		    	    }
					@Override
		    	    protected void onProgressUpdate(String ...progressParams)
		    	    {
		    	    	if(progressParams[0].equals("Display Toast .. already up to date server Data"))
		    	    		Toast.makeText(getBaseContext(), "Latest MD5 Integrity Checker:	Already UP TO DATE DATA!", Toast.LENGTH_LONG).show();
		    	    	else
			    	    	progressDialog.setMessage(progressParams[0]);
		    	    }
					@Override
		    	    protected void onPostExecute(Exception exceptionOccured)
					{
						if(progressDialog!=null)
							progressDialog.dismiss();
							
						if(exceptionOccured==null)
						{
							if(needUpdation)
								if(receivedServerXMLFileSuccess)
									Toast.makeText(getBaseContext(), "Successfully received server Data.", Toast.LENGTH_SHORT).show();
								else
									Toast.makeText(getBaseContext(), "There was an error in receiving/interpreting Presets sent by server .. please retry", Toast.LENGTH_SHORT).show();
						}else
							Toast.makeText(getBaseContext(), "Exception occured while fetching/updating presets .. please retry.\nException Details: "+exceptionOccured.getMessage(), Toast.LENGTH_LONG).show();
					}
			    };
			    bgTaskFetchUpdatePresets.execute();
			}
		});
		
		
		spinnerChooseMiniEventName.setAdapter(miniEventNameDataAdapter);
		spinnerChooseTeam.setAdapter(teamIDAdapter);
		btnChooseMiniEventName.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(xmlFileOfMiniEventNamesAndTeamCount.exists())
				{
					if(!earlierXMLMD5.equals(getMD5OfParam(xmlFileOfMiniEventNamesAndTeamCount)))
					{
						try
						{
							miniEventNameDataAdapter.clear();
							teamIDAdapter.clear();
							
							//Parse XML	using XMLPUllParser(Android provided is more efficient as after few insertions of start times DOM parsing would be mem inefficient) as XML File has been changed since last parsing
							XmlPullParser xpp = Xml.newPullParser();
							xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
						    xpp.setInput(new FileReader(xmlFileOfMiniEventNamesAndTeamCount));
						
						    int eventType = xpp.getEventType();
						    while(eventType != XmlPullParser.END_DOCUMENT)
						    {
						        if (eventType == XmlPullParser.START_TAG)
						            if (xpp.getName().equals("TeamCount"))
						            	totalTeamCount = Integer.parseInt(xpp.nextText());
						            else if(xpp.getName().equals("MiniEventName"))
						            	miniEventNameDataAdapter.add(xpp.nextText());
						        eventType = xpp.next(); //move to next element
						    }
						    
				        	miniEventNameDataAdapter.notifyDataSetChanged();
				        	for(int i=1;i<=totalTeamCount;i++)
				        		teamIDAdapter.add("Team "+Integer.toString(i));
				        	teamIDAdapter.notifyDataSetChanged();
				        	
				        	earlierXMLMD5 = getMD5OfParam(xmlFileOfMiniEventNamesAndTeamCount);
						}catch(Exception e)
						{
							Toast.makeText(getBaseContext(), "Exception occured: "+e.getMessage(), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
					
					spinnerChooseMiniEventName.performClick();
					
				}else
					Toast.makeText(getBaseContext(), "MiniEvent Data Doesnt exists,  please fetch/update it from server", Toast.LENGTH_LONG).show();
			}
		});

		//If spinner needed not to be visible => Keep width and Height of spinner to 0 dip and keep visibility as invisible not as gone => only then this listener works
		spinnerChooseMiniEventName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id)
			{
				btnChooseMiniEventName.setText(parent.getItemAtPosition(pos).toString());
			}
				
			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				if(btnChooseMiniEventName.getText().equals("Chosen Mini-Event: None"))
				{
					Toast.makeText(MiniEventScoresFeederActivity.this.getBaseContext(), "A mini-event name must be compulsorily chosen!", Toast.LENGTH_SHORT).show();
					spinnerChooseMiniEventName.performClick();
				}
			}
		});
		
	/*********************************************************************************************************/
	}
	static void deleteFolderContents(File dir)
	{
		File[] files = dir.listFiles();
		if(files!=null)
			for(File f:files)
				if(f.isFile())
					f.delete();
				else if(f.isDirectory())
				{
					deleteFolderContents(f);
					f.delete();
				}
	}
	static String getMD5OfParam(File xmlFileMiniEventNamesAndTeamCount)
	{
		try
		{
			FileInputStream fin = new FileInputStream(xmlFileMiniEventNamesAndTeamCount);
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

	class MiniEventAmtWonModificationButtonListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			String alreadyExistingText = editTextMiniEventAmtWonLost.getText().toString().trim();
			int eventScrID = v.getId();
			strWriter.getBuffer().setLength(0);
			
			if(miniEventAmtWon==0.0 && editTextMiniEventAmtWonLost.getText().toString().trim().length()>0)
				miniEventAmtWon = Double.parseDouble(editTextMiniEventAmtWonLost.getText().toString().trim().replaceAll("[,+]", ""));
			
			if(eventScrID==btnAddToAmt10.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					miniEventAmtWon = 10;
					editTextMiniEventAmtWonLost.setText("10");
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 10));
			}else if(eventScrID==btnAddToAmt100.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("100");
					miniEventAmtWon = 100;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 100));
			}else if(eventScrID==btnAddToAmt1k.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("1,000");
					miniEventAmtWon = 1000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 1000));
			}else if(eventScrID==btnAddToAmt10k.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10,000");
					miniEventAmtWon = 10000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 10000));
			}else if(eventScrID==btnAddToAmt1Lac.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("1,00,000");
					miniEventAmtWon = 100000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 100000));
			}else if(eventScrID==btnAddToAmt10Lac.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10,00,000");
					miniEventAmtWon = 1000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 1000000));
			}else if(eventScrID==btnAddToAmt1Cr.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("1,00,00,000");
					miniEventAmtWon = 10000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 10000000));
			}else if(eventScrID==btnAddToAmt5Cr.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("5,00,00,000");
					miniEventAmtWon = 50000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 50000000));
			}else if(eventScrID==btnAddToAmt10Cr.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10,00,00,000");
					miniEventAmtWon = 100000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon + 100000000));
			}else if(eventScrID==btnMulAmtBy10.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10");
					miniEventAmtWon = 10;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 10));
			}else if(eventScrID==btnMulAmtBy100.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("100");
					miniEventAmtWon = 100;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 100));
			}else if(eventScrID==btnMulAmtBy1k.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("1,000");
					miniEventAmtWon = 1000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 1000));
			}else if(eventScrID==btnMulAmtBy10k.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10,000");
					miniEventAmtWon = 10000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 10000));
			}else if(eventScrID==btnMulAmtBy1Lac.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("1,00,000");
					miniEventAmtWon = 100000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 100000));
			}else if(eventScrID==btnMulAmtBy10Lac.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10,00,000");
					miniEventAmtWon = 1000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 1000000));
			}else if(eventScrID==btnMulAmtBy1Cr.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("1,00,00,000");
					miniEventAmtWon = 1000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 10000000));
			}else if(eventScrID==btnMulAmtBy5Cr.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("5,00,00,000");
					miniEventAmtWon = 5000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 50000000));
			}else if(eventScrID==btnMulAmtBy10Cr.getId())
			{
				if(alreadyExistingText!=null && alreadyExistingText.equals(""))
				{
					editTextMiniEventAmtWonLost.setText("10,00,00,000");
					miniEventAmtWon = 10000000;
				}else
					editTextMiniEventAmtWonLost.setText(Double.toString(miniEventAmtWon = miniEventAmtWon * 100000000));
			}
			pw.format("%f", miniEventAmtWon);
			editTextMiniEventAmtWonLost.setText(strWriter.getBuffer().toString());
			getNumberInWords.setNumber((long)miniEventAmtWon);
			txtviewShowMiniEventAmtWonInWords.setText((miniEventAmtWon<0?"Minus ":"")+getNumberInWords.getNumberInWords());
		}
	}
	private void initViews()
	{
		btnAddToAmt10 = (Button)findViewById(R.id.btnAddToAmt10);
		btnAddToAmt100 = (Button)findViewById(R.id.btnAddToAmt100);
		btnAddToAmt1k = (Button)findViewById(R.id.btnAddToAmt1k);
		btnAddToAmt10k = (Button)findViewById(R.id.btnAddToAmt10k);
		btnAddToAmt1Lac = (Button)findViewById(R.id.btnAddToAmt1Lac);
		btnAddToAmt10Lac = (Button)findViewById(R.id.btnAddToAmt10Lac);
		btnAddToAmt1Cr = (Button)findViewById(R.id.btnAddToAmt1Cr);
		btnAddToAmt5Cr = (Button)findViewById(R.id.btnAddToAmt5Cr);
		btnAddToAmt10Cr = (Button)findViewById(R.id.btnAddToAmt10Cr);
		
		btnMulAmtBy10 = (Button)findViewById(R.id.btnMulAmtBy10);
		btnMulAmtBy100 = (Button)findViewById(R.id.btnMulAmtBy100);
		btnMulAmtBy1k = (Button)findViewById(R.id.btnMulAmtBy1k);
		btnMulAmtBy10k = (Button)findViewById(R.id.btnMulAmtBy10k);
		btnMulAmtBy1Lac = (Button)findViewById(R.id.btnMulAmtBy1Lac);
		btnMulAmtBy10Lac = (Button)findViewById(R.id.btnMulAmtBy10Lac);
		btnMulAmtBy1Cr = (Button)findViewById(R.id.btnMulAmtBy1Cr);
		btnMulAmtBy5Cr = (Button)findViewById(R.id.btnMulAmtBy5Cr);
		btnMulAmtBy10Cr = (Button)findViewById(R.id.btnMulAmtBy10Cr);
		
		btnMulByMinus1 = (Button)findViewById(R.id.btnMulByMinus1);
		btnClearOffMiniEventAmtWon = (Button)findViewById(R.id.btnClearOffMiniEventAmtWon);
		
		btnUpdateFetchMiniEventDetails = (Button)findViewById(R.id.btnUpdateFetchMiniEventDetails);
		btnChooseMiniEventName = (Button)findViewById(R.id.btnChooseMiniEventName);
		spinnerChooseMiniEventName = (Spinner)findViewById(R.id.spinnerChooseMiniEventName);
		spinnerChooseTeam = (Spinner)findViewById(R.id.spinnerChooseTeam);
		
		btnDeleteLatestEntry = (Button)findViewById(R.id.btnDeleteLatestEntry);
		btnInsertAsLatestEntry = (Button)findViewById(R.id.btnInsertAsLatestEntry);
		btnUpdateLatestEntry = (Button)findViewById(R.id.btnUpdateLatestEntry);
		btnViewLatestEntry = (Button)findViewById(R.id.btnViewLatestEntry);
		
		editTextMiniEventAmtWonLost = (EditText)findViewById(R.id.editTextMiniEventAmtWonLost);
		txtviewShowMiniEventAmtWonInWords = (TextView)findViewById(R.id.txtviewShowMiniEventAmtWonInWords);
	}
	public void onBackPressed()							//Logout confirmation, if user wants to logout then send inform AMSServer that user is logging out so that its ClientServicingThread can be stopped and receive its confirmation 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(MiniEventScoresFeederActivity.this);
		builder.setTitle("Logout?");
		builder.setMessage("Are you sure you want to Logout?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
		    	AsyncTask<Void, String, Exception> bgTaskInformLoggingOutToMiniEventsScorerServer = new AsyncTask<Void, String, Exception>()
		    	{
		    		ProgressDialog progressDialog = null;
		    		boolean logoutSuccess = false;
		    	    	       				
		    	    @Override
		    	    protected void onPreExecute()
		    	    {
		    	    	progressDialog = new ProgressDialog(MiniEventScoresFeederActivity.this);
		    	    	progressDialog.setTitle("Processing ..");
		    	    	progressDialog.setMessage("Logging out .. Please wait");
		    	    	progressDialog.setCancelable(false);
		    	    	progressDialog.setIndeterminate(true);
		    	    	progressDialog.show();
		    	    }
		    	    	    	    							
		    	    @Override
		    	    protected Exception doInBackground(Void ...params)
		    	    {
		    	    	try
		    	    	{
		    	    		MainActivity.toServerOOStrm.writeObject("LOGOUT_DONE_WITH_CURRENT_MINI_EVENTS_SESSION ");
		    	    		MainActivity.toServerOOStrm.flush();
		    	    		
		    	    		canFinishActivity = logoutSuccess = ((String)MainActivity.fromServerOIS.readObject()).equals("OK! Stopping your servicing thread.");
		    	    	}catch (Exception e) 
		    	    	{
		    	    		Log.e("Exception", e.getMessage()==null?"null":e.getMessage());
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
							if(logoutSuccess)
							{
								Toast.makeText(getBaseContext(), "Successfully Logged out.", Toast.LENGTH_SHORT).show();
								canFinishActivity = true;
							}else
								Toast.makeText(getBaseContext(), "There was an error in logging out .. please retry", Toast.LENGTH_SHORT).show();
						}else
							Toast.makeText(getBaseContext(), "Exception occured while logging out .. please retry.\nException Details: "+exceptionOccured.getMessage(), Toast.LENGTH_LONG).show();
					}
		    	};
		    	bgTaskInformLoggingOutToMiniEventsScorerServer.execute();
		    	while(!canFinishActivity);							//loop infinitely until progressDialog is dismissed and canFinishActivity is set in onPostExecute
		    	if(canFinishActivity)
		    		finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}
}