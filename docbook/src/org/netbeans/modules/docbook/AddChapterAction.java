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
package org.netbeans.modules.docbook;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 * Add a new chapter to an existing docbeans chapter. 
 * Generates the generic xml chapter file and pre-populates with 
 * dummy data. 
 * 
 * todo move this chapter template to another file and call file
 * from code.
 * @author Tim Boudreau
 */
public class AddChapterAction extends AbstractAction implements ContextAwareAction, LookupListener {
    private final Lookup lkp;
    private final Lookup.Result res;
    /** Creates a new instance of SetMainFileAction */
    public AddChapterAction() {
        this (Utilities.actionsGlobalContext());
    }

    public AddChapterAction (Lookup lkp) {
        this.lkp = lkp;
        putValue (NAME, "Add Chapter");
        assert lkp != null;
        this.res = lkp.lookupResult(DataObject.class);
        resultChanged (null);
    }

    public void actionPerformed(ActionEvent e) {
        DataObject ob = (DataObject) lkp.lookup (DataObject.class);
        if (ob != null) {
            addChapter (ob);
        }
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        return new AddChapterAction (actionContext);
    }

    public void resultChanged(LookupEvent ev) {
        res.allInstances();
        DataObject dob = (DataObject) lkp.lookup(DataObject.class);
        setEnabled (true); //XXX check content
    }

    private void addChapter (DataObject ob) {
        FileObject file = ob.getPrimaryFile();
        new WaitWriter (ob);
    }

    private static class WaitWriter implements PropertyChangeListener {
        private final DataObject ob;
        public WaitWriter (DataObject ob) {
            this.ob = ob;
            EditorCookie cookie = (EditorCookie) ob.getCookie (EditorCookie.class);
            if (cookie == null) {
                OpenCookie ck = null;
                EditCookie eck = null;
                ck = (OpenCookie) ob.getCookie(OpenCookie.class);
                eck = (EditCookie) ob.getCookie (EditCookie.class);
                if (ck != null || eck != null) {
                    ob.addPropertyChangeListener(this);
                    if (ck != null) {
                        ck.open();
                    } else {
                        eck.edit();
                    }
                }
            } else {
                handle (cookie);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_COOKIE.equals(evt.getPropertyName())) {
                EditorCookie cookie = (EditorCookie) ob.getCookie (EditorCookie.class);
                if (cookie != null) {
                    ob.removePropertyChangeListener(this);
                    handle (cookie);
                }
            }
        }

        private void handle (final EditorCookie ck) {
            RequestProcessor.getDefault().post (new Runnable() {
                public void run() {
                    doHandle (ck);
                }
            });
        }

