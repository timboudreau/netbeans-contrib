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


package org.netbeans.modules.vcscore.ui.views;

import java.io.*;
import org.openide.util.*;
import org.openide.util.actions.SystemAction;
import org.openide.explorer.*;
import org.openide.nodes.*;
import org.openide.awt.JMenuPlus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JMenu;


/**
 * Subclass of TopComponent that is used within the vcs mode.
 * @author  mkleint
 */
public class VcsExplorerPanel extends org.openide.explorer.ExplorerPanel {


    static final long serialVersionUID = 5519624846009571015L;
    
    /** Creates new VcsExplorerPanel */
    public VcsExplorerPanel() {
        super();
        putClientProperty("PersistenceType", "Never"); //NOI18N
    }

/*    public void addDisplayer(PersistentCommandDisplayer displ) {
        displayer = displ;
        add(displ.getComponent());
    }
    
    public PersistentCommandDisplayer getEqualDisplayer(File file, Class type,  Object comparisonData) {
        if (displayer == null) return null;
        if (displayer.equalDisplayedData(file, type, comparisonData)) {
            return displayer;
        }
        return null;
    }
    
    public PersistentCommandDisplayer getDisplayer() {
        return displayer;
    }
  */  
    /** Get the system actions which will appear in
     * the popup menu of this component.
     * <p>Subclasses are encouraged to override this method to specify
     * their own sets of actions.
     * <p>Remember to call the super method when overriding and add your actions
     * to the superclass' ones (in some order),?
     * because the default implementation provides support for standard
     * component actions like save, close, and clone.
     * @return system actions for this component
 */
    public SystemAction[] getSystemActions() {
        SystemAction[] retValue;
        retValue = super.getSystemActions();
        return retValue;
/*        SystemAction[] all = new SystemAction[retValue.length + 1];
        all[0] = new JTCAction();
        for (int i=0; i < retValue.length; i++) {
            all[i + 1] = retValue[i];
        }
        return all;
 */
    }  

        /**
         * Disable serialization.
         * @return null
         */
        protected Object writeReplace () throws java.io.ObjectStreamException {
            return null;
        }
    
        /** Called only when top component was closed so that now it is closed
         * on all workspaces in the system. The intent is to provide subclasses
         * information about TopComponent's life cycle across workspaces.
         * Subclasses will usually perform cleaning tasks here.
         */
        protected void closeNotify() {
            super.closeNotify();
        }
    
        protected void updateTitle() {
            //do nothing..
            String name = ""; // NOI18N
            ExplorerManager manager = getExplorerManager();
            if (manager != null) {
                Node n = manager.getRootContext();
                if (n != null) {
                    String nm = n.getDisplayName();
                    if (nm != null) {
                        name = nm;
                    }
                }
            }
        }
}
