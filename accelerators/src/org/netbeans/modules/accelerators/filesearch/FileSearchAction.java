/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

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
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.filesearch;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.awt.Mnemonics;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
/**
 *
 * @author Andrei Badea
 */
public class FileSearchAction extends NodeAction {
    
    public FileSearchAction() {
        // XXX this should be in initialize()?
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    protected void performAction(Node[] activatedNodes) {
        Project project = findProject(activatedNodes);
        if (project == null) {
            return;
        }

        FileSearchResult result = new FileSearchResult();
        FileSearch search = new FileSearch(project, result);
        
        boolean selectInProjects = System.getProperty("netbeans.accelerators.select_in_projects") != null; // NOI18N
        Object[] buttons;
        
        JButton openBtn = new JButton();
        Mnemonics.setLocalizedText(openBtn, NbBundle.getMessage(FileSearchAction.class, "CTL_Open"));
        JButton closeBtn = new JButton();
        Mnemonics.setLocalizedText(closeBtn, NbBundle.getMessage(FileSearchAction.class, "CTL_Close"));
        JButton selectInPrjBtn = null;
        if (selectInProjects) {
            selectInPrjBtn = new JButton();
            Mnemonics.setLocalizedText(selectInPrjBtn, NbBundle.getMessage(FileSearchAction.class,"CTL_SelectInProjects"));
            buttons = new Object[] { openBtn, selectInPrjBtn, closeBtn };
        } else {
            buttons = new Object[] { openBtn, closeBtn };
        }
        
        String titlePattern = NbBundle.getMessage(FileSearchAction.class, "MSG_FileSearchDlgTitle");
        String title = MessageFormat.format(titlePattern, new Object[] { ProjectUtils.getInformation(project).getDisplayName() });
        
        FileSearchPanel panel = new FileSearchPanel(search, project.getProjectDirectory());
        DialogDescriptor d = new DialogDescriptor(panel, title, true, buttons, openBtn, DialogDescriptor.DEFAULT_ALIGN, null, null);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(d);
        
        if (selectInProjects) {
            final JButton button = selectInPrjBtn;
            ((JDialog)dialog).getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "selectInProjects");
            ((JDialog)dialog).getRootPane().getActionMap().put("selectInProjects", new AbstractAction() { // NOI18N
                public void actionPerformed(ActionEvent e) {
                    button.doClick();
                }
            });
        }
        
        dialog.setVisible(true);
        dialog.dispose();
        
        // just to be sure
        search.cancel();
        
        FileObject selected  = panel.getSelectedFile();
        if (selected == null) {
            return;
        }
        
        if (d.getValue() == openBtn) {
            openFileObject(selected);
        } else if (selectInProjects && d.getValue() == selectInPrjBtn) {
            selectFileObject(selected);
        }
    }
    
    protected boolean enable(Node[] activatedNodes) {
        return findProject(activatedNodes) != null;
    }

    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return NbBundle.getMessage(FileSearchAction.class, "CTL_FileSearchAction");
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    private static Project findProject(Node[] activatedNodes) {
        for (int i = 0; i < activatedNodes.length; i++) {
            // maybe it's a project node
            Project p = (Project)activatedNodes[i].getLookup().lookup(Project.class);
            if (p != null) {
                return p;
            }
            // maybe it's a file under a project
            DataObject dataObject = (DataObject)activatedNodes[i].getLookup().lookup(DataObject.class);
            if (dataObject == null) {
                continue;
            }
            p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            if (p != null) {
                return p;
            }
        }
        return null;
    }
    
    private static void openFileObject(final FileObject selected) {
        DataObject selectedDO = null;
        try {
            selectedDO = DataObject.find(selected);
        } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
            return;
        }
        
