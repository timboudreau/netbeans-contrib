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
package org.netbeans.modules.editor.retools.probe;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.FocusManager;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Vita Stejskal <vstejskal at netbeans.org>
 */
public class Probe {

    private static final Logger LOG = Logger.getLogger(Probe.class.getName());
    
    private final Map<String, Throwable> originsToMention = new HashMap<String, Throwable>();
    
    public Probe() {
        
    }
    
    public String getStatus() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, "utf-8"); //NOI18N

            try {
                String msg = "Editor Probe " + getVersion() + " dumping info..."; //NOI18N
                Installer.FLOG.info(msg);
                ps.println(msg);
    
                if (isJdkPatched()) {
                    ps.println("The Probe's JDK patches detected, excellent!"); //NOI18N
                } else {
                    ps.println("The Probe can't detect its JDK patches and is running with limited functionality only."); //NOI18N
                }
                
                originsToMention.clear();
                addEnvironmentInfo(ps);
                addAllMopdules(ps);

                addGeneralStatus(ps);

                List<? extends JTextComponent> editors = EditorRegistry.componentList();
                for(int i = 0; i < editors.size(); i++) {
                    addSingleEditorStatus(editors.get(i), i, ps);
                }
                
                addFailedFocusRequests(ps);
                addOrigins(ps);
                
                msg = "Editor Probe " + getVersion() + " dump finished!"; //NOI18N
                Installer.FLOG.info(msg);
                ps.println(msg);
            } finally {
                ps.flush();
                ps.close();
            }

            return baos.toString("utf-8"); //NOI18N
        } catch (UnsupportedEncodingException uee) {
            LOG.log(Level.WARNING, null, uee);
            return uee.getMessage();
        }
    }
    
    private void addEnvironmentInfo(PrintStream ps) {
        try {
            ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);
            Class<?> topLoggingClass = cl.loadClass("org.netbeans.core.startup.TopLogging"); //NOI18N
            Method printSystemInfoMethod = topLoggingClass.getDeclaredMethod("printSystemInfo", PrintStream.class); //NOI18N
            printSystemInfoMethod.setAccessible(true);
            printSystemInfoMethod.invoke(null, ps);
        } catch (Exception e) {
            LOG.log(Level.WARNING, null, e);
        }
    }

    private void addAllMopdules(PrintStream ps) {
        Collection<? extends ModuleInfo> modules = Lookup.getDefault().lookupAll(ModuleInfo.class);
        for(ModuleInfo mi : modules) {
            ps.print("  "); //NOI18N
            ps.print(mi.getCodeName());
            ps.print(" ["); //NOI18N
            ps.print(mi.getSpecificationVersion());
            ps.print(" "); //NOI18N
            ps.print(mi.getBuildVersion());
            ps.println("]"); //NOI18N
        }
        addSeparator(ps);
    }
    
    private void addGeneralStatus(PrintStream ps) {
        List<? extends JTextComponent> editors = EditorRegistry.componentList();
        ps.println("Registered editors: " + editors.size()); //NOI18N
        ps.println("Last focused editor: " + editorId(EditorRegistry.lastFocusedComponent())); //NOI18N
        ps.println("Currently focused editor: " + editorId(EditorRegistry.focusedComponent())); //NOI18N
        ps.println();
        
        // Dump focus info:
        KeyboardFocusManager kfm = FocusManager.getCurrentKeyboardFocusManager();
        ps.println("Focus manager: " + s2s(kfm)); //NOI18N
        ps.println("Active window: " + s2s(kfm.getActiveWindow())); //NOI18N
        ps.println("Focused window: " + s2s(kfm.getFocusedWindow())); //NOI18N
        ps.println("Netbeans main window: " + s2s(WindowManager.getDefault().getMainWindow())); //NOI18N
        ps.println("Focus cycle root: " + s2s(kfm.getCurrentFocusCycleRoot())); //NOI18N
        ps.println("Permanent focus owner: " + s2s(kfm.getPermanentFocusOwner())); //NOI18N
        ps.print("Focus owner: "); //NOI18N
        dumpAncestors(kfm.getFocusOwner(), ps, ""); //NOI18N
        addSeparator(ps);
    }
    
    private void addSingleEditorStatus(JTextComponent editor, int index, PrintStream ps) {
        ps.println("Editor[" + index + "]: " + editorId(editor)); //NOI18N

        // Dump component info
        ps.println("JTextComponent: " + s2s(editor)); //NOI18N
        ps.println("Enabled: " + editor.isEnabled()); //NOI18N
        ps.println("Editable: " + editor.isEditable()); //NOI18N
        ps.println("Focusable: " + editor.isFocusable()); //NOI18N
        ps.println("Focus owner: " + editor.isFocusOwner()); //NOI18N
        ps.println("Valid: " + editor.isValid()); //NOI18N
        ps.println("Caret: " + s2s(editor.getCaret())); //NOI18N
        ps.println("Caret offset: " + editor.getCaretPosition()); //NOI18N
        ps.println("Selection: <" + editor.getSelectionStart() + "," + editor.getSelectionEnd() + ">"); //NOI18N
        ps.println("UI: " + s2s(editor.getUI())); //NOI18N
        ps.println("Parent: " + s2s(editor.getParent())); //NOI18N
        ps.println("Keymap size: " + editor.getKeymap().getBoundKeyStrokes().length); //NOI18N
        ps.println();
        
        // Dump document info
        Document document = editor.getDocument();
        ps.println("Document: " + s2s(document)); //NOI18N
        ps.println("Mimetype: '" + document.getProperty("mimeType") + "'"); //NOI18N
        ps.println("Length: " + document.getLength()); //NOI18N
        ps.println("Read locked: " + DocumentUtilities.isReadLocked(document)); //NOI18N
        ps.println("Write locked: " + DocumentUtilities.isWriteLocked(document)); //NOI18N
        // dump all fields from the whole class hierarchy
        ps.println();
        
        // Dump file info
        FileObject file = getFileObjectFor(document);
        if (file != null) {
            ps.println("File: " + file.getPath()); //NOI18N
            ps.println("Mimetype: '" + file.getMIMEType() + "'"); //NOI18N
            ps.println("Readonly: " + !file.canWrite()); //NOI18N
        } else {
            ps.println("Stream: " + document.getProperty(Document.StreamDescriptionProperty)); //NOI18N
        }
        ps.println();
        
        // Dump TopComponent hierarchy
        TopComponent topComponent = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class, editor);
        dumpComponentHierarchy(topComponent, ps, "");
        
        addSeparator(ps);
    }
    
    private void addOrigins(PrintStream ps) {
        if (originsToMention.size() > 0) {
            ps.println("Known java.awt.Component origins:"); //NOI18N

            List<String> references = new ArrayList<String>(originsToMention.keySet());
            Collections.sort(references);

            for(String r : references) {
                Throwable origin = originsToMention.get(r);
                ps.println("<a name=\"" + r + "\"></a>"); //NOI18N
                origin.printStackTrace(ps);
            }
            
            addSeparator(ps);
        }
    }
    
    private void addFailedFocusRequests(PrintStream ps) {
        ps.println("Failed calls to java.awt.Component.requestFocus() and similar:"); //NOI18N

        Throwable [] failedRequests = getFailedFocusRequests();
        if (failedRequests.length > 0) {
            for(Throwable stacktrace : failedRequests) {
                ps.println();
                stacktrace.printStackTrace(ps);
            }
        } else {
            ps.println("No failures!"); //NOI18N
        }   
        
        addSeparator(ps);
    }
    
    private void addSeparator(PrintStream ps) {
        ps.println("-------------------------------------------------------------------------------"); // NOI18N
    }
    
    public static String s2s(Object o) {
        return _s2s(o, false);
    }
    
    private static String _s2s(Object o, boolean linkToOrigin) {
        if (o == null) {
            return "null"; //NOI18N
        } else {
            String classAndHash = o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o)); //NOI18N
            
            if (linkToOrigin) {
                classAndHash = "<a href=\"#" + classAndHash + "\">" + classAndHash + "</a>"; //NOI18N
            }
            
            if (o instanceof Container) {
                return classAndHash 
                    + "; name='" + ((Component) o).getName() + "'" //NOI18N
                    + "; isFocusable=" + ((Component) o).isFocusable() //NOI18N
                    + "; isFocusOwner=" + ((Component) o).isFocusOwner() //NOI18N
                    + "; focusTraversalPolicy=" + s2s(((Container) o).getFocusTraversalPolicy()) //NOI18N
                ;
            } else if (o instanceof Component) {
                return classAndHash 
                    + "; name='" + ((Component) o).getName() + "'" //NOI18N
                    + "; isFocusable=" + ((Component) o).isFocusable() //NOI18N
                    + "; isFocusOwner=" + ((Component) o).isFocusOwner() //NOI18N
                ;
            } else {
                return classAndHash;
            }
        }
    }

    private String s2sLink(Component o) {
        Throwable origin = findOrigin(o);
        if (origin != null) {
            String classAndHash = o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o)); //NOI18N
            originsToMention.put(classAndHash, origin);
            return _s2s(o, true);
        } else {
            return s2s(o);
        }
    }

    public static boolean isJdkPatched() {
        findOrigin(null);
        getFailedFocusRequests();
        return !noFindOriginMethod && !noGetFailedFocusRequestsMethod;
    }
    
    private static boolean noFindOriginMethod = false;
    private static Throwable findOrigin(Component c) {
        Throwable origin = null;
        if (!noFindOriginMethod) {
            try {
                Method findOriginMethod = Component.class.getDeclaredMethod("findOrigin", Component.class); //NOI18N
                findOriginMethod.setAccessible(true);
                origin = (Throwable) findOriginMethod.invoke(null, c);
            } catch (Exception e) {
                noFindOriginMethod = true;
                LOG.log(Level.WARNING, null, e);
            }
        }
        return origin;
    }
    
    private static boolean noGetFailedFocusRequestsMethod = false;
    private static Throwable[] getFailedFocusRequests() {
        Throwable[] failedRequests = new Throwable[0];
        if (!noGetFailedFocusRequestsMethod) {
            try {
                Method findOriginMethod = Component.class.getDeclaredMethod("getFailedFocusRequests"); //NOI18N
                findOriginMethod.setAccessible(true);
                failedRequests = (Throwable []) findOriginMethod.invoke(null);
            } catch (Exception e) {
                noGetFailedFocusRequestsMethod = true;
                LOG.log(Level.WARNING, null, e);
            }
        }
        return failedRequests;
    }
    
    private String editorId(JTextComponent c) {
        if (c == null) {
            return "null"; //NOI18N
        } else {
            Document d = c.getDocument();
            Object stream = d.getProperty(Document.StreamDescriptionProperty);
            FileObject f = getFileObjectFor(d);
            return s2sLink(c) + "; stream=" + (f != null ? f.getPath() : s2s(stream)) + "; mimeType='" + d.getProperty("mimeType") + "'"; //NOI18N
        }
    }
    
    private static FileObject getFileObjectFor(Document d) {
        Object stream = d.getProperty(Document.StreamDescriptionProperty);
        FileObject f = null;

        if (stream instanceof FileObject) {
            f = (FileObject) stream;
        } else if (stream instanceof DataObject) {
            f = ((DataObject) stream).getPrimaryFile();
        }
        
        return f;
    }
    
    private void dumpComponentHierarchy(Component c, PrintStream ps, String indent) {
        if (c != null) {
            if (c.isFocusOwner()) {
                ps.print("* "); //NOI18N
            } else {
                ps.print("  "); //NOI18N
            }

            ps.print(indent);
            ps.print(s2sLink(c));
            ps.println();

            if (c instanceof Container) {
                for(Component child : ((Container) c).getComponents()) {
                    dumpComponentHierarchy(child, ps, indent + "  "); //NOI18N
                }
            }
        } else {
            ps.print("  "); //NOI18N
            ps.print(indent);
            ps.println(s2s(c));
        }
    }

    private void dumpAncestors(Component c, PrintStream ps, String indent) {
        ps.print(indent);
        if (c != null) {
            ps.println(s2sLink(c));
            if (c.getParent() != null) {
                dumpAncestors(c.getParent(), ps, indent + "  "); //NOI18N
            }
        } else {
            ps.println(s2s(c));
        }
    }
    
    public static String getVersion() {
        Collection<? extends ModuleInfo> modules = Lookup.getDefault().lookupAll(ModuleInfo.class);
        for(ModuleInfo m : modules) {
            if (m.getCodeNameBase().equals("org.netbeans.modules.editor.probe")) { //NOI18N
                return m.getSpecificationVersion().toString();
            }
        }
        return "?"; //NOI18N
    }
}
