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

package org.netbeans.module.copyfqn.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.JEditorPane;
import org.netbeans.jmi.javamodel.Array;
import org.netbeans.jmi.javamodel.Element;
import org.netbeans.jmi.javamodel.JavaClass;
import org.netbeans.jmi.javamodel.MultipartId;
import org.netbeans.jmi.javamodel.PrimitiveType;
import org.netbeans.jmi.javamodel.Resource;
import org.netbeans.jmi.javamodel.Type;
import org.netbeans.modules.java.JavaDataObject;
import org.netbeans.modules.javacore.api.JavaModel;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.util.datatransfer.ExClipboard;
import org.openide.windows.TopComponent;

/**
 * This action copies the fully qualified name of the Java Class under the caret
 * or the Java Class of selected node in the Projects/Files windows to the system
 * clipboard.
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public final class CopyFQNAction extends CookieAction {
    private Clipboard clipboard;

    public CopyFQNAction() {
        clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
    }

    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes.length > 0) {
            DataObject dataObject = (DataObject) activatedNodes[0].getLookup().lookup(DataObject.class);
            if (dataObject instanceof JavaDataObject) {
                FileObject fileObject = dataObject.getPrimaryFile();

                // start a read transaction
                JavaModel.getJavaRepository().beginTrans(false);
                Resource resource = null;
                try {
                    JavaModel.setClassPath(fileObject);
                    resource = JavaModel.getResource(fileObject);
                    EditorCookie ec = (EditorCookie) activatedNodes[0].getCookie(EditorCookie.class);
                    if (ec != null) {
                        JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes != null) {
                            TopComponent activetc = TopComponent.getRegistry().getActivated();
                            for (int i = 0; i < panes.length; i++) {
                                if (activetc.isAncestorOf(panes[i])) {
                                    int dot = panes[i].getCaret().getDot();
                                    Element element = resource.getElementByOffset(dot);
                                    Type type = null;
                                    if (element instanceof JavaClass) {
                                        type = ((JavaClass)element);
                                    } else if (element instanceof MultipartId) {
                                        type = ((MultipartId)element).getType();
                                    }
                                    while (type instanceof Array) {
                                        type = ((Array) type).getType();
                                    }
                                    if (type != null && !(type instanceof PrimitiveType)) {
                                        clipboard.setContents(new StringSelection(type.getName()), null);
                                        return;
                                    }
                                }
                            }
                        }
                    }

                    List javaClasses = resource.getClassifiers();
                    if (javaClasses != null && javaClasses.size()>0) {
                        clipboard.setContents(new StringSelection(((JavaClass)javaClasses.get(0)).getName()), null);
                        return;
                    }
                } finally {
                    // end transaction in finally block to make
                    // sure that the lock is released under any circumstances
                    JavaModel.getJavaRepository().endTrans();
                }
            } else {
                ErrorManager.getDefault().log(NbBundle.getBundle(CopyFQNAction.class).getString("MSG_NoJavaNodeSelected")); // NOI18N
            }
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(CopyFQNAction.class, "CTL_CopyFQNAction");
    }

    protected Class[] cookieClasses() {
        return new Class[] {
            JavaDataObject.class
        };
    }

    protected String iconResource() {
        return "org/netbeans/module/copyfqn/actions/fqn.gif";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }

}

