/*
 * DefaultUniverseFactory.java
 *
 * Created on September 27, 2005, 5:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.venice.viewer.lg3d;

import java.awt.GraphicsConfiguration;
import javax.media.j3d.Canvas3D;
import com.sun.j3d.utils.universe.ConfiguredUniverse;
import java.net.URL;
import java.util.logging.Logger;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import org.jdesktop.lg3d.displayserver.UniverseFactoryInterface;
/**
 *
 * @author paulby
 */
public class VeniceUniverseFactory implements UniverseFactoryInterface {
    
    private Logger logger = Logger.getLogger("lg.displayserver");
    private Canvas3D c3d=null;
    
    private static UniverseListener listener = null;
    
    public ConfiguredUniverse createUniverse(URL configURL) { 
        GraphicsConfiguration config =
           ConfiguredUniverse.getPreferredConfiguration();
        
        c3d = new Canvas3D(config);
        
        ConfiguredUniverse ret =  new com.sun.j3d.utils.universe.ConfiguredUniverse(c3d);
        View view = ret.getViewer().getView();
        view.setWindowEyepointPolicy(View.RELATIVE_TO_COEXISTENCE);
        view.setWindowMovementPolicy(View.VIRTUAL_WORLD);
        view.setWindowResizePolicy(View.VIRTUAL_WORLD);
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);
        view.setCoexistenceCenteringEnable(true);
        view.setWindowEyepointPolicy(View.RELATIVE_TO_WINDOW);
        view.setWindowMovementPolicy(View.PHYSICAL_WORLD);
        view.setFrontClipDistance(0.01f);
        view.setBackClipDistance(10f);
        
        ViewPlatform vp = ret.getViewingPlatform().getViewPlatform();
        vp.setViewAttachPolicy(View.NOMINAL_SCREEN);
        
        c3d.getScreen3D().setPhysicalScreenHeight(0.360f);
        c3d.getScreen3D().setPhysicalScreenWidth(0.288f);
        
        ret.getViewingPlatform().setNominalViewingTransform();
        
//        URL url = null;
//        try {
//            url = new URL("file:///c:/paulby/code/java_net/lg3d/venice/lg3d-core/src/etc/lg3d/displayconfig/j3d1x1");
//        } catch(MalformedURLException mue) {
//            mue.printStackTrace();
//        }
//        
//        ConfiguredUniverse ret = new ConfiguredUniverse(url);
//        c3d = ret.getViewer().getView().getCanvas3D(0);
//        Container cont = c3d.getParent();
//        Window w = SwingUtilities.getWindowAncestor(c3d);
//        cont.remove(c3d);
//        w.setVisible(false);
//        w.dispose();
        
        if (listener!=null)
            listener.universeCreated(c3d);
        else
            throw new RuntimeException("No Canvas3D listener registered");
        
        return ret;
    }
    
    public static void addUniverseListener(UniverseListener l) {
        listener = l;
    }
    
    public interface UniverseListener {
        public void universeCreated( Canvas3D canvas );
    }
        
}
