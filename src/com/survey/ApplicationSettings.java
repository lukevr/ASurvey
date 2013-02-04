package com.survey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ApplicationSettings {
    
    private static final String TAG = "ApplicationSettings";
    
    private static final String TAG_TIMEOUT = "timeout";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_WIFI = "wifi";
    private static final String TAG_YES_BUTTON_TEXT = "btnyes";
    private static final String TAG_NO_BUTTON_TEXT = "btnno";
    private static final String TAG_TEXT_SIZE = "testsize";
    private static final String TAG_SERVER_URL = "serverurl";
    
    // Default settings
    public static int timeOut = 3000;
    public static String password = "qwerty";
    public static String wifiName = "any";
    public static String yesButtonText = "Yes";
    public static String noButtonText = "No";
    public static int textSize = 20;
    public static String serverURL = "http://94.45.131.226/get_task.php";    
    
    // Load saved settings from file
    public static void load(Context context) {
        String basePath = getBasePath(context);
        File baseDir = new File(basePath);
        baseDir.mkdirs();
        
        while(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            
            try {
                Thread.sleep(500);
                Log.i(TAG, "Waiting for SD Card Ready");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        File file = new File(basePath + "settings.xml");
        if(file.exists()){
            parseSettingsFromFile(file);
            Log.i(TAG, "ApplicationSettings LOADED");
        }else{
            Log.i(TAG, "ApplicationSettings NOT LOADED, DEFAULTS ARE TAKEN");
        }
    }
    
    // Save settings to file
    public static void save(Context context) {
        String basePath = getBasePath(context);
        File baseDir = new File(basePath);
        baseDir.mkdirs();
        
        String content = formSettingsFileString();
        
        File file = new File(basePath + "settings.xml");
        writeBytesToFile(file, content.getBytes());
        
        Log.i(TAG, "ApplicationSettings SAVED");
        
    }
    
    private static String getBasePath(Context context){
        return Environment.getExternalStorageDirectory()
        .getAbsolutePath()
        + "/Android/data/"
        + context.getPackageName() + "/cache/";        
    }
    
    private static String formSettingsFileString(){
        
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        
        result = result + "<settings>";
        
        result = result + "<" + TAG_TIMEOUT + ">" + timeOut + "</" + TAG_TIMEOUT + ">";
        result = result + "<" + TAG_PASSWORD + ">" + password + "</" + TAG_PASSWORD + ">";
        result = result + "<" + TAG_WIFI + ">" + wifiName + "</" + TAG_WIFI + ">";
        
        result = result + "<" + TAG_YES_BUTTON_TEXT + ">" + yesButtonText + "</" + TAG_YES_BUTTON_TEXT + ">";
        result = result + "<" + TAG_NO_BUTTON_TEXT + ">" + noButtonText + "</" + TAG_NO_BUTTON_TEXT + ">";
        result = result + "<" + TAG_TEXT_SIZE + ">" + textSize + "</" + TAG_TEXT_SIZE + ">";
        result = result + "<" + TAG_SERVER_URL + ">" + serverURL + "</" + TAG_SERVER_URL + ">";
        
        result = result + "</settings>";
        
        return result;
        
    }
    
    private static void parseSettingsFromFile(File file){
        
        Document doc = getDocFromFile(file);
        
        Node nodeTimeout = doc.getElementsByTagName(TAG_TIMEOUT).item(0);
        Node nodePassword = doc.getElementsByTagName(TAG_PASSWORD).item(0);
        Node nodeWifi = doc.getElementsByTagName(TAG_WIFI).item(0);
        Node nodeYes = doc.getElementsByTagName(TAG_YES_BUTTON_TEXT).item(0);
        Node nodeNo = doc.getElementsByTagName(TAG_NO_BUTTON_TEXT).item(0);
        Node nodeTextSize = doc.getElementsByTagName(TAG_TEXT_SIZE).item(0);
        Node nodeserverURL = doc.getElementsByTagName(TAG_SERVER_URL).item(0);
        
        if(nodeTimeout != null){
            timeOut = Integer.parseInt(getNodeText(nodeTimeout));
        }
        
        if(nodeTimeout != null){
            password = getNodeText(nodePassword);
        }
        
        if(nodeTimeout != null){
            wifiName = getNodeText(nodeWifi);
        }
        
        if(nodeYes != null){
            yesButtonText = getNodeText(nodeYes);
        }
        
        if(nodeNo != null){
            noButtonText = getNodeText(nodeNo);
        }
        
        if(nodeTextSize != null){
            textSize = Integer.parseInt(getNodeText(nodeTextSize));
        }
        
        if(nodeserverURL != null){
            serverURL = getNodeText(nodeserverURL);
        }
        
    }
    
    private static final String getNodeText(Node node){
        
        if(node != null){
            
            Node child = node.getFirstChild();
            if(child != null && child.getNodeType() == Node.TEXT_NODE){
                return child.getNodeValue();
            }
            
        }  
        
        return null;
        
    }
    
    private static Document getDocFromFile(File file){

        Document doc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
            try {

                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(file);

            } catch (ParserConfigurationException e) {
                System.out.println("XML parse error: " + e.getMessage());
                return null;
            } catch (SAXException e) {
                System.out.println("Wrong XML file structure: " + e.getMessage());
                return null;
            } catch (IOException e) {
                System.out.println("I/O exeption: " + e.getMessage());
                return null;
            }

            return doc;

   }
    
    private static void writeBytesToFile(File file, byte[] bytes) {

        try {
            
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    

}