        Node selectedNode = selectedDO.getNodeDelegate();
        Action preferredAction = selectedNode.getPreferredAction();
        if (preferredAction != null) {
            performAction(preferredAction, selectedNode);
        } else {
            OpenCookie openCookie = (OpenCookie)selectedDO.getCookie(OpenCookie.class);
            if (openCookie != null) {
                openCookie.open();
            } else {
                EditCookie editCookie = (EditCookie)selectedDO.getCookie(EditCookie.class);
                if (editCookie != null) {
                    editCookie.edit();
                }
            }
        }
        // XXX notify that can't open? or try to select in the Projects view?
    }
    
    /**
     * Ugly hack which tries to select a FO in the Projects tab. 
     * Anyone reading this code, don't let it inspire you!
     */
    private static void selectFileObject(FileObject selected) {
        TopComponent projectsTC = null;
        for (Iterator i = TopComponent.getRegistry().getOpened().iterator(); i.hasNext();) {
            TopComponent component = (TopComponent)i.next();
            if ("Projects".equals(component.getName())) { // NOI18N
                projectsTC = component;
            }
        }
        if (projectsTC == null) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, "Can't find the Projects top component."); // NOI18N
            return;
        }
        
        Component[] children = projectsTC.getComponents();
        if (children.length == 0) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, "The Projects top component has no children."); // NOI18N
            return;
        }
        
        ExplorerManager explorer = ExplorerManager.find(children[0]);
        if (explorer == null) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, "The Projects top component has no explorer manager."); // NOI18N
            return;
        }
        
        Node root = explorer.getRootContext();
        if (root == null) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, "The Projects explorer manager has no root context."); // NOI18N
            return;
        }
        
        Project p = FileOwnerQuery.getOwner(selected);
        Node projectNode = findProjectNode(root, p);
        if (projectNode == null) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, "Can't get the project node."); // NOI18N
            return;
        }
        
        LogicalViewProvider lvp = (LogicalViewProvider)p.getLookup().lookup(LogicalViewProvider.class);
        Node found = lvp.findPath(projectNode, selected);
        
        if (found == null) {
            ErrorManager.getDefault().log(ErrorManager.ERROR, "Can't find the file in the project's LVP."); // NOI18N
            return;
        }
        
        try {
            explorer.setSelectedNodes(new Node[] { found });
        } catch (PropertyVetoException ex) {
            ErrorManager.getDefault().notify(ex);
            return;
        }
        
        projectsTC.requestActive();
    }
    
    private static Node findProjectNode(Node child, Project p) {
        Node[] children = child.getChildren().getNodes(false);
        for (int i = 0; i < children.length; i++) {
            Project foundP = (Project)children[i].getLookup().lookup(Project.class);
            if (foundP != null && foundP.equals(p)) {
                return children[i];
            }
        }
        return null;
    }

    private static void performAction(Action action, Node node) {
        Action a = takeAction(action, node);
        if (a != null) {
            if (a.isEnabled()) {
                a.actionPerformed(new ActionEvent(node, ActionEvent.ACTION_PERFORMED, "")); // NOI18N
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
    
    /**
     * Copied from TreeView.takeAction()
     */
    private static Action takeAction(Action action, Node node) {
        // bugfix #42843, use ContextAwareAction if possible
        if (action instanceof ContextAwareAction) {
            Lookup contextLookup = node.getLookup();
            Lookup.Result res = contextLookup.lookup(new Lookup.Template(Node.class));

            // #55826, don't added the node twice
            Iterator it = res.allInstances().iterator();

            // temporary workaround #55938
            boolean add = true;

            while (it.hasNext() && add) {
                add = !node.equals(it.next());
            }

            if (add) {
                contextLookup = new ProxyLookup(new Lookup[] { Lookups.singleton(node), node.getLookup() });
            }

            Action contextInstance = ((ContextAwareAction) action).createContextAwareInstance(contextLookup);
            assert contextInstance != action : "Cannot be same. ContextAwareAction:  " + action +
            ", ContextAwareInstance: " + contextInstance;
            action = contextInstance;
        }

        return action;
    }    
}
