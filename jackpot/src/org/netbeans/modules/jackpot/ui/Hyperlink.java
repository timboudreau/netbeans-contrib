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

package org.netbeans.modules.jackpot.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.Line;
import org.openide.util.WeakSet;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 * Hyperlink errors to rule file lines from ScriptParser log messages.
 */
public class Hyperlink  extends Annotation implements OutputListener, PropertyChangeListener {
    private URL url;
    private int line;
    private String errorMsg;
    private boolean dead = false;

    public static Hyperlink parse(String error) {
        StringTokenizer st = new StringTokenizer(error, ":");
        try {
            String filename = st.nextToken();
            File file = FileUtil.normalizeFile(new File(filename));
            if (!file.exists())
                return null;
            URL url = file.toURI().toURL();
            int line = Integer.parseInt(st.nextToken());
            String err = st.nextToken();
            return new Hyperlink(url, line, err);
        } catch (Exception e) {
            // not an error line, ignore
            return null;
        }
    }

    Hyperlink(URL url, int line, String err) {
        this.url = url;
        this.line = line;
        this.errorMsg = err;
        synchronized (hyperlinks) {
            hyperlinks.add(this);
        }
    }

    public String getShortDescription() {
        return errorMsg;
    }

    public String getAnnotationType() {
        return "jackpot-rules-error-annotation"; // NOI18N
    }

    public void outputLineSelected(OutputEvent ev) {
        if (dead) 
            return;
        FileObject file = URLMapper.findFileObject(url);
        if (file == null)
            return;
        try {
            DataObject dob = DataObject.find(file);
            EditorCookie ed = (EditorCookie) dob.getCookie(EditorCookie.class);
            if (ed != null) {
                if (ed.getDocument() == null)
                    // The document is not opened, don't bother with it.
                    return;
                Line l = ed.getLineSet().getOriginal(line - 1);
                if (! l.isDeleted()) {
                    attachAsNeeded(l);
                    l.show(Line.SHOW_TRY_SHOW);
                }
            }
        } catch (DataObjectNotFoundException donfe) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, donfe);
        } catch (IndexOutOfBoundsException iobe) {
            // Probably harmless. Bogus line number.
        }
    }

    public void outputLineCleared(OutputEvent ev) {
        doDetach();
    }
    
    public void outputLineAction(OutputEvent ev) {
        if (dead) 
            return;
        FileObject file = URLMapper.findFileObject(url);
        if (file == null) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }
        try {
            DataObject dob = DataObject.find(file);
            EditorCookie ed = (EditorCookie) dob.getCookie(EditorCookie.class);
            if (ed != null && file == dob.getPrimaryFile()) {
                ed.openDocument();
                try {
                    Line l = ed.getLineSet().getOriginal(line - 1);
                    if (! l.isDeleted()) {
                        attachAsNeeded(l);
                            l.show(Line.SHOW_GOTO);
                    }
                } catch (IndexOutOfBoundsException ioobe) {
                    // Probably harmless. Bogus line number.
                    ed.open();
                }
            } else {
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
        } catch (DataObjectNotFoundException donfe) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, donfe);
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ioe);
        }
        if (errorMsg != null) {
            // Try to do after opening the file, since opening a new file
            // clears the current status message.
            StatusDisplayer.getDefault().setStatusText(errorMsg);
        }
    }

    public void propertyChange(PropertyChangeEvent ev) {
        if (dead) return;
        String prop = ev.getPropertyName();
        if (prop == null ||
        prop.equals(Annotatable.PROP_TEXT) ||
        prop.equals(Annotatable.PROP_DELETED)) {
            // Affected line has changed.
            // Assume user has edited & corrected the error.
            doDetach();
        }
    }
    
    void destroy() {
        doDetach();
        dead = true;
    }
    
    private synchronized void attachAsNeeded(Line l) {
        if (getAttachedAnnotatable() == null) {
            Annotatable ann = l;
            attach(l);
            synchronized (hyperlinks) {
                Iterator it = hyperlinks.iterator();
                while (it.hasNext()) {
                    Hyperlink h = (Hyperlink)it.next();
                    if (h != this) {
                        h.doDetach();
                    }
                }
            }
            l.addPropertyChangeListener(this);
        }
    }

    private synchronized void doDetach() {
        Annotatable ann = getAttachedAnnotatable();
        if (ann != null) {
            ann.removePropertyChangeListener(this);
            detach();
        }
    }
    
    private static final Set<Hyperlink> hyperlinks = new WeakSet<Hyperlink>();
    public static void detachAllAnnotations() {
        synchronized (hyperlinks) {
            Iterator<Hyperlink> it = hyperlinks.iterator();
            while (it.hasNext()) {
                it.next().destroy();
            }
        }
    }
}
