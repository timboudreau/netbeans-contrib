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
package org.netbeans.modules.vcscore.grouping;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.*;
import java.io.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import org.openide.actions.PropertiesAction;
import org.openide.TopManager;
import org.openide.ErrorManager;

/** A node with some children.
 *
 * @author builder
 */
public class VcsGroupNode extends AbstractNode {
    public static final String PROPFILE_EXT = "properties"; //NOI18N
    private DataFolder groupDO;
    private String groupName;
    private String groupDescription = "";
    
//    private static ShadowOnlyDataFilter SHADOW_ONLY = new ShadowOnlyDataFilter();
    
    public VcsGroupNode(DataFolder dobj) {
        super (new VcsGroupChildren(dobj));
        groupDO = dobj;
        setIconBase("org/netbeans/modules/vcscore/grouping/VcsGroupNodeIcon"); //NOI18N
        // Whatever is most relevant to a user:
        // Set FeatureDescriptor stuff:
        groupName = groupDO.getName();
        FileObject propsFo = dobj.getPrimaryFile().getParent().getFileObject(dobj.getPrimaryFile().getName(), PROPFILE_EXT);
        if (propsFo != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(propsFo.getInputStream()));
                groupName = getBundleValue(reader.readLine());
                groupDescription = getBundleValue(reader.readLine());
                groupDescription = org.openide.util.Utilities.replaceString(groupDescription, "\\n", "\n"); //NOI18N
            } catch (IOException exc) {
                TopManager.getDefault().getErrorManager().notify(ErrorManager.WARNING, exc);
            }
        }
    }
    
    private String getBundleValue(String keyValue) {
        if (keyValue != null) {
            int index = keyValue.indexOf('=');
            if (index > 0 && keyValue.length() > index) {
                return keyValue.substring(index + 1);
            }
        }
        return "";
    }
    
    
