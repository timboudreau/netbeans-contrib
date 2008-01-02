package org.netbeans.modules.a11ychecker.traverse;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import org.netbeans.modules.form.CreationFactory;
import org.netbeans.modules.form.ViewConverter;
import org.netbeans.modules.form.ViewConverter.Convert;

/**
 *
 * @author Mikee
 */
public class MyConverter implements ViewConverter { 
        public Convert convert(Object component, boolean root, boolean designRestrictions) { 
            Class compClass = component.getClass(); 
            Class convClass = null; 
            if ((RootPaneContainer.class.isAssignableFrom(compClass) 
                        && Window.class.isAssignableFrom(compClass)) 
                    || Frame.class.isAssignableFrom(compClass)) { 
                convClass = JRootPane.class; 
            } else if (Window.class.isAssignableFrom(compClass) 
                       || java.applet.Applet.class.isAssignableFrom(compClass)) { 
                convClass = Panel.class; 
            } 
            if (convClass == null) { 
                return null; // no conversion needed 
            } 
 
            try { 
                Component converted = (Component) CreationFactory.createDefaultInstance(convClass); 
                Component enclosed = null; 
 
                if (converted instanceof JRootPane) { // RootPaneContainer or Frame converted to JRootPane 
                    Container contentCont = (Container) CreationFactory.createDefaultInstance( 
                            RootPaneContainer.class.isAssignableFrom(compClass) ? JPanel.class : Panel.class); 
                    ((JRootPane)converted).setContentPane(contentCont); 
                } 
 
                return new ConvertResult(converted, enclosed); 
            } catch (Exception ex) { // some instance creation failed, very unlikely to happen 
                Logger.getLogger(MyConverter.class.getName()).log(Level.INFO, null, ex); 
                return null; 
            } 
        } 
 
        public boolean canVisualize(Class componentClass) { 
            return false; // not able to visualize non-visual components 
              // AWT menus are converted, but never used as the root in the design view 
        } 
    } 
 
    class ConvertResult implements ViewConverter.Convert { 
        private Object converted; 
        private Object enclosed; 
        ConvertResult(Object converted, Object enclosed) { 
            this.converted = converted; 
            this.enclosed = enclosed; 
        } 
        public Object getConverted() { 
            return converted; 
        } 
        public Object getEnclosed() { 
            return enclosed; 
        } 
    } 

