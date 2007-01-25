/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.netbeans.api.java.source.query.Query;
import org.netbeans.api.java.source.transform.Transformer;
import org.netbeans.modules.jackpot.ui.RefactoringManagerPanel;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

/**
 * The list of all inspections, as stored in the system filesystem in
 * /Jackpot/Inspections/.
 */
public class InspectionsList extends AbstractListModel {
    DataFolder inspectionsRoot;
    List<DataObject> commands;
    private static InspectionsList instance;
    
    public static InspectionsList instance() {
        if (instance == null)
            instance = new InspectionsList();
        return instance;
    }
    
    private InspectionsList() {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("/Jackpot/Inspections"); // NOI18N
        inspectionsRoot = DataFolder.findFolder(fo);
        updateInspectionList();
        inspectionsRoot.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateInspectionList();
            }
        });
    }
    
    public Inspection create(String query, String refactoring, String description) throws IOException {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("/Templates/Jackpot/Rules.rules"); // NOI18N
        DataObject template = DataObject.find(fo);
        DataObject dao = template.createFromTemplate(inspectionsRoot, query);
        return add(dao, query, refactoring, description);
    }
    
    public Inspection importRuleFile(String path) throws IOException {
        DataObject dao = JackpotCommand.create(path);
        return add(dao, dao.getName(), "", "");
    }
    
    private Inspection add(DataObject dao, String query, String refactoring, String description) throws IOException {
        Inspection insp = new Inspection(dao);
        int pos = commands.size();
        insp.setAttribute("inspector", query, false);
        insp.setAttribute("transformer", refactoring, false);
        insp.setAttribute("description", description, false);
        synchronized(commands) {
            commands.add(insp.getDataObject());
            inspectionsRoot.setOrder(commands.toArray(new DataObject[0]));
        }
        fireIntervalAdded(this, pos, pos);
        return insp;
        
    }
    
    public synchronized Inspection[] getInspections() {
        Inspection[] inspections = new Inspection[size()];
        for (int i = 0; i < inspections.length; i++)
            inspections[i] = new Inspection(commands.get(i));
        return inspections;
    }
    
    public int size() {
        return commands.size();
    }
    
    public synchronized int indexOf(Inspection inspection) {
        FileObject fo = inspection.getFileObject();
        int n = size();
        for (int i = 0; i < n; i++) {
            if (fo == commands.get(i).getPrimaryFile())
                return i;
        }
        return -1;
    }
    
    public synchronized String[] getInspectorNames() {
        String[] names = new String[size()];
        for (int i = 0; i < names.length; i++)
            names[i] = getAttribute(commands.get(i), "inspector");
        return names;
    }
    
    public synchronized Inspection getInspection(String inspectorName) {
        for (DataObject dobj : commands)
            if (inspectorName.equals(getAttribute(dobj, "inspector")))
                return new Inspection(dobj);
        return null;
    }
    
    synchronized Inspection getInspectionByFilename(String fileName) {
        for (DataObject dobj : commands)
            if (fileName.equals(dobj.getPrimaryFile().getName()) ||
                fileName.equals(dobj.getPrimaryFile().getNameExt()))
                return new Inspection(dobj);
        return null;
    }
    
    public Inspection getInspection(int index) {
        return new Inspection(commands.get(index));
    }
    
    public void inspectionUpdated(int index) {
        fireContentsChanged(this, index, index);
    }
    
    private String getAttribute(DataObject dobj, String attribute) {
        FileObject fo = dobj.getPrimaryFile();
        return (String)fo.getAttribute(attribute); // NOI18N
    }
    
    void setAttribute(DataObject dobj, String attribute, String value) {
        try {
            FileObject fo = dobj.getPrimaryFile();
            fo.setAttribute(attribute, value);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
    
    public DataFolder getInspectionsFolder() {
        return inspectionsRoot;
    }
    
    private DataObject get(Inspection inspection) {
        int pos = indexOf(inspection);
        return commands.get(pos);
    }
    
    public void moveUp(Inspection inspection) {
        int pos = indexOf(inspection);
        DataObject dobj = commands.get(pos);
        if (pos > 0) 
            try {
                commands.remove(pos);
                commands.add(pos-1, dobj);
                inspectionsRoot.setOrder(commands.toArray(new DataObject[0]));
                fireContentsChanged(this, pos-1, pos);
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
    }
    
    public void moveDown(Inspection inspection) {
        int pos = indexOf(inspection);
        DataObject dobj = commands.get(pos);
        if (pos < commands.size() - 1)
            try {
                commands.remove(pos);
                commands.add(pos+1, dobj);
                inspectionsRoot.setOrder(commands.toArray(new DataObject[0]));
                fireContentsChanged(this, pos, pos+1);
            } catch (Exception e) {
                ErrorManager.getDefault().notify(e);
            }
    }
    
    public void delete(Inspection[] nodes) {
	for (int i = 0; i < nodes.length; i++) {
	    try {
                int pos = indexOf(nodes[i]);
                commands.remove(pos);
                nodes[i].getFileObject().delete();
                fireIntervalRemoved(this, pos, pos);
	    } catch (IOException ioe) {
		ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
	    }
	}
    }

    private void updateInspectionList() {
        Vector<DataObject> vec = new Vector<DataObject>();
        for (DataObject dobj : inspectionsRoot.getChildren())
            if (isJackpotCommandDataObject(dobj))
                vec.add(dobj);
        synchronized(this) {
            commands = vec;
        }
    }
    
    static private boolean isJackpotCommandDataObject(DataObject dobj) {
	try {
	    InstanceCookie ic = (InstanceCookie)dobj.getCookie(InstanceCookie.class);
	    if (ic != null) {
		Class c = ic.instanceClass();
		return c.equals(JackpotCommand.class) ||
                       c.equals(Query.class) || c.equals(Transformer.class);
	    }
	    return false;
	} catch (Exception e) {
	    return false;
	}
    }

    public Object getElementAt(int index) {
        return new Inspection(commands.get(index));
    }

    public int getSize() {
        return size();
    }
}
