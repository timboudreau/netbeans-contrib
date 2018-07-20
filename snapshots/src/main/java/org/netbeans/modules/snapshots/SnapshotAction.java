/*
 * This is my template
 *
 * SnapshotAction.java
 *
 * Created on April 11, 2006, 6:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.snapshots;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Timothy Boudreau
 */
public class SnapshotAction extends AbstractAction implements Presenter.Toolbar, Presenter.Menu {
    
    /** Creates a new instance of SnapshotAction */
    public SnapshotAction() {
        putValue (NAME, "snapshots");
    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException();
    }

    private JPopupMenu popup = null;
    private JButton button = null;
    public Component getToolbarPresenter() {
        refresh();
        return button;
    }

    private JMenu menu = null;
    public JMenuItem getMenuPresenter() {
        refresh();
        return menu;
    }

    private boolean inRefresh = false;
    private void refresh() {
        if (running) {
            System.err.println("Refresh when running - give up");
            return;
        }
        inRefresh = true;
        try {
            if (menu == null) {
                menu = new JMenu();
                menu.setText (NbBundle.getMessage(SnapshotAction.class,
                        "LBL_SnapshotsToolbar"));
            } else {
                menu.removeAll();
            }
            if (popup == null) {
                popup = new JPopupMenu();
            } else {
                popup.removeAll();
            }
            if (button == null) {
                button = new JButton ("Snapshot");
                button.addActionListener (al);
            }

            String[] names = Snapshots.getNames();
            boolean wasZero = names.length == 0;
            if (wasZero) {
                names = new String[] { "" };
            } else {
                for (int i=0; i < names.length; i++) {
                    JMenuItem item = new JMenuItem (names[i]);
                    item.addActionListener(al);
                    menu.add (item);
                    item = new JMenuItem (names[i]);
                    item.addActionListener(al);
                    popup.add (item);
                }
                popup.add (new JSeparator());
                menu.add (new JSeparator());
            }
            JMenuItem createItem = new JMenuItem (al.toString());
            createItem.putClientProperty (CREATE, al.toString());
            createItem.addActionListener(al);
            menu.add (createItem);

            createItem = new JMenuItem (al.toString());
            createItem.putClientProperty (CREATE, al.toString());
            createItem.addActionListener(al);
            popup.add (createItem);

        } finally {
            inRefresh = false;
        }
    }

    private static final String CREATE = "CREATE";

    public boolean running = false;

    private AL al = new AL();
    private class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.err.println("ap...");
            if (running) {
                System.err.println("Running - give up");
                return;
            }
            if (inRefresh) {
                return;
            }
            String name = null;
            if (e.getSource() instanceof JComboBox) {
                JComboBox box = (JComboBox) e.getSource();
                System.err.println("Selected item is " + box.getSelectedItem() +
                        " - " + box.getSelectedItem().getClass());
                if (box.getSelectedItem() == al) {
                    System.err.println("Calling create");
                    create();
                    return;
                } else {
                    name = ((JComboBox) e.getSource()).getSelectedItem().toString();
                }
            } else if (e.getSource() instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) e.getSource();
                if (item.getClientProperty(CREATE) != null) {
                    System.err.println("Calling create for menu");
                    create();
                    return;
                }
                name = item.getText();
            } else if (e.getSource() instanceof JButton) {
                popup.show(button, 0, button.getHeight());
                return;
            } else {
                throw new IllegalArgumentException (e.getSource().toString());
            }
            if ("".equals(name)) {
                System.err.println("Empty name - give up");
                return;
            }
            System.err.println("Restore snapshot " + name);
            running = true;
            try {
                Snapshots.restoreSnapshot(name);
            } finally {
                running = false;
            }
        }

        public String toString() {
            return NbBundle.getMessage (SnapshotAction.class, "LBL_NewSnapshot");
        }
    }

    private void create() {
        running = true;
        try {
            NotifyDescriptor.InputLine line = new NotifyDescriptor.InputLine (
                    NbBundle.getMessage(SnapshotAction.class, "LBL_TakeSnapshot"),
                    NbBundle.getMessage(SnapshotAction.class, "TTL_TakeSnapshot")
                    );
            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(line))) {
                String s = line.getInputText();
                if ("".equals(s.trim()) || s.indexOf('/') >= 0 || s.indexOf(".") >= 0) {
                    NotifyDescriptor.Message msg = new NotifyDescriptor.Message (
                            NbBundle.getMessage(SnapshotAction.class, "MSG_BadName"));
                    DialogDisplayer.getDefault().notify(msg);
                    return;
                }
                if (Snapshots.isValid(s)) {
                    NotifyDescriptor msg = new NotifyDescriptor (
                            NbBundle.getMessage(SnapshotAction.class, "MSG_SnapshotExists", s),
                            NbBundle.getMessage(SnapshotAction.class, "TTL_SnapshotExists"),
                            NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.WARNING_MESSAGE,
                            new Object[] { NotifyDescriptor.OK_OPTION, NotifyDescriptor.CANCEL_OPTION},
                            NotifyDescriptor.OK_OPTION);
                    if (!NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(msg))) {
                        return;
                    }
                }
                Snapshots.takeSnapshot(s);
            }
        } finally {
            running = false;
        }
        refresh();
    }

    private class R extends DefaultListCellRenderer {
        private Border clear = new EmptyBorder (0, 0, 1, 0);
        private Border line = new MatteBorder (0, 0, 1, 0, UIManager.getColor("textText"));
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JComponent result = (JComponent)
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            result.setBorder(value == al ? line : clear);
            return result;
        }
    }
}
