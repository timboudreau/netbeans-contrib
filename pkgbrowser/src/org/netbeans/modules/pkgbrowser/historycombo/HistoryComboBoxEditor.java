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
package org.netbeans.modules.pkgbrowser.historycombo;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Tim Boudreau
 */
public class HistoryComboBoxEditor implements ComboBoxEditor, DocumentListener, KeyListener, FocusListener {
    private final JTextField jtf = new JTextField();
    private final JComboBox box;
    /** Creates a new instance of HistoryComboBoxEditor */
    public HistoryComboBoxEditor(JComboBox box) {
        jtf.getDocument().addDocumentListener(this);
        jtf.addKeyListener(this);
        jtf.addFocusListener(this);
        this.box = box;
    }

    public Component getEditorComponent() {
        return jtf;
    }

    private CompletionComboBoxModel mdl() {
        return (CompletionComboBoxModel) box.getModel();
    }

    private String item;
    public void setItem(Object o) {
        assert o == null || o instanceof String;
        if (o == null) {
            o = "";
        }
        this.item = o.toString();
    }

    public Object getItem() {
        return this.item;
    }

    public void selectAll() {
        jtf.setSelectionStart(0);
        jtf.setSelectionEnd(jtf.getText().length());
    }

    private List listeners = Collections.synchronizedList (new LinkedList());
    public void addActionListener(ActionListener l) {
        listeners.add (l);
    }

    public void removeActionListener(ActionListener l) {
        listeners.remove (l);
    }

    public void insertUpdate(DocumentEvent e) {
        change();
    }

    public void removeUpdate(DocumentEvent e) {
        change();
    }

    public void changedUpdate(DocumentEvent e) {
        change();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
    }

    private void change() {
        String unsel = getUnselectedText();
        String sel = getSelectedText();
        if (isTextAutoSelected() || "".equals(sel)) {
            
        }
    }

    private String getUnselectedText() {
        String s = jtf.getText();
        int start = jtf.getSelectionStart();
        int end = jtf.getSelectionEnd();
        if (start == end) {
            return s;
        } else if (start > 0) {
            return s.substring(0, start);
        } else {
            return "";
        }
    }

    private String getSelectedText() {
        String s = jtf.getText();
        int start = jtf.getSelectionStart();
        int end = jtf.getSelectionEnd();
        if (start == end) {
            return "";
        } else if (end == s.length() -1) {
            return s.substring (start, end);
        } else {
            return "";
        }
    }

    private String lastAutoSelectedText = "";
    private boolean isTextAutoSelected() {
        return lastAutoSelectedText.equals(getSelectedText())
            && !"".equals(lastAutoSelectedText);
    }
}
