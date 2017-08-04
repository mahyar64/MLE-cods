/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;



public class MLE extends Activity implements OnClickListener
{
	

	private Context mContext;
	private float availableSpeed = 0.0f;
	private long lastGPSSpeed = 0;
	//EditText txtData;
	private LocationManager locationManager;
	/*the array is inputArray[0]=wifi (on/Off),inputArray[1] wifi-name, inputArray[2]=gps (on/off),inputArray[3]=GPS_state (move/static), inputArray[4]=GSM_ID,
	inputArray[5]=time, inputArray[6]=day
	*/
	float inputArray[] = new float[7];
	//outputArray[0]=current volume, outputArray[1]=vibration (on/off), outputArray[2]=GPS (on/off), outputArray[3]= wifi (on/off), outputArray[4]=mobiledata 
	float outputArray[] = new float[5];
	float outputArrayFromNN[] = new float[5];
	//values for wifi name
	String [] wifiNameArray  =new String [100];
	float WifiNumber=0;
	//values for GSM naem
	float GSMNameArray []  = new float [100];
	int FirstLineNNInput []=new int [3];
	float GSMNumber=0; 
    int FileLineNumber;
	
	private ArrayList<String> wifiNames = new ArrayList<String>();
	private ArrayList<Integer> cellidNames = new ArrayList<Integer>();
	
	private Timer timer = new Timer();
	private Timer timer1 = new Timer();
	//we are going to use a handler to be able to run in our TimerTask
	final Handler handler = new Handler();
	
	class RepetitiveTask extends TimerTask {

