package com.survey;

import java.io.Serializable;

public class ApplicationState implements Serializable{
    
    public static final int STATE_IDLE = 0;
    public static final int STATE_YES_NO = 1;
    public static final int STATE_ONE_NUMBER = 5;
    public static final int STATE_SEVERAL_NUMBERS = 8;
    public static final int STATE_EXIT = 200;
    
    private int state = STATE_IDLE;
    private int alert = 0;
    private int range = 0;
    private static String hwid = "0050BF7A68220108FAC21F110700FB4A";
    private String text  = "Searching for WiFi...";
    private String result = "";
    
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    public static String getHwid() {
        return hwid;
    }
    public void setHwid(String hwid) {
        ApplicationState.hwid = hwid;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {        
        this.text = text;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public int getAlert() {
        return alert;
    }
    public void setAlert(int alert) {
        this.alert = alert;
    }
    public int getRange() {
        return range;
    }
    public void setRange(int range) {
        this.range = range;
    }
    public void copyState(ApplicationState state2) {
        this.state = state2.getState();
        this.alert = state2.getAlert();
        this.range = state2.getRange();
        this.result = state2.getResult();        
    }

}
