/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */


package org.netbeans.modules.vcscore.search;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.ErrorManager;
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
            WeakListeners.propertyChange(getDataObjectListener(), dataObject)
        );
        
    }

    /** Gets property change listener which listens on changes on searched data object. */
    private synchronized PropertyChangeListener getDataObjectListener() {
        if(propListener == null) {
            propListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if(DataObject.PROP_COOKIE.equals(evt.getPropertyName()))
                        firePropertyChange(PROP_OBJECT_CHANGED, null, evt.getSource());
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

    /**
     * Implements superclass abstract method.
     * As side effect set stauses field
     */
    public boolean enabled(Node[] nodes) {
        if(nodes == null || nodes.length == 0)
            return false;

        boolean statusesAdded = false;
        //statuses = new Vector();
        for(int i = 0; i < nodes.length; i++) {
            SearchInfo info = (SearchInfo) nodes[i].getLookup().lookup(SearchInfo.class);
            if (info != null) {
                // heuristics, try first 5 file objects
                // ignored cornercase: it fails for project scattered over several versionig FS workdirs
                Iterator it = info.objectsToSearch();
                int sampleSize = 5;
                while (it.hasNext()) {
                    DataObject dataObject = (DataObject) it.next();
                    FileObject fo = dataObject.getPrimaryFile();
                    statusesAdded |= fillStatuses(fo, statusesAdded);
                    if (sampleSize-- == 0) break;
                }
            } else {
                DataFolder dataFolder = (DataFolder)nodes[i].getCookie(DataFolder.class);
                if(dataFolder != null) {
                    FileObject fo = dataFolder.getPrimaryFile();
                    statusesAdded |= fillStatuses(fo, statusesAdded);
                } else {
                    // DataSystem does not have a DataObject cookie => skip all nodes with DataObject cookies
                    if (nodes[i].getCookie(DataObject.class) != null) continue;
                    InstanceCookie.Of ic = (InstanceCookie.Of)nodes[i].getCookie(InstanceCookie.Of.class);
                    if(ic != null && ic.instanceOf(Repository.class)) {
                        FileSystem[] fileSystems = org.openide.filesystems.Repository.getDefault().toArray();
                        for(int j = 0; j < fileSystems.length; j++) {
                            VcsSearchTypeFileSystem searchFS;
                            if (fileSystems[j] instanceof VcsSearchTypeFileSystem) {
                                searchFS = (VcsSearchTypeFileSystem) fileSystems[j];
                            } else {
                                searchFS = (VcsSearchTypeFileSystem) fileSystems[j].getRoot().getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
                            }
                            if (searchFS != null) {
                                String[] possibleStatuses = searchFS.getPossibleFileStatuses();
                                if(!statusesAdded) {
                                    statuses = new Vector();
                                    statusesAdded = true;
                                }
                                addStatuses(possibleStatuses);
                            }
                        }
                    }
                }
            }
        }

        return statusesAdded;
    }

    /** Initialize statuses field from given fileobject. */
    private boolean fillStatuses(FileObject fo, boolean statusesAdded) {
        FileSystem fs = null;
        try {
            fs = fo.getFileSystem();
        } catch(FileStateInvalidException fsie) {
            ErrorManager.getDefault().notify(fsie);
        }
        VcsSearchTypeFileSystem searchFS;
        if (fs instanceof VcsSearchTypeFileSystem) {
            searchFS = (VcsSearchTypeFileSystem) fs;
        } else {
            searchFS = (VcsSearchTypeFileSystem) fo.getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
        }
        if (searchFS != null) {
            String[] possibleStatuses = searchFS.getPossibleFileStatuses();
            if(statusesAdded == false) {
                statuses = new Vector();
                statusesAdded = true;
            }
            addStatuses(possibleStatuses);
        }
        return statusesAdded;
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
    
    public int[] getStatusIndexes() {
        return indexes;
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
        VcsSearchTypeFileSystem searchFS;
        if (fs instanceof VcsSearchTypeFileSystem) {
            searchFS = (VcsSearchTypeFileSystem) fs;
        } else {
            searchFS = (VcsSearchTypeFileSystem) fo.getAttribute(VcsSearchTypeFileSystem.VCS_SEARCH_TYPE_ATTRIBUTE);
        }
        if (searchFS == null) return false;
        String[] states = searchFS.getStates(dobj);
        if (matchExcept) {
            List statesList = Arrays.asList(states);
            return !matchStatuses.containsAll(statesList);
        } else {
            boolean contains = false;
            for (int i = 0; i < states.length; i++) {
                contains = contains || matchStatuses.contains(states[i]);
            }
            return contains;
        }
    }

    public HelpCtx getHelpCtx() {
        return null;
    }

}
