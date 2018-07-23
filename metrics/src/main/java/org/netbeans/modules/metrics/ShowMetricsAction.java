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

package org.netbeans.modules.metrics;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public class ShowMetricsAction extends NodeAction {

    private static final long serialVersionUID = -39196477989978839L;

    private static final boolean debug = false;

    protected boolean enable(Node[] arr) {
        if ((arr == null) || (arr.length == 0)) 
            return false;
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
        return "org/netbeans/modules/metrics/resources/barchart.gif"; //NOI18N
    }

    /**
    * Standard perform action extended by actually activated nodes.
    *
    * @param activatedNodes gives array of actually activated nodes.
    */
    protected void performAction (final Node[] activatedNodes) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Set metricsSet = createMetricsSet(activatedNodes);
                new MetricsPane(metricsSet).setVisible(true);
            }
        });
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
                DataObject dobj = (DataObject)node.getCookie(DataObject.class);
                if (dobj != null) {
                    FileObject fobj = dobj.getPrimaryFile();
                    if (fobj.getExt().equals("java")) {
                        addClassFiles(fobj, metricsSet);
                    }
                }
            }
        }

        // Scan for dependencies between classes.
        Iterator iter = metricsSet.iterator();
        while (iter.hasNext()) {
            ClassMetrics cm = (ClassMetrics)iter.next();
            StatusDisplayer.getDefault().setStatusText(
                scanningMsg + " " + cm.getName());
            cm.scanDependencies();
        }
        StatusDisplayer.getDefault().setStatusText("");

        return metricsSet;
    }

    private void addDirectoryClasses(DataFolder df, Set metricsSet) {
        if (df.getName().equals("CVS")) //FIXME: use "hidden files" system option
            return;
        DataObject dobj[] = df.getChildren();
        for (int i = 0; i < dobj.length; i++) {
            if (dobj[i] instanceof DataFolder)
                addDirectoryClasses((DataFolder)dobj[i], metricsSet);
            else {
                DataObject data = (DataObject)dobj[i].getCookie(DataObject.class);
                if (data != null) {
                    FileObject fobj = data.getPrimaryFile();
                    if (fobj.getExt().equals("java")) {
                        addClassFiles(fobj, metricsSet);
                    }
                }
            }
        }
    }

    // Add all class files associated with a Java FileObject.
    private void addClassFiles(FileObject fobj, Set metricsSet) {
        List /*<FileObject>*/ classes = ClassFinder.getCompiledClasses(fobj);
        for (Iterator i = classes.iterator(); i.hasNext();)
            try {
                FileObject fo = (FileObject)i.next();
                ClassMetrics cm = getClassMetrics(fo.getName(), fo);
                metricsSet.add(cm);
            } catch (IOException e) {
                if (debug)
                    System.err.println(e.toString());
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
        StatusDisplayer.getDefault().setStatusText(msg);
        return ClassMetrics.getClassMetrics(fo);
    }

    protected boolean asynchronous() {
        return false;
    }
}
