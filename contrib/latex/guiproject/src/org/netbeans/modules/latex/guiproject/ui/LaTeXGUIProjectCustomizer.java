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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
