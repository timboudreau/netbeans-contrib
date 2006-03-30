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
            return new BuildPanel(project);
        }
        
        throw new IllegalArgumentException("Unsupported category: " + category.getName());
    }

    public void windowClosed(WindowEvent e) {
        ProjectSettings.getDefault(project).rollBack();
        current = null;
    }

}
