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
 *
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
            System.err.println("Adding chapter");
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
        System.err.println("add Chapter");
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
                System.err.println("Cookie was null, will open and wait");
                if (ck != null || eck != null) {
                    ob.addPropertyChangeListener(this);
                    if (ck != null) {
                        System.err.println("Calling opencookie");
                        ck.open();
                    } else {
                        System.err.println("calling editcookie");
                        eck.edit();
                    }
                }
            } else {
                System.err.println("Got a cookie right off");
                handle (cookie);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_COOKIE.equals(evt.getPropertyName())) {
                EditorCookie cookie = (EditorCookie) ob.getCookie (EditorCookie.class);
                if (cookie != null) {
                    System.err.println("got cookie chabnge");
                    ob.removePropertyChangeListener(this);
                    handle (cookie);
                }
            }
        }

        private void handle (final EditorCookie ck) {
            RequestProcessor.getDefault().post (new Runnable() {
                public void run() {
                    System.err.println("DoHandle in rp");
                    doHandle (ck);
                }
            });
        }

        private void doHandle (EditorCookie ck) {
            ob.removePropertyChangeListener(this);
            System.err.println("Handle");
            ck.open();
            try {
                System.err.println("Waiting for document");
                StyledDocument d = ck.openDocument();
                System.err.println("got document");
                try {
                    StringBuilder b = new StringBuilder (d.getText(0, d.getLength()));
                    System.err.println("Create matcher");
                    Matcher m = DOCTYPE.matcher(b);
                    System.err.println("Got matcher " );
                    System.err.println("Matches? " + m.matches());
                    boolean hit = m.lookingAt();
                    System.err.println("HIT ? " + hit + " on " + b);
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
                                System.err.println("Create entity " + entity);
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

                        System.err.println("Entities: " + entities);
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

                            System.err.println("Known Entity Names " + entityNames);
                            while (mat.find()) {
                                String nm = mat.group(1);
                                System.err.println("FOund used entity " + nm + " known " + entityNames.contains(nm));
                                if (entityNames.contains(nm)) {
                                    //found a spot where another chapter is
                                    //defined
                                    pos = m.end() + 1;
                                    System.err.println("Found insertion point for entity use at " + pos);
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

    private static final Pattern DOCTYPE = Pattern.compile (
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
