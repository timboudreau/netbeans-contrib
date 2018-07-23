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
package org.netbeans.modules.docbook.project;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author Tim Boudreau
 */
public class SetMainFileAction extends AbstractAction implements ContextAwareAction, LookupListener {
    private final Lookup lkp;
    private final Lookup.Result res;
    /** Creates a new instance of SetMainFileAction */
    public SetMainFileAction() {
        this (Utilities.actionsGlobalContext());
    }

    public SetMainFileAction (Lookup lkp) {
        this.lkp = lkp;
        putValue (NAME, "Set Main File");
        assert lkp != null;
        this.res = lkp.lookupResult(DataObject.class);
        resultChanged (null);
    }

    public void actionPerformed(ActionEvent e) {
        DbProject proj = getDbProject();
        DataObject ob = lkp.lookup (DataObject.class);
        if (ob != null && proj != null) {
            proj.setMainFile(ob.getPrimaryFile());
        }
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new SetMainFileAction (actionContext);
    }

    public void resultChanged(LookupEvent ev) {
        res.allInstances();
        DataObject dob = lkp.lookup(DataObject.class);
        DbProject proj = getDbProject();
        boolean enable = proj != null && dob != null;
        System.err.println("First test " + enable + " dob " + dob + " proj " + proj);
        if (enable) {
            FileObject main = proj.getMainFile();
            FileObject mine = dob.getPrimaryFile();
            enable = !mine.equals(main);
            System.err.println("Second test " + enable + " for " + mine + " on lkp " + lkp + " main file " + main);
        }
        setEnabled (enable);
    }

    private DbProject getDbProject() {
        DataObject ob = lkp.lookup(DataObject.class);
        DbProject result = null;
        if (ob != null) {
            Project p = FileOwnerQuery.getOwner(ob.getPrimaryFile());
            System.err.println("Found project " + p);
            System.err.println("Class match: " + (DbProject.class.isAssignableFrom(p.getClass())));
            System.err.println("  cm2 " + (p instanceof DbProject));
            if (p instanceof DbProject) {
                result = (DbProject) p;
                System.err.println("DBPROJECT IS PROJECT " + result);
            } else if (p != null) {
                result = p.getLookup().lookup (DbProject.class);
                System.err.println("Found DbProject " + result);
            }
        }
        return result;
    }

}
