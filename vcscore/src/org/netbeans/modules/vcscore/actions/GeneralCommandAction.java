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

import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Enumeration;
import java.lang.ref.WeakReference;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;
import org.openide.util.SharedClassObject;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;

import org.netbeans.modules.vcscore.grouping.GroupCookie;

/** 
 * Action sensitive to nodes, that delegates the enable/perform processing
 * to CommandActionSupporter instances.
 * First all te fileobject that belong to the activated nodes are extracted.
 * Then the CommandActionSupporter instances are found (these are attributes fo fileobjects)
 * in case all the nodes have one supporter, the nodes are grouped by supporters
 * and the supporters are asked about enabled() state.
 * All supporters need to allow this action to be enabled.
 * When performing the action the again the performing code is delegated to the supporters.
 * Each supporter is given the fileobjects that belong to him.
 *
 * @author  Milos Kleint
 */
public class GeneralCommandAction extends NodeAction {

    /**
     * Name of a FileObject attribute. Needs to be set on primary file of a node(dataobject)
     * in order to trigger the GeneralCommandAction and it's suclasses.
     * The value of the attribute is the 
     */
    
    public static final String VCS_ACTION_ATTRIBUTE = "VcsActionAttributeCookie"; //NOI18N
   
    /** 
     * a property accessible via the getValue() method.
     * for VcsGroup nodes it holds the description of the group. Otherwise null.
     * Can be used within the CommandActionSupporters in the modules.
     */
    public static final String GROUP_DESCRIPTION_PROP = "GROUP_DESCRIPTION"; //NOI18N
    /** 
     * a property accessible via the getValue() method.
     * for VcsGroup nodes it holds the display name of the group. Otherwise null.
     * Can be used within the CommandActionSupporters in the modules. eg. for commit message.
     */
    public static final String GROUP_NAME_PROP = "GROUP_NAME"; //NOI18N
    
    
    private transient java.awt.Component toolBarPresent;
    
    private Set toolBarNamesSet;
    
    private boolean wasReset;
    
    static final long serialVersionUID = 5771601379701397185L;    
    
    
    private static transient HashMap suppMap;
    
    private static transient WeakReference nodesRef;

    
    protected GeneralCommandAction() {
    }
    
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    /**
     * This method doesn't extract the fileobjects from the activated nodes itself, but rather
     * consults delegates to  a list of supporters.
     */
    protected void performAction (Node[] nodes) {
        if (nodes == null || nodes.length == 0) return;
//        HashMap suppMap;
        suppMap = this.getSupporterMap(nodes);
        if (suppMap == null) return;
        Iterator it = suppMap.keySet().iterator();
        while (it.hasNext()) {
            CommandActionSupporter support = (CommandActionSupporter)it.next();
            Set files = (Set)suppMap.get(support);
//            System.out.println("executing.. files size =" + files.size());
            FileObject[] filesArr = new FileObject[files.size()];
            filesArr = (FileObject[])files.toArray(filesArr);
            support.performAction(this, filesArr);
        }
    }

    /**
     * 
     * Each supporter are checked if if they enable the action.
     * All supporters need to come to a concensus in order for the action to be enabled.
     * *experimental* annotates the toolbar tooltip according to the supporter's requests.
     */
    
    protected boolean enable (Node[] nodes) {
        toolBarNamesSet = new HashSet();
        
        if (nodes == null || nodes.length == 0) {
            if (!wasReset) {
                resetDisplayNames();
            }
            wasReset = true;
            return false;
        }
//        HashMap suppMap = null;
        suppMap = this.getSupporterMap(nodes);
        
        if (suppMap == null) { 
            if (!wasReset) {
                resetDisplayNames();
            }
            wasReset = true;
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
        wasReset = false;
        resetDisplayNames();
        return enabled;
    }
    
    
    private void resetDisplayNames() {
        String toolBarName = "";
        Iterator it = toolBarNamesSet.iterator();
        boolean atLeastOne = false;
        while (it.hasNext()) {
            String next = (String)it.next();
            if (!next.equals(getName())) {
                if (atLeastOne) {
                    toolBarName = toolBarName + "," + next; //NOI18N
                } else {
                    toolBarName = next; //NOI18N
                    atLeastOne = true;
                }
            }
        }
        if (!atLeastOne) {
            toolBarName = getName();
        }
/*        if (toolBarNamesSet.size() < this.getSupporterMap().keySet().size()) {
            toolBarName = getName() + " [" + toolBarName + "]";
        }
        if (toolBarPresent != null && toolBarPresent instanceof javax.swing.JComponent) {
             toolBarName = ((javax.swing.JComponent)toolBarPresent).getToolTipText();
             // TODO: in org.openide.awt.Actions.connect(): if b.updateState() is removed,
             //       then toolBarName == null !!
             if (toolBarName == null) {
                 toolBarName = "" + getName();
             }
             int index = toolBarName.lastIndexOf('[');
             if (index > 0) {
                 toolBarName = toolBarName.substring(0, index - 1);
             }
        }
 */
        
        if (toolBarPresent != null && toolBarPresent instanceof javax.swing.JComponent) {
            String oldBar = ((javax.swing.JComponent)toolBarPresent).getToolTipText();
            if (oldBar == null || (!oldBar.equals(toolBarName))) {
                ((javax.swing.JComponent)toolBarPresent).setToolTipText(toolBarName);
            }
        }
    }
    
    private void addDisplayName(String name) {
        toolBarNamesSet.add(name);
//        getToolbarPresenter().setName(getName());
//        getMenuPresenter().setName(getName());
//        getPopupPresenter().setName(getName());
    }
    
    public String getName () {
        return NbBundle.getMessage(GeneralCommandAction.class, "LBL_Action"); //NOI18N
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

    
    /**
     * returns a map with CommandActionSupporters as keys.
     * values are Sets of activated FileObjects.
     * returns null, if the action wasn't enabled. (thus none should be).
     */
    public HashMap getSupporterMap(Node[] activatedNodes) {
        if (nodesRef == null) {
            createSupporterMap(activatedNodes);
            nodesRef = new WeakReference(activatedNodes);
        }
        Object array = nodesRef.get();
        if (array == null) {
            createSupporterMap(activatedNodes);
            nodesRef = new WeakReference(activatedNodes);
        } else {
            Node[] nodes = (Node[])array;
            boolean hasAnyGroups = false;
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] != null) {
                    GroupCookie gc = (GroupCookie) nodes[i].getCookie(GroupCookie.class);
                    if (gc != null) {
//                    System.out.println("has groupos..");
                        hasAnyGroups = true;
                        break;
                    }
                }
            }
            boolean equal = org.openide.util.Utilities.compareObjects(array, activatedNodes);
            if (!equal  || hasAnyGroups ) {
//                System.out.println("recreating..");
                createSupporterMap(activatedNodes);
                nodesRef = new WeakReference(activatedNodes);
            }
        }
        
