/*
 * ProjectListModel.java
 *
 * Created on May 29, 2004, 2:31 PM
 */

package org.netbeans.modules.packager.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.packager.PackagerProject;
import org.netbeans.spi.project.SubprojectProvider;

/**
 * List model which shows projects and their subprojects.
 *
 * @author  Tim Boudreau
 */
public class ProjectListModel implements ListModel {
    private ArrayList listeners = new ArrayList();
    private ArrayList projects = new ArrayList();
    private ArrayList data = new ArrayList();
    
    /** Creates a new instance of ProjectListModel */
    public ProjectListModel() {
    }
    
    public synchronized void add (Project p) {
        projects.add(p);
        sync();
    }
    
    public synchronized void remove (Project p) {
        projects.remove(p);
        sync();
    }
    
    public Project[] getProjects() {
        Project[] p = new Project[projects.size()];
        p = (Project[]) projects.toArray(p);
        return p;
    }
    
    public Project[] getAllProjects() {
        Project[] p = new Project[data.size()];
        p = (Project[]) data.toArray(p);
        return p;
    }
    
    public boolean hasExecutableProject() {
        boolean result = false;
        for (Iterator i=projects.iterator(); i.hasNext();) {
            result = isExecutable((Project) i.next());
            if (result) break;
        }
        return result;
    }
    
    public synchronized void remove (int idx) {
        int pidx = projects.indexOf(data.get(idx));
        if (pidx != -1) {
            remove ((Project) data.get(idx));
        } else {
            throw new IllegalArgumentException (idx + " must refer to a subproject");
        }
    }
    
    public synchronized boolean isDependency (Project p) {
        return !projects.contains(p);
    }
    
    public boolean isExecutable (Project p) {
        return PackagerProject.findMainClass(p) != null;
    }
    
    public boolean isExecutable (int idx) {
        return isExecutable ((Project) data.get(idx));
    }
    
    public boolean isDependency (int idx) {
        return isDependency ((Project) data.get(idx));
    }
    
    private void sync() {
        data.clear();
        for (Iterator i=projects.iterator(); i.hasNext();) {
            Project p = (Project) i.next();
            if (!data.contains(p)) {
                data.add (p);
                installDeps (p, data);
            }
        }
        ListDataEvent lde = new ListDataEvent (this, ListDataEvent.CONTENTS_CHANGED,0, data.size());
        fire(lde);
    }
    
    private void installDeps (Project p, List data) {
        SubprojectProvider prov = (SubprojectProvider) p.getLookup().lookup(SubprojectProvider.class);
        if (prov != null) {
            Set s = prov.getSubprojects();
            if (!s.isEmpty()) {
                for (Iterator i=s.iterator(); i.hasNext();) {
                    Project sub = (Project) i.next();
                    if (!data.contains(sub)) {
                        data.add (sub);
                        installDeps (sub, data);
                    }
                }
            }
        }
    }
    
    private synchronized void fire (ListDataEvent lde) {
        for (Iterator i=listeners.iterator(); i.hasNext();) {
            ListDataListener ldl = (ListDataListener) i.next();
            ldl.contentsChanged(lde);
        }
    }
    
    public Object getElementAt(int param) {
        return data.get(param);
    }
    
    public int getSize() {
        return data.size();
    }

    public synchronized void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
}
