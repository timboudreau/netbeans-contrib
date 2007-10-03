/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
