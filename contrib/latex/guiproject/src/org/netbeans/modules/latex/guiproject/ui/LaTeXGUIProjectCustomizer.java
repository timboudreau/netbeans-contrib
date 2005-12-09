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
    private static final String CAT_LATEX = "LaTeX";
    private static final String CAT_BIBTEX = "BiBTeX";
    private static final String CAT_SHOW = "show";
    private static final String CAT_SHOWXDVI = "showXDVI";
    
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
        
        Category build = ProjectCustomizer.Category.create(CAT_BUILD, "Build", null, new Category[] {
            ProjectCustomizer.Category.create(CAT_LATEX, "LaTeX", null, null),
            ProjectCustomizer.Category.create(CAT_BIBTEX, "BiBTeX", null, null),
        });
        Category show = ProjectCustomizer.Category.create(CAT_SHOW, "Show", null, new Category[] {
            ProjectCustomizer.Category.create(CAT_SHOWXDVI, "XDVI", null, null),
        });
        
        currentPresenters = new ArrayList();
        
        current = ProjectCustomizer.createCustomizerDialog(new Category[] {build, show}, this, null, new ActionListener() {
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
            Collection buildTargets = new ArrayList();
            
            buildTargets.add("latex2dvi");
            buildTargets.add("latex2ps");
            buildTargets.add("latex2pdf");
            
            return new BuildPanel("Default Build Target:", buildTargets, "defaultBuildCommand");
        }
        
        if (CAT_LATEX.equals(category.getName())) {
            return new LaTeXProperties();
        }
        
        if (CAT_BIBTEX.equals(category.getName())) {
            return new BiBTeXProperties();
        }
        
        if (CAT_SHOW.equals(category.getName())) {
            Collection buildTargets = new ArrayList();
            
            buildTargets.add("xdvi");
            buildTargets.add("gv");
            
            return new BuildPanel("Default Show Target:", buildTargets, "defaultShowCommand");
        }
        
        if (CAT_SHOWXDVI.equals(category.getName())) {
            JLabel panelNotImplemented = new JLabel("Panel not implemented yet.");
            
            panelNotImplemented.setForeground(Color.RED);
            
            return panelNotImplemented;
        }
        
        throw new IllegalArgumentException("Unsupported category: " + category.getName());
    }

    public void windowClosed(WindowEvent e) {
        ProjectSettings.getDefault(project).rollBack();
        current = null;
    }

}
