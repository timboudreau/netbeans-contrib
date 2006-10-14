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
