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

package org.netbeans.modules.vcscore.versioning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.util.WeakListener;

import org.netbeans.modules.vcscore.actions.VSRevisionAction;
import org.netbeans.modules.vcscore.util.VcsUtilities;
//import org.netbeans.modules.vcscore.VcsFileSystem;

/**
 *
 * @author  Martin Entlicher
 */
public class RevisionNode extends AbstractNode implements /*OpenCookie, */PropertyChangeListener {

    private static final String ICON_BRANCH = "/org/netbeans/modules/vcscore/revision/branchIcon";
    //private static final String ICON_OPEN_BRANCH = "/org/openide/resources/defaultFolderOpen.gif";
    private static final String ICON_REVISION = "/org/netbeans/modules/vcscore/revision/revisionIcon";
    private static final String ICON_REVISION_CURRENT = "/org/netbeans/modules/vcscore/revision/revisionCurrentIcon";

    private RevisionItem item = null;
    private RevisionList list = null;
    
    private Sheet.Set propertiesSet;
    private Node.Property propertyRevision;
    private Node.Property propertyMessage;
    private Node.Property propertyDate;
    private Node.Property propertyAuthor;
    private Node.Property propertyLocker;
    private Node.Property propertyTags;
    
    /** Creates new RevisionNode */
    public RevisionNode(RevisionList list, RevisionChildren ch) {
        super(ch);//new RevisionChildren(list));
        //((RevisionChildren) this.getChildren()).setNode(this);
        init(list, null);
    }
    
    public RevisionNode(RevisionChildren children) {
        super(children);
        //children.setNode(this);
        init(children.getList(), null);
    }
    
    public RevisionNode(RevisionList list, RevisionItem item) {
        super(Children.LEAF);
        setName(item.getRevisionVCS());
        setDisplayName(item.getDisplayName());
        init(list, item);
    }
    
    private void init(RevisionList list, RevisionItem item) {
        this.list = list;
        this.item = item;
        addCookies();
        setIcon();
        if (item != null) item.addPropertyChangeListener(WeakListener.propertyChange(this, item));
    }
    
    private void addCookies() {
        if (item != null && !item.isBranch()) {
            getCookieSet().add(new RevisionEditorSupport(list, item));//this);
        }
        if (list != null) getCookieSet().add(list);
        if (item != null) getCookieSet().add(item);
    }
    
    private void setIcon() {
        if (item != null && !item.isBranch()) {
            //System.out.println("setIconBase("+getName()+", "+getDisplayName()+") = "+ICON_REVISION);
            if (item.isCurrent()) {
                setIconBase(ICON_REVISION_CURRENT);
            } else {
                setIconBase(ICON_REVISION);
            }
        } else {
            //System.out.println("setIconBase("+getName()+", "+getDisplayName()+") = "+ICON_BRANCH);
            setIconBase(ICON_BRANCH);
        }
    }
    
    private void refreshIcons() {
        setIcon();
        fireIconChange();
    }

    public void setItem(RevisionItem item) {
        this.item = item;
        setName(item.getRevisionVCS());
        setDisplayName(item.getDisplayName());
        addCookies();
        setIcon();
        if (item != null) item.addPropertyChangeListener(WeakListener.propertyChange(this, item));
    }
    
    public RevisionItem getItem() {
        return item;
    }
    
    /*
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
     */

    public String getType() {
        if (item == null || item.isBranch()) return NbBundle.getBundle(RevisionNode.class).getString("CTL_TypeBranch");
        else return NbBundle.getBundle(RevisionNode.class).getString("CTL_TypeRevision");
    }
    
    public boolean canCopy() {
        return false;
    }
    
    public boolean canCut() {
        return false;
    }
    
    public boolean canDestroy() {
        return false;
    }
        
