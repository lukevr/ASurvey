package com.survey.comunication;

import com.survey.ApplicationState;

public class OutupPackagesFormer {
  
    private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
    private static final String TAG_REQUEST = "request";
    private static final String TAG_HWID = "hwid";
    private static final String TAG_STATE = "state";
    private static final String TAG_CTLVAL = "ctlval";    
    
//--------------------------------------------------------------------------    

    public static String formAppStatePackage(ApplicationState state){
        //if(state != null){
            return formStatePackage(state);
        //}else{
          //  return null;
        //}
    }
    
//---------------------------------------------------------------------------    
    
    private static String formStatePackage(ApplicationState state){
        
        String result = XML_HEADER;
        result = result + openTag(TAG_REQUEST);
        
        result = result + openTag(TAG_HWID);
        result = result + ApplicationState.getHwid();
        result = result + closeTag(TAG_HWID);
        
        result = result + openTag(TAG_STATE);
        result = result + String.valueOf(state.getState());
        result = result + closeTag(TAG_STATE);
        
        // we do not send ctval in Idle State
        if(state.getState() != 0) {
            result = result + openTag(TAG_CTLVAL);
            result = result + state.getResult();
            result = result + closeTag(TAG_CTLVAL);
        }
        
        result = result + closeTag(TAG_REQUEST);
        
        return result;
    }
   
//---------------------------------------------------------------------------
    
    private static String openTag(String tag){
        return "<" + tag + ">";
    }
    
    private static String closeTag(String tag){
        return "</" + tag + ">";
    }
    
}