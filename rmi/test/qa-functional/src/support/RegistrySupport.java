/*
 * RegistrySupport.java
 *
 * Created on November 29, 2001, 9:50 AM
 */

package support;

import org.netbeans.test.oo.gui.jelly.*;
//import com.sun.jemmy.operators.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.test.oo.gui.jam.*;
import org.openide.loaders.*;
import org.netbeans.modules.rmi.registry.*;
import support.Support;
import support.PropertySupport;
import java.util.*;

/**
 *
 * @author  tb115823
 */
public class RegistrySupport {

    public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");
    static Support sup;
    static HashSet RunningDataObjects;
    
    /** Creates a new instance of RegistrySupport */
    public RegistrySupport() throws Exception {
    }
    
    
    
    public static void runRMIServer(String source,String executor, String args, boolean forceExec) throws Exception {
        Support sup = new Support(null,null);
        
        DataObject obj=sup.getDataObject(source);
        
        if (RunningDataObjects == null) RunningDataObjects = new HashSet();
        
        if (RunningDataObjects.add(obj) || forceExec) {
        
            try {
                PropertySupport.setPropertyValue(bundle.getString("RMI_Export/Service_URL"),"",obj.getNodeDelegate()); // NOI18N
                PropertySupport.setPropertyValue(bundle.getString("RMI_Export/Port"),new Integer(0),obj.getNodeDelegate());
            }
            catch (Exception e) { 
                sup.exceptionlog(bundle.getString("Exception_during_setting_RMI_Export_properties_for_") + obj.getName(), e);
            }
            
            sup.setExecutor(obj,executor);
            sup.execute(obj,args); // NOI18N
            
            System.out.println("... register and wait 5s");
            try { Thread.currentThread().sleep(5000);}catch(Exception ex){}
        }    
    }
    
    
    public static void runLocalRegistry() {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        System.out.println("... do refresh wait 1s");
        try { Thread.currentThread().sleep(1000);}catch(Exception ex){}
        
        explorer.pushPopupMenu("Local Registry...",bundle.getString("RMI_registry"));
        JamDialog dialog = new JamDialog("Local Registry");
        try {
            dialog.getJamButton("Start").doClick();
        } catch(Exception ex) {
            System.out.println("... local registry already started");
        }
        
        System.out.println("... do refresh wait 3s");
        try { Thread.currentThread().sleep(3000);}catch(Exception ex){}
        
        try {
            JamDialog exDialog = new JamDialog("Error");
            exDialog.getJamButton("OK").doClick();
        } catch(Exception ex) {
            System.out.println("... nedockal jsem se Errror-u");
        }    

        dialog.getJamButton("Close").doClick();
    }
    
    
    public static void refreshRegistry(String host) {
        
        System.out.println("... get Explorer");
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        System.out.println("... do refresh wait 1s");
        try { Thread.currentThread().sleep(1000);}catch(Exception ex){}
        
        String addHost = "";
        if (host != null) addHost = "|"+ host;
        explorer.pushPopupMenu("Refresh",bundle.getString("RMI_registry") + addHost);
    }

    
    public static void resetLoader() {
        System.out.println("... reseting loader");
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        System.out.println("... do reset loader wait 1s");
        try { Thread.currentThread().sleep(1000);}catch(Exception ex){}
        
        explorer.pushPopupMenu("Reset Loader",bundle.getString("RMI_registry"));
    }
    
    
    
    public static void checkInterface(String path, String interfaceName, String method) {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        if (method.length() > 0) 
            explorer.selectNode(path + "|" + "interface " + interfaceName + "|" + method);
        else
            explorer.selectNode(path + "|" + "interface " + interfaceName);
    }
    
    
    
    public static void addRegistry() {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        explorer.pushPopupMenu("Add Registry...","RMI Registry");
        JamDialog dialog = new JamDialog("Add Registry");
        dialog.getJamButton("OK").doClick();
    }
    
    
    
    public static void deleteRegistryItem(String path) {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
     
        explorer.pushPopupMenu("Delete",path);
        JamDialog dialog = new JamDialog("Confirm Object Deletion");
        dialog.getJamButton("Yes").doClick();
    }
}
