package com.survey.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.survey.ApplicationSettings;
import com.survey.R;
import com.survey.SurveyApplication;

public class ExitingActivity extends BasicSurveyActivity{
    
    private EditText editText;
    private CheckBox cbOpenSettings;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        android.provider.Settings.System.putInt(this.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
        
        setContentView(R.layout.screen_exit_layout);
        
        editText = (EditText) findViewById(R.id.editText1);
        Button okBtn = (Button) findViewById(R.id.button1);
        Button cancelBtn = (Button) findViewById(R.id.button2);
        cbOpenSettings = (CheckBox) findViewById(R.id.checkBox1);
        
        okBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if(text.equals(ApplicationSettings.password) || ApplicationSettings.password == null){                    
                    finish();
                    SurveyApplication application = (SurveyApplication) getApplication();
                    if(cbOpenSettings.isChecked()){
                    	application.startSettingsActivity();
                    }else{
                    	application.exitApplication();
                    }
                }
                
            }            
            
        });
        
        cancelBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();                
            }
            
        });
        
        
    }
    
    @Override
    public void onResume(){
        super.onResume();
        SurveyApplication application = (SurveyApplication) getApplication();
        application.setUpdatingActivites(false);
    }
    
    @Override
    public void onPause(){
        super.onPause();
        SurveyApplication application = (SurveyApplication) getApplication();
        application.setUpdatingActivites(true);        
    }    

}