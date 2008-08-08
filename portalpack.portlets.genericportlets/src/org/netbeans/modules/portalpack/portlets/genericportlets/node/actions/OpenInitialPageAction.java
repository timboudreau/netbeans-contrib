/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.portlets.genericportlets.node.actions;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.CoreUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.frameworks.util.PortletProjectUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.InitialPageNode;
import org.netbeans.modules.portalpack.portlets.genericportlets.node.PortletNode;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author satyaranjan
 */
public class OpenInitialPageAction extends NodeAction {

    private static Logger logger = Logger.getLogger(CoreUtil.CORE_LOGGER);
    @Override
    protected void performAction(Node[] activatedNodes) {
        if ((activatedNodes == null) || (activatedNodes.length != 1)) {
            return;
        }
        
        final InitialPageNode node = activatedNodes[0].getLookup().lookup(InitialPageNode.class);
        
        if (node != null) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                   
                    String filePath = node.getName();
                   
                    if (filePath != null && filePath.trim().length() != 0) {
                        //get parent node and project
                        Node parent = node.getParentNode();
                        
                        PortletNode portletNode = parent.getLookup().lookup(PortletNode.class);
                        if(portletNode == null)
                            return;
                        
                        DataObject dobj = portletNode.getDataObject();
                        if(dobj == null)
                            return;
                        
                        FileObject portletXMLObj = dobj.getPrimaryFile();
                        if(portletXMLObj == null)
                            return;
                        
                        Project project = NetbeansUtil.getProject(portletXMLObj);
                        if(project == null)
                            return;
                        
                        FileObject docRoot = PortletProjectUtils.getDocumentRoot(project);
                        if(docRoot == null)
                            return;
                        
                        File docRootFile = FileUtil.toFile(docRoot);
                        
                        FileObject fob = FileUtil.toFileObject(new File(docRootFile,filePath));
                        if (fob != null) {  
                            
                            try {
                                
                                DataObject dob = DataObject.find(fob);
                                OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
                                if (oc != null) {
                                    oc.open();
                                }
                            } catch (DataObjectNotFoundException ex) {
                                //Exceptions.printStackTrace(ex);
                                logger.info(ex.getMessage());                                
                            }
                        }
                    }
                }
            });


        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(OpenInitialPageAction.class,
            "OPEN_INITIAL_PAGE_ACTION_NAME");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
}
