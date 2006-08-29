/*
 * FixedComboButton.java
 *
 * Created on August 29, 2006, 8:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package combobutton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Tim Boudreau
 */
public class FixedComboButton extends ComboButton {
    private boolean initialized;
    public FixedComboButton() {
        setActions (new Action[0]);
        initialized = true;
    }
    
    public void updateUI() {
        setUI (FixedComboButtonUI.createUI(this));
    }
    
    public void setActions (Action[] actions) {
        if (!Arrays.equals(getActions(), actions)) {
            Action[] old = getActions();
            super.setModel (new ImmutableComboBoxModel(actions));
            firePropertyChange ("actions", old, actions);
        }
    }
    
    public Action getPrimaryAction() {
        Action[] a = getActions();
        return a.length == 0 ? null : a[0];
    }
    
    public Action getAction (int index) {
        Action[] a = getActions();
        return a[index];
    }
    
    public void setModel (ComboBoxModel mdl) {
        if (initialized) { //allow calls from superclass constructor
            throw new UnsupportedOperationException();
        }
    }
    
    public int getSelectedIndex() {
        return 0;
    }
    
    public ComboBoxModel getModel() {
        ComboBoxModel smdl = super.getModel();
        if (smdl == null) {
            return new ImmutableComboBoxModel (new Action[0]);
        }
        return smdl;
    }
    
    public Action[] getActions() {
        return ((ImmutableComboBoxModel) getModel()).getActions().clone();
    }

    protected void fireActionEvent() {
        //do nothing
    }
    
    private boolean firingAction;
    void fireAction (Action a) {
        if (firingAction) return;
        firingAction = true;
        try {
            ActionListener[] al = getActionListeners();
            if (al.length > 0) {
                ActionEvent ae = new ActionEvent (a, ActionEvent.ACTION_PERFORMED,
                    (String) a.getValue(Action.ACTION_COMMAND_KEY));
                for (int i = 0; i < al.length; i++) {
                    al[i].actionPerformed(ae);
                }
            }
        } finally {
            firingAction = false;
        }
    }
    
    private final class ImmutableComboBoxModel implements ComboBoxModel {
        final Action[] actions;
        public ImmutableComboBoxModel (Action[] actions) {
            this.actions = actions.clone();
        }
        
        Action[] getActions() {
            return actions;
        }
        
        public Object getSelectedItem() {
            return actions.length == 0 ? null : actions[0];
        }

        public int getSize() {
            return actions.length;
        }
        
        public Action getAction (int index) {
            return (Action) getElementAt(index);
        }

        public Object getElementAt(int index) {
            return actions[index];
        }

        public void setSelectedItem(Object anItem) {
            ((FixedComboButtonUI) getUI()).itemSelected ((Action) anItem);
        }

        public void addListDataListener(ListDataListener l) {
            //do nothing
        }

        public void removeListDataListener(ListDataListener l) {
            //do nothing
        }
    }
}
