package com.survey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.survey.activities.IdleActivity;

public class BootCompletedReceiver extends BroadcastReceiver {
    
    public static final String Screenoff = "android.intent.action.SCREEN_OFF";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        //if (intent.getAction().equals(Screenoff)) return;
        
        Intent startActivityIntent = new Intent(context, IdleActivity.class);
        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
    }
    
}