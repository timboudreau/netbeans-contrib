/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class EditEntryAction extends AbstractAction implements PropertyChangeListener {
    
    /** Creates a new instance of EditEntryAction */
    public EditEntryAction() {
        putValue(NAME, "Edit Entry");
        
        //Temporary hack. (leaks etc...)
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        Entry entry = (Entry) TopComponent.getRegistry().getActivatedNodes()[0].getCookie(Entry.class); //see propertyChange
        
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
    
    public void propertyChange(PropertyChangeEvent evt) {
        Node[] activatedNodes = TopComponent.getRegistry().getActivatedNodes();
        
        if (activatedNodes.length != 1) {
            setEnabled(false);
            return ;
        }
        
        Node active = activatedNodes[0];
        
        setEnabled(active.getCookie(Entry.class) != null);
    }
    
}
