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
public class ActivationBrowserSupport {

    public static final java.util.ResourceBundle bundle=java.util.ResourceBundle.getBundle("data/RMITests");
    static Support sup;
    
    /** Creates a new instance of RegistrySupport */
    public ActivationBrowserSupport() throws Exception {
    }
    
    
    public static void initActivationBrowserSupport() {
    }
    
    
    public static void refresh() {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        explorer.pushPopupMenu("Refresh","RMI Activation Browser");
    }
        
    public static void addActivationGroup() {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        explorer.selectNode("RMI Activation Browser|localhost:1098");
        Support.sleep(4000);
        explorer.pushPopupMenuNoBlock("Add Activation Group","RMI Activation Browser|localhost:1098");
        JamDialog dialog = new JamDialog("Activation Group");
        JamTextField jtf = new JamTextField(dialog);
        jtf.setText("TestGroup");
        dialog.getJamButton("OK").doClick();
        
    }
    
    
    public static void addActivationSystem() {
        Explorer explorer = new Explorer();
        explorer.switchToRuntimeTab();
        explorer.pushPopupMenu("Add Activation System...","RMI Activation Browser");
        JamDialog dialog = new JamDialog("Add Activation System");
        dialog.getJamButton("OK").doClick();
    }
   
}
