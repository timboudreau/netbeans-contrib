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
import javax.swing.text.Document;
import org.netbeans.modules.latex.bibtex.loaders.BiBTexDataObject;
import org.netbeans.modules.latex.model.Utilities;
import org.netbeans.modules.latex.model.bibtex.BiBTeXModel;
import org.netbeans.modules.latex.model.bibtex.Entry;

import org.netbeans.modules.latex.model.bibtex.PublicationEntry;


import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jan Lahoda
 */
public class NewEntryAction extends AbstractAction implements PropertyChangeListener {
    
    /** Creates a new instance of EditEntryAction */
    public NewEntryAction() {
        putValue(NAME, "New Entry");
        
        //Temporary hack. (leaks etc...)
        TopComponent.getRegistry().addPropertyChangeListener(this);
        
        updateEnabledState();
    }
    
    public void actionPerformed(ActionEvent e) {
        BiBTeXModel model = getModelForActivatedNodes();
        
        PublicationEntry pEntry = new PublicationEntry();
        
        pEntry.setType("ARTICLE");
        
        BiBPanel panel = new BiBPanel();
            
        panel.setContent(pEntry);
            
        DialogDescriptor dd     = new DialogDescriptor(panel, "Edit Entry");
        Dialog           dialog = DialogDisplayer.getDefault().createDialog(dd);
            
        dialog.show();
            
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            panel.fillIntoEntry(pEntry);
            model.addEntry(pEntry);
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        updateEnabledState();
    }
    
    public void updateEnabledState() {
        setEnabled(getModelForActivatedNodes() != null);
    }
    
    private BiBTeXModel getModelForActivatedNodes() {
        Node[] activatedNodes = TopComponent.getRegistry().getActivatedNodes();
        
        if (activatedNodes.length != 1) {
            return null;
        }
        
        Node active = activatedNodes[0];
        
        BiBTexDataObject file = (BiBTexDataObject) active.getLookup().lookup(BiBTexDataObject.class); //see propertyChange
        
        if (file == null) {
            Entry e = (Entry) active.getLookup().lookup(Entry.class);
            
            if (e == null)
                return null;
            
            BiBTeXModel model = e.getModel();
            
            assert model != null : "The model of each visible node is supposed to be != null!";
            
            return model;
        }
        
        return BiBTeXModel.getModel(file.getPrimaryFile());
    }
}
