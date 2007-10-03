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

package org.netbeans.modules.j2ee.blueprints;

import java.lang.ref.WeakReference;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.awt.*;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.accessibility.*;
import org.openide.ErrorManager;

/**
 * The BluePrints screen.
 * @author  Ludo
 */
class BluePrintsComponent extends TopComponent{
    static final long serialVersionUID=6021472310161712674L;

    private static WeakReference/*<BluePrintsComponent>*/ component = 
            new WeakReference(null); 
        
    private JComponent panel;

    private boolean initialized = false;
    
    private BluePrintsComponent(){
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(BluePrintsComponent.class, "LBL_Tab_Title"));   //NOI18N             
        panel = null;
        initialized = false;
    }
    
    protected String preferredID(){
        return "BluePrints";    //NOI18N
    }
    
    /**
     * #38900 - lazy addition of GUI components
     */    
    
    private void doInitialize() {
        initAccessibility();
        
        try{
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            panel =(JComponent)Class.forName(NbBundle.getMessage(
                  BluePrintsComponent.class,"CLASS_content_panel"), true, cl).newInstance();
            //panel =(JComponent)Class.forName(NbBundle.getMessage(BluePrintsComponent.class,"CLASS_content_panel")).newInstance();
        }catch(Exception e){
            ErrorManager.getDefault().notify(e);
        }
        if(panel == null)
            return;

        // Removed code that added scrollbars - we do not want them here.
        add(panel);
        setFocusable(true);
    }
        
    /* Singleton accessor. As BluePrintsComponent is persistent singleton this
     * accessor makes sure that BluePrintsComponent is deserialized by window system.
     * Uses known unique TopComponent ID "BluePrints" to get BluePrintsComponent instance
     * from window system. "BluePrints" is name of settings file defined in module layer.
     */
    public static BluePrintsComponent findComp() {
        BluePrintsComponent wc = (BluePrintsComponent)component.get();
        if (wc == null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent("BluePrints"); // NOI18N
            if (tc != null) {
                if (tc instanceof BluePrintsComponent) {
                    wc = (BluePrintsComponent)tc;
                    component = new WeakReference(wc);
                } else {
                    //Incorrect settings file?
                    IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned." // NOI18N
                    + " Expected:" + BluePrintsComponent.class.getName() // NOI18N
                    + " Returned:" + tc.getClass().getName()); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    //Fallback to accessor reserved for window system.
                    wc = BluePrintsComponent.createComp();
                }
            } else {
                //BluePrintsComponent cannot be deserialized
                //Fallback to accessor reserved for window system.
                wc = BluePrintsComponent.createComp();
            }
        }
        return wc;
    }
    
    /* Singleton accessor reserved for window system ONLY. Used by window system to create
     * BluePrintsComponent instance from settings file when method is given. Use <code>findComp</code>
     * to get correctly deserialized instance of BluePrintsComponent. */
    public static BluePrintsComponent createComp() {
        BluePrintsComponent wc = (BluePrintsComponent)component.get();
        if(wc == null) {
            wc = new BluePrintsComponent();
            component = new WeakReference(wc);
        }
        return wc;
    }
    
    /** Overriden to explicitely set persistence type of BluePrintsComponent
     * to PERSISTENCE_ALWAYS */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    static void clearRef(){
        component.clear();
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(BluePrintsComponent.class, "ACS_BluePrints_DESC")); // NOI18N
    }
    
    public void requestFocus(){
        super.requestFocus();
        panel.requestFocus();
    }
    
    /**
     * #38900 - lazy addition of GUI components
     */    
    public void addNotify() {
        if (!initialized) {
            initialized = true;
            doInitialize();
        }
        super.addNotify();
    }
    
    /**
     * Called when <code>TopComponent</code> is about to be shown.
     * Shown here means the component is selected or resides in it own cell
     * in container in its <code>Mode</code>. The container is visible and not minimized.
     * <p><em>Note:</em> component
     * is considered to be shown, even its container window
     * is overlapped by another window.</p>
     * @since 2.18
     *
     * #38900 - lazy addition of GUI components
     *
     */
    protected void componentShowing() {
        if (!initialized) {
            initialized = true;
            doInitialize();
        }
        super.componentShowing();
    }
    
}

