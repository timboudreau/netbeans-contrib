/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * PackagerCustomizerProvider.java
 *
 * Created on May 26, 2004, 3:25 AM
 */

package org.netbeans.modules.packager;

import java.awt.Dialog;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.modules.packager.ui.PackagerCustomizer;
import org.netbeans.spi.project.ant.AntArtifactProvider;
import org.netbeans.spi.project.ui.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class PackagerCustomizerProvider implements CustomizerProvider {
    private PackagerProject project;
    /** Creates a new instance of PackagerCustomizerProvider */
    public PackagerCustomizerProvider(PackagerProject project) {
        this.project = project;
    }
    
    public void showCustomizer() {
        PackagerCustomizer cust = new PackagerCustomizer(project);
       
        DialogDescriptor dlg = new DialogDescriptor (
            cust, 
            NbBundle.getMessage(PackagerCustomizerProvider.class, "LBL_CustomizeProject")); //NOI18N
        
        Dialog d = DialogDisplayer.getDefault().createDialog(dlg);
        d.setModal(true);
        d.setVisible(true);
        
        Set s = project.getSubprojectProvider().getSubProjects();
        Project[] curr = new Project[s.size()];
        curr = (Project[]) s.toArray(curr);
        
        Project[] after = cust.getChildProjects();
        
        Set removed = new HashSet (Arrays.asList(curr));
        removed.removeAll (Arrays.asList(after));
        
        Set added = new HashSet (Arrays.asList(after));
        added.removeAll(Arrays.asList(curr));
        /*
        if (!added.isEmpty()) {
            for (Iterator i=added.iterator(); i.hasNext();) {
                Project proj = (Project) i.next();
                AntArtifactProvider prov = (AntArtifactProvider) 
                    proj.getLookup().lookup(AntArtifactProvider.class);
                if (prov != null) {
                    AntArtifact[] artifacts = prov.getBuildArtifacts();
                    for (int j=0; j < artifacts.length; j++) {
                        project.getReferenceHelper().addReference(artifacts[j]);
                    }
                }
            //XXX programmatical open the project somehow
            }
        }
        
        if (!removed.isEmpty()) {
            for (Iterator i=removed.iterator(); i.hasNext();) {
                Project proj = (Project) i.next();
                AntArtifactProvider prov = (AntArtifactProvider) 
                    proj.getLookup().lookup(AntArtifactProvider.class);
                if (prov != null) {
                    AntArtifact[] artifacts = prov.getBuildArtifacts();
                    for (int j=0; j < artifacts.length; j++) {
                        project.getReferenceHelper().removeReference(artifacts[j].getArtifactFile().getPath());
                    }
                }
            }
        }
        
        if (!removed.isEmpty() || !added.isEmpty()) {
            try {
                ProjectManager.getDefault().saveProject (project);
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
        }
         */
        
    }
    
}
