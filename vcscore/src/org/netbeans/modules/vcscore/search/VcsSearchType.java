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


package org.netbeans.modules.vcscore.search;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.TopManager;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;
import org.openidex.search.*;


/**
 * SearchType which searches statuses of files in cvs filesystems.
 *
 * @author  Martin Entlicher, Peter Zavadsky
 * @see org.openidex.search.SearchType
 */
public class VcsSearchType extends SearchType {

    public static final long serialVersionUID = 812466793021976245L;    
    
    private String matchStatus = null;
    private boolean matchExcept = false;

    private static Vector statuses = new Vector();
    private int[] indexes;
    private Vector matchStatuses = null;

    /** Property change listener. */
    private PropertyChangeListener propListener;
    
    
    /** Creates new VcsSearchType */
    public VcsSearchType() {
    }


    /** Prepares search object for search. Listens on the underlying 
     * object and fires SearchType.PROP_OBJECT_CHANGED property change
     * in cases object has changed. */
    protected void prepareSearchObject(Object searchObject) {
        DataObject dataObject = extractDataObject(searchObject);

        if(dataObject == null) 
            return;
        
        dataObject.addPropertyChangeListener(
            WeakListener.propertyChange(getDataObjectListener(), dataObject)
        );
        
    }

    /** Gets property change listener which listens on changes on searched data object. */
    private synchronized PropertyChangeListener getDataObjectListener() {
        if(propListener == null) {
            propListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if(DataObject.PROP_COOKIE.equals(evt.getPropertyName()))
                        firePropertyChange(SearchType.PROP_OBJECT_CHANGED, null, evt.getSource());
                }
            };
        }
        
        return propListener;
    }
    
    /** Tests object. Implements superclass abstract method. */
    public boolean testObject(Object object) {
        DataObject dataObject = extractDataObject(object);
            
        if(dataObject == null)
            return false;
        
        return testDataObject(dataObject);
    }

    /** Gets data object from search object. */
    private static DataObject extractDataObject(Object object) {
        DataObject dataObject = null;
        
        if(object instanceof DataObject) {
            dataObject = (DataObject)object;
        } else if(object instanceof FileObject) {
            try{
                dataObject = DataObject.find((FileObject)object);
            } catch(DataObjectNotFoundException dnfe) {
                dnfe.printStackTrace();
            }
        }

        return dataObject;
    }

    /** Creates array of search type classes.
     * @return array containing one element - <code>DataObject</code> class */
    protected Class[] createSearchTypeClasses() {
        return new Class[] {DataObject.class};
    }
    
    /** Adds available cvs statuses. */
    private void addStatuses(String[] possibleStatuses) {
        if (possibleStatuses == null)
            return;
        
        for(int i = 0; i < possibleStatuses.length; i++) {
            if (!statuses.contains(possibleStatuses[i])) statuses.add(possibleStatuses[i]);
        }
        String[] sorted = (String[]) statuses.toArray(new String[0]);
        Arrays.sort(sorted);
        statuses = new Vector(Arrays.asList(sorted));
    }

    public String[] getStatuses() {
        //System.out.println("getStatuses(this = "+this+"): return = "+statuses);
        return (String[]) statuses.toArray(new String[0]);
    }

    /** Overrides superclass method. */
    public Node[] acceptSearchRootNodes(Node[] roots) {
        if(roots == null || roots.length == 0) 
            return roots;

        List acceptedRoots = new ArrayList(roots.length);
        //statuses = new Vector();
        for(int i = 0; i < roots.length; i++) {
            Node root = roots[i];
            
            DataFolder dataFolder = (DataFolder)root.getCookie(DataFolder.class);
            if(dataFolder != null) {
                FileObject fo = dataFolder.getPrimaryFile();
                FileSystem fs = null;
                try {
                    fs = fo.getFileSystem();
                } catch(FileStateInvalidException fsie) {
                    if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                        fsie.printStackTrace();
                    }
                }
                
                if(fs instanceof VcsSearchTypeFileSystem) {
                    acceptedRoots.add(root);
                    continue;
                }
            }

            try {
                InstanceCookie ic = (InstanceCookie)root.getCookie (InstanceCookie.class);
                if(ic != null && Repository.class.isAssignableFrom (ic.instanceClass ())) {
                    acceptedRoots.add(root);
                }
                
            } catch(IOException ioe) {
                // does not provide instance
                if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                    ioe.printStackTrace();
                }
            } catch(ClassNotFoundException cnfe) {
                // does not provide instance
                if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                    cnfe.printStackTrace();
                }
            }

        }

        return (Node[])acceptedRoots.toArray(new Node[acceptedRoots.size()]);
    }

    /** Implements superclass abstract method. */
    public boolean enabled(Node[] nodes) {
        if(nodes == null || nodes.length == 0)
            return false;

        boolean statusesAdded = false;
        //statuses = new Vector();
        for(int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            
            DataFolder dataFolder = (DataFolder)node.getCookie(DataFolder.class);
            if(dataFolder != null) {
                FileObject fo = dataFolder.getPrimaryFile();
                FileSystem fs = null;
                try {
                    fs = fo.getFileSystem();
                } catch(FileStateInvalidException fsie) {
                    if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                        fsie.printStackTrace();
                    }
                }

                if(fs instanceof VcsSearchTypeFileSystem) {
                    String[] possibleStatuses = ((VcsSearchTypeFileSystem)fs).getPossibleFileStatuses();
                    if(!statusesAdded) {
                        statuses = new Vector();
                        statusesAdded = true;
                    }

                    addStatuses(possibleStatuses);
                    
                    return true;
                }
            }
        }

        for(int i = 0; i < nodes.length; i++) {
            try {
                InstanceCookie ic = (InstanceCookie)nodes[i].getCookie (InstanceCookie.class);
                if(ic != null && Repository.class.isAssignableFrom (ic.instanceClass ())) {
                    FileSystem[] fileSystems = TopManager.getDefault().getRepository().toArray();
                    
                    for(int j = 0; j < fileSystems.length; j++) {
                        if(fileSystems[j] instanceof VcsSearchTypeFileSystem) {
                            String[] possibleStatuses = ((VcsSearchTypeFileSystem)fileSystems[j]).getPossibleFileStatuses();
                            if(!statusesAdded) {
                                statuses = new Vector();
                                statusesAdded = true;
                            }

                            addStatuses(possibleStatuses);
                            
                            
                            return true;
                        }
                    }
                    
                    return false;
                }
            } catch(IOException ioe) {
                // does not provide instance
                if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                    ioe.printStackTrace();
                }
            } catch(ClassNotFoundException cnfe) {
                // does not provide instance
                if(Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                    cnfe.printStackTrace();
                }
            }
        }

        return false;
    }

    public String getTabText() {
        return NbBundle.getBundle(VcsSearchType.class).getString ("CTL_Status");
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public Vector getMatchStatuses() {
        Vector matchStatuses = new Vector(indexes.length);
        for(int i = 0; i < indexes.length; i++) {
            matchStatuses.add(statuses.get(indexes[i]));
        }
        return matchStatuses;
    }

    public void setMatchStatus(String status) {
        if (status == null) {
            setValid(false);
            throw new IllegalArgumentException();
        }
        String old = matchStatus;
        this.matchStatus = status;
        setValid(true);
        firePropertyChange("Status", old, status);
    }

    public void setStatusIndexes(int[] indexes) {
        this.indexes = indexes;
        setValid(indexes.length > 0);
        firePropertyChange("Status", null, null);
        matchStatuses = getMatchStatuses();
    }

    public boolean getMatchExcept() {
        return matchExcept;
    }

    public void setMatchExcept(boolean matchExcept) {
        this.matchExcept = matchExcept;
    }


    private boolean testDataObject(DataObject dobj) {
        if (matchStatuses == null) return true;
        FileObject fo = dobj.getPrimaryFile();
        FileSystem fs = null;
        try {
            fs = fo.getFileSystem();
        } catch(FileStateInvalidException exc) {
            fs = null;
        }
        if (fs == null || !(fs instanceof VcsSearchTypeFileSystem)) return false;
        VcsSearchTypeFileSystem vfs = (VcsSearchTypeFileSystem) fs;
        String status = vfs.getStatus(dobj);
        if (matchExcept) {
            return !matchStatuses.contains(status);
        } else {
            return matchStatuses.contains(status);
        }
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(this.getClass());
    }

}
