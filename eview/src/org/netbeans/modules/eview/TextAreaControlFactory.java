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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.eview;

import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentListener;
import org.netbeans.api.eview.ControlFactory;
import org.openide.filesystems.FileObject;

/**
 * ControlFactory creating JTextAreas.
 * Supports additional attributes "initValue" and "verifier".
 * @author David Strupl
 */
public class TextAreaControlFactory implements ControlFactory {

    /** Sharedevent instance. */
    private static final PropertyChangeEvent pcEvent = new PropertyChangeEvent(TextFieldControlFactory.class, "text", null, null);
    
    /** String value that is put into the text field right after initialization */
    private String initValue;
    /** Verifier object used to verify the text input */
    private InputVerifier verifier;
    
    /** */
    private boolean separateLines = false;
    
    /** */
    private int numberOfLines = 3;
    
    /**
     * Creates a new instance of TextAreaControlFactory 
     */
    public TextAreaControlFactory(FileObject f) {
        Object o1 = f.getAttribute("initValue");
        if (o1 != null) {
            initValue = o1.toString();
        }
        Object o2 = f.getAttribute("verifier");
        if (o2 instanceof InputVerifier) {
            verifier = (InputVerifier)o2;
        }
        Object o3 = f.getAttribute("separateLines");
        if (o3 instanceof Boolean) {
            separateLines = ((Boolean)o3).booleanValue();
        }
        Object o4 = f.getAttribute("lines");
        if (o4 instanceof Integer) {
            numberOfLines = ((Integer)o4).intValue();
        }
    }

    public void addPropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            ControlListener controlListener = new ControlListener(l);
            jtf.getDocument().removeDocumentListener(controlListener);
            jtf.getDocument().addDocumentListener(controlListener);
        }
    }

    public JComponent createComponent() {
        JTextArea result = new JTextArea();
        if (verifier != null) {
            result.setInputVerifier(verifier);
        }
        if (initValue != null) {
            result.setText(initValue);
        }
        result.setRows(numberOfLines+1);
 
        Set set = new HashSet(result.getFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS ) );
        set.add(KeyStroke.getKeyStroke("TAB"));
        result.setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
 
        set = new HashSet(result.getFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        set.add( KeyStroke.getKeyStroke("shift TAB"));
        result.setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, set);
        JScrollPane jsp = new JScrollPane();
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setViewportView(result);
        jsp.getVerticalScrollBar().setFocusable(false);
        return jsp;
    }

    public Object getValue(JComponent c) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            if (separateLines) {
                String s = jtf.getText();
                String []ss = s.split("\n");
                return ss;
            }
            return jtf.getText();
        }
        return null;
    }

    public String convertValueToString(JComponent c, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String[]) {
            String[] sa = (String[])value;
            if (sa.length > 0) {
                return sa[0];
            }
            return "";
        }
        return value.toString();
    }
    
    public void removePropertyChangeListener(JComponent c, PropertyChangeListener l) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            ControlListener controlListener = new ControlListener(l);
            jtf.getDocument().removeDocumentListener(controlListener);
        }
    }

    public void setValue(JComponent c, Object value) {
        if (c instanceof JScrollPane) {
            JScrollPane jsp = (JScrollPane)c;
            c = (JComponent)jsp.getViewport().getView();
        }
        if (c instanceof JTextArea) {
            JTextArea jtf = (JTextArea)c;
            if (value == null) {
                value = "null"; // NOI18N
            }
            if (separateLines) {
                String res = "";
                if (value instanceof String[]) {
                    String ss[] = (String[])value;
                    for (int i = 0; i < ss.length; i++) {
                        res += ss[i] + "\n";
                    }
                }
                jtf.setText(res);
            } else {
                jtf.setText(value.toString());
            }
        }
    }
    
    /**
     * Listener attached to the control to propagate changes to our
     * listeners.
     */
    private class ControlListener implements DocumentListener {
        private PropertyChangeListener pcl;
        public ControlListener(PropertyChangeListener pcl){
            this.pcl = pcl;
        }
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            pcl.propertyChange(pcEvent);
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            pcl.propertyChange(pcEvent);
        }

        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            pcl.propertyChange(pcEvent);
        }
        public boolean equals(Object anotherObject) {
            if ( ! ( anotherObject instanceof ControlListener ) ) {
                return false;
            }
            ControlListener theOtherOne = (ControlListener)anotherObject;
            return pcl.equals(theOtherOne.pcl);
        }
        public int hashCode() {
            return pcl.hashCode();
        }
    }
}
