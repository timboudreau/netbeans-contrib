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

/*
 * WS70AddServerChoicePanel.java
 */

package org.netbeans.modules.j2ee.sun.ws7.ui;

import org.openide.WizardDescriptor;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


import org.openide.util.HelpCtx;
/**
 *
 * @author Mukesh Garg
 */
public class WS70AddServerChoicePanel implements WizardDescriptor.Panel, ChangeListener{

    private final List listeners = new ArrayList();
    private WS70AddServerChoiceVisualPanel panel;
    private WizardDescriptor wizard;    
    
    /** Creates a new instance of WS70AddServerChoicePanel */
    public WS70AddServerChoicePanel() {
    }    
    //WizardDescriptor.Panel method implementation
    public Component getComponent(){
        if(panel==null){
            panel = new WS70AddServerChoiceVisualPanel();
            panel.addChangeListener(this);
        }
        return panel;
    }
    //WizardDescriptor.Panel method implementation
    public HelpCtx getHelp(){
        return new HelpCtx("wsplugin_webserver7_plugin_help");
    }
    //WizardDescriptor.Panel method implementation
    public boolean isValid(){
        WS70AddServerChoiceVisualPanel p = (WS70AddServerChoiceVisualPanel)getComponent();
        boolean retval = p.isValid(wizard);
        return retval;
    }
    //WizardDescriptor.Panel method implementation
    public void readSettings(Object obj){
        wizard = (WizardDescriptor)obj;
    }
    //WizardDescriptor.Panel method implementation
    public void storeSettings(Object obj){
        
    }
    //WizardDescriptor.Panel method implementation
    public void addChangeListener(ChangeListener l){
        synchronized (listeners) {
            listeners.add(l);
        }        
    }
    //WizardDescriptor.Panel method implementation
    public void removeChangeListener(ChangeListener l){
        synchronized (listeners) {
            listeners.remove(l);
        }        
    }
    public void stateChanged(ChangeEvent event) {
        fireChange(event);
    }
    private void fireChange(ChangeEvent event) {
        ArrayList tempList;

        synchronized (listeners) {
            tempList = new ArrayList(listeners);
        }

        Iterator iter = tempList.iterator();
        while (iter.hasNext()){
            ((ChangeListener)iter.next()).stateChanged(event);
        }
    }    
    
}