        return suppMap;
    }
    
    /**
     * returns true when all activated nodes have a vcs supporter..
     *
     */
    
    private boolean createSupporterMap(Node[] nodes) {
        if (getValue(GROUP_NAME_PROP) != null) {
            putValue(GROUP_NAME_PROP, null);
        }
        if (getValue(GROUP_DESCRIPTION_PROP) != null) {
            putValue(GROUP_DESCRIPTION_PROP, null);
        }
        if (nodes == null || nodes.length == 0) {
            suppMap = null;
            return false;
        }
/*        if (getClass() != AbstractCommandAction.class) {
            System.out.println("------");
             System.out.println("creatingSupporter map for" + getClass());
        }
 */
        suppMap = new HashMap();
        for (int i = 0; i < nodes.length; i++) {
            GroupCookie gc = (GroupCookie) nodes[i].getCookie(GroupCookie.class);
            if (gc != null) {
                // setValue for recognition by the supporters
                putValue(GROUP_DESCRIPTION_PROP, gc.getDescription());
                putValue(GROUP_NAME_PROP, gc.getDisplayName());
                Enumeration childs = nodes[i].getChildren().nodes();
//                System.out.println("create supp. map for group.. count=" + grNode.getChildren().getNodesCount());
                while (childs.hasMoreElements()) {
                    Node nd = (Node)childs.nextElement();
                    DataObject dobj = (DataObject)nd.getCookie(DataObject.class);
                    while (dobj != null && dobj instanceof DataShadow) {
                        dobj = ((DataShadow)dobj).getOriginal();
                    }
                    if (dobj != null) {
/*                        if (getClass() != AbstractCommandAction.class) {
                            System.out.println("checking action on data shadow=" + dobj.getName() + "   " + dobj.getClass());
                        }
 */
                        if (!checkDataObject(dobj)) return false;
                    }
                }
            } else {
                DataObject dataObj;
                dataObj = (DataObject)nodes[i].getCookie(DataObject.class);
                while (dataObj != null && dataObj instanceof DataShadow) {
                    dataObj = ((DataShadow)dataObj).getOriginal();
                }
//                System.out.println("dataobj =" + dataObj);
                if (!checkDataObject(dataObj)) return false;
            }
        }
        return true;
    }
    
    private boolean checkDataObject(DataObject dataObj) {
        if (dataObj == null) {
            suppMap = null;
            return false;
        }
        FileObject fileOb = dataObj.getPrimaryFile();
        if (fileOb == null) {
            suppMap = null;
            return false;
        }
        FileSystem primaryFS = (FileSystem) fileOb.getAttribute(org.netbeans.modules.vcscore.VcsAttributes.VCS_NATIVE_FS);
        CommandActionSupporter supp = (CommandActionSupporter)fileOb.getAttribute(VCS_ACTION_ATTRIBUTE);
        if (supp != null) {
            Set fileSet = new HashSet();
            Iterator it = dataObj.files().iterator();
            while (it.hasNext()) {
                FileObject fo = (FileObject)it.next();
                supp = (CommandActionSupporter)fileOb.getAttribute(VCS_ACTION_ATTRIBUTE);                
                if (supp != null) {
                    if (primaryFS != null) {
                        FileSystem fs = (FileSystem) fo.getAttribute(org.netbeans.modules.vcscore.VcsAttributes.VCS_NATIVE_FS);
                        if (!primaryFS.equals(fs)) {
                            // We have a secondary file on another filesystem!
                            continue;
                        }
                    }
                    fileSet.clear();
                    fileSet.add(fo);
                    addToMap(suppMap, supp, fileSet);
                }
            }
//            addToMap(suppMap, supp, dataObj.files());
        } else {
            suppMap = null;
//            System.out.println("no supporter found for " + dataObj.getName());
            // one of the files is not under version control..
            return false;
        }
        return true;
        
    }
    
    private void addToMap(HashMap map, CommandActionSupporter supporter, Set files) {
        Set set = (Set)map.get(supporter);
        if (set == null) {
            set = new HashSet();
        }    
        if (files != null) {
            Iterator it = files.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj != null) {
                     set.add(obj);
                }
            }
        }
        map.put(supporter, set);
    }
    

    /**
     * The action performs it's job on the activated nodes..
     */
    public void performAction() {
        performAction(getActivatedNodes());
    }    
    

    
}
