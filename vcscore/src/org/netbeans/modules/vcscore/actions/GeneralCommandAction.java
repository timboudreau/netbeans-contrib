/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.actions;

import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import java.io.File;
import org.openide.util.HelpCtx;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import org.openide.util.SharedClassObject;

/** Action sensitive to the node selection that does something useful.
 * All subclasses don't process the activated nodes by themselves, but rather
 * use the AbstractCommandAction for this purpose. 
 * 
 *
 * @author  Milos Kleint
 */
public class GeneralCommandAction extends NodeAction {

    /**
     * Name of a FileObject attribute. Needs to be set on primary file of a node(dataobject)
     * in order to trigger the GeneralCommandAction and it's suclasses.
     * The value of the attribute is the 
     */
    
    public static final String VCS_ACTION_ATTRIBUTE = "VcsActionAttributeCookie";
    
    private javax.swing.JMenuItem menuPresent;
    private javax.swing.JMenuItem popupPresent;
    private java.awt.Component toolBarPresent;
    
    private Set menuNamesSet;
    private Set toolBarNamesSet;
    private Set popupNamesSet;
    
    static final long serialVersionUID = 5771601379701397185L;    
    
    public static AbstractCommandAction abstractAction;
    
    protected GeneralCommandAction() {
        if (abstractAction == null) {
            abstractAction = (AbstractCommandAction)SystemAction.get(AbstractCommandAction.class);
            abstractAction.reinitialize();
        }
    }
    
    protected void performAction (Node[] nodes) {
        if (nodes == null || nodes.length == 0) return;
        AbstractCommandAction genAction = (AbstractCommandAction)SystemAction.get(AbstractCommandAction.class);
        HashMap suppMap = genAction.getSupporterMap();
        if (suppMap == null) return;
        Iterator it = suppMap.keySet().iterator();
        while (it.hasNext()) {
            CommandActionSupporter support = (CommandActionSupporter)it.next();
            Set files = (Set)suppMap.get(support);
            FileObject[] filesArr = new FileObject[files.size()];
            filesArr = (FileObject[])files.toArray(filesArr);
            support.performAction(this, filesArr);
        }
    }

    protected boolean enable (Node[] nodes) {
        toolBarNamesSet = new HashSet();
        menuNamesSet = new HashSet();
        popupNamesSet = new HashSet();
        
        if (nodes == null || nodes.length == 0) {
            resetDisplayNames();
            return false;
        }
        AbstractCommandAction genAction = (AbstractCommandAction)SystemAction.get(AbstractCommandAction.class);
        HashMap suppMap = genAction.getSupporterMap();
        if (suppMap == null) { 
            resetDisplayNames();
            return false;
        }
        Iterator it = suppMap.keySet().iterator();
        boolean enabled = true;
        while (it.hasNext() && enabled) {
            CommandActionSupporter support = (CommandActionSupporter)it.next();
            Set files = (Set)suppMap.get(support);
            FileObject[] filesArr = new FileObject[files.size()];
            filesArr = (FileObject[])files.toArray(filesArr);
            enabled = support.isEnabled(this, filesArr);
            if (enabled) {
                addDisplayName(support.getToolBarDisplayName(this));
            }
        }
        resetDisplayNames();
        return enabled;
    }
    
    
    private void resetDisplayNames() {
        String toolBarName = getName();
        if (toolBarPresent != null && toolBarPresent instanceof javax.swing.JComponent) {
             toolBarName = ((javax.swing.JComponent)toolBarPresent).getToolTipText();
             int index = toolBarName.lastIndexOf('[');
             if (index > 0) {
                 toolBarName = toolBarName.substring(0, index - 1);
             }
        }
        Iterator it = toolBarNamesSet.iterator();
        boolean atLeastOne = false;
        while (it.hasNext()) {
            String next = (String)it.next();
            if (!next.equals(getName())) {
                if (atLeastOne) {
                    toolBarName = toolBarName + "," + next;
                } else {
                    toolBarName = toolBarName + " [" + next;
                    atLeastOne = true;
                }
            }
        }
        if (atLeastOne) {
            toolBarName = toolBarName + "]";
        }
        if (toolBarPresent != null && toolBarPresent instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent)toolBarPresent).setToolTipText(toolBarName);
        }
    }
    
    private void addDisplayName(String name) {
        toolBarNamesSet.add(name);
//        getToolbarPresenter().setName(getName());
//        getMenuPresenter().setName(getName());
//        getPopupPresenter().setName(getName());
    }
    
    public String getName () {
        return NbBundle.getMessage(GeneralCommandAction.class, "LBL_Action");
    }
    

    protected String iconResource () {
        return null;
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (JavaCvsCommandAction.class);
    }

    public java.awt.Component getToolbarPresenter() {
        java.awt.Component retValue;
        
        retValue = super.getToolbarPresenter();
        toolBarPresent = retValue;
        return retValue;
    }    

    public javax.swing.JMenuItem getPopupPresenter() {
        javax.swing.JMenuItem retValue;
        
        retValue = super.getPopupPresenter();
        popupPresent = retValue;
        return retValue;
    }
    
    public javax.swing.JMenuItem getMenuPresenter() {
        javax.swing.JMenuItem retValue;
        
        retValue = super.getMenuPresenter();
        menuPresent = retValue;
        return retValue;
    }
   
    /**
     * doesn't listen to the change in activated nodes event. It registers with AbstractCommandAction
     *and that calss will take care of dispatching that the change occured.
     */

    protected void addNotify() {
        if (abstractAction == null) {
            abstractAction = (AbstractCommandAction)SystemAction.get(AbstractCommandAction.class);
        }
        abstractAction.addDependantAction(this);
    }

    /**
     * doesn't listen to the change in activated nodes event. It registers with AbstractCommandAction
     *and that class will take care of dispatching that the change occured.
     */
    
    protected void removeNotify() {
        if (abstractAction == null) {
            abstractAction = (AbstractCommandAction)SystemAction.get(AbstractCommandAction.class);
        }
        abstractAction.removeDependantAction(this);
    }

    /**
    public void readExternal(java.io.ObjectInput objectInput) throws java.io.IOException, java.lang.ClassNotFoundException {
        super.readExternal(objectInput);
        if (abstractAction == null) {
            System.out.println("recreating action - read external");
            abstractAction = (AbstractCommandAction)SystemAction.get(AbstractCommandAction.class);
            abstractAction.reinitialize();
        }
    }  
     */  
    
}
