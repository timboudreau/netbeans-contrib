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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author Vita Stejskal <vstejskal at netbeans.org>
 */
public class Probe {

    private static final Logger LOG = Logger.getLogger(Probe.class.getName());
    
    public Probe() {
        
    }
    
    public String getStatus() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, "utf-8"); //NOI18N

            try {
                ps.println("Editor probe dumping info..."); //NOI18N
                
                addEnvironmentInfo(ps);
                addAllMopdules(ps);

                addGeneralStatus(ps);

                List<? extends JTextComponent> editors = EditorRegistry.componentList();
                for(int i = 0; i < editors.size(); i++) {
                    addSingleEditorStatus(editors.get(i), i, ps);
                }
                
                ps.println("Editor probe dump finished!"); //NOI18N
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
            Class topLoggingClass = cl.loadClass("org.netbeans.core.startup.TopLogging"); //NOI18N
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
        addSeparator(ps);
    }
    
    private void addSingleEditorStatus(JTextComponent editor, int index, PrintStream ps) {
        ps.println("Editor[" + index + "]: " + editorId(editor));

        // Dump component info
        ps.println("JTextComponent: " + s2s(editor));
        ps.println("Enabled: " + editor.isEnabled());
        ps.println("Editable: " + editor.isEditable());
        ps.println("Focusable: " + editor.isFocusable());
        ps.println("Focus owner: " + editor.isFocusOwner());
        ps.println("Valid: " + editor.isValid());
        ps.println("Caret: " + s2s(editor.getCaret()));
        ps.println("Caret offset: " + editor.getCaretPosition());
        ps.println("Selection: <" + editor.getSelectionStart() + "," + editor.getSelectionEnd() + ">");
        ps.println("UI: " + s2s(editor.getUI()));
        ps.println("Parent: " + s2s(editor.getParent()));
        ps.println("Keymap size: " + editor.getKeymap().getBoundKeyStrokes().length);
        ps.println();
        
        // Dump document info
        Document document = editor.getDocument();
        ps.println("Document: " + s2s(document));
        ps.println("Mimetype: '" + document.getProperty("mimeType") + "'");
        ps.println("Length: " + document.getLength());
        ps.println("Read locked: " + DocumentUtilities.isReadLocked(document));
        ps.println("Write locked: " + DocumentUtilities.isWriteLocked(document));
        // dump all fields from the whole class hierarchy
        ps.println();
        
        // Dump file info
        FileObject file = getFileObjectFor(document);
        if (file != null) {
            ps.println("File: " + file.getPath());
            ps.println("Mimetype: '" + file.getMIMEType() + "'");
            ps.println("Readonly: " + !file.canWrite());
        } else {
            ps.println("Stream: " + document.getProperty(Document.StreamDescriptionProperty));
        }
        ps.println();
        
        // Dump TopComponent hierarchy
        TopComponent topComponent = (TopComponent) SwingUtilities.getAncestorOfClass(TopComponent.class, editor);
        dumpComponentHierarchy(topComponent, ps, "");
        
        addSeparator(ps);
    }
    
    private void addSeparator(PrintStream ps) {
        ps.println("-------------------------------------------------------------------------------"); // NOI18N
    }
    
    private static String s2s(Object o) {
        return o == null ? "null" : o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o)); //NOI18N
    }
    
    private static String editorId(JTextComponent c) {
        if (c == null) {
            return "null"; //NOI18N
        } else {
            Document d = c.getDocument();
            Object stream = d.getProperty(Document.StreamDescriptionProperty);
            FileObject f = getFileObjectFor(d);
            return s2s(c) + "; stream=" + (f != null ? f.getPath() : s2s(stream)) + "; mimeType='" + d.getProperty("mimeType") + "'"; //NOI18N
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
    
    private static void dumpComponentHierarchy(Component c, PrintStream ps, String indent) {
        if (c != null) {
            if (c.isFocusOwner()) {
                ps.print("* ");
            } else {
                ps.print("  ");
            }

            ps.print(indent);
            ps.print(s2s(c));
            ps.print(": name=" + c.getName()); //NOI18N
            ps.print("; isFocusable=" + c.isFocusable()); //NOI18N
            ps.print("; isFocusOwner=" + c.isFocusOwner()); //NOI18N
            ps.println();

            if (c instanceof Container) {
                for(Component child : ((Container) c).getComponents()) {
                    dumpComponentHierarchy(child, ps, indent + "  "); //NOI18N
                }
            }
        } else {
            ps.print("  ");
            ps.print(indent);
            ps.print(s2s(c));
        }
    }

}
