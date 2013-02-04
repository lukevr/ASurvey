package com.survey.comunication;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.survey.ApplicationSettings;
import com.survey.ApplicationState;

public class InputPackageParser {
    
    private static final String PARAMETER = "parameter";
    private static final String TYPE = "type";
    private static final String TEXT = "text";
    private static final String ALERT = "alert";
    private static final String RANGE = "range";
    
    private static final String SETTINGS = "settings";
    private static final String TIMEOUT = "timeout";
    private static final String PASSWORD = "password";
    private static final String WIFI = "wifi";
    private static final String YESTITLE = "yestitle";
    private static final String NOTITLE = "notitle";
    private static final String TEXTSIZE = "textsize";
    private static final String SERVERURL = "serverurl";

//---------------------------------------------------------------------------    
    
    public static ApplicationState parseApplicationState(String xmlString){
        
        // fix due to incorrect XML syntax from server
        xmlString = xmlString.replace("<xml version=\"1.0\">", "<?xml version=\"1.0\"?>");
        xmlString = xmlString.replace(" \n<?", "<?");
        xmlString = xmlString.replace(" <?xml", "<?xml");
        
        Document doc = XMLfromString(xmlString);
        
        NodeList nodes = doc.getElementsByTagName(PARAMETER);
        
        int i = nodes.getLength();
        ApplicationState state = new ApplicationState();

        if(i == 0) {
         // this is new settings, not new application state
            boolean serverUrlChanged = parseAndSetApplicationSettings(doc);
            if(serverUrlChanged) {

                // settings server url has been changed, so we'll start exit from application.
                state.setState(ApplicationState.STATE_EXIT);
                return state;
                
            } else {
            
                // Application state has not been changed, just new settings has been applied, so we'll return null.
                return null;
                
            }
            
        }
        
        
        // We have new Application state, let's parse it
        
        Element e = (Element)nodes.item(0);
        
        String newState = getValue(e, TYPE);
        if(newState != null) {
            state.setState(Integer.parseInt(newState));
        }
        
        String text = getValue(e, TEXT);
        if(text != null) {
            
            state.setText(text);
        }
        
        String alert = getValue(e, ALERT);
        if(alert != null) {
            state.setAlert(Integer.parseInt(alert));
        }
        
        String range = getValue(e, TYPE);
        if(range != null) {
            state.setRange(Integer.parseInt(range));
        }

        return state;
        
    }
    
    private static boolean parseAndSetApplicationSettings(Document doc){
        
        boolean serverUrlChanged = false;
        
        NodeList nodes = doc.getElementsByTagName(SETTINGS);
        
        int i = nodes.getLength();
        
        if(i == 0)
            return serverUrlChanged;
        
        Element e = (Element)nodes.item(0);
        
        String timeout = getValue(e, TIMEOUT);
        if(timeout != null) {
            ApplicationSettings.timeOut = Integer.parseInt(timeout);
        }
        
        String password = getValue(e, PASSWORD);
        if(password != null) {
            ApplicationSettings.password = password;
        }
        
        String wifiName = getValue(e, WIFI);
        if(wifiName != null) {
            ApplicationSettings.wifiName = wifiName;
        }
        
        String yesButtonText = getValue(e, YESTITLE);
        if(yesButtonText != null) {
            ApplicationSettings.yesButtonText = yesButtonText;
        }
        
        String noButtonText = getValue(e, NOTITLE);
        if(noButtonText != null) {
            ApplicationSettings.noButtonText = noButtonText;
        }
        
        String textSize = getValue(e, TEXTSIZE);
        if(textSize != null) {
            ApplicationSettings.textSize = Integer.parseInt(textSize);
        }
        
        String serverURL = getValue(e, SERVERURL);
        if(textSize != null) {
            if(!ApplicationSettings.serverURL.equals(serverURL)) {
                
                ApplicationSettings.serverURL = serverURL;
                serverUrlChanged = true;
                
            }
        }
        
        return serverUrlChanged;
    }    
    
    private static Document XMLfromString(String xmlString){

        Document doc = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xmlString));
                doc = db.parse(is); 

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
    
    public static String getValue(Element item, String str) {       
        NodeList n = item.getElementsByTagName(str);        
        return getElementValue(n.item(0));
    }
    
    public final static String getElementValue( Node elem ) {
        Node kid;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
                    if( kid.getNodeType() == Node.TEXT_NODE  ){
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return null;
    }
    
//---------------------------------------------------------------------------   
    
    
    
}