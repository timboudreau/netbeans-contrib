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

import org.openide.loaders.*;
import org.openide.util.NbBundle;
import java.io.File;
import org.openide.util.HelpCtx;
import org.openide.*;
import org.openide.nodes.Node;
import org.openide.filesystems.FileObject;
import org.openide.util.actions.NodeAction;
import java.util.*;
import org.netbeans.modules.vcscore.grouping.VcsGroupNode;

/** Action that keeps a list of vcs supported activated nodes, subclasses can delegate the recognition of such 
 * nodes to this class to achieve performance improvement.
 * (splits the nodes to fileobjects)
 *  Vcs Enabled fileobjects are recognized by the fileobject attribute named
 *  "VcsActionAttributeCookie", which value should be an instance of the CommandActionSupporter class.
 *
 *
 * @author  Milos Kleint
 */
public class AbstractCommandAction extends NodeAction {

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
    
    /**
     * Name of a FileObject attribute. Needs to be set on primary file of a node(dataobject)
     * in order to trigger the GeneralCommandAction and it's suclasses.
     * The value of the attribute is the 
     */
    
    public static final String VCS_ACTION_ATTRIBUTE = "VcsActionAttributeCookie"; //NOI18N
    
    private HashMap suppMap;
    private HashSet actionSet;
    
    
    static final long serialVersionUID = 3425234373723671084L;    
    
    public AbstractCommandAction() {
        super();
//        System.out.println("constructor abstract");
    }
    
    protected void initialize() {
        super.initialize();
//        System.out.println("Abstract initialized");
        if (getClass().equals(AbstractCommandAction.class)) {
            addNotify();
        }
    }
    
    protected void performAction (Node[] nodes) {
    }
    
    /**
     * returns a map with CommandActionSupporters as keys.
     * values are Sets of activated FileObjects.
     * returns null, if the action wasn't enabled. (thus none should be).
     */
    public HashMap getSupporterMap() {
        return suppMap;
    }
    
    protected boolean createSupporterMap(Node[] nodes) {
        putValue(GROUP_NAME_PROP, null);
        putValue(GROUP_DESCRIPTION_PROP, null);
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
            if (nodes[i] instanceof VcsGroupNode) {
                // setValue for recognition by the supporters
                VcsGroupNode grNode = (VcsGroupNode)nodes[i];
                putValue(GROUP_DESCRIPTION_PROP, grNode.getShortDescription());
                putValue(GROUP_NAME_PROP, grNode.getDisplayName());
                Enumeration childs = nodes[i].getChildren().nodes();
//                System.out.println("create supp. map for group..");
                while (childs.hasMoreElements()) {
                    Node nd = (Node)childs.nextElement();
                    DataObject dobj = (DataObject)nd.getCookie(DataObject.class);
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
        CommandActionSupporter supp = (CommandActionSupporter)fileOb.getAttribute(VCS_ACTION_ATTRIBUTE);
        if (supp != null) {
            addToMap(suppMap, supp, dataObj.files());
        } else {
            suppMap = null;
            //                System.out.println("no supporter found for" + nodes[i].getName());
            // one of the files is not under version control..
            return false;
        }
        return true;
        
    }

    protected boolean enable (Node[] nodes) {
        // debug
/*        if (nodes != null) {
            System.out.print("nodes1:");
            for (int i = 0; i < nodes.length; i++) {
                System.out.print(nodes[i].getDisplayName() + ", ");
            }
            System.out.println("");
        }
        Node[] newNodes = TopManager.getDefault().getWindowManager().getRegistry().getActivatedNodes();
        if (newNodes != null) {
            System.out.print("nodes2:");
            for (int i = 0; i < newNodes.length; i++) {
                System.out.print(newNodes[i].getDisplayName() + ", ");
            }
            System.out.println("");
        }
 */
        // debug end
        createSupporterMap(nodes);
        if (actionSet != null) {
            Iterator it = actionSet.iterator();
            while (it.hasNext()) {
                org.openide.util.actions.SystemAction act = (org.openide.util.actions.SystemAction)it.next();
                act.isEnabled();
            }
        }
        return false;
    }
    
    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (JavaCvsCommandAction.class);
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

    public String getName () {
        return NbBundle.getMessage(AbstractCommandAction.class, "LBL_Action"); //NOI18N
    }
    

    protected String iconResource () {
        return null;
    }

    /**
     * Every action that wants to use the AbstractCommandaction as proxy, should call 
     * this method in removeNotify() instead of the the default behaviour.
     */

    public void removeDependantAction(org.openide.util.actions.SystemAction action) {
        if (action == null) return;
        if (actionSet == null) {
            actionSet = new HashSet();
        }
        actionSet.remove(action);
    }
 
    /**
     * Every action that wants to use the AbstractCommandaction as proxy, should call 
     * this method in addNotify() instead of the the default behaviour.
     */
    public void addDependantAction(org.openide.util.actions.SystemAction action) {
        if (action == null) return;
        if (actionSet == null) {
            actionSet = new HashSet();
        }
        actionSet.add(action);
    }
    
    public void reinitialize() {
        addNotify();
    }
    
}
