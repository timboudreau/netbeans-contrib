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
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;

/** Action sensitive to the node selection that does something useful.
 *
 * @author  Milos Kleint
 */
public class AbstractCommandAction extends NodeAction {

    /**
     * Name of a FileObject attribute. Needs to be set on primary file of a node(dataobject)
     * in order to trigger the GeneralCommandAction and it's suclasses.
     * The value of the attribute is the 
     */
    
    public static final String VCS_ACTION_ATTRIBUTE = "VcsActionAttributeCookie";
    
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
        addNotify();
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
    
    public boolean createSupporterMap(Node[] nodes) {
        if (nodes == null || nodes.length == 0) {
            suppMap = null;
            return false;
        }
        suppMap = new HashMap();
        for (int i = 0; i < nodes.length; i++) {
            DataObject dataObj = (DataObject)nodes[i].getCookie(DataObject.class);
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
        }
        return true;
    }

    protected boolean enable (Node[] nodes) {
        createSupporterMap(nodes);
        if (actionSet != null) {
            Iterator it = actionSet.iterator();
            while (it.hasNext()) {
                GeneralCommandAction act = (GeneralCommandAction)it.next();
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
        return NbBundle.getMessage(GeneralCommandAction.class, "LBL_Action");
    }
    

    protected String iconResource () {
        return null;
    }

    public void removeDependantAction(GeneralCommandAction action) {
        if (actionSet == null) {
            actionSet = new HashSet();
        }
        actionSet.remove(action);
    }

    public void addDependantAction(GeneralCommandAction action) {
        if (actionSet == null) {
            actionSet = new HashSet();
        }
        actionSet.add(action);
    }
    
    public void reinitialize() {
        addNotify();
    }
    
}
