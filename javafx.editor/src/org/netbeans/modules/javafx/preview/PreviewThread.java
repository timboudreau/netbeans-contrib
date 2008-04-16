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

package org.netbeans.modules.javafx.preview;

import java.security.CodeSource;
import java.security.PermissionCollection;
import org.netbeans.modules.javafx.editor.*;
import java.security.Permissions;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

//import sun.awt.AppContext;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import org.openide.execution.ExecutionEngine;
//import sun.awt.SunToolkit;
import org.openide.execution.ExecutorTask;
import org.openide.execution.NbClassPath;
import org.openide.util.Exceptions;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.util.RequestProcessor;
import javax.tools.Diagnostic;

        
public class PreviewThread extends Thread {
    
    private FXDocument doc;
    private JComponent comp = null;

    class EE extends ExecutionEngine {
        
        public EE() {}

        protected NbClassPath createLibraryPath() {
            return new NbClassPath(new String[0]);
        }

        protected PermissionCollection createPermissions(CodeSource cs, InputOutput io) {
            PermissionCollection allPerms = new Permissions();
            //allPerms.add(new AllPermission());
            //allPerms.setReadOnly();
            return allPerms;
        }

        public ExecutorTask execute(String name, Runnable run, InputOutput io) {
            return new ET(run, name, io);
        }
        
        private class ET extends ExecutorTask {
            private RequestProcessor.Task task;
            private int resultValue;
            private final String name;
            private InputOutput io;
            
            public ET(Runnable run, String name, InputOutput io) {
                super(run);
                this.resultValue = resultValue;
                this.name = name;
                task = RequestProcessor.getDefault().post(this);
            }
            
            public void stop() {
                task.cancel();
            }
            
            public int result() {
                waitFinished();
                return resultValue;
            }
            
            public InputOutput getInputOutput() {
                return io;
            }
            
            public void run() {
                try {
                    super.run();
                } catch (RuntimeException x) {
                    x.printStackTrace();
                    resultValue = 1;
                }
            }
        }       
    } 

    class R implements Runnable {

        public void run() {
            Object obj = null;
            try {
                obj = CodeManager.execute(doc);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            if (obj != null)
                comp = CodeManager.parseObj(obj);
            else {
                List <Diagnostic> diagnostics = CodeManager.getDiagnostics();
                if (!diagnostics.isEmpty()) {
                    comp = new JPanel();
                    comp.setLayout(new BoxLayout(comp, BoxLayout.Y_AXIS));
                    for (Diagnostic diagnostic : diagnostics) {
                        JTextArea jta = new JTextArea();
                        jta.setLineWrap(true);
                        jta.append(diagnostic.toString());
                        comp.add(jta);
                        comp.add(new JSeparator(SwingConstants.HORIZONTAL));
                    }
                }
            }
                
        }
    }

    public PreviewThread(FXDocument doc) {
        super(new ThreadGroup("SACG"), "SACT");
        //super();
        this.doc = doc;
    }

    @Override
    public void run() {
        try {
            //SunToolkit.createNewAppContext();
            //System.out.println("Current app context " + AppContext.getAppContext());
            
            ExecutionEngine ee = new EE();
            ExecutorTask task = ee.execute("prim", new R(), IOProvider.getDefault().getIO("JavaFX preview", false));
  
            task.addTaskListener(new TaskListener() {
                public void taskFinished(Task task) {
                    ((JavaFXDocument)doc).renderPreview(comp);
                }
            });
            
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}