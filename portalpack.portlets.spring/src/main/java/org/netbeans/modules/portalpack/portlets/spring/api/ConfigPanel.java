/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.spring.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.RequestProcessor;

/**
 * This class can be extended to implement your own Server Config Panel.
 * 
 * @author Satya
 */
public abstract class ConfigPanel extends JPanel{
    
    private WizardDescriptor wd;
    public ConfigPanel()
    {
        
    }
  

    public abstract void read(WizardDescriptor wizardDescriptor);

    /**
     * This method is called to store configuration properties.
     * @param wizardDescriptor Instance properties are set in wizardDescriptor
     *
     * Sample implementation for this method looks like below
     * <code>
     *  WizardPropertyReader wr = new WizardPropertyReader(d);
     *  wr.setAdminUser("admin");
     *  //set other configured property for the server instance
     * </code>
     */
    public abstract void store(WizardDescriptor wizardDescriptor);

    /**
     * Validate input value in the config panel
     * @return true if the input values are valid
     *         false if the input values are not valid
     */
    public abstract boolean validate(Object wizardDescriptor);
    
    /**
     * @return description shown for the server config panel.
     */
    public abstract String getDescription();
        
    public void readSettings(WizardDescriptor wd)
    {
        this.wd = wd;
        read(wd);
    }
  
    private ArrayList/*<ChangeListener.*/ listenrs = new ArrayList/*<Changelisteners.*/();
    
    public final void addChangeListener(ChangeListener l) {
        synchronized (listenrs) {
            listenrs.add(l);
        }
    }
    
    public final void removeChangeListener(ChangeListener l ) {
       // System.out.println("Removing....."+l+" to configpanel listener list.........");
        synchronized (listenrs) {
            listenrs.remove(l);
        }
    }
    
    RequestProcessor.Task changeEvent = null;
    
    /**
     * This method can be called whenever the input values are changed
     * or framework needs to know about the new changed value or 
     * validation result.
     */
    public final void fireChangeEvent() {
        // don't go so fast here, since this can get called a lot from the
        // document listener
                
        if (changeEvent == null) {
            changeEvent = RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Iterator it;
                            synchronized (listenrs) {
                                it = new HashSet(listenrs).iterator();
                            }
                            ChangeEvent ev = new ChangeEvent(this);
                            while (it.hasNext()) {
                                ((ChangeListener)it.next()).stateChanged(ev);
                            }
                        }
                    });
                    
                }
            }, 100);
        } else {
            changeEvent.schedule(100);
        }
    }
    
    public void setErrorMessage(String message)
    {
        if(wd != null)
            wd.putProperty("WizardPanel_errorMessage", message);
    }
}
