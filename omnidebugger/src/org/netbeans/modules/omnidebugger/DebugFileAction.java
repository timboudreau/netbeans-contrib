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

package org.netbeans.modules.omnidebugger;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.modules.omnidebugger.Debug.ClassKind;
import org.openide.awt.DynamicMenuContent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * Action to debug one file.
 * @author Jesse Glick
 */
public class DebugFileAction extends AbstractAction implements ContextAwareAction, DynamicMenuContent {
    
    public DebugFileAction() {
        super("Omniscient Debug File"); // XXX I18N
    }

    public void actionPerformed(ActionEvent e) {
        assert false;
    }

    public Action createContextAwareInstance(Lookup context) {
        return new ContextAction(context);
    }

    public JComponent[] getMenuPresenters() {
        return new JComponent[] {new JMenuItem(new ContextAction(Utilities.actionsGlobalContext()))};
    }

    public JComponent[] synchMenuPresenters(JComponent[] items) {
        return getMenuPresenters();
    }
    
    private final class ContextAction extends AbstractAction {
        
        private final FileObject selection;
        
        public ContextAction(Lookup context) {
            DataObject d = context.lookup(DataObject.class);
            if (d != null) {
                selection = d.getPrimaryFile();
            } else {
                selection = context.lookup(FileObject.class);
            }
            if (selection == null) {
                setEnabled(false);
            } else {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        if (ClassPath.getClassPath(selection, ClassPath.EXECUTE) == null || ClassPath.getClassPath(selection, ClassPath.SOURCE) == null) {
                            setEnabled(false);
                            return;
                        }
                        JavaSource src = JavaSource.forFileObject(selection);
                        if (src == null) {
                            setEnabled(false);
                            return;
                        }
                        try {
                            src.runWhenScanFinished(Debug.getKindTask(new Debug.KindCallback() {
                                public void computed(ClassKind kind) {
                                    if (kind == ClassKind.NONE) {
                                        EventQueue.invokeLater(new Runnable() {
                                            public void run() {
                                                // XXX for some reason this is too late for DataNode context menu (works e.g. in editor)
                                                // there are no listeners on the action... why?
                                                setEnabled(false);
                                                firePropertyChange(NAME, null, getValue(NAME));
                                            }
                                        });
                                    }
                                }
                            }), true);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
            }
        }

        public void actionPerformed(ActionEvent e) {
            Debug.start(selection);
        }

        public @Override Object getValue(String key) {
            if (key.equals(NAME) && isEnabled()) {
                return "Omniscient Debug " + selection.getNameExt(); // XXX I18N
            }
            return DebugFileAction.this.getValue(key);
        }

    }
    
}
