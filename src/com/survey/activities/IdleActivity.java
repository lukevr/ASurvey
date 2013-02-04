package com.survey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.survey.ApplicationSettings;
import com.survey.ApplicationState;
import com.survey.R;
import com.survey.SurveyApplication;
import com.survey.gui.StatusImageView;

public class IdleActivity extends BasicSurveyActivity{
    
    private TextView textView;
    private static final String TAG = "IdleActivity";
    private PowerManager.WakeLock wl = null;
    
    private StatusImageView chargeImage;
    private StatusImageView wifiImage;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {       
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.screen_0_layout);        

        textView = (TextView) findViewById(R.id.textView1);
        textView.setTextSize((float) ApplicationSettings.textSize);
        
        chargeImage = (StatusImageView) findViewById(R.id.charge_icon);
        chargeImage.addStateBitmapDrawable(R.drawable.icon_charge0_256);
        chargeImage.addStateBitmapDrawable(R.drawable.icon_charge1_256);
        chargeImage.addStateBitmapDrawable(R.drawable.icon_charge2_256);
        chargeImage.addStateBitmapDrawable(R.drawable.icon_charge3_256);
        
        wifiImage = (StatusImageView) findViewById(R.id.wifi_icon);
        wifiImage.addStateBitmapDrawable(R.drawable.icon_connect0_256);
        wifiImage.addStateBitmapDrawable(R.drawable.icon_connect3_256);
        
        Button closeBtn = (Button) findViewById(R.id.close_button);
        //closeBtn.setBitmapResources(R.drawable.icon_close_256, R.drawable.icon_close_pressed_256, R.drawable.icon_close_pressed_256);        
        closeBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                SurveyApplication application = (SurveyApplication) getApplication();
                application.startExitActivity();
            }
            
        });
        
        SurveyApplication application = (SurveyApplication) getApplication();
        // let's reset state if we are back to context while starting application
        application.resetState();
        
        ApplicationState state = (ApplicationState) application.getApplicationState();
        setStateData(state);
        
        processIntent(getIntent());
        
    }
    
    @Override
    public void setStateData(ApplicationState state) {
        
        if(state != null){
            
            // Set new text
            if(state.getText() != null){                
                textView.setText(state.getText());
            }else{
                textView.setText("");
            }
            
        }
        
    }
    
    protected void onNewIntent (Intent intent){        
        processIntent(intent);
    }
    
    private void processIntent(Intent intent){
        
        SurveyApplication application = (SurveyApplication) getApplication();
        chargeImage.setProgress(application.getChargeLevel());
        double wifiLevel = application.getWifiLevel();        
        wifiImage.setProgress(wifiLevel);
        
        // Apply new settings
        textView.setTextSize((float) ApplicationSettings.textSize);        
        
        if(intent != null && intent.getExtras() != null){
            
            ApplicationState state = (ApplicationState) intent.getExtras().getSerializable(SurveyApplication.EXTRA_STATE);
            setStateData(state);
            
            if(intent.getExtras().getBoolean(SurveyApplication.EXTRA_FINISH, false)){
                //exitHandler.sendEmptyMessageDelayed(0, 500);
                finish();
            }
            
        }
        
    }

}