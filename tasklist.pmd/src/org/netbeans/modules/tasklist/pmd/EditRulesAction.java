/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.pmd;

import java.awt.Component;
import java.awt.Dialog;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Actions;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.CallableSystemAction;

import org.openide.explorer.propertysheet.*;
import org.netbeans.api.tasklist.*;

import pmd.config.PMDOptionsSettings;
import pmd.config.ui.RuleEditor;

/**
 * Edit the set of PMD rules used by the rule violation provider.
 * <p>
 *
 * @author Tor Norbye
 */

public class EditRulesAction extends NodeAction {

    protected boolean enable(Node[] node) {
        if ((node == null) || (node.length != 1)) {
            return false;
        }
        Suggestion s = (Suggestion)node[0].getCookie(Suggestion.class);
        if (s == null) {
            return false;
        }
        return true;
    }

    protected void performAction(Node[] node) {
        PMDOptionsSettings settings = PMDOptionsSettings.getDefault();
        String rules = settings.getRules();
        RuleEditor editor = new RuleEditor();
        editor.setValue(rules);
        Component customizer = editor.getCustomEditor();

        DialogDescriptor d = new DialogDescriptor(customizer,
                    NbBundle.getMessage(EditRulesAction.class,
                    "TITLE_editRules")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();
        if (d.getValue() == NotifyDescriptor.OK_OPTION) {
            Object value = editor.getValue();
            settings.setRules(value.toString());
            Suggestion s = (Suggestion)node[0].getCookie(Suggestion.class);
            if (s != null) {
                SuggestionProvider provider = s.getProvider();
                if ((provider != null) && 
                    (provider instanceof ViolationProvider)) {
                    ((ViolationProvider)provider).rescan();
                }
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(EditRulesAction.class,
                                   "EditRules"); // NOI18N
    }

    /*
    protected String iconResource() {
        return "org/netbeans/modules/tasklist/pmd/editRules.gif"; // NOI18N
    }
    */
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
    
}
