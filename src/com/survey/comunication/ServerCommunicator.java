package com.survey.comunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.util.Log;

import com.survey.ApplicationSettings;
import com.survey.ApplicationState;
import com.survey.SurveyApplication;

public class ServerCommunicator extends Thread {

    boolean running = false;
    Application application;

    public ServerCommunicator(Application application) {
        this.application = application;
        
    }
    
    synchronized public void setRunning(boolean b) {
        this.running = b;
    }
    synchronized public boolean getRunning() {
        return this.running;
    }

    @Override
    public void run() {

        while (getRunning()) {
            try {
                
                String xmlToServer = ((SurveyApplication) application).getApplicationStateXml();
                if(((SurveyApplication) application).isNetworkProperWifi()){
                    String xmlFromServer = postAndReceiveDataFromServer(xmlToServer);
                    if(xmlFromServer != null) {
                        ((SurveyApplication) application).postMessage(xmlFromServer, SurveyApplication.MESSAGE_NEW);
                    } 
                } else {
                    ((SurveyApplication) application).postMessage(null, SurveyApplication.MESSAGE_NO_NETWORK);
                }
                
               
                Log.i("ServerCommunicator", "Tick...");
                sleep(ApplicationSettings.timeOut);                
                
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
            
        }
    }
    
    private String postAndReceiveDataFromServer(String xmlToServer) {
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ApplicationSettings.serverURL);
        
        try {
            StringEntity se = new StringEntity(xmlToServer, HTTP.UTF_8);
            se.setContentType("text/xml");
            httppost.setEntity(se);

            HttpResponse httpresponse = httpclient.execute(httppost);
            return inputStreamToString(httpresponse.getEntity().getContent());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
  
    /*
     * We need this due to encoding of the cyrillic text in the messages from server
     */
    private String inputStreamToString(final InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "windows-1251"), 2 * 1024);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
    }
    
//-----------------------------------------------------------------------------------------------------------------
   
    // Forsed messages will be send in other thread
    
    private ExecutorService senderPool = Executors.newFixedThreadPool(1);
    
    public void forceSendDataToServer(ApplicationState state) {
        
        String xmlToServer = OutupPackagesFormer.formAppStatePackage(state);
        ForceDataSender sender = new ForceDataSender();
        sender.xmlToServer = xmlToServer;
        sender.communicator = this;
        
        senderPool.execute(sender);
    
    }
    
    
    private static class ForceDataSender implements Runnable{

        public String xmlToServer;
        public ServerCommunicator communicator;
        
        @Override
        public void run() {
            communicator.postAndReceiveDataFromServer(xmlToServer);
            Log.i("ServerCommunicator", "Forced Message Sent to Server!");
        }
        
        
    }
    
}
