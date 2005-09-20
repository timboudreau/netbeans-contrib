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

package org.netbeans.modules.importcruncher;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author Timothy Boudreau
 */
class Prefs {
    Preferences p = Preferences.userNodeForPackage(Prefs.class);
    
    private static final String BREAKUP = "breakup"; //NOI18N
    private static final String NO_FQNS = "eliminateFqns"; //NOI18N
    private static final String SORT = "sort"; //NOI18N
    private static final String NO_WILDCARDS = "eliminateWildcards"; //NOI18N
    private static final String SHOW_DIALOG = "showDialog"; //NOI18N
    
    private static final String[] KEYS = new String[] {
       BREAKUP,
       NO_FQNS,
       SORT,
       NO_WILDCARDS,
       SHOW_DIALOG,
    };
    
    public Prefs() {
//       set (SHOW_DIALOG, true);
    }
    
    public boolean isShowDialog() {
        EventObject eo = EventQueue.getCurrentEvent();
        if (eo instanceof InputEvent) {
            //I know, I know, not good UI.  But how to add to the lovely
            //new preferences dialog?
            int mods = ((InputEvent) eo).getModifiersEx();
            if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) {
                return true;
            }
        }
        return get(SHOW_DIALOG);
    }
    
    public boolean isSort() {
        return get(SORT);
    }
    
    public boolean isBreakup() {
        return get(BREAKUP);
    }
    
    public boolean isEliminateWildcards() {
        return get(NO_WILDCARDS);
    }
    
    public boolean isEliminateFqns() {
        return get(NO_FQNS);
    }
    
    public boolean showDialog(boolean showDlgCheckbox) {
        final JPanel pnl = new JPanel();
        pnl.setLayout (new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder (BorderFactory.createEmptyBorder(12,12,0,12));

        final DialogDescriptor des = new DialogDescriptor (pnl, NbBundle.getMessage(
                Prefs.class, "TTL_Prefs"), true, //NOI18N
                DialogDescriptor.OK_CANCEL_OPTION, 
                DialogDescriptor.OK_OPTION, null);
        
        final JCheckBox[] boxen = new JCheckBox[KEYS.length];
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JCheckBox box = ((JCheckBox) ae.getSource());
                if (SORT.equals(box.getName())) {
                    boolean en = box.isSelected();
                    int breakupIdx = Arrays.asList(KEYS).indexOf(BREAKUP);
                    boxen[breakupIdx].setEnabled(en);
                    if (!en) {
                        boxen[breakupIdx].setSelected(false);
                    }
                }
                boolean hasSel = false;
                for (int i=0; i < boxen.length-1; i++) {
                    hasSel |= boxen[i].isSelected();
                }
                des.setValid(hasSel);
            }
        };
        
        boolean hasSel = false;
        for (int i=0; i < boxen.length; i++) {
            boxen[i] = new JCheckBox ();
            boxen[i].setName(KEYS[i]);
            boxen[i].addActionListener(al);
            boxen[i].setSelected (KEYS[i] == SHOW_DIALOG ? !get(KEYS[i]) : get(KEYS[i]));
            if (i != boxen.length-1) {
                hasSel |= boxen[i].isSelected();
            }
            if (BREAKUP.equals(KEYS[i])) {
                boxen[i].setEnabled(isSort());
            }
            boxen[i].setText(NbBundle.getMessage(Prefs.class, KEYS[i]));
            if (showDlgCheckbox || i != boxen.length - 1) {
                pnl.add (boxen[i]);
            }
            if (NO_WILDCARDS.equals(KEYS[i]) && showDlgCheckbox) {
                JPanel spacer = new JPanel();
                spacer.setPreferredSize (new Dimension(12,12));
                pnl.add (spacer);
            }
        }
        des.setValid(hasSel);
        boxen[boxen.length-1].setHorizontalTextPosition(SwingConstants.LEADING);
        boxen[boxen.length-1].setHorizontalAlignment(SwingConstants.TRAILING);
        
        if (DialogDisplayer.getDefault().notify(des) == DialogDescriptor.OK_OPTION) {
            for (int i=0; i < boxen.length; i++) {
                String nm = boxen[i].getName();
                set (nm, i == boxen.length - 1 ? 
                    !boxen[i].isSelected() : boxen[i].isSelected());
            }
            try {
                p.flush();
            } catch (BackingStoreException bse) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, 
                        bse);
            }
            return true;
        }
        return false;
    }
    
    private boolean get(String key) {
        return p.getBoolean(key, true);
    }
    
    private void set(String key, boolean val) {
        p.putBoolean(key, val);
    }
    
}
