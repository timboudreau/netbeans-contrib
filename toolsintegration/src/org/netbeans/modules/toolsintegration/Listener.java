/*
 * Listener.java
 *
 * Created on 10. leden 2006, 16:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.toolsintegration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author Administrator
 */
class Listener implements PropertyChangeListener {
    
    /** Creates a new instance of Listener */
    Listener () {
        TopComponent.getRegistry ().addPropertyChangeListener (this);
    }
    
    public void propertyChange (PropertyChangeEvent evt) {
        
    }
}