    public boolean canRename() {
        return false;
    }
    
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (item == null) set.put(new PropertySupport.Name(this));
        if (item != null) {
            propertiesSet = set;
            createProperties(item, set);
            String[] propNames = item.getAdditionalPropertiesSetNames();
            for (int i = 0; i < propNames.length; i++) {
                Sheet.Set addSet = new Sheet.Set();
                addSet.setName(propNames[i]);
                addSet.setDisplayName(propNames[i]);
                createAdditionalProperties(item.getAdditionalPropertiesSets()[i], addSet);
                sheet.put(addSet);
            }
        }
        return sheet;
    }
    
    private void createProperties(final RevisionItem item, final Sheet.Set set) {
        if (item.getRevision() != null)
            set.put(propertyRevision = new PropertySupport.ReadOnly(
                        "revision", String.class,
                        NbBundle.getBundle(RevisionNode.class).getString("MSG_RevisionNumber"), ""
                        ) {
                            public Object getValue() {
                                return item.getRevisionVCS();
                            }
                    });
        if (item.getMessage() != null)
            set.put(propertyMessage = new PropertySupport.ReadOnly(
                        "message", String.class,
                        NbBundle.getBundle(RevisionNode.class).getString("MSG_Message"), ""
                        ) {
                            public Object getValue() {
                                return item.getMessage();
                            }
                    });
        if (item.getDate() != null)
            set.put(propertyDate = new PropertySupport.ReadOnly(
                        "date", String.class,
                        NbBundle.getBundle(RevisionNode.class).getString("MSG_Date"), ""
                        ) {
                            public Object getValue() {
                                return item.getDate();
                            }
                    });
        if (item.getAuthor() != null)
            set.put(propertyAuthor = new PropertySupport.ReadOnly(
                        "author", String.class,
                        NbBundle.getBundle(RevisionNode.class).getString("MSG_Author"), ""
                        ) {
                            public Object getValue() {
                                return item.getAuthor();
                            }
                    });
        if (item.getLocker() != null)
            set.put(propertyLocker = new PropertySupport.ReadOnly(
                        "locker", String.class,
                        NbBundle.getBundle(RevisionNode.class).getString("MSG_Locker"), ""
                        ) {
                            public Object getValue() {
                                return item.getLocker();
                            }
                    });
        final String[] tags = item.getTagNames();
        if (tags != null && tags.length > 0)
            set.put(propertyTags = new PropertySupport.ReadOnly(
                        "tag", String.class,
                        (tags.length > 1) ? NbBundle.getBundle(RevisionNode.class).getString("MSG_TagNames")
                                          : NbBundle.getBundle(RevisionNode.class).getString("MSG_TagName"), ""
                        ) {
                            public Object getValue() {
                                String tag = VcsUtilities.arrayToString(tags);
                                return tag.substring(1, tag.length() - 1);
                            }
                    });
        Hashtable additional = item.getAdditionalProperties();
        for(Enumeration enum = additional.keys(); enum.hasMoreElements(); ) {
            final String name = (String) enum.nextElement();
            final String value = (String) additional.get(name);
            set.put(new PropertySupport.ReadOnly(
                        "additional_"+name, String.class, name, ""
                        ) {
                            public Object getValue() {
                                return value;
                            }
                    });
        }
    }
    
    private void createAdditionalProperties(Map properties, Sheet.Set set) {
        for(Iterator it = properties.keySet().iterator(); it.hasNext(); ) {
            final String name = (String) it.next();
            final String value = (String) properties.get(name);
            set.put(new PropertySupport.ReadOnly(
                        "additional_"+name, String.class, name, ""
                        ) {
                            public Object getValue() {
                                return value;
                            }
                    });
        }
    }
    
    public VersioningFileSystem getVersioningFileSystem() {
        org.openide.filesystems.FileSystem fs;
        try {
            fs = list.getFileObject().getFileSystem();
        } catch (org.openide.filesystems.FileStateInvalidException exc) {
            fs = null;
        }
        return (VersioningFileSystem) fs;
    }
    
    public FileObject getFileObject() {
        return list.getFileObject();        
    }
    
    public SystemAction [] getActions() {
        ArrayList actions = new ArrayList();
        VersioningFileSystem vs = getVersioningFileSystem();
        //SystemAction[] revActions = vs.getRevisionActions(list.getFileObject());
        if (getCookie(OpenCookie.class) != null) {
            actions.add(SystemAction.get(OpenAction.class));
        }
        actions.add(SystemAction.get(VSRevisionAction.class));
        /*
        if (revActions != null && revActions.length > 0) {
            actions.add(null);
            actions.addAll(Arrays.asList(revActions));
        }
         */
        //actions.add(SystemAction.get(org.netbeans.modules.vcscore.revision.RevisionAction.class)); //new RevisionAction(fs, list.getFileObject()));
        //actions.add(null);
        actions.add(SystemAction.get(PropertiesAction.class));
        SystemAction[] array = new SystemAction [actions.size()];
        actions.toArray(array);
        return array;
    }
    
    /*
    public void open() {
        org.openide.util.RequestProcessor.postRequest(new Runnable() {
            public void run() {
                VersioningFileSystem vs = getVersioningFileSystem();
                //RevisionAction action = new RevisionAction(fs, getFileObject());
                //RevisionAction.openAction(item.getRevisionVCS(), vs, getFileObject());
                FileObject fo = getFileObject();
                final VersioningEditorSupport.VersioningEnvironment env = new VersioningEditorSupport.VersioningEnvironment(fo, item.getRevisionVCS());
                final VersioningEditorSupport editor = new VersioningEditorSupport(env);
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        editor.open();
                    }
                });
            }
        });
    }
     */
    
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String propertyName = propertyChangeEvent.getPropertyName();
        if (RevisionItem.PROP_CURRENT_REVISION.equals(propertyName)) {
            refreshIcons();
        } else if (RevisionItem.PROP_DISPLAY_NAME.equals(propertyName)) {
            setDisplayName(item.getDisplayName());
        } else if (propertiesSet != null) {
            if (RevisionItem.PROP_REVISION.equals(propertyName)) {
                propertiesSet.put(propertyRevision);
            } else if (RevisionItem.PROP_MESSAGE.equals(propertyName)) {
                propertiesSet.put(propertyMessage);
            } else if (RevisionItem.PROP_DATE.equals(propertyName)) {
                propertiesSet.put(propertyDate);
            } else if (RevisionItem.PROP_AUTHOR.equals(propertyName)) {
                propertiesSet.put(propertyAuthor);
            } else if (RevisionItem.PROP_LOCKER.equals(propertyName)) {
                propertiesSet.put(propertyLocker);
            } else if (RevisionItem.PROP_TAGS.equals(propertyName)) {
                propertiesSet.put(propertyTags);
            } else if (RevisionItem.PROP_ADDITIONAL_PROPERTIES.equals(propertyName)) {
                //set.put(propertyRevision);
            }
        }
    }
    
    private String g(String resource) {
        return org.openide.util.NbBundle.getBundle(RevisionNode.class).getString(resource);
    }
    
}
