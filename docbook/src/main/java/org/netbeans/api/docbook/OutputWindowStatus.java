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
package org.netbeans.api.docbook;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import org.openide.ErrorManager;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;
import org.openide.windows.TopComponent;


/**
 *
 * @author Tim Boudreau
 */
public final class OutputWindowStatus extends Renderer.JobStatus {
    private final Renderer.JobStatus other;
    private final String displayName;
    public OutputWindowStatus(String displayName, Renderer.JobStatus other) {
        this.other = other;
        this.displayName = displayName;
    }

    public OutputWindowStatus(String displayName) {
        this.other = null;
        this.displayName = displayName;
    }

    private InputOutput io;
    private synchronized InputOutput getIO() {
        if (io == null) {
            io = IOProvider.getDefault().getIO(displayName, false);
            io.select();
        }
        return io;
    }

    public void started(String msg) {
        running = true;
        InputOutput io = getIO();
        OutputWriter out = io.getOut();
        try {
            out.reset();
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ex);
            return;
        }
        io.select();
        out.println (msg);
        if (other != null) {
            other.started (msg);
        }
    }

    public void progress(String msg) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getOut();
        out.println (msg);
        if (other != null) {
            other.progress (msg);
        }
    }

    public void finished(String msg, File result) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getOut();
        out.println (msg);
        if (other != null) {
            other.finished (msg, result);
        }
        end();
    }

    public void warn (String msg) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getErr();
        OutputListener ol = getListener(msg);
        try {
            if (ol != null) {
                out.println (msg, ol);
            } else {
                out.println (msg);
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify (ioe);
        }
        if (other != null) {
            other.warn (msg);
        }
    }

    public void failed(Throwable t) {
//        assert running;
        InputOutput io = getIO();
        OutputWriter out = io.getErr();
        t.printStackTrace(out);
        end();
    }

    private void end() {
        io.getOut().close();
        io.getErr().close();
        synchronized (this) {
            io = null;
        }
        running = false;
    }

    private volatile boolean running = false;
    public boolean isRunning() {
        return running;
    }


    private static final Pattern errorLinesPattern =
            Pattern.compile("Error.*?file:/(.*?);.*?Line#:\\s(.*?);.*?Column#:\\s([0-9]*)", 0);

    private OutputListener getListener (String str) {
        Matcher m = errorLinesPattern.matcher(str);
        if (m.lookingAt()) {
            String file = m.group(1);
            String lineString = m.group(2);
            String colString = m.group(3);
            int line;
            int col;
            try {
                line = Integer.parseInt(lineString);
                col = Integer.parseInt(colString);
            } catch (NumberFormatException nfe) {
                System.err.println("BAD NUMBER '" + lineString + "' or '" + colString + "'");
                return null;
            }
            return new OL (file, line, col);
        } else {
            System.err.println("Did not match " + str);
        }
        return null;
    }

    private static final class OL implements OutputListener, ActionListener, PropertyChangeListener {
        private final String file;
        private final int line;
        private final int column;
        public OL (String file, int line, int column) {
            this.file = file.replace('/', File.separatorChar);
            this.line = line;
            this.column = column;
        }

        public void outputLineSelected(OutputEvent ev) {
        }

        public void outputLineAction(OutputEvent ev) {
            File f = new File (file);
            if (f.exists()) {
                FileObject fob = FileUtil.toFileObject (f);
                try {
                    DataObject dob = DataObject.find (fob);
                    EditorCookie eck = (EditorCookie) dob.getCookie(EditorCookie.class);
                    if (eck == null) {
                        boolean needTimer = false;
                        OpenCookie ck = (OpenCookie) dob.getCookie(OpenCookie.class);
                        if (ck == null) {
                            EditCookie ec = (EditCookie) dob.getCookie(EditCookie.class);
                            if (ec != null) {
                                startTimer (dob);
                                ec.edit();
                            }
                        } else {
                            startTimer (dob);
                            ck.open();
                        }
                    } else {
                        handleEditorCookie (eck);
                    }
                } catch (DataObjectNotFoundException donfe) {
                    //do nothing
                }
            }
        }

        public void outputLineCleared(OutputEvent ev) {
            if (timer != null && timer.isRunning()) done();
        }

        private Timer timer = null;
        private DataObject dob;
        private void startTimer(DataObject dob) {
            if (timer != null && timer.isRunning()) {
                return;
            } else {
                this.dob = dob;
                timer = new Timer (2000, this);
                timer.setRepeats(false);
                timer.start();
            }
            dob.addPropertyChangeListener(this);
        }

        private void handleEditorCookie(EditorCookie eck) {
            JEditorPane[] panes = eck.getOpenedPanes();
            JEditorPane pane;
            if (panes != null && panes.length > 0) {
                pane = panes[0];
            } else {
                eck.open();
                try {
                    eck.openDocument();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify (ex);
                    return;
                }
                panes = eck.getOpenedPanes();
                if (panes.length == 0) {
                    return;
                }
                pane = panes[0];
            }
            TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(
                    TopComponent.class, pane);
            if (tc != null) {
                tc.open();
                tc.requestActive();
            }
            StyledDocument doc = eck.getDocument();
            Element root = doc.getDefaultRootElement();
            int lineCount = root.getElementCount();
            if (line >= lineCount) {
                return;
            }
            Element lineEl = root.getElement(line);
            int offset = lineEl.getStartOffset();
            if (offset + column < doc.getLength()) {
                offset += column;
            }
            pane.setSelectionStart(offset);
            pane.setSelectionEnd (offset);
            try {
                Rectangle r = pane.modelToView(offset);
                if (r != null) {
                    pane.scrollRectToVisible(r);
                }
                pane.requestFocus();
            } catch (BadLocationException ex) {
                //Can happen if document changed under us
                ErrorManager.getDefault().notify (
                        ErrorManager.INFORMATIONAL, ex);
            }
        }

        private void done() {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
            if (dob != null) {
                dob.removePropertyChangeListener(this);
            }
        }

        public void actionPerformed(ActionEvent e) {
            EditorCookie ck = (EditorCookie) dob.getCookie (EditorCookie.class);
            if (ck != null) {
                handleEditorCookie (ck);
            }
            done();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_COOKIE.equals(evt.getPropertyName())) {
                EditorCookie ck = (EditorCookie) dob.getCookie (EditorCookie.class);
                if (ck != null) {
                    done();
                    handleEditorCookie (ck);
                }
            }
        }
    }

}