		@Override
		public void run() {
			//use a handler to run a toast that shows the current timestamp
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "task in execution", Toast.LENGTH_SHORT).show();
									writeBtn();
											}
			});
		}
	}
	
    final Handler handler1 = new Handler();
	
	class RepetitiveTask1 extends TimerTask {

		@Override
		public void run() {
			//use a handler to run a toast that shows the current timestamp
			handler1.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "Training stoped and we are using NN after", Toast.LENGTH_SHORT).show();
				    updateInputs();
					ArrayFromJNI();
					OutputsFromNN ();
				   // stringFromJNI(inputArray[0],inputArray[1],inputArray[2], inputArray[3], inputArray[4], inputArray[5], inputArray[6]);
					
									}

			
			});
		}
	}
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
        
         */
        setContentView(R.layout.main);
        
        wifiNames.add("null");	// TODO initialise the values of wifiNames from a file
        //clear the file to write new data;
        Log.d("test 1", "info from activity");
       
      //  TextView  tv = (TextView) this.findViewById(R.id.textView1);
        //tv.setText( stringFromJNI() );
       //v.setText("test");
        //tv.setText(stringFromJNI(1.0f,2.0f,3.0f) );
       
    //    tv.setText(stringFromJNI());
       // tv.setText(ArrayFromJNI());
        
        timer.schedule(new RepetitiveTask(), 7*1000, 60*5*1000);	// every 15 sec
	
       
        Button button = (Button) this.findViewById(R.id.button1);
        button.setText("check GPS (on/off)");
        button.setOnClickListener(this);
        
        Button button2 = (Button) this.findViewById(R.id.button2);
        button2.setText("check GPS speed");
        button2.setOnClickListener(this);
        
        Button button3 = (Button) this.findViewById(R.id.button3);
        button3.setText("check Wi-Fi");
        button3.setOnClickListener(this);
        mContext = this.getApplicationContext();
        
        Button button4 = (Button) this.findViewById(R.id.button4);
        button4.setText("Check GSM-ID:");
        button4.setOnClickListener(this);
        
        Button button5 = (Button) this.findViewById(R.id.button5);
        button5.setText("Check time and day:");
        button5.setOnClickListener(this);
        
        Button button6 = (Button) this.findViewById(R.id.button6);
        button6.setText("Check Current Volume:");
        button6.setOnClickListener(this);
        
        Button button7 = (Button) this.findViewById(R.id.button7);
        button7.setText("Check the vobration (on/off");
        button7.setOnClickListener(this);
        
        Button button8 = (Button) this.findViewById(R.id.button8);
        button8.setText("Check mobile data(on/off)");
        button8.setOnClickListener(this);
       
        Button button9 = (Button) this.findViewById(R.id.button9);
        button9.setText("Training the NN");
        button9.setOnClickListener(this);
        
        Button button10 = (Button) this.findViewById(R.id.button10);
        button10.setText("Manual Settings");
        button10.setOnClickListener(this);
                
               
        Button button11 = (Button) this.findViewById(R.id.button11);
        button11.setText("start automatic settings");
        button11.setOnClickListener(this);
        
        
        Button button13 = (Button) this.findViewById(R.id.button13);
        button13.setText("Reset Training");
        button13.setOnClickListener(this);
       
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				//location.getLatitude();
				availableSpeed = location.getSpeed();
				lastGPSSpeed = System.currentTimeMillis();
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}
			public void onProviderEnabled(String provider) {}
			public void onProviderDisabled(String provider) {}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
				    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();
				timer.cancel();
			}
       
   
    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    //public native String  stringFromJNI();
    public native String  stringFromJNI(float a, float b, float c, float d, float e, float f, float g );
    //public native String  stringFromJNI();
    
    public void ArrayFromJNI() {
  //  float [] outputArrayFromNN = 
    	
    		 String result=stringFromJNI(inputArray[0],inputArray[1],inputArray[2], inputArray[3],
    				                     inputArray[4], inputArray[5], inputArray[6]); // the last inputs for test the NN
    		 StringTokenizer st = new StringTokenizer(result,",");
    		 int i=0;
    		 while (st.hasMoreTokens()) {
    	     
    			String x=st.nextToken();
    			
    	       float y= Float.parseFloat(x);
    	        outputArrayFromNN[i] =y;
    	        i++;
    	     }
    		 for (int j=0;j<5;j++)
    			 Toast.makeText(this, "float amount is:" + outputArrayFromNN[j], Toast.LENGTH_SHORT).show();
    		 TextView  tv = (TextView) this.findViewById(R.id.textView1);
	     		tv.setText(stringFromJNI(inputArray[0],inputArray[1],inputArray[2], inputArray[3], inputArray[4], inputArray[5], inputArray[6]));
    }
    
   
    //gps automatic on/off
      
    public void turnGPSOnOff()
     {
    	
    	 final LocationManager manager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     
    	 if (outputArrayFromNN[2]>0.7 && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
     {
    		 Toast.makeText(this, "MLE suggests you to turn on GPS", Toast.LENGTH_SHORT).show();
    		 
    		 Intent i = new
    				 Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    				 startActivity(i);
    	}
    	 if (outputArrayFromNN[2]<-0.7 && manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    	 {
    		 Toast.makeText(this, "MLE suggests you to turn off GPS", Toast.LENGTH_SHORT).show();
    		 Intent i = new
    				 Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    				 startActivity(i);
    	 }
     }
    	
    
    
    public void turnWifiOnOff(){
    	WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	 NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	if (outputArrayFromNN [3]>0.2 && !wifi.isWifiEnabled() )
    	wifi.setWifiEnabled(true);
    	
    	if (outputArrayFromNN [3]<-0.5 && !mWifi.isConnected())
    	wifi.setWifiEnabled(false);
    }
    
    
    //ask user to turn mobiledata button on/off 
    
    public void turnMobileDataOnOff (){
    	
    	 ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
  	   State mobile = conMan.getNetworkInfo(0).getState();
  	  
    	if (outputArrayFromNN [4]>0.7 && (mobile != NetworkInfo.State.CONNECTED)){
    		
    		//"MLE suggest you to turn on mobiledata to receive data if you are not connected to any Wi-Fi"
    		Toast.makeText(this, "MLE suggest you to turn on mobiledata", Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent();
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
    	startActivity(intent);}
    	
    	if (outputArrayFromNN [4]<-0.7 && (mobile == NetworkInfo.State.CONNECTED) )
    	{	//&& (mobile == NetworkInfo.State.CONNECTED)
    		Toast.makeText(this, "MLE suggest you to turn OFF mobiledata", Toast.LENGTH_SHORT).show();
    		Intent intent = new Intent();
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.setAction(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
        	startActivity(intent);
    	}
    }
    
    // Change ringtone volume
    public void changeVolumeUsingNN (){
    	AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	         	
    	 int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    	  		audioManager.setStreamVolume(AudioManager.STREAM_RING, streamMaxVolume, AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
    	  	       	   		int vol= (int) (outputArrayFromNN[0]*8 + 8) ;
    	  	  audioManager.setStreamVolume(AudioManager.STREAM_RING, vol , AudioManager.FLAG_ALLOW_RINGER_MODES|AudioManager.FLAG_PLAY_SOUND);
    	    	    	    	 }
    
    //change vibration state
  
    @SuppressWarnings("deprecation")
	public void vibrationOnOffNN()
    {
    	    	
    	AudioManager audio = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
    	if (outputArrayFromNN [1]>0.7 && audio.getRingerMode()!= AudioManager.RINGER_MODE_VIBRATE )
    	{
    	audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,AudioManager.VIBRATE_SETTING_ON);
    audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,AudioManager.VIBRATE_SETTING_ON);}
    	
    	
    	if (outputArrayFromNN [1]<-0.7 && audio.getRingerMode()== AudioManager.RINGER_MODE_VIBRATE )
    	{
    		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,AudioManager.VIBRATE_SETTING_OFF);
    	audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,AudioManager.VIBRATE_SETTING_OFF);
    	}
    }
    
    
   // private boolean contains(String string) {
		// TODO Auto-generated method stub
	//	return false;
//	}

	/* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
          
   public void detectWifi () {
	   WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	   
	   if (wifi.isWifiEnabled()) {

		   Toast.makeText(this, "wifi Button is enable", Toast.LENGTH_SHORT).show();
		   
		   inputArray[0] = 1;
		   outputArray[3] = 1;
		   
		   ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		   NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		   
		   String wifiName = "null";
		   
		   if (mWifi.isConnected()) {
			   //WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
			   WifiInfo wifiInfo = wifi.getConnectionInfo();
			   
			   wifiName = wifiInfo.getSSID();
			   
			   Toast.makeText(this, "wifi is connected", Toast.LENGTH_SHORT).show();
			   Toast.makeText(this, "wifi name is" + wifiName, Toast.LENGTH_SHORT).show();
		   }
		   
		   int index = 0;	// maximum index = 100
		    	
		   if ( ! wifiNames.contains(wifiName))
		    	wifiNames.add(wifiName);
		   index = wifiNames.indexOf(wifiName);
		    	
		   float wifiNameIndex = (index / 50.0f) - 1;	// scale it down to [-1,1]
		   inputArray[1] = wifiNameIndex;
		   
		   
		   // test
		   for (String s : wifiNames)
			   Log.v("wifi names", "name - " + s);

	   } else {	// when the Wifi is disabled
		   Toast.makeText(this, "wifi Button is disable", Toast.LENGTH_SHORT).show();
		   inputArray[0] = -1;
		   outputArray[3] = -1;
	   }
   }
   
   
   //Check the GPS as an input and output for NN
      public void detectGPS (){ 
	   Log.v("GPS", "start detecting...");
	   	   final LocationManager manager = 
			   (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	  
	   if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		   Toast.makeText(this, "GPS enabled", Toast.LENGTH_SHORT).show();
		   inputArray[2]=1;
		   outputArray[2]=1;
		      } 
	   	   else {
		   Toast.makeText(this, "GPS disabled", Toast.LENGTH_SHORT).show();
		   inputArray[2]=-1;
		   outputArray[2]=-1;
	   }  
	    }
      
  //GPS is static or moving as an input for NN
   public void GPSpeed (){
	   
	   if (System.currentTimeMillis() - lastGPSSpeed > 1000 * 2 * 60)
		  availableSpeed = 0;
	   	   
		Toast.makeText(this, "Current speed:" + availableSpeed, Toast.LENGTH_SHORT).show();
		
		// inputArray[3] -1 for static; 1 for moving
		
		if  (availableSpeed < 2)	
			inputArray[3]=-1;
		else 
			inputArray[3]=1;
		}
   
   
   //read GSM ID as an input for NN
   public void GSMID (){
	   TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	   GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

	   int cellid= cellLocation.getCid();
	  // int celllac = cellLocation.getLac();
	   Toast.makeText(this, "GSM-ID:" + cellid, Toast.LENGTH_SHORT).show();
	  // Toast.makeText(this, "GSM-Loc:" + celllac, Toast.LENGTH_SHORT).show();
	  // Toast.makeText(this, "GSM-Loc:" + cellLocation.toString(), Toast.LENGTH_SHORT).show();
	   int index = 0;	// maximum index = 100
   	
	   if ( ! cellidNames.contains(cellid))
		   cellidNames.add(cellid);
	   index = cellidNames.indexOf(cellid);
	    	
	   float cellidIndex = (index / 50.0f) - 1;	// scale it down to [-1,1]
	   inputArray[4] = cellidIndex;
	 		 		   	     }
   
   //curent time and weekday as inputs for NN
   public void CurentTime (){
	  
	  int min=(int)(System.currentTimeMillis()/(1000*60)%60);
	  int hours= (int) ((System.currentTimeMillis() / (1000*60*60)) % 24);
 	   Toast.makeText(this, "current hour:" +  hours, Toast.LENGTH_SHORT).show();
 	   Toast.makeText(this, "current min:" +  min, Toast.LENGTH_SHORT).show();
 	   int timeInMinues = hours * 60 + min;
 	   
 	   inputArray[5] = timeInMinues / 720.0f - 1;
 	 
 	  Calendar c = Calendar.getInstance();
 	  
 	 java.util.Date date = c.getTime();
 	 // 3 letter name form of the day
 	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
 	Toast.makeText(this, "day of week:" + new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime())+dayOfWeek, Toast.LENGTH_SHORT).show();
 	inputArray[6]= ((dayOfWeek+1)-4.0f) /4.0f;
   }
   
 
   //check volume as an output for NN
   public void volume (){
   AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
   int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
   //outputArray[0]=(currentVolume + 1)/8 - 1;
   outputArray[0]=currentVolume/8.0f-1.0f;
   Toast.makeText(this, "current valume:" + currentVolume, Toast.LENGTH_SHORT).show();
  
        }
   
  
   //check the vibration as an output for NN
   public void vibration (){
	   	   AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	   if(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
		   Toast.makeText(this, "current valume:Vobration is ON" , Toast.LENGTH_SHORT).show();  
	   outputArray[1]=1;}
	   else 
	   {
		   {Toast.makeText(this, "current valume:Vobration is OFF" , Toast.LENGTH_SHORT).show();  
		   outputArray[1]=-1;}
		   }
   }
   
   
 
   //check the mobile data as an output for NN
   public void mobiledata (){
	   ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	   State mobile = conMan.getNetworkInfo(0).getState();
	   if (mobile == NetworkInfo.State.CONNECTED) 
	   {
		   Toast.makeText(this, "Mobile data is ON", Toast.LENGTH_SHORT).show();
		   outputArray[4]=1;
	   }
	   else 
		   {
		   Toast.makeText(this, "Mobile data is OFF", Toast.LENGTH_SHORT).show();
		   outputArray[4]=-1;
		   }
		   }
	   
   
   
	
   
   // write text to file 
	@SuppressLint("SdCardPath")
	
	public void writeBtn() {
				// add-write text into file
		try {
			
			updateInputs();
			updateOutputs();
					    
		        FileOutputStream fOut = new FileOutputStream("/sdcard/test-m.txt", true);
		        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		       
		        for (int i = 0; i < inputArray.length; i++)
		        	myOutWriter.write(inputArray[i] + " ");
		        myOutWriter.write("\n");
		        for (int i = 0; i < outputArray.length; i++)
		        	myOutWriter.write(outputArray[i] + " ");
		        myOutWriter.write("\n");
		        
		        myOutWriter.close();
		        fOut.close();
		        //display file saved message
			Toast.makeText(this, "File saved successfully!",Toast.LENGTH_SHORT).show();
			FileLineNumber+=1; // count the line number of first data file to give to NN as input file
			 
		} catch (Exception e) {
			e.printStackTrace();
		}}

	
	@SuppressLint("SdCardPath")
	public void creatNNInputFile()
	{
		try {
	
	 FileOutputStream fOut = new FileOutputStream("/sdcard/inputNN.txt", true);
     OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
 	 myOutWriter.write(FileLineNumber + " ");
 	 myOutWriter.write(7 + " ");// 7 inputs to nn
 	 myOutWriter.write(5 + " ");// 5 output for nn
 	 myOutWriter.write("\n");
 	
 	
 	BufferedReader in = new BufferedReader(new FileReader("/sdcard/test-m.txt"));
    String str;

    while((str = in.readLine()) != null){
    	
    	myOutWriter.write(str + "\n");
    }
 	
	
 	 in.close();
 	 myOutWriter.close();
 	
     
	}catch (Exception e) {
		e.printStackTrace();
	}
		}
	
   //clear the first data file 
	@SuppressLint("SdCardPath")
	public void Clear(){    

	     PrintWriter writer;
	    try {
	        writer = new PrintWriter("/sdcard/test-m.txt");
	         writer.print("");
	         writer.close();
	                 
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	     }
	
	
	 //clear the NN data file 
		@SuppressLint("SdCardPath")
		public void clearNNFile(){    

		     PrintWriter writer;
		    try {
		         writer = new PrintWriter("/sdcard/inputNN.txt");
		         writer.print("");
		         writer.close();
		         
		    } catch (FileNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		     }
	
	
   private Activity getActivity() {
		// TODO Auto-generated method stub
		return null;
	}
   
  	static {
        System.loadLibrary("hello-jni");
    }
  	
  	
	@Override
	public void onClick(View v) {
			if (v.getId() == R.id.button1)
			            detectGPS();
				else if (v.getId() == R.id.button2)
		     	      GPSpeed ();
				else if (v.getId()==R.id.button3)
		              detectWifi ();
				else if (v.getId()==R.id.button4)
					    GSMID ();
				else if (v.getId()==R.id.button5)
					CurentTime ();
				else if (v.getId()==R.id.button6)
		            volume ();
				else if (v.getId()==R.id.button7)
					vibration ();
				else if (v.getId()==R.id.button8)
					mobiledata ();	
				else if (v.getId()==R.id.button9)
		     		{
			         
					clearNNFile();
					writeBtn();
					creatNNInputFile();
		    	
		    	
		    	//tv.setText( stringFromJNI() );
		    	}
			
			else if (v.getId()==R.id.button10){
				ManualOutputSetting();
				}
							 
			else if (v.getId()==R.id.button11)
				
				automaticOutputSetting();
		
			else if (v.getId()==R.id.button13)
			{
			
				Clear();
				clearNNFile();
				FileLineNumber= 0;
								}
	}
	
	public void updateInputs() {
		detectGPS();
		GPSpeed ();
		detectWifi ();
		GSMID ();
		CurentTime ();
		
		
	}
	
	public void updateOutputs() {
		volume ();
		vibration ();
		mobiledata ();
			}
	
	public void OutputsFromNN (){
		turnGPSOnOff();
		turnWifiOnOff();
		turnMobileDataOnOff ();
		changeVolumeUsingNN ();
		vibrationOnOffNN();
	}
	public void ManualOutputSetting()
	{
		 
		
		OutputsFromNN ();
		ArrayFromJNI();
	
	}
	public void automaticOutputSetting(){
		
		timer.cancel();
		creatNNInputFile();
		timer1.schedule(new  RepetitiveTask1(),0, 60*5*1000);
					
	}

    public void onDestroy1() {
		super.onDestroy();
				timer1.cancel();
			}

}
