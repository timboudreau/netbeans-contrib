/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.hexeditor;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.hexedit.HexEditPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.*;
import org.openide.windows.TopComponent;

/**
 * Action that opens a Hex Editor
 *
 * @author Tim Boudreau
 */
public class HexEditAction extends CookieAction {
    private static boolean WARNED =
            NbPreferences.forModule(HexEditAction.class).getBoolean("warned", //NOI18N
            false);
    public HexEditAction() {
    }
    
    public void performAction (Node[] n) {
        try {
            TopComponent tc = new TC(n[0]);
            tc.open();
            tc.requestActive();
            if (!WARNED) {
                warn();
            }
        } catch (FileNotFoundException fe) {
            Logger.getLogger(HexEditAction.class.getName()).log(Level.INFO,
                    null, fe);
            Toolkit.getDefaultToolkit().beep();
        }
    }


    @Override
    protected boolean enable(Node[] activatedNodes) {
        boolean result = activatedNodes.length == 1;
        if (result) {
            DataObject dob = activatedNodes[0].getLookup().lookup(DataObject.class);
            result = dob != null;
            if (result && dob.isValid()) {
                FileObject fo = dob.getPrimaryFile();
                result = fo.isData() && !fo.isFolder();
                if (result) {
                    result = FileUtil.toFile(fo) != null;
                }
            }
        }
        return result;
    }

    private static final class TC extends TopComponent {
        TC (Node n) throws FileNotFoundException {
            DataObject dob = (DataObject) n.getLookup().lookup (DataObject.class);
            FileObject fileObject = dob.getPrimaryFile();
            File f = FileUtil.toFile (fileObject);
            setActivatedNodes(new Node[] { n });
            associateLookup (n.getLookup());
            setDisplayName (n.getDisplayName());
            setLayout (new BorderLayout());
            add (new HexEditPanel (f), BorderLayout.CENTER);
        }
        
        @Override
        public int getPersistenceType() {
            return PERSISTENCE_NEVER;
        }

        @Override
        protected void componentOpened() {
            ((HexEditPanel) getComponents()[0]).resetSplitterPosition();
        }
    }

    private void warn() {
        String msg = NbBundle.getMessage (HexEditAction.class, "WARNING"); //NOI18N
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg,
                NotifyDescriptor.WARNING_MESSAGE));
        NbPreferences.forModule(HexEditAction.class).putBoolean("warned", //NOI18N
            true);
        WARNED = true;
    }
    
    protected Class[] cookieClasses() {
        return new Class[] { DataObject.class };
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName () {
        return NbBundle.getMessage ( HexEditAction.class, "LBL_Action" ); //NOI18N
    }
    
    public String displayName () {
        return getName();
    }

    protected String iconResource () {
        return "org/netbeans/modules/hexeditor/resources/HexEditIcon.gif"; //NOI18N
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean asynchronous () {
        return false;
    }
}

