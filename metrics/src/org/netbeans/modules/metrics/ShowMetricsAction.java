/*
 * ShowMetricsAction.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.openide.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.*;

import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.clazz.CompiledDataObject;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

public class ShowMetricsAction extends NodeAction {

    private static final long serialVersionUID = -39196477989978839L;

    private static final boolean debug = false;

    protected boolean enable(Node[] arr) {
        if ((arr == null) || (arr.length == 0)) return false;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getCookie (DataObject.class) == null) {
                return false;
            }
        }

        return true;
    }

    /** Human presentable name of the action. This should be
    * presented as an item in a menu.
    * @return the name of the action
    */
    public String getName() {
        return MetricsNode.bundle.getString("ACT_ShowMetrics");
    }

    /** Help context where to find more about the action.
    * @return the help context for this action
    */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ShowMetricsAction.class);
    }

    protected String iconResource(){
        return "/org/netbeans/modules/metrics/resources/barchart.gif"; //NOI18N
    }

    /**
    * Standard perform action extended by actually activated nodes.
    *
    * @param activatedNodes gives array of actually activated nodes.
    */
    protected void performAction (final Node[] activatedNodes) {
        Set metricsSet = createMetricsSet(activatedNodes);
        new MetricsPane(metricsSet).setVisible(true);
    }

    // Translate the array of Explorer nodes into a set of ClassMetrics.
    private Set createMetricsSet(final Node[] activatedNodes) {
        Set metricsSet = new HashSet();
        for (int i = 0; i < activatedNodes.length; ++i) {
            Node node = activatedNodes[i];
            DataFolder df = (DataFolder)node.getCookie(DataFolder.class);
            if (df != null)
                addDirectoryClasses(df, metricsSet);
            else {
                JavaDataObject jdo = 
                    (JavaDataObject)node.getCookie(JavaDataObject.class);
                if (jdo != null)
                    addClassFiles(jdo, metricsSet);
                else {
                    CompiledDataObject cdo = 
                        (CompiledDataObject)node.getCookie(CompiledDataObject.class);
                    if (cdo != null) {
                        try {
                            ClassMetrics cm = getClassMetrics(cdo.getName(),
                                cdo.getPrimaryFile());
                            metricsSet.add(cm);
                        } catch (IOException e) {
                            if (debug)
                                System.err.println(e.toString());
                        }
                    }
                }
            }
        }

        // Scan for dependencies between classes.
        Iterator iter = metricsSet.iterator();
        while (iter.hasNext()) {
            ClassMetrics cm = (ClassMetrics)iter.next();
            TopManager.getDefault().setStatusText(
                scanningMsg + " " + cm.getName());
            cm.scanDependencies();
        }
        TopManager.getDefault().setStatusText("");

        return metricsSet;
    }

    private void addDirectoryClasses(DataFolder df, Set metricsSet) {
        DataObject dobj[] = df.getChildren();
        for (int i = 0; i < dobj.length; i++) {
            if (dobj[i] instanceof DataFolder)
                addDirectoryClasses((DataFolder)dobj[i], metricsSet);
            else {
                JavaDataObject jdo = 
                    (JavaDataObject)dobj[i].getCookie(JavaDataObject.class);
                if (jdo != null)
                    addClassFiles(jdo, metricsSet);
                else {
                    CompiledDataObject cdo = 
                        (CompiledDataObject)dobj[i].getCookie(CompiledDataObject.class);
                    if (cdo != null)
                        try {
                            ClassMetrics cm = getClassMetrics(cdo.getName(),
                                cdo.getPrimaryFile());
                            metricsSet.add(cm);
                        } catch (IOException e) {
                            if (debug)
                                System.err.println(e.toString());
                        }
                }
            }
        }
    }

    // Add all class files associated with a JavaDataObject.
    private void addClassFiles(JavaDataObject jdo, Set metricsSet) {
        Collection c = jdo.getCompiledClasses();
        if (c != null) {
            Iterator iter = c.iterator();
            while (iter.hasNext()) {
                try {
                    FileObject fo = (FileObject)iter.next();
                    ClassMetrics cm = getClassMetrics(fo.getName(), fo);
                    metricsSet.add(cm);
                } catch (IOException e) {
                    if (debug)
                        System.err.println(e.toString());
                }
            }
        }
    }

    private static final String readingMsg = 
            MetricsNode.bundle.getString("MSG_Reading");
    private static final String scanningMsg = 
            MetricsNode.bundle.getString("MSG_Scanning");

    private static ClassMetrics getClassMetrics(String name, FileObject fo) 
        throws IOException 
    {
        String msg = readingMsg + " " + name;
        TopManager.getDefault().setStatusText(msg);
        return ClassMetrics.getClassMetrics(fo);
    }
}
