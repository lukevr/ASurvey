package com.survey.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.survey.ApplicationSettings;
import com.survey.ApplicationState;
import com.survey.R;
import com.survey.SurveyApplication;

public class SettingsActivity extends Activity{
	
    private EditText timeoutEdit;
    private EditText passwordEdit;
    private EditText wifiNameEdit;
    private EditText yesEdit;
    private EditText noEdit;
    private EditText textSizeEdit;
    private EditText serverURLEdit;
    private TextView deviceId;
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.provider.Settings.System.putInt(this.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
        
        setContentView(R.layout.screen_settings_layout);  
        
        timeoutEdit = (EditText) findViewById(R.id.timeout);
        passwordEdit = (EditText) findViewById(R.id.password);
        wifiNameEdit = (EditText) findViewById(R.id.wifi_name);
        yesEdit = (EditText) findViewById(R.id.yes_button_text);
        noEdit = (EditText) findViewById(R.id.not_button_text);
        textSizeEdit = (EditText) findViewById(R.id.text_size);
        serverURLEdit = (EditText) findViewById(R.id.server_url);
        deviceId = (TextView) findViewById(R.id.device_id);
        
        timeoutEdit.setText(String.valueOf(ApplicationSettings.timeOut));
        passwordEdit.setText(ApplicationSettings.password);
        wifiNameEdit.setText(ApplicationSettings.wifiName);
        yesEdit.setText(ApplicationSettings.yesButtonText);
        noEdit.setText(ApplicationSettings.noButtonText);
        textSizeEdit.setText(String.valueOf(ApplicationSettings.textSize));
        serverURLEdit.setText(ApplicationSettings.serverURL);
        SurveyApplication application = (SurveyApplication) getApplication();
        deviceId.setText(ApplicationState.getHwid());
        
        Button saveBtn = (Button) findViewById(R.id.save_button);
        Button exitBtn = (Button) findViewById(R.id.exit_button);
        
        saveBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                
                ApplicationSettings.timeOut = Integer.parseInt(timeoutEdit.getText().toString());
                ApplicationSettings.password = passwordEdit.getText().toString();
                ApplicationSettings.wifiName = wifiNameEdit.getText().toString();
                ApplicationSettings.yesButtonText = yesEdit.getText().toString();
                ApplicationSettings.noButtonText = noEdit.getText().toString();
                ApplicationSettings.textSize = Integer.parseInt(textSizeEdit.getText().toString());
                ApplicationSettings.serverURL = serverURLEdit.getText().toString();
                
                ApplicationSettings.save(SettingsActivity.this);
                finish();
            }
            
        });
        
        exitBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
            
        });
        
        
        application.setUpdatingActivites(false);
        
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	SurveyApplication application = (SurveyApplication) getApplication();   
    	application.exitApplication();
    }    
	
    
}