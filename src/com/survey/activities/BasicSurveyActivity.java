package com.survey.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.survey.ApplicationState;
import com.survey.SurveyApplication;

public class BasicSurveyActivity  extends Activity{
    private static final String TAG = "SurveyActivity";
    private PowerManager.WakeLock wl = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        // Stay power on while activity active, use wl.release(); for releasing this mode
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        wl.acquire();
                
        // Do not lock screen when power off/on pressed
        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        
        SurveyApplication application = (SurveyApplication) getApplication();
    	application.startServerCommunicator();
    	
    	application.setUpdatingActivites(true);
    	application.getActivities().add(this);
        
       
    }
    
    public void setStateData(ApplicationState state) {        
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	SurveyApplication application = (SurveyApplication) getApplication();
    	application.getActivities().remove(this);
    	application.stopServerCommunicator();
    	wl.release();
    }
   
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onAttachedToWindow()
     * Home button disable
     */
    public void onAttachedToWindow()
    {  
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
        
    }
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed Called and skiped");
       // super.onBackPressed();

    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            Log.d(TAG, "Menu Pressed and skiped");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            Log.d(TAG, "Search Pressed and skiped");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.d(TAG, "Volume Down Pressed and skiped");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.d(TAG, "Volume Up Pressed and skiped");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_POWER) {
            Log.d(TAG, "Power Pressed and skiped");
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }


}