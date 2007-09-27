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

package org.netbeans.modules.quickfilechooser;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * The UI for the {@link JFileChooser}.
 * @author Jesse Glick, Tim Boudreau
 */
public class ChooserComponentUI extends BasicFileChooserUI {
    
    private JTextField text;
    private DefaultListModel completionsModel;
    private JList completions;
    private String maximalCompletion;
    private boolean currentDirectoryChanging;
    private JButton approve;
    private JPanel buttons;
    private JLabel filterLabel;
    private boolean historyChanging;

    public ChooserComponentUI(JFileChooser jfc) {
        super(jfc);
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new ChooserComponentUI((JFileChooser) c);
    }
    
    private JFileChooser filechooser = null;
    private JComboBox box = null;
    public void installComponents(JFileChooser fc) {
        super.installComponents(fc);
        fc.setLayout(new BorderLayout());
        filechooser = fc;
        
        String[] hist = getHistory();
        JPanel histPanel = new JPanel();
        histPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        box = new JComboBox(hist);
    
        // XXX this is not so good, since moving arrow keys in box changes selection *before* it is closed:
        box.addActionListener(new HAL());
        histPanel.setLayout(new BorderLayout());
        JLabel histInstructions = new JLabel(getBundle().getString("LBL_History"));
        histInstructions.setDisplayedMnemonic(getBundle().getString("MNEM_History").charAt(0));
        histInstructions.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        histInstructions.setLabelFor(box);
        histPanel.add(box, BorderLayout.CENTER);
        histPanel.add(histInstructions, BorderLayout.LINE_START);
        if (getHistory().length == 0) {
            box.setEnabled(false);
        }
        
        text = new JTextField(100) {
            public void addNotify() {
                super.addNotify();
                // #62761: need to set this up on the focus cycle root, not the text field:
                getFocusCycleRootAncestor().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                        Collections.singleton(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK)));
            }
        };
        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "complete");
        text.getActionMap().put("complete", new CompleteAction());
        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_DOWN_MASK), "delete-path-component");
        text.getActionMap().put("delete-path-component", new DeletePathComponentAction());
        text.setFont(new Font("Monospaced", Font.PLAIN, text.getFont().getSize())); // NOI18N
        Action up = new UpDownAction(true);
        Action down = new UpDownAction(false);
        text.getActionMap().put("up", up);
        text.getActionMap().put("down", down);
        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up"); //NOI18N
        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                "down"); //NOI18N
        
        text.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                refreshCompletions();
            }
            public void removeUpdate(DocumentEvent e) {
                refreshCompletions();
            }
            public void changedUpdate(DocumentEvent e) {}
        });
        JPanel pnl = new JPanel() {
            // XXX crude but I'm not sure how else to give text focus before box:
            public void addNotify() {
                super.addNotify();
                text.requestFocus();
            }
        };
        pnl.setLayout(new BorderLayout());
        pnl.add(text, BorderLayout.CENTER);
        JLabel instructions = new JLabel(getBundle().getString("LBL_TextField"));
        instructions.setDisplayedMnemonic(getBundle().getString("MNEM_TextField").charAt(0));
        instructions.setLabelFor(text);
        instructions.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        histPanel.add(instructions, BorderLayout.PAGE_END);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        pnl.add(histPanel, BorderLayout.PAGE_START);
        
        fc.add(pnl, BorderLayout.PAGE_START);
        completionsModel = new DefaultListModel();
        completions = new JList(completionsModel);
        completions.addMouseListener(new CML());
        
        completions.setVisibleRowCount(25);
        completions.setEnabled(false);
        completions.setCellRenderer(new FileCellRenderer());
        JScrollPane jsc = new JScrollPane(completions);
        JPanel pnl2 = new JPanel();
        pnl2.setLayout(new BorderLayout());
        filterLabel = new JLabel();
        pnl2.add(filterLabel, BorderLayout.PAGE_START);
        pnl2.add(jsc, BorderLayout.CENTER);
        pnl2.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 0));
        
        fc.add(pnl2, BorderLayout.CENTER);
        approve = new JButton(getApproveButtonText(fc));
        approve.addActionListener(getApproveSelectionAction());
        approve.setDefaultCapable(true);
        JButton cancel = new JButton(cancelButtonText);
        cancel.addActionListener(getCancelSelectionAction());
        buttons = new JPanel();
        buttons.setLayout(new BorderLayout());
        JPanel rightButtons = new JPanel();
        rightButtons.setLayout(new FlowLayout(FlowLayout.TRAILING));
        rightButtons.add(approve);
        rightButtons.add(cancel);
        JButton classic = new JButton(getBundle().getString("LBL_Classic"));
        classic.setMnemonic(getBundle().getString("MNEM_Classic").charAt(0));
        classic.setEnabled(Install.isInstalled());
        classic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Just calling setUI does not work well:
                // JFileChooser.updateUI is needed to update JFC.uiFileView;
                // otherwise get NPEs when trying to display anything, since icons are all null.
                Install.uninstall();
                filechooser.updateUI();
                // When closed, reinstall UI so it will be available for the next chooser.
                filechooser.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Install.install();
                    }
                });
            }
        });
        JPanel leftButtons = new JPanel();
        leftButtons.add(classic);
        buttons.add(leftButtons, BorderLayout.LINE_START);
        buttons.add(rightButtons, BorderLayout.LINE_END);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
        fc.add(buttons, BorderLayout.PAGE_END);
        getAccessoryPanel().setBorder(
                BorderFactory.createEmptyBorder(12,0,12,12));
        fc.add(getAccessoryPanel(), BorderLayout.LINE_END);
        JComponent x = fc.getAccessory();
        if (x != null) {
            getAccessoryPanel().add(x);
        }
        updateButtons();
        updateFilterDisplay();
    }
    
    private static ResourceBundle getBundle() {
        // Avoid using NbBundle, so we can work as a library in any app.
        return ResourceBundle.getBundle("org.netbeans.modules.quickfilechooser.Bundle");
    }
    
    private void updateButtons() {
        File f = getFileChooser().getSelectedFile();
        
        if (getFileChooser() != null && getFileChooser().getFileFilter() != null && f != null) {
            boolean accepted = getFileChooser().getFileFilter().accept(f);
            getApproveSelectionAction().setEnabled(accepted);
            approve.setEnabled(accepted);
            if (accepted && approve.isShowing() && approve.getTopLevelAncestor() instanceof JDialog) {
                JDialog dlg = (JDialog) approve.getTopLevelAncestor();
                dlg.getRootPane().setDefaultButton(approve);
            } else {
                approve.setEnabled(false);
            }
        } else {
            getApproveSelectionAction().setEnabled(false);
            approve.setEnabled(false);
        }
    }
    
    
    protected JButton getApproveButton(JFileChooser fc) {
        return approve;
    }
    
    
    private class HAL implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            if (historyChanging) {
                return;
            }
            updateFromHistory();
        }
    }
    
    private void updateFromHistory() {
        Object item = box.getSelectedItem();
        if (item == null) {
            return;
        }
        String name = item.toString();
        if (!name.endsWith(File.separator)) {
            name += File.separatorChar;
        }
        text.setText(name);
        refreshCompletions();
        text.requestFocus();
    }

    public void uninstallComponents(JFileChooser fc) {
        fc.removeAll();
        text = null;
        completions = null;
        completionsModel = null;
        super.uninstallComponents(fc);
    }
    
    public String getFileName() {
        return normalizeFile(text.getText());
    }
    
    private static String normalizeFile(String text) {
        // See #21690 for background.
        // XXX what are legal chars for var names? bash manual says only:
        // "The braces are required when PARAMETER [...] is followed by a
        // character that is not to be interpreted as part of its name."
        Pattern p = Pattern.compile("(^|[^\\\\])\\$([a-zA-Z_0-9.]+)");
        Matcher m;
        while ((m = p.matcher(text)).find()) {
            // Have an env var to subst...
            // XXX handle ${PATH} too? or don't bother
            String var = System.getenv(m.group(2));
            if (var == null) {
                // Try Java system props too, and fall back to "".
                var = System.getProperty(m.group(2), "");
            }
            // XXX full readline compat would mean vars were also completed with TAB...
            text = text.substring(0, m.end(1)) + var + text.substring(m.end(2));
        }
        if (text.equals("~")) {
            return System.getProperty("user.home");
        } else if (text.startsWith("~" + File.separatorChar)) {
            return System.getProperty("user.home") + text.substring(1);
        } else {
            int i = text.lastIndexOf("//");
            if (i != -1) {
                // Treat /home/me//usr/local as /usr/local
                // (so that you can use "//" to start a new path, without selecting & deleting)
                return text.substring(i + 1);
            }
            i = text.lastIndexOf(File.separatorChar + "~" + File.separatorChar);
            if (i != -1) {
                // Treat /usr/local/~/stuff as /home/me/stuff
                return System.getProperty("user.home") + text.substring(i + 2);
            }
            return text;
        }
    }
    
    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String name = e.getPropertyName();
                if (JFileChooser.ACCESSORY_CHANGED_PROPERTY.equals(name)) {
                    JComponent x = (JComponent) e.getOldValue();
                    if (x != null) {
                        getAccessoryPanel().remove(x);
                    }
                    x = (JComponent) e.getNewValue();
                    if (x != null) {
                        getAccessoryPanel().add(x);
                    }
                } else if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(name) && !currentDirectoryChanging) {
                    currentDirectoryChanging = true;
                    try {
                        String t = getFileChooser().getCurrentDirectory().getAbsolutePath() + File.separatorChar;
                        text.setText(t);
                        text.setCaretPosition(t.length());
                    } finally {
                        currentDirectoryChanging = false;
                    }
                } else if ((JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(name) ||
                        JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(name)) && !currentDirectoryChanging) {
                    currentDirectoryChanging = true;
                    try {
                        File f = getFileChooser().getSelectedFile();
                        String t;
                        if (f != null) {
                            if (f.isDirectory() && getFileChooser().getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY) {
                                // Works best when called from
                                // org.netbeans.modules.project.ui.actions.OpenProject.actionPerformed
                                t = f.getAbsolutePath() + File.separatorChar;
                            } else {
                                t = f.getAbsolutePath();
                            }
                        } else {
                            t = "";
                        }
                        text.setText(t);
                        text.setCaretPosition(t.length());
                    } finally {
                        currentDirectoryChanging = false;
                    }
                } else if (JFileChooser.CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY.equals(name)) {
                    buttons.setVisible(getFileChooser().getControlButtonsAreShown());
                } else if (JFileChooser.FILE_FILTER_CHANGED_PROPERTY.equals(name)) {
                    refreshCompletions();
                    updateFilterDisplay();
                }
            }
        };
    }
    
    private void refreshCompletions() {
        completionsModel.clear();
        maximalCompletion = null;
        String name = getFileName();
        int slash = name.lastIndexOf(File.separatorChar);
        if (slash != -1) {
            String prefix = name.substring(0, slash + 1);
            String suffix = name.substring(slash + 1);
            int suffixLen = suffix.length();
            File d = new File(prefix);
            if (d.isDirectory()) {
                String[] kids = d.list();
                if (kids != null) {
                    Arrays.sort(kids, Collator.getInstance());
                    for (int i = 0; i < kids.length; i++) {
                        File kid = new File(d, kids[i]);
                        if (!getFileChooser().accept(kid)) {
                            continue;
                        }
                        if (getFileChooser().getFileSelectionMode() == JFileChooser.DIRECTORIES_ONLY && !kid.isDirectory()) {
                            continue;
                        }
                        if (kids[i].regionMatches(true, 0, suffix, 0, suffixLen)) {
                            completionsModel.addElement(kid);
                            if (maximalCompletion == null) {
                                maximalCompletion = kids[i];
                            } else {
                                int p = maximalCompletion.length();
                                while (p > 0) {
                                    if (kids[i].regionMatches(true, 0, maximalCompletion, 0, p)) {
                                        break;
                                    }
                                    p--;
                                }
                                maximalCompletion = maximalCompletion.substring(0, p);
                            }
                        }
                    }
                }
            }
        }
        // Fire changes to interested listeners. Note that we only support a single
        // file selection, but some listeners may be asking for getSelectedFiles, so humor them.
        if (!currentDirectoryChanging) {
            currentDirectoryChanging = true;
            try {
                File file = new File(getFileName());
                getFileChooser().setSelectedFiles(new File[] {file});
                setDirectorySelected(file.exists() && file.isDirectory());
            } finally {
                currentDirectoryChanging = false;
            }
        }
        updateButtons();
    }
    
    private final class CompleteAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            String name = getFileName();
            int slash = name.lastIndexOf(File.separatorChar);
            if (slash == -1) {
                // #67972: maybe user is just starting to type a filename and pressed TAB.
                // Harmless but we will not try to complete anything just yet.
                return;
            }
            String newname = maximalCompletion != null ? name.substring(0, slash + 1) + maximalCompletion : null;
            File newnameF = newname != null ? new File(newname) : null;
            if (newnameF != null && newnameF.isDirectory() && !newname.endsWith(File.separator)) {
                // Also check that there is no non-dir completion (e.g. .../nb_all/nbbuild/build{,.xml,.properties})
                String[] siblings = newnameF.getParentFile().list();
                boolean complete = true;
                String me = newnameF.getName();
                for (int i = 0; i < siblings.length; i++) {
                    if (siblings[i].startsWith(me) && !siblings[i].equals(me)) {
                        complete = false;
                        break;
                    }
                }
                if (complete && !newname.endsWith(File.separator)) {
                    newname += File.separatorChar;
                }
            }
            if (maximalCompletion != null && !newname.equals(name)) {
                text.setText(newname);
            } else {
                // Just scroll completions pane if necessary.
                int start = completions.getFirstVisibleIndex();
                int end = completions.getLastVisibleIndex();
                if (start != -1 && end != -1) { // something visible now
                    if (end < completionsModel.size() - 1) {
                        // Scroll down some. Keep two overlap lines, since lines can be half-visible.
                        completions.ensureIndexIsVisible(Math.min(2 * end - start - 1, completionsModel.size() - 1));
                    } else if (start > 0) {
                        // Scroll back up to the top.
                        completions.ensureIndexIsVisible(0);
                    }
                }
            }
        }
    }
    
    private final class DeletePathComponentAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            String t = text.getText();
            int cut = Math.max(t.lastIndexOf(File.separatorChar), t.lastIndexOf('.') - 1);
            if (cut + 1 == t.length()) {
                t = t.substring(0, t.length() - 1);
                cut = Math.max(t.lastIndexOf(File.separatorChar), t.lastIndexOf('.') - 1);
            }
            String newText;
            if (cut == -1) {
                newText = "";
            } else if (cut == t.length() - 1) {
                // XXX when running in JDK 1.4 (but not 1.6, didn't check 1.5)
                // it seems that DefaultEditorKit.DeletePrevCharAction is run after
                // this action (bound to unmodified BACK_SPACE), for no obvious reason
                newText = t.substring(0, cut);
            } else {
                newText = t.substring(0, cut + 1);
            }
            text.setText(newText);
        }
        
    }
    
    private final class FileCellRenderer extends DefaultListCellRenderer/*<File>*/ {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            File f = (File) value;
            Component soop = super.getListCellRendererComponent(list, f.getName(), index, isSelected, cellHasFocus);
            setIcon(getFileChooser().getIcon(f));
            setEnabled(true); // don't draw text or icons grayed out
            return soop;
        }
        
    }
    
    public Action getApproveSelectionAction() {
        return new ProxyApproveSelectionAction(super.getApproveSelectionAction());
    }
    
    private static final int HISTORY_MAX_SIZE = 25;
    
    private static void updateHistory(JFileChooser jfc) {
        File f = jfc.getSelectedFile();
        String pth = f.getParent();
        if (history == null) {
            history = new ArrayList();
        }
        // Always put the new entry at the front:
        history.remove(pth);
        history.add(0, pth);
        if (history.size() > HISTORY_MAX_SIZE) {
            history.subList(HISTORY_MAX_SIZE, history.size()).clear();
        }
        StringBuffer buf = new StringBuffer();
        for (Iterator i = history.iterator(); i.hasNext();) {
            buf.append((String) i.next());
            if (i.hasNext()) {
                buf.append(File.pathSeparatorChar);
            }
        }
        Preferences prefs = Preferences.userNodeForPackage(ChooserComponentUI.class);
        prefs.put(KEY, buf.toString());
    }
    
    private static final String KEY = "recentFolders";
    
    private static List/*<String>*/ history = null;
    private static String[] getHistory() {
        if (history == null) {
            loadHistory();
        }
        return (String[]) history.toArray(new String[history.size()]);
    }
    
    private static void loadHistory() {
        Preferences prefs = Preferences.userNodeForPackage(ChooserComponentUI.class);
        String hist = prefs.get(KEY, "");
        history = new ArrayList();
        if (hist.length() > 0) {
            for (StringTokenizer tok = new StringTokenizer(hist, File.pathSeparator); tok.hasMoreTokens();) {
                String f = tok.nextToken();
                if ((new File(f)).exists()) {
                    history.add(f);
                }
            }
        }
    }

    private void updateFilterDisplay() {
        FileFilter filter = getFileChooser().getFileFilter();
        if (filter != null) {
            filterLabel.setText(filter.getDescription());
            filterLabel.setVisible(true);
        } else {
            filterLabel.setVisible(false);
        }
    }
    
    private class ProxyApproveSelectionAction implements Action, PropertyChangeListener {
        private final Action delegate;
        public ProxyApproveSelectionAction(Action delegate) {
            this.delegate = delegate;
        }
        
        public Object getValue(String key) {
            return delegate.getValue(key);
        }
        
        public void putValue(String key, Object value) {
            delegate.putValue(key, value);
        }
        
        public void setEnabled(boolean b) {
            delegate.setEnabled(b);
        }
        
        public boolean isEnabled() {
            return delegate.isEnabled();
        }
        
        private List l = new ArrayList();
        public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
            l.add(listener);
            if (l.size() == 1) {
                delegate.addPropertyChangeListener(this);
            }
        }
        
        public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
            l.remove(listener);
            if (l.isEmpty()) {
                delegate.removePropertyChangeListener(this);
            }
        }
        
        public void actionPerformed(ActionEvent e) {
            delegate.actionPerformed(e);
            updateHistory(filechooser);
        }
        
        public void propertyChange(PropertyChangeEvent old) {
            PropertyChangeListener[] pcl;
            synchronized (this) {
                pcl = (PropertyChangeListener[]) l.toArray(new PropertyChangeListener[l.size()]);
            }
            if (pcl.length > 0) {
                PropertyChangeEvent nue = new PropertyChangeEvent(this,
                        old.getPropertyName(), old.getOldValue(),
                        old.getNewValue());
                for (int i=0; i < pcl.length; i++) {
                    pcl[i].propertyChange(nue);
                }
            }
        }
    }
    
    private class CML extends MouseAdapter {
        public void mousePressed(MouseEvent me) {
            JList jl = (JList) me.getSource();
            int idx = jl.locationToIndex(me.getPoint());
            if (idx != -1) {
                text.setText(jl.getModel().getElementAt(idx).toString());
                if (me.getClickCount() == 2) {
                    getApproveSelectionAction().actionPerformed(new ActionEvent(approve, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        }
    }
    
    private class UpDownAction extends AbstractAction {
        private final boolean up;
        UpDownAction(boolean up) {
            this.up = up;
        }
        public void actionPerformed(ActionEvent ae) {
            updateFromHistory();
            int sz = box.getModel().getSize();
            int sel = box.getSelectedIndex();
            if (up) {
                sel++;
            } else {
                sel--;
            }
            if (sel < 0) {
                sel = box.getModel().getSize() -1;
            } else if (sel >= box.getModel().getSize()) {
                sel = 0;
            }
            // setSelectedIndex will fire an action event on box, so suppress it for a moment:
            assert !historyChanging;
            historyChanging = true;
            try {
                box.setSelectedIndex(sel);
            } finally {
                historyChanging = false;
            }
        }
        public boolean isEnabled() {
            return box.getModel().getSize() > 0;
        }
    }
}
