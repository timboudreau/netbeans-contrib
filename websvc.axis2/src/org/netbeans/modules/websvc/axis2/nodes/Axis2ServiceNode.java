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
package org.netbeans.modules.websvc.axis2.nodes;

import java.io.IOException;
import javax.swing.Action;
import org.netbeans.modules.websvc.axis2.actions.DeployAction;
import org.netbeans.modules.websvc.axis2.actions.EditWsdlAction;
import org.netbeans.modules.websvc.axis2.actions.GenerateWsdlAction;
import org.netbeans.modules.websvc.axis2.actions.ServiceConfigurationAction;
import org.netbeans.modules.websvc.axis2.config.model.Service;
import org.openide.actions.DeleteAction;
import org.openide.actions.OpenAction;
import org.openide.actions.PropertiesAction;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class Axis2ServiceNode extends AbstractNode implements OpenCookie {
    Service service;
    FileObject srcRoot;
    
    public Axis2ServiceNode(Service service, FileObject srcRoot) {
        this(service, srcRoot, new InstanceContent());
    }
    
    private Axis2ServiceNode(Service service, FileObject srcRoot, InstanceContent content) {
        super(Children.LEAF,new AbstractLookup(content));
        this.service=service;
        this.srcRoot = srcRoot;
        content.add(service);
        content.add(srcRoot);
        content.add(this);
    }
    
    public String getName() {
        return service.getNameAttr();
    }
    
    public String getDisplayName() {
        return service.getNameAttr();
    }
    
    @Override
    public String getShortDescription() {
        return service.getServiceClass();
    }
    
    public void open() {
        FileObject fo = srcRoot.getFileObject(service.getServiceClass().replace('.', '/')+".java"); //NOI18N
        try {
            DataObject dObj = DataObject.find(fo);
            if (dObj!=null) {
                EditCookie ec = dObj.getCookie(EditCookie.class);
                if (ec != null) {
                    ec.edit();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    void changeIcon() {
        fireIconChange();
    }
    
    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenAction.class);
    }
    
    // Create the popup menu:
    @Override
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(OpenAction.class),
            SystemAction.get(DeployAction.class),
            null,
            SystemAction.get(ServiceConfigurationAction.class),
            SystemAction.get(GenerateWsdlAction.class),
            SystemAction.get(EditWsdlAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            null,
            SystemAction.get(PropertiesAction.class),
        };
    }
    
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    // Handle deleting:
    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws java.io.IOException {
    }
    
    public void nameChanged(String oldName, String newName) {
        fireNameChange(oldName, newName);
        fireDisplayNameChange(oldName, newName);
    }
    
}
