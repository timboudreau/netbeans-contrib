/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.portlets.hook.api;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;

/**
 *
 * @author admin
 */
public class WizardDescriptorPanelWrapper implements WizardDescriptor.Panel, ChangeListener{
    
    private ConfigPanel configPanel;
    private WizardDescriptor wd;
    public WizardDescriptorPanelWrapper(ConfigPanel configPanel) {
        this.configPanel = configPanel;
        this.configPanel.addChangeListener(this);
    }

    public Component getComponent() {
        return configPanel;
    }

    public HelpCtx getHelp() {
        return null;
    }

    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor)settings;
        configPanel.read(wd);
    }

    public void storeSettings(Object settings) {
        configPanel.store(wd);
    }
     
    public boolean isValid() {
        getComponent();
        return configPanel.validate(wd); 
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

    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }

}
