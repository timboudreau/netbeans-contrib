/*
 * The contents of this file are subject to the terms of the Common
 * Development
The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 */
package org.netbeans.modules.edm.editor.Wizard;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.ui.view.wizards.EDMCollaborationWizard;
import org.netbeans.modules.sql.framework.ui.graph.IGraphView;


public final class MashupWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    private SimpleTargetChooserPanel collaborationNamePanel;
    private EDMCollaborationWizard owner;
    private String title;
    private IGraphView view;
    
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            List<WizardDescriptor.Panel> myPanels = createPanels();
            
            WizardDescriptor.Panel[] pnlArray = new WizardDescriptor.Panel[myPanels.size()];
            panels = (WizardDescriptor.Panel[]) myPanels.toArray(pnlArray);
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }
    
    public Set instantiate() throws IOException {
        FileObject dir = Templates.getTargetFolder(wizard);
        if(dir != null) {
            DataFolder df = DataFolder.findFolder( dir );
            FileObject template = Templates.getTemplate(wizard);
            
            DataObject dTemplate = DataObject.find(template);
            DataObject dobj = dTemplate.createFromTemplate(df, Templates.getTargetName(wizard));
            if(dobj instanceof  MashupDataObject) {
                final MashupDataObject mashupDataObj = (MashupDataObject) dobj;
                Runnable run = new Runnable() {
                    public void run() {
                        mashupDataObj.initialize(wizard);
                        if (mashupDataObj.getNodeDelegate() != null) {
                            OpenCookie openCookie = (OpenCookie) mashupDataObj.getNodeDelegate().
                                    getCookie(OpenCookie.class);
                            openCookie.open();
                        }
                    }
                };
                
                SwingUtilities.invokeLater(run);
            }
            
            return Collections.singleton(dobj.getPrimaryFile());
        }
        return new HashSet();
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
    
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }
    
    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }
    
    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }
    
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
    
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        
        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }
        
        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
    
    protected List<WizardDescriptor.Panel> createPanels() {
        List<WizardDescriptor.Panel> panels = new ArrayList<WizardDescriptor.Panel>(2);
        if(wizard != null) {
            Project project = Templates.getProject(wizard);
            if(project != null) {
                Sources sources = ProjectUtils.getSources(project);
                SourceGroup[] groups = sources.getSourceGroups(
                        Sources.TYPE_GENERIC
                        );
                
                if ((groups == null) || (groups.length < 1)) {
                    groups = sources.getSourceGroups(Sources.TYPE_GENERIC);
                }
                
                collaborationNamePanel = new SimpleTargetChooserPanel(project, groups, null, false);
            }
            
            if(collaborationNamePanel != null) {
                panels.add(collaborationNamePanel);
            }
        
            panels.add(new ChooseTablesWizardPanel());
            panels.add(new ETLCollaborationWizardJoinFinishPanel(owner,title,view));
            panels.add(new SelectTargetTableWizardPanel());
            
            
         
        }
        return Collections.unmodifiableList(panels);
    }
}
