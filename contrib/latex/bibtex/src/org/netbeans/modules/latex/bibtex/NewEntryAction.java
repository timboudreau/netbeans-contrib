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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
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
