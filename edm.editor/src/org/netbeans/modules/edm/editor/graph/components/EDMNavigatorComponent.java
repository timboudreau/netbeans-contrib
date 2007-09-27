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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.edm.editor.graph.components;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

/**
 *
 * @author karthikeyan s
 */
public class EDMNavigatorComponent implements NavigatorPanel, NavigatorLookupHint {
    
    /** holds UI of this panel */
    private JComponent panelUI;
    
    /** template for finding data in given context.
     * Object used as example, replace with your own data source, for example JavaDataObject etc */
    private static final Lookup.Template EDM_DATA = new Lookup.Template(MashupDataObject.class);
    /** current context to work on */
    private Lookup.Result curContext;
    /** listener to context changes */
    private LookupListener contextL;
    
    /** public no arg constructor needed for system to instantiate provider well */
    public EDMNavigatorComponent() {
    }
    
    public String getDisplayHint() {
        return "EDM Editor Navigator";
    }
    
    public String getDisplayName() {
        return "EDM Editor Navigator";
    }
    
    public JComponent getComponent() {
        if(panelUI == null) {
            panelUI = new JPanel();
        }
        return panelUI;
    }
    
    public void panelActivated(Lookup context) {
        // lookup context and listen to result to get notified about context changes
        curContext = context.lookup(EDM_DATA);
        curContext.addLookupListener(getContextListener());
        // get actual data and recompute content
        Collection data = curContext.allInstances();
        setNewContent(data);
    }
    
    public void panelDeactivated() {
        curContext.removeLookupListener(getContextListener());
        curContext = null;
        panelUI = null;
    }
    
    public Lookup getLookup() {
        // go with default activated Node strategy
        return null;
    }
    
    /************* non - public part ************/
    
    private void setNewContent(Collection newData) {
        // put your code here that grabs information you need from given
        // collection of data, recompute UI of your panel and show it.
        // Note - be sure to compute the content OUTSIDE event dispatch thread,
        // just final repainting of UI should be done in event dispatch thread.
        // Please use RequestProcessor and Swing.invokeLater to achieve this.
        Iterator it = newData.iterator();
        while(it.hasNext()) {
            MashupDataObject dObj = (MashupDataObject) it.next();
            if(panelUI == null) {
                panelUI = new JPanel();
            }
            panelUI.removeAll();
            while(true) {
                try {
                    panelUI.add(dObj.getGraphManager().getSatelliteView());
                    break;
                } catch (Exception exception) {
                    // wait till scene is loaded
                }
            }            
            panelUI.revalidate();
            break;
        }
    }
    
    /** Accessor for listener to context */
    private LookupListener getContextListener() {
        if (contextL == null) {
            contextL = new ContextListener();
        }
        return contextL;
    }
    
    /** Listens to changes of context and triggers proper action */
    private class ContextListener implements LookupListener {
        
        public void resultChanged(LookupEvent ev) {
            Collection data = ((Lookup.Result)ev.getSource()).allInstances();
            setNewContent(data);
        }
        
    } // end of ContextListener
    
    public String getContentType() {
        return "x-mashup+xml";
    }
}