/*    public Node[] getDataShadowNodesInGroup() {
        LinkedList list = new LinkedList();
        Enumeration childs = groupDO.children(false);
        Set actions = new HashSet();
        while (childs.hasMoreElements()) {
            DataObject dos = (DataObject)childs.nextElement();
            if (dos instanceof DataShadow) {
                 DataShadow shadow = (DataShadow)dos;
                 Node node = new VcsGroupFileNode(shadow);
                 list.add(node);
            }
            
        }
        Node[] fos = new Node[list.size()];
        fos = (Node[])list.toArray(fos);
        return fos;
        
    }
 */

    // Create the popup menu:
    public SystemAction[] getActions() {
        Node[] childs = getChildren().getNodes();
        Set actions = new HashSet();
        HashMap map = new HashMap();
        List actionsList = new LinkedList();
        if (childs != null) {
            for (int i = 0; i < childs.length; i++) {
                try {
                    DataObject dob = (DataObject)childs[i].getCookie(DataObject.class);
                    if (dob != null) {
                        FileObject fo = dob.getPrimaryFile();
                        SystemAction[] acts = fo.getFileSystem().getActions();
                        for (int m =0; m < acts.length; m++) {
//                            System.out.println("group action class=" + acts[m]);
                            if (!acts[m].isEnabled()) continue;
                            if (!acts[m].getClass().equals(org.netbeans.modules.vcscore.actions.AddToGroupAction.class)) {
                                actions.add(acts[m]);
                                actionsList.add(acts[m]);
                                LinkedList lst = (LinkedList)map.get(acts[m]);
                                if (lst == null) {
                                    lst = new LinkedList();
                                    map.put(acts[m], lst);
                                }
                                lst.add(childs[i]);
                            }
                        }
                        
                    }
                } catch (FileStateInvalidException exc) {
//                    System.out.println("fileystateinvalid..");
                }
            }
        }
        // now check if the actions are enabled on all the nodes... if not.. remove them
        Iterator actIt = map.keySet().iterator();
        while (actIt.hasNext()) {
            Object act = actIt.next();
            LinkedList list = (LinkedList)map.get(act);
            if (list != null) {
                if (list.size() != childs.length) {
                    actions.remove(act);
                }
            } else {
                actions.remove(act);
            }
            
        }
        SystemAction[] toReturn;
/*        if (actions.size() > 1) {
            toReturn = new SystemAction[7];
        } else {
 */
            toReturn = new SystemAction[actions.size() + 7];
//        }
        Iterator it = actionsList.iterator();
        int index = 0;
        while (it.hasNext()) {
            SystemAction act = (SystemAction)it.next();
            if (actions.contains(act)) {
                toReturn[index] = act;
                index = index + 1;
                actions.remove(act);
            }
            if (actions.size() == 0) {
                break;
            }
        }
        toReturn[toReturn.length - 6] = (SystemAction)SharedClassObject.findObject(VerifyGroupAction.class, true);
        toReturn[toReturn.length - 5] = null;
        toReturn[toReturn.length - 4] = SystemAction.get (DeleteAction.class);
        toReturn[toReturn.length - 3] = SystemAction.get (RenameAction.class);
        toReturn[toReturn.length - 2] = null;
        toReturn[toReturn.length - 1] = SystemAction.get (PropertiesAction.class);
        return toReturn;
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return new org.openide.util.HelpCtx(this.getClass());
    }



    // RECOMMENDED - handle cloning specially (so as not to invoke the overhead of FilterNode):
    public Node cloneNode () {
	// Try to pass in similar constructor params to what you originally got:
        return new VcsGroupNode (groupDO);
    }

    // Create a property sheet:
    protected Sheet createSheet () {
	Sheet sheet = super.createSheet ();
	// Make sure there is a "Properties" set:
	Sheet.Set props = Sheet.createPropertiesSet();
        sheet.put (props);
        props.setValue("helpID", VcsGroupNode.class.getName()+"_properties");
        createProperties(props);
        return sheet;
    }
    
    private void createProperties(Sheet.Set set) {
        java.util.ResourceBundle bundle = NbBundle.getBundle(VcsGroupNode.class);
        set.put(new PropertySupport.ReadWrite("shortDescription", String.class, //NOI18N
        bundle.getString("LBL_Description"), bundle.getString("DESC_Description")) { //NOI18N
            public void setValue(Object value) {
                VcsGroupNode.this.setShortDescription(value.toString());
            }
            public Object getValue() {
                return VcsGroupNode.this.groupDescription;
            }
        });
        set.put(new PropertySupport.ReadWrite("name", String.class, //NOI18N
        bundle.getString("LBL_GroupName"), bundle.getString("DESC_GroupName")) { //NOI18N
            public void setValue(Object value) {
                VcsGroupNode.this.setName(value.toString());
            }
            public Object getValue() {
                return VcsGroupNode.this.getName();
            }
        });
    }

    // Handle renaming:
    public boolean canRename () {
	return true;
    }
    
    public void setName(String name) {
        String oldName = this.groupName;
        this.groupName = name;
        super.setName(name);
        saveProperties();
        firePropertyChange("name", oldName, name);
    }

    public String getName() {
        return this.groupName;
    }
    
    public String getDisplayName() {
        return this.groupName;
    }
    
    public String getShortDescription() {
        if (this.groupDescription.length() == 0) {
            return null;
        }
        return this.groupDescription;
    }
    
    public void setShortDescription(String desc) {
        String oldDesc = this.groupDescription;
        this.groupDescription = desc;
        super.setShortDescription(desc);
        saveProperties();
        firePropertyChange("shortDescription", oldDesc, desc);
    }

    // Handle deleting:
    public boolean canDestroy () {
	return true;
    }
    public void destroy() throws IOException {
        // Actually remove the node itself and fire property changes:
        super.destroy();
        FileObject parent = groupDO.getPrimaryFile().getParent();
        String name = groupDO.getPrimaryFile().getName();
        if (groupDO.isValid()) {
            try {
                groupDO.delete();
            } catch (IOException exc) {
                //TODO
                TopManager.getDefault().getErrorManager().annotate(exc, 
                             NbBundle.getBundle(VcsGroupNode.class).getString("VcsGroupNode.cannotDestroyGroup")); //NOI18N
                return;
            }
        }
        FileObject props = parent.getFileObject(name, PROPFILE_EXT);
        try {
            if (props != null) {
                props.delete(props.lock());
            }
        } catch (IOException ex) {
            TopManager.getDefault().getErrorManager().annotate(ex, 
                       NbBundle.getBundle(VcsGroupNode.class).getString("VcsGroupNode.cannotDestroyGroupProps")); //NOI18N
        }
        // perform additional actions, i.e. delete underlying object
        // (and don't forget about objects represented by your children!)
    }

    
    private void saveProperties() {
        FileObject fo = groupDO.getPrimaryFile();
        PrintWriter writer = null;
        try {
            FileObject props = fo.getParent().getFileObject(fo.getName(), PROPFILE_EXT);
            if (props == null) {
                props = fo.getParent().createData(fo.getName(), PROPFILE_EXT);
            }
            writer = new PrintWriter(props.getOutputStream(props.lock()));
            writer.println(PROP_NAME + "=" + getDisplayName()); //NOI18N
            String oneLineDescription = org.openide.util.Utilities.replaceString(
                                                    this.groupDescription, "\n", "\\n"); //NOI18N
            writer.println(PROP_SHORT_DESCRIPTION + "=" + oneLineDescription); //NOI18N
            writer.close();
        } catch (IOException exc) {
            if (writer != null) {
                writer.close();
            }
        }
        
    }
    
    public org.openide.nodes.Node.Cookie getCookie(java.lang.Class clazz) {
        org.openide.nodes.Node.Cookie retValue;
        if (clazz.equals(DataObject.class)) {
            return groupDO;
        }
        retValue = super.getCookie(clazz);
        return retValue;
    }
    
    
    private static class ShadowOnlyDataFilter implements DataFilter {
        
        private static final long serialVersionUID = 6549358585875198736L;
        
        public boolean acceptDataObject(org.openide.loaders.DataObject dataObject) {
            if (dataObject instanceof DataShadow) {
                return true;
            }
            return false;
        }
        
    }
    
    


}
