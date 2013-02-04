package com.survey;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.survey.activities.ExitingActivity;
import com.survey.activities.IdleActivity;
import com.survey.activities.RatingActivity;
import com.survey.activities.SettingsActivity;
import com.survey.activities.YesNoActivity;
import com.survey.comunication.InputPackageParser;
import com.survey.comunication.OutupPackagesFormer;
import com.survey.comunication.ServerCommunicator;

public class SurveyApplication extends Application {

    public static final String EXTRA_STATE = "State";
    public static final String EXTRA_FINISH = "Finish";
    
    protected static final int STATE_NOT_INITIALIZED = 0;    
    public static final int MESSAGE_NEW = 1;
    public static final int MESSAGE_NO_NETWORK = 2;
    
    private ApplicationState state;
    private ServerCommunicator serverCommunicator;
    private int serverCommunicatorCounter = 0;
    
    private double chargeLevel = 1;
    
    private boolean updatingActivites = true;
    
    private ArrayList<Activity> activities = new ArrayList<Activity>();
    
    private BroadcastReceiver chargeInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            chargeLevel = (double)level / 100;
            Log.e("charge", "level = " + String.valueOf(level));
            if(getUpdatingActivites()){
                informActivity(null);
            }
        }
    };
    
    @Override
    public void onCreate() {  
        
        state = new ApplicationState();
        String deviceId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);        
        state.setHwid(deviceId);
        
    }
    
    public List<Activity> getActivities(){
        return activities;
    }
    
    public double getChargeLevel(){
        return chargeLevel;
    }
    
    public void setUpdatingActivites(boolean v){
        updatingActivites = v;        
    }
    
    public boolean getUpdatingActivites(){
        return updatingActivites;
    }
    
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
        
            switch (msg.what) {
            case MESSAGE_NEW : 
                
                ApplicationState newState = InputPackageParser.parseApplicationState((String) msg.obj);
                if(newState != null) {
                    // We got potential new application state from server, let's try to start new activity
                    // if it is really new state
                    setApplicationState(newState);
                } else {
                    // Settings has been updated, we need to reset current activity to get new settings working
                    // and let's save this new settings into the file
                    ApplicationSettings.save(SurveyApplication.this);
                    setApplicationState(state);
                }
                return;
                
            case MESSAGE_NO_NETWORK :
                ApplicationState idle = new ApplicationState();
                setApplicationState(idle);
                return;
            }
        }        
    };
    
    public void postMessage(String xmlFromServer, int messageID) {
        
        Message msg = handler.obtainMessage(messageID);
        msg.arg1 = 0;
        msg.arg2 = 0;
        msg.obj = xmlFromServer;
        msg.sendToTarget();
                
    }
    
   
    public void startServerCommunicator() {

    	if(serverCommunicatorCounter == 0){
    	    
    	    ApplicationSettings.load(this);
    	    
    		serverCommunicator = new ServerCommunicator(this);
    		serverCommunicator.setRunning(true);
    		serverCommunicator.start();
    		Log.e("SurveyApplication", "Communicator started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		registerReceiver(chargeInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    		
    	}
    	
    	serverCommunicatorCounter++;
        
    }
    
    public void stopServerCommunicator() {
    	serverCommunicatorCounter--;
    	if(serverCommunicatorCounter == 0){
    		serverCommunicator.setRunning(false);
    		Log.e("SurveyApplication", "Communicator STOPED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    		unregisterReceiver(chargeInfoReceiver);
    	}
    }
    
    public ServerCommunicator getServerCommunicator(){
        
        return serverCommunicator; 
    }
    
    public String getApplicationStateXml() {        
        return OutupPackagesFormer.formAppStatePackage(state);       
    }    
    

    /**
     * Checks if we have a valid Internet Connection on the device.
     */

    public double getWifiLevel() {
        if(isNetworkProperWifi()){
            return 1;
        }else{
            return 0;
        }
    }
    
    /**
     * Checks if we have a valid Wifi Internet Connection on the device.
     */

    public boolean isNetworkProperWifi() {
        
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
        
        if(!wifiMgr.isWifiEnabled()){  
            
            wifiMgr.setWifiEnabled(true);
            
        }

        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        State wifi = conMan.getNetworkInfo(1).getState();

        if (wifi == NetworkInfo.State.CONNECTED
                || wifi == NetworkInfo.State.CONNECTING) {

            
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            String name = wifiInfo.getSSID();

            if(name != null && name.equals(ApplicationSettings.wifiName) || ApplicationSettings.wifiName.equals("any")) {
                return true;
            }
        }
                
        return false;

    }
    
//-----------------------------------------------------------------------
    
    public ApplicationState getApplicationState(){
        return state;
    }
    
    public void setApplicationState(ApplicationState state){
        
        if(getUpdatingActivites()){
            
            Log.i("SurveyApplication", "updating activities...");
        
            informActivity(null);
            boolean resetScreen = needResetScreen(state, this.state);
            
            if(resetScreen){
                this.state = state;
            
                switch(state.getState()) {
                
                case ApplicationState.STATE_IDLE:
                    startIdleActivity(state);
                    this.state.setResult("");
                    return;
                
                case ApplicationState.STATE_YES_NO:
                    startYesNoActivity(state);
                    // set default result
                    this.state.setResult("100");
                    return;
                    
                case ApplicationState.STATE_ONE_NUMBER:
                    startRatingActivity(state);
                    // set default result
                    this.state.setResult("-101,-102,-103,-104,-105,-106,-107,-108,-109");
                    return;
                    
                case ApplicationState.STATE_SEVERAL_NUMBERS:
                    startRatingActivity(state);
                    // set default result
                    this.state.setResult("-101,-102,-103,-104,-105,-106,-107,-108,-109");
                    return;
                    
                case ApplicationState.STATE_EXIT:
                    exitApplication();
                    return;
                }
                
            }
        
        }
        
    }
    
    private boolean needResetScreen(ApplicationState newState, ApplicationState currState){

        if(newState.getState() != currState.getState() || newState.getText()!= null && currState.getText() != null && !newState.getText().equals(currState.getText())) {
        
            return true;
        }
        
        return false;
    }
    
    private void startIdleActivity(ApplicationState state){
        Intent intent = new Intent(this, IdleActivity.class);
        intent.putExtra(EXTRA_STATE, state);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
    
    private void startYesNoActivity(ApplicationState state){
        Intent intent = new Intent(this, YesNoActivity.class);
        intent.putExtra(EXTRA_STATE, state);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
    
    private void startRatingActivity(ApplicationState state){
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra(EXTRA_STATE, state);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
        this.startActivity(intent);
    }
    
    public void startExitActivity(){
        Intent intent = new Intent(this, ExitingActivity.class);
        intent.putExtra(EXTRA_STATE, state);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
        this.startActivity(intent);
    }
    
    public void startSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
    
//----------------------------------------------------------------------------------
    
    
    private void informIdleActivity(Bundle extras){
        Intent intent = new Intent(this, IdleActivity.class);
        if(extras != null){
            intent.putExtras(extras);
        }        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
    
    private void informYesNoActivity(Bundle extras){
        Intent intent = new Intent(this, YesNoActivity.class);        
        if(extras != null){
            intent.putExtras(extras);
        }        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
    
    private void informRatingActivity(Bundle extras){
        Intent intent = new Intent(this, RatingActivity.class);
        if(extras != null){
            intent.putExtras(extras);
        }        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
        this.startActivity(intent);
    }
    
    private void informActivity(Bundle extras){
        
        switch(state.getState()) {
        case ApplicationState.STATE_IDLE:
            informIdleActivity(extras);
            return;
        case ApplicationState.STATE_YES_NO:
            informYesNoActivity(extras);
            return;
        case ApplicationState.STATE_ONE_NUMBER:
            informRatingActivity(extras);
            return;
        case ApplicationState.STATE_SEVERAL_NUMBERS:
            informRatingActivity(extras);
            return;
        }
    }
    
    
    public void exitApplication(){        
        
        for(Activity activity : activities){
            activity.finish();
        }
       
    }

    public void resetState() {
        
        state = new ApplicationState();
        
    }    
    
}
