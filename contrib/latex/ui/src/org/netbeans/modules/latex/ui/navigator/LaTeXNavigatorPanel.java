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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.latex.ui.navigator;

import java.awt.BorderLayout;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationInfo;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.structural.StructuralNodeFactory;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXNavigatorPanel implements NavigatorPanel {
    
    private JComponent panel;
    private final ExplorerManager manager = new ExplorerManager();
    
    /** Creates a new instance of LaTeXNavigatorPanel */
    public LaTeXNavigatorPanel() {
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(LaTeXNavigatorPanel.class, "LBL_DocumentStructure");//NOI18N
    }
    
    public String getDisplayHint() {
        return NbBundle.getMessage(LaTeXNavigatorPanel.class, "SD_DocumentStructure");//NOI18N
    }
    
    public JComponent getComponent() {
        if (panel == null) {
            final BeanTreeView view = new BeanTreeView();
            view.setRootVisible(true);
            view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            class Panel extends JPanel implements ExplorerManager.Provider, Lookup.Provider {
                // Make sure action context works correctly:
                private final Lookup lookup = ExplorerUtils.createLookup(manager, new ActionMap());
                {
                    setLayout(new BorderLayout());
                    add(view, BorderLayout.CENTER);
                }
                public ExplorerManager getExplorerManager() {
                    return manager;
                }
                public Lookup getLookup() {
                    return lookup;
                }
            }
            panel = new Panel();
        }
        return panel;
    }
    
    public void panelActivated(Lookup context) {
        LaTeXNavigatorFactory.getInstance().setLookup(context, new TaskImpl());
    }

    public void panelDeactivated() {
        LaTeXNavigatorFactory.getInstance().setLookup(Lookup.EMPTY, null);
    }
    
    public Lookup getLookup() {
        return null;
    }
    
    private final class TaskImpl implements CancellableTask<CompilationInfo> {
        public void cancel() {}

        public void run(CompilationInfo ci) throws Exception {
            LaTeXParserResult lpr = (LaTeXParserResult) ci.getParserResult();
            manager.setRootContext(StructuralNodeFactory.createNode(lpr.getStructuralRoot()));
        }
        
    }
    
}
