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

import java.util.*;

import org.openidex.search.*;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.NbBundle;


/**
 *
 * @author  Martin Entlicher
 * @version 
 */
public class VcsSearchType extends org.netbeans.modules.search.types.DataObjectType /*SearchType*/ {

    private String matchStatus = null;
    private boolean matchExcept = false;

    private static Vector statuses = new Vector();
    private int[] indexes;
    private Vector matchStatuses = null;

    public static final long serialVersionUID = 812466793021976245L;

    /** Creates new VcsSearchType */
    public VcsSearchType() {
        setValid(false);
    }

    private void addStatuses(String[] possibleStatuses) {
        if (possibleStatuses == null) return;
        for(int i = 0; i < possibleStatuses.length; i++) {
            //System.out.println("VcsSeatchType: addStatuses(): adding "+possibleStatuses[i]);
            if (!statuses.contains(possibleStatuses[i])) statuses.add(possibleStatuses[i]);
            //System.out.println("this = "+this+", statuses = "+statuses);
        }
        String[] sorted = (String[]) statuses.toArray(new String[0]);
        Arrays.sort(sorted);
        statuses = new Vector(Arrays.asList(sorted));
    }

    public String[] getStatuses() {
        //System.out.println("getStatuses(this = "+this+"): return = "+statuses);
        return (String[]) statuses.toArray(new String[0]);
    }

    public boolean enabled(Node[] nodes) {
        //System.out.println("enabled(): this = "+this);
        boolean isVcsFileSystem = false;
        if (nodes == null || nodes.length == 0) return false;
        boolean statusesAdded = false;
        //statuses = new Vector();
        for (int i =0; i < nodes.length; i++) {
            Node node = nodes[i];
            if (node.getCookie(org.openide.loaders.DataFolder.class) != null) {
                Node.Cookie cake = node.getCookie(org.openide.loaders.DataFolder.class);
                if ( cake != null) {
                    DataFolder folder = (DataFolder) cake;
                    FileObject fo = folder.getPrimaryFile();
                    FileSystem fs = null;
                    try {
                        fs = fo.getFileSystem();
                    } catch (FileStateInvalidException exc) {
                        fs = null;
                    }
                    if (fs == null) return false;
                    if (fs instanceof VcsSearchTypeFileSystem) {
                        String[] possibleStatuses = ((VcsSearchTypeFileSystem) fs).getPossibleFileStatuses();
                        if (!statusesAdded) {
                            statuses = new Vector();
                            statusesAdded = true;
                        }
                        addStatuses(possibleStatuses);
                        isVcsFileSystem = true;
                    } else return false;
                }
            }

            if (node.getCookie(org.openide.filesystems.Repository.class) != null) {
                return isVcsFileSystem;
            }
        }

        return isVcsFileSystem;
    }

    /*
    public Class getScannerClass() {
        return VcsScanner.class;
    }

    public Class[] getDetailClasses() {
        return null;
    }
    */

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

    /*
    public boolean test(VcsFileSystem fs, FileObject fo) {
        if (!fs.isImportant(fo.getPackageNameExt('/', '.'))) return false;
        String status = fs.getStatus(fo);
        if (matchStatus == null) return true;
        if (matchExcept) {
            //System.out.println("Except: "+status+" = "+matchStatus+": "+!matchStatus.equals(status));
            return !matchStatus.equals(status);
        } else {
            //System.out.println("Cmp.  : "+status+" = "+matchStatus+": "+matchStatus.equals(status));
            return matchStatus.equals(status);
        }
    }
    */

    public boolean test(DataObject dobj) {
        if (matchStatuses == null) return true;
        FileObject fo = dobj.getPrimaryFile();
        FileSystem fs = null;
        try {
            fs = fo.getFileSystem();
        } catch(org.openide.filesystems.FileStateInvalidException exc) {
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
