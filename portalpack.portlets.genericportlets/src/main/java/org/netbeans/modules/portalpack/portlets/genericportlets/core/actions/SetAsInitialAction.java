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
package org.netbeans.modules.portalpack.portlets.genericportlets.core.actions;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletProjectUtils;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletSupportException;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.actions.util.PortletSupportImpl;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletModeType;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletApp;
import org.netbeans.modules.portalpack.portlets.genericportlets.ddapi.PortletType;

import org.openide.awt.JMenuPlus;
import org.openide.awt.Mnemonics;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.actions.Presenter;

/**
 * Abstract Action for setting the start (initial) page.
 *
 * This class is copy of visualweb.project.jsf.actions.SetStartPageAction .
 * @author Peter Zavadsky (refactored previous actions)
 * @author Mark Dey (originally action for jsp)
 * @author David Botterill (originally action for portlet)
 * @author Satyaranjan (Modified for portlet actions)
 */
public abstract class SetAsInitialAction extends AbstractAction
    implements Presenter.Menu, Presenter.Popup, ContextAwareAction {

    private static final int TYPE_NONE = 0;
    private static final int TYPE_PORTLET = 2;
    protected int type;
    private FileObject fo;
    protected DataObject dataObject;

    /** Creates a new instance of SetStartPageAction */
    public SetAsInitialAction() {
        init(TYPE_NONE, null); // Fake action -> The context aware is real one, drawback of the NB design?

    }

    public void init(int type, DataObject inDataObject) {
        this.type = type;
        this.dataObject = inDataObject;

        if (null != inDataObject) {
            this.fo = dataObject.getPrimaryFile();
        } else {
            this.fo = null;
        }
        String name;
        if (type == TYPE_PORTLET) {
            name = NbBundle.getMessage(SetAsInitialAction.class, "LBL_SetInitalPageAction_SETINITIALVIEWPAGE");
        } else {
            name = null;
        }
        putValue(Action.NAME, name);
    }

    public void actionPerformed(ActionEvent evt) {
        if (type == TYPE_PORTLET) {
            // Copy from previous SetInitialPageAction (David)
            Project project = FileOwnerQuery.getOwner(fo);
            if (project == null) {
                return;
            }
            if (!PortletProjectUtils.isPortletSupported(project)) {
                return;
            //if (portletSupport == null) return;
            }
           
            String actionCommand = evt.getActionCommand();
            String portlet = "";
            if(evt.getSource() instanceof ModeMenuItem) {
                
                portlet = ((ModeMenuItem)evt.getSource()).getPortlet();
                System.out.println("Portlet Name is :::::::::::::::::: " + portlet);
            }
            PortletSupportImpl portletSupport =
                new PortletSupportImpl(project, getViewPageParamName(),
                getEditPageParamName(), getHelpPageParamName());

            try {
                /**
                 * Fix for CR  6337056.  Need to get the root path to the JSP files to be able 
                 * to set the currently set initial page icon back to the default one.
                 * -David Botterill 10/14/2005
                 */
                /**
                 * We need the path to the JSP root directory so we can use it to set the icons.
                 */
                FileObject jspRootFO = PortletProjectUtils.getDocumentRoot(project);
                File jspRootFile = FileUtil.toFile(jspRootFO);
                String dataNodePath = null;
                try {
                    dataNodePath = jspRootFile.getCanonicalPath();
                } catch (IOException ioe) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ioe);
                }
                if (actionCommand.equals(NbBundle.getMessage(SetAsInitialAction.class, "MNU_VIEWMODE"))) {
                    /**
                     * Set the icon for the current one for this mode to the default icon.
                     */
                    String currentViewPage = portletSupport.getInitialPage(PortletModeType.VIEW, portlet);
                    if (null != currentViewPage) {
                        FileObject currentFO = FileUtil.toFileObject(new File(dataNodePath + File.separator + currentViewPage));
                        /**
                         * Fix for CR 6329425
                         * Make sure the page is found since the user may have deleted the page after they set the initial
                         * mode.
                         * -David Botterill 9/28/2005
                         */
                        if (null != currentFO) {
                            try {
                                DataObject currentDO = DataObject.find(currentFO);
                                //((DataNode) currentDO.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/jruby.png"); //NOI18N

                            } catch (DataObjectNotFoundException donfe) {
                                NbBundle.getMessage(SetAsInitialAction.class,
                                    "MSG_UnableToSetDefaultIcon", currentViewPage);
                            }
                        }

                    }
                    portletSupport.setInitialPage(PortletModeType.VIEW, portlet, fo);
                    /**
                     * Now set the right data node with the mode icon.
                     */
                    //((DataNode) dataObject.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/initialviewpage.png"); //NOI18N

                    //((DataNode) dataObject.getNodeDelegate()).setShortDescription(NbBundle.getMessage(SetAsInitialAction.class, "LBL_InitialViewShortDesc"));
                } else if (actionCommand.equals(NbBundle.getMessage(SetAsInitialAction.class, "MNU_EDITMODE"))) {
                    /**
                     * Set the icon for the current one for this mode to the default icon.
                     */
                    String currentEditPage = portletSupport.getInitialPage(PortletModeType.EDIT, portlet);
                    if (null != currentEditPage) {
                        FileObject currentFO = FileUtil.toFileObject(new File(dataNodePath + File.separator + currentEditPage));
                        /**
                         * Fix for CR 6329425
                         * Make sure the page is found since the user may have deleted the page after they set the initial
                         * mode.
                         * -David Botterill 9/28/2005
                         */
                        if (null != currentFO) {
                            try {
                                DataObject currentDO = DataObject.find(currentFO);
                            //    ((DataNode) currentDO.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/jruby.png"); //NOI18N

                            } catch (DataObjectNotFoundException donfe) {
                                NbBundle.getMessage(SetAsInitialAction.class,
                                    "MSG_UnableToSetDefaultIcon", currentEditPage);
                            }
                        }

                    }
                    portletSupport.setInitialPage(PortletModeType.EDIT, portlet, fo);
                    /**
                     * Now set the right data node with the mode icon.
                     */
                    //((DataNode) dataObject.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/initialeditpage.png");//NOI18N

                    //((DataNode) dataObject.getNodeDelegate()).setShortDescription(NbBundle.getMessage(SetAsInitialAction.class, "LBL_InitialEditShortDesc"));
                } else if (actionCommand.equals(NbBundle.getMessage(SetAsInitialAction.class, "MNU_HELPMODE"))) {
                    /**
                     * Set the icon for the current one for this mode to the default icon.
                     */
                    String currentHelpPage = portletSupport.getInitialPage(PortletModeType.HELP, portlet);
                    FileObject currentFO = FileUtil.toFileObject(new File(dataNodePath + File.separator + currentHelpPage));
                    /**
                     * Fix for CR 6329425
                     * Make sure the page is found since the user may have deleted the page after they set the initial
                     * mode.
                     * -David Botterill 9/28/2005
                     */
                    if (null != currentFO) {
                        try {
                            DataObject currentDO = DataObject.find(currentFO);
                         //   ((DataNode) currentDO.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/jruby.png"); //NOI18N

                        } catch (DataObjectNotFoundException donfe) {
                            NbBundle.getMessage(SetAsInitialAction.class,
                                "MSG_UnableToSetDefaultIcon", currentHelpPage);
                        }
                    }
                    portletSupport.setInitialPage(PortletModeType.HELP, portlet, fo);

                    /**
                     * Now set the right data node with the mode icon.
                     */
                    //((DataNode) dataObject.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/initialhelppage.png");//NOI18N

                    //((DataNode) dataObject.getNodeDelegate()).setShortDescription(NbBundle.getMessage(SetAsInitialAction.class, "LBL_InitialHelpShortDesc"));
                } else if (actionCommand.equals(NbBundle.getMessage(SetAsInitialAction.class, "MNU_NONEMODE"))) {
                    /**
                     * Unset the page as an initial page.
                     */
                    portletSupport.unsetInitialPage(portlet, fo);

                    /**
                     * Now set the icon to the default icon.
                     */
                    try {
                        DataObject currentDO = DataObject.find(fo);
                       // ((DataNode) currentDO.getNodeDelegate()).setIconBaseWithExtension("org/netbeans/modules/portalpack/servers/liferay/portlets/resources/jruby.png"); //NOI18N

                    } catch (DataObjectNotFoundException donfe) {
                        NbBundle.getMessage(SetAsInitialAction.class,
                            "MSG_UnableToSetDefaultIcon", fo.getNameExt());
                    }


                }
            } catch (PortletSupportException jpse) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, jpse);
            }
        }
    }

    public Action createContextAwareInstance(Lookup context) {
        DataObject dob = (DataObject) context.lookup(DataObject.class);

        if (dob == null) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,
                new IllegalStateException("SetStartPageAction: missing DataObject instance in the context, context=" + context)); // NOI18N

            return null;
        }


        FileObject fo = dob.getPrimaryFile();
        if (isInPortletProject(fo)) {
            //return new SetAsInitialAction(TYPE_PORTLET, dob);
            init(TYPE_PORTLET, dob);
            return this;//getSetAsInitialAction(TYPE_PORTLET, dob);

        }
        return null;
    }
    // Implementation of Presenter.Menu -----------------------------------

    public JMenuItem getMenuPresenter() {
        if (isInPortletProject(fo)) {
            JMenu mainItem = new JMenuPlus();
            String name = NbBundle.getMessage(SetAsInitialAction.class, "LBL_SetInitalPageAction_SETINITIALVIEWPAGE");
            Mnemonics.setLocalizedText(mainItem,
                name);
            mainItem.addMenuListener(new InitialItemListener(this));

            return mainItem;
        }
        return null;
    }

    // Implementation of Presenter.Popup ----------------------------------
    public JMenuItem getPopupPresenter() {
        if (isInPortletProject(fo)) {
            JMenu mainItem = new JMenuPlus();
            String name = NbBundle.getMessage(SetAsInitialAction.class, "LBL_SetInitalPageAction_SETINITIALVIEWPAGE");
            Mnemonics.setLocalizedText(mainItem,
                name);
            mainItem.addMenuListener(new InitialItemListener(this));

            return mainItem;
        }
        return null;
    }

    public abstract String getExt();

    private boolean isInPortletProject(FileObject fo) {
        //check for jsp extension
        if (fo == null) {
            return false;
        }
        if (fo.getExt().compareToIgnoreCase(getExt()) == 0) { // NOI18N
            //check if this is a portlet project

            Project thisProj = FileOwnerQuery.getOwner(fo);
            if (PortletProjectUtils.isPortletSupported(thisProj)) {
                return true;
            }
        }
        return false;
    }

    /** Listens to selection of the INITIAL menu item and expands it
     * into a submenu listing INITIAL modes.
     */
    private class InitialItemListener implements MenuListener {

        ActionListener actionListener;

        public InitialItemListener(ActionListener inListener) {
            this.actionListener = inListener;
        }

        public void menuCanceled(MenuEvent e) {
        }

        public void menuDeselected(MenuEvent e) {
            JMenu menu = (JMenu) e.getSource();
            menu.removeAll();
        }

        public void menuSelected(MenuEvent e) {

            /**
             * Now set the one that is currently selected.
             */
            Project project = FileOwnerQuery.getOwner(fo);
            if (project == null) {
                return;
            }
            PortletSupportImpl portletSupport =
                new PortletSupportImpl(project, getViewPageParamName(),
                getEditPageParamName(), getHelpPageParamName());
            if (null == portletSupport) {
                return;
            }
            List<String> typePortlets = new ArrayList();

            try {

                File portletFile = portletSupport.getPortletDD();
                PortletApp portletApp = NetbeansUtil.getPortletApp(portletFile);
                PortletType[] portlets = portletApp.getPortlet();

                for (PortletType portlet : portlets) {
                    if (isPortletOfType(portlet)) {
                        typePortlets.add(portlet.getPortletName());
                    }
                }

            } catch (PortletSupportException ex) {
            }

            JMenu menu = (JMenu) e.getSource();

            for (String p : typePortlets) {

                JMenu jMenu = new JMenu(p);
                jMenu.setName(p);

                ButtonGroup group = new ButtonGroup();

                ModeMenuItem rbViewItem = new ModeMenuItem(NbBundle.getMessage(SetAsInitialAction.class, "MNU_VIEWMODE"));
                rbViewItem.addActionListener(actionListener);
                group.add(rbViewItem);
                jMenu.add(rbViewItem);
                rbViewItem.setMnemonic(NbBundle.getMessage(SetAsInitialAction.class, "MNE_VIEWMODE").charAt(0));
                rbViewItem.setPortlet(p);
                
                
                ModeMenuItem rbEditItem = new ModeMenuItem(NbBundle.getMessage(SetAsInitialAction.class, "MNU_EDITMODE"));
                rbEditItem.addActionListener(actionListener);
                group.add(rbEditItem);
                jMenu.add(rbEditItem);
                rbEditItem.setMnemonic(NbBundle.getMessage(SetAsInitialAction.class, "MNE_EDITMODE").charAt(0));
                rbEditItem.setPortlet(p);
                
                ModeMenuItem rbHelpItem = new ModeMenuItem(NbBundle.getMessage(SetAsInitialAction.class, "MNU_HELPMODE"));
                rbHelpItem.addActionListener(actionListener);
                group.add(rbHelpItem);
                jMenu.add(rbHelpItem);
                rbHelpItem.setMnemonic(NbBundle.getMessage(SetAsInitialAction.class, "MNE_HELPMODE").charAt(0));
                rbHelpItem.setPortlet(p);


                ModeMenuItem rbNoneItem = new ModeMenuItem(NbBundle.getMessage(SetAsInitialAction.class, "MNU_NONEMODE"));
                rbNoneItem.addActionListener(actionListener);
                group.add(rbNoneItem);
                jMenu.add(rbNoneItem);
                rbNoneItem.setMnemonic(NbBundle.getMessage(SetAsInitialAction.class, "MNE_NONEMODE").charAt(0));
                rbNoneItem.setPortlet(p);
                
                menu.add(jMenu);

                try {
                    if (portletSupport.isInitialPage(PortletModeType.VIEW, p, fo)) {
                        rbViewItem.setSelected(true);
                    } else if (portletSupport.isInitialPage(PortletModeType.EDIT, p, fo)) {
                        rbEditItem.setSelected(true);
                    } else if (portletSupport.isInitialPage(PortletModeType.HELP, p, fo)) {
                        rbHelpItem.setSelected(true);
                    } else {
                        rbNoneItem.setSelected(true);
                    }
                } catch (PortletSupportException jpse) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, jpse);
                }


            }

        }
    }
    
    private class ModeMenuItem extends JRadioButtonMenuItem {
        
        String portlet;
        
        public ModeMenuItem(String text) {
            super(text);
        }
        public void setPortlet(String portlet) {
            this.portlet = portlet;
        }
        
        public String getPortlet() {
            return portlet;
        }
    }

    public abstract String getViewPageParamName();

    public abstract String getEditPageParamName();

    public abstract String getHelpPageParamName();

    public abstract boolean isPortletOfType(PortletType portletType);
    // public abstract SetAsInitialAction getSetAsInitialAction(int type,DataObject dob);
}