        private void doHandle (EditorCookie ck) {
            ob.removePropertyChangeListener(this);
            ck.open();
            try {
                StyledDocument d = ck.openDocument();
                try {
                    StringBuilder b = new StringBuilder (d.getText(0, d.getLength()));
                    Matcher m = DOCTYPE.matcher(b);
                    boolean hit = m.lookingAt();
                    if (hit) {
                        String rootElement = m.group(1);
                        String publicOne = m.group(2);
                        String publicTwo = m.group(3);
                        String en = m.group(5);
                        List entities = new ArrayList ();
                        Set entityNames = new HashSet();
                        if (en != null && en.length() > 0) {
                            Matcher em = ENTITIES.matcher(en);
                            while (em.find()) {
                                Entity entity = new Entity (em.start(), em.group(1), em.group(2), em.end());
                                entityNames.add (em.group(1).trim());
                                entities.add (entity);
                            }
                        }
                        int insertPoint = entities.isEmpty() ?
                            m.end(3) : ((Entity) entities.get(0)).start()-1;

                        int start = m.end(2);
                        int realStart = b.indexOf("[", start);
                        if (realStart > 0) {
                            insertPoint = realStart + 1;
                        } else {
                            insertPoint = start + 1;
                        }

                        AddChapterPanel pnl = new AddChapterPanel();
                        DialogDescriptor dd =
                            new DialogDescriptor(pnl, "Add Chapter");
                        pnl.setDlg (dd);
                        if (DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd))){
                            String title = pnl.getTitle();
                            String name = pnl.getName();
                            FileObject parent = ob.getPrimaryFile().getParent();
                            FileObject chapterDir = parent.createFolder(name);
                            FileObject chapter = chapterDir.createData(name, "xml");
                            String data =
                              "<chapter>\n" +
                              "    <title>" + title + "</title>\n" +
                              "    <para>\n" +
                              "    [content]\n" +
                              "    </para>\n" +
                              "    <section>\n" +
                              "       <title>" + title + " - Whatcha Gonna Do About It?</title>\n" +
                              "        <para>\n" +
                              "        [more content]\n" +
                              "        </para>\n" +
                              "    </section>\n" +
                              "</chapter>\n";
                            FileLock lock = chapter.lock();
                            OutputStream out = chapter.getOutputStream(lock);
                            PrintWriter pw = new PrintWriter (out);
                            try {
                                pw.println (data);
                            } finally {
                                pw.close();
                                lock.releaseLock();
                            }
                            String entityName = pnl.getEntityName();

                            Pattern p = Pattern.compile ("&(\\S*?);");
                            Matcher mat = p.matcher (b);
                            int pos = -1;

                            while (mat.find()) {
                                String nm = mat.group(1);
                                if (entityNames.contains(nm)) {
                                    //found a spot where another chapter is
                                    //defined
                                    pos = m.end() + 1;
                                    break;
                                }
                            }
                            //XXX insert at end of doc if no other place
                            if (pos != -1 && pos > insertPoint) {
                                d.insertString (pos, "\n&" + entityName + ";\n",
                                        null);
                            }

                            Entity nue = new Entity (insertPoint, entityName, chapterDir.getName() + '/' + chapter.getName(), insertPoint);
                            d.insertString(insertPoint, "\n    " +nue.toString(), null);

                            DataObject dob = DataObject.find (chapter);
                            OpenCookie oc = (OpenCookie) dob.getCookie (OpenCookie.class);
                            if (oc != null) oc.open();
                        }
                    }

                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify (ex);
                }

            } catch (IOException ex) {
                ErrorManager.getDefault().notify (ex);
            }
        }
    }

    private static class Entity {
        public final int loc;
        public final String name;
        public final String systemName;
        public final int len;
        public Entity (int loc, String name, String systemName, int len) {
            this.loc = loc;
            this.name = name;
            this.systemName = systemName;
            this.len = len;
        }

        public int start() {
            return loc;
        }

        public int end() {
            return loc + len;
        }

        public String toString() {
            return "<!ENTITY " + name + " SYSTEM \"" + systemName + ".xml\">";
        }
    }

    static final Pattern DOCTYPE = Pattern.compile (
        "<?.*?\\s*?<!DOCTYPE\\s(.*?)\\s*PUBLIC\\s*\"(.*?)\"\\s*\"(.*?)\"\\s*?(\\[\\s*((.*\\n)*)\\s*\\])*\\s*>");

    private static final Pattern ENTITIES = Pattern.compile (
            "<!ENTITY\\s*(.*?)\\s*SYSTEM.*\"(.*?)\"\\s*>");

    /*
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE book PUBLIC "-//OASIS//DTD DocBook XML V4.4//EN" "http://www.oasis-open.org/docbook/xml/4.4/docbookx.dtd"[
    <!ENTITY modularArchitecture SYSTEM "modularArchitecture/modularArchitecture.xml">
    <!ENTITY looseCoupling SYSTEM "looseCoupling/looseCoupling.xml">
    <!ENTITY modularProgramming SYSTEM "modularProgramming/modularProgramming.xml">
    <!ENTITY mdash "-">
]>
     */
}
