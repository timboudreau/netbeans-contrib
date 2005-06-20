/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.quickfilechooser;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * The UI for the {@link JFileChooser}.
 * @author Jesse Glick
 */
public class ChooserComponentUI extends BasicFileChooserUI {
    
    private JTextField text;
    private DefaultListModel completionsModel;
    private JList completions;
    private String maximalCompletion;
    private boolean currentDirectoryChanging;
    
    public ChooserComponentUI(JFileChooser jfc) {
        super(jfc);
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new ChooserComponentUI((JFileChooser) c);
    }

    public void installComponents(JFileChooser fc) {
        super.installComponents(fc);
        fc.setLayout(new BorderLayout());
        text = new JTextField(100);
        text.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
            Collections.singleton(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_DOWN_MASK)));
        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "complete");
        text.getActionMap().put("complete", new CompleteAction());
        text.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_DOWN_MASK), "delete-path-component");
        text.getActionMap().put("delete-path-component", new DeletePathComponentAction());
        text.setFont(new Font("Monospaced", Font.PLAIN, text.getFont().getSize())); // NOI18N
        text.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                refreshCompletions();
            }
            public void removeUpdate(DocumentEvent e) {
                refreshCompletions();
            }
            public void changedUpdate(DocumentEvent e) {}
        });
        fc.add(text, BorderLayout.PAGE_START);
        completionsModel = new DefaultListModel();
        completions = new JList(completionsModel);
        completions.setVisibleRowCount(25);
        completions.setEnabled(false);
        completions.setCellRenderer(new FileCellRenderer());
        fc.add(new JScrollPane(completions), BorderLayout.CENTER);
	JButton approve = new JButton(getApproveButtonText(fc));
	approve.addActionListener(getApproveSelectionAction());
        approve.setDefaultCapable(true);
	JButton cancel = new JButton(cancelButtonText);
	cancel.addActionListener(getCancelSelectionAction());
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.TRAILING));
        buttons.add(approve);
        buttons.add(cancel);
        fc.add(buttons, BorderLayout.SOUTH);
        fc.add(getAccessoryPanel(), BorderLayout.EAST);
	JComponent x = fc.getAccessory();
	if (x != null) {
	    getAccessoryPanel().add(x);
	}
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
        if (text.equals("~")) {
            return System.getProperty("user.home");
        } else if (text.startsWith("~" + File.separatorChar)) {
            return System.getProperty("user.home") + text.substring(1);
        } else {
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
                }
                // XXX may have to handle JFileChooser.FILE_CHANGED_PROPERTY too
                // XXX may have to enable/disable approval button acc. to some event?
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
                getFileChooser().setSelectedFiles(new File[] {new File(getFileName())});
            } finally {
                currentDirectoryChanging = false;
            }
        }
    }
    
    private final class CompleteAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            String name = getFileName();
            int slash = name.lastIndexOf(File.separatorChar);
            assert slash != -1;
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
                if (complete) {
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

}
