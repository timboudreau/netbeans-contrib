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
        //XXX should only have close button
        
        Dialog d = DialogDisplayer.getDefault().createDialog(dlg);
        d.setModal(true);
        d.setVisible(true);
        
        Project[] after = cust.getDependentProjects();
        project.setPackagedProjects(after);
        
    }
    
}
