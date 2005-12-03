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

package org.netbeans.modules.latex.ui.navigator;

import java.util.Collection;
import javax.swing.JComponent;
import org.netbeans.modules.latex.model.command.DebuggingSupport;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.ui.NodeNode;
import org.netbeans.modules.latex.ui.StructuralExplorer;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

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
        return "LaTeX Navigator";
    }
    
    public String getDisplayHint() {
        return "LaTeX Navigator";
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
