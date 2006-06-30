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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2005.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.awt.Dialog;
import java.awt.Toolkit;
import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Jan Lahoda
 */
public class EditEntryAction extends NodeAction {

    /** Creates a new instance of EditEntryAction */
    public EditEntryAction() {
    }
    
    protected void performAction(Node[] activatedNodes) {
        Entry entry = (Entry) activatedNodes[0].getLookup().lookup(Entry.class); //see propertyChange
        
        if (entry instanceof PublicationEntry) {
            PublicationEntry pEntry = (PublicationEntry) entry;
            BiBPanel panel = new BiBPanel();
            
            panel.setContent(pEntry);
            
            DialogDescriptor dd     = new DialogDescriptor(panel, "Edit Entry");
            Dialog           dialog = DialogDisplayer.getDefault().createDialog(dd);
            
            dialog.show();
            
            if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                panel.fillIntoEntry(pEntry);
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes.length != 1)
            return false;
        
        Node active = activatedNodes[0];
        
        return active.getLookup().lookup(Entry.class) != null;
    }

    public String getName() {
        return "Edit Entry";
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
}
