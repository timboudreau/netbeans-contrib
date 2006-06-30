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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.navigator;

import java.util.Collection;
import javax.swing.JComponent;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.ui.StructuralExplorer;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXNavigatorPanel implements NavigatorPanel {
    
    private Lookup.Result dataObjectSelection;
    private Lookup.Result sourceSelection;
    private final LookupListener selectionListener = new LookupListener() {
        public void resultChanged(LookupEvent ev) {
            Collection sources = sourceSelection.allInstances();
            
            if (sources.isEmpty()) {
                displayDO(dataObjectSelection.allInstances());
            } else {
                displaySource(sources);
            }
        }
    };
    private StructuralExplorer structure;
    
    /** Creates a new instance of LaTeXNavigatorPanel */
    public LaTeXNavigatorPanel() {
        structure = new StructuralExplorer();
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(LaTeXNavigatorPanel.class, "LBL_DocumentStructure");//NOI18N
    }
    
    public String getDisplayHint() {
        return NbBundle.getMessage(LaTeXNavigatorPanel.class, "SD_DocumentStructure");//NOI18N
    }
    
    public JComponent getComponent() {
        return structure;
    }
    
    public void panelActivated(Lookup context) {
        dataObjectSelection = context.lookup(new Lookup.Template(DataObject.class));
        dataObjectSelection.addLookupListener(selectionListener);
        sourceSelection = context.lookup(new Lookup.Template(LaTeXSource.class));
        sourceSelection.addLookupListener(selectionListener);
        selectionListener.resultChanged(null);
    }
    
    public void panelDeactivated() {
        dataObjectSelection.removeLookupListener(selectionListener);
        dataObjectSelection = null;
        sourceSelection.removeLookupListener(selectionListener);
        sourceSelection = null;
    }
    
    public Lookup getLookup() {
        return null;
    }
    
    private void displayDO(Collection/*<DataObject>*/ selectedFiles) {
        // Show list of targets for selected file:
        if (selectedFiles.size() == 1) {
            DataObject d = (DataObject) selectedFiles.iterator().next();
            
            structure.setCurrentSource(LaTeXSource.get(d.getPrimaryFile()));
            return ;
        }
        // Fallback:
        structure.setCurrentSource(null);
    }
    
    private void displaySource(Collection/*<LaTeXSource>*/ selectedSources) {
        LaTeXSource source = (LaTeXSource) selectedSources.iterator().next();
        
        structure.setCurrentSource(source);
    }
}
