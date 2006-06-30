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
package org.netbeans.modules.latex.guiproject.ui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.HelpCtx;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectCustomizer extends WindowAdapter implements CustomizerProvider, ProjectCustomizer.CategoryComponentProvider {
    
    private static final String CAT_BUILD = "build";
    
    private LaTeXGUIProject project;
    private List/*<StorableSettingsPresenter>*/ currentPresenters;
    private Dialog current;
    
    /** Creates a new instance of LaTeXGUIProjectCustomizer */
    public LaTeXGUIProjectCustomizer(LaTeXGUIProject project) {
        this.project = project;
    }
    
    public void showCustomizer() {
        if (current != null) {
            current.requestFocus();
            return ;
        }
        
        Category build = ProjectCustomizer.Category.create(CAT_BUILD, "Build&Show", null, null);
        
        currentPresenters = new ArrayList();
        
        current = ProjectCustomizer.createCustomizerDialog(new Category[] {build}, this, null, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProjectSettings settings = ProjectSettings.getDefault(project);
                
                current.setVisible(false);
                current.dispose();
                
                for (Iterator i = currentPresenters.iterator(); i.hasNext(); ) {
                    ((StorableSettingsPresenter) i.next()).store(settings);
                }
                
                settings.commit();
            }
        }, HelpCtx.DEFAULT_HELP);
        
        current.addWindowListener(this);
        
        current.setVisible(true);
    }

    public JComponent create(ProjectCustomizer.Category category) {
        JComponent comp = createImpl(category);
        
        if (comp instanceof StorableSettingsPresenter) {
            StorableSettingsPresenter presenter = (StorableSettingsPresenter) comp;
            
            presenter.load(ProjectSettings.getDefault(project));
            currentPresenters.add(presenter);
        }
        
        return comp;
    }
    
    public JComponent createImpl(ProjectCustomizer.Category category) {
        if (CAT_BUILD.equals(category.getName())) {
            return new BuildPanel(project, category);
        }
        
        throw new IllegalArgumentException("Unsupported category: " + category.getName());
    }

    public void windowClosed(WindowEvent e) {
        ProjectSettings.getDefault(project).rollBack();
        current = null;
    }

}
