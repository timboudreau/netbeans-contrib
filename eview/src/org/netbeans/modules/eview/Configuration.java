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
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
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
package org.netbeans.modules.eview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.eview.ControlFactory;
import org.netbeans.api.registry.AttributeEvent;
import org.netbeans.api.registry.BindingEvent;
import org.netbeans.api.registry.Context;
import org.netbeans.api.registry.ContextListener;
import org.netbeans.api.registry.SubcontextEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.WeakListeners;

/**
 * @author David Strupl
 */
public class Configuration {
    
    private static final Logger log = Logger.getLogger(Configuration.class.getName());
    private static boolean LOGGABLE = log.isLoggable(Level.FINE);

    /** Singleton instance of this class. */
    private static Map instanceCache = new HashMap();
    
    /**
     * We hold a reference to the listener for preventing
     * the garbage collection.
     */
    private Listener listener;
    
    /**
     * Prevent the listeners to be attached more than once.
     */
    private boolean listenersAttached = false;

    /**
     * Location of the configuration folder for this Configuration
     * on the system file system.
     */
    private String location;

    private ContainerEntry computedEntry;
    
    /**
     * Creates a new instance of Configuration 
     */
    private Configuration(String location) {
        this.location = location;
    }
    
    public ContainerEntry getConfig() {
        if (computedEntry == null) {
            build();
        }
        return computedEntry;
    }
    
    /**
     * Lazy creation of the singleton instance of this class.
     * NEVER keep the resulting reference for long. Always ask this method
     * if you need it.
     */
    public static Configuration getInstance(String location) {
        Configuration result = (Configuration)instanceCache.get(location);
        if (result == null) {
            result = new Configuration(location);
            instanceCache.put(location, result);
        }
        return result;
    }

    private FileObject getConfigRoot() {
        return Repository.getDefault().getDefaultFileSystem().findResource(
                location);
    }
    
    private void build() {
        long startTime = System.currentTimeMillis();
        FileObject root = getConfigRoot();
        Context con = Context.getDefault().getSubcontext(root.getPath());
        if (con == null) {
            if (LOGGABLE) log.fine("build() returning - config folder " + location + " does not exist.");
            return;
        }
        if (!listenersAttached) {
            ContextListener l1 = getContextListener(con);
            con.addContextListener(l1);
        }
        ContainerEntry res = new ContainerEntry();
        scanFolder(root, res);
        if (LOGGABLE) {
            long finishTime = System.currentTimeMillis();
            log.fine("Configuration building has taken " + (finishTime - startTime));
            log.fine(this.toString());
        }
        computedEntry = res;
    }
    
    /**
     * Creates part of the cache by traversing the given folder
     * and creating config entries.
     */
    private void scanFolder(FileObject folder, ContainerEntry container) {
        if (LOGGABLE) log.fine("scanFolder(" + folder.getPath() + ") START");
        container.displayName = folder.getName();
        try {
            container.displayName = folder.getFileSystem ().getStatus ().annotateName(folder.getName(), Collections.singleton(folder));
        } catch (Exception x) {
            log.log(Level.WARNING, container.displayName, x);
        }
        container.entries = new ArrayList();
        Object rowsAttr = folder.getAttribute("rows");
        if (rowsAttr != null) {
            container.rows = ((Integer)rowsAttr).intValue();
        }
        Object columnsAttr = folder.getAttribute("columns");
        if (columnsAttr != null) {
            container.columns = ((Integer)columnsAttr).intValue();
        }
        Object labelFormat = folder.getAttribute("labelFormat");
        if (labelFormat instanceof String) {
            container.labelFormat = (String)labelFormat;
        }
        // in order to get the order we need Registry API:
        Context con = Context.getDefault().getSubcontext(folder.getPath());
        List orderedNames = con.getOrderedNames();
        for (Iterator it = orderedNames.iterator(); it.hasNext();) {
            String name = (String) it.next();
            if (LOGGABLE) log.fine("scanFolder checking " + name);
            if (name.endsWith("/")) {
                name = name.substring(0, name.length()-1);
            }
            FileObject child = folder.getFileObject(name);
            String []extensions = { "instance", "ser", "setting", "xml", "shadow" };
            int extNum = 0;
            while ((child == null) && (extNum < extensions.length)) {
                child = folder.getFileObject(name, extensions[extNum++]);
            }
            if (child == null) {
                log.fine("child == null: Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (! child.isValid()) {
                log.fine("!child.isValid(): Registry returned an invalid name " + name + " in folder " + folder.getPath());
                continue;
            }
            if (child.isData()) {
                String ext = child.getExt();
                String componentID = (String)child.getAttribute("componentID");
                if (componentID == null) {
                    log.fine("File " + child.getPath() + " is missing the componentID attribute");
                    continue;
                }
                if (LOGGABLE) log.fine("adding result with ID " + componentID);
                ControlEntry e = new ControlEntry();
                e.id = componentID;
                Object c = con.getObject(name, null);
                if (c instanceof ControlFactory) {
                    e.control = (ControlFactory)c;
                } else {
                    log.fine("Invalid control " + name + " in folder " + folder.getPath()+ " expected Control but was " + c);
                    continue;
                }
                e.label = (String)child.getAttribute("label");
                e.labelBundle = (String)child.getAttribute("labelBundle");
                Object lac = child.getAttribute("labelAbove");
                if (lac instanceof Boolean) {
                    e.labelAboveControl = ((Boolean)lac).booleanValue();
                }
                container.entries.add(e);
            }
            if (child.isFolder()) {
                Object a1 = child.getAttribute("createHandle");
                if ((a1 instanceof Boolean) && ((Boolean)a1).booleanValue()) {
                    ContainerEntry c = new ContainerEntry();
                    scanFolder(child, c);
                    container.entries.add(c);
                }
            }
        }
        if (LOGGABLE) log.fine("scanFolder(" + folder.getPath() + ") END");
    }
    
    /**
     * Lazy initialization of the listener variable. This method
     * will return a weak listener.
     * The weak listener references the object hold
     * by the <code> listener </code> variable.
     */
    private ContextListener getContextListener(Object source) {
        if (listener == null) {
            listener = new Listener();
        }
        return (ContextListener)WeakListeners.create(ContextListener.class, listener, source);
    }
    
    /**
     * Whatever happens in the selected context this listener only clears
     * the actions reference. This cause the list of actions to
     * be computed next time someone asks for them.
     */
    private class Listener implements ContextListener {
        public void attributeChanged(AttributeEvent evt) {
            if (LOGGABLE) log.fine("attributeChanged("+evt+") called on listener from " + Configuration.this);
            computedEntry = null;
        }
        
        public void bindingChanged(BindingEvent evt) {
            if (LOGGABLE) log.fine("bindingChanged("+evt+") called on listener from " + Configuration.this);
            computedEntry = null;
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            if (LOGGABLE) log.fine("subcontextChanged("+evt+") called on listener from " + Configuration.this);
            computedEntry = null;
        }
    }

    public static class ControlEntry {
        public String id;
        public String label;
        public String labelBundle;
        public boolean labelAboveControl = false;
        public int x;
        public int y;
        public ControlFactory control;
        
        public String toString() {
            return "ControlEntry[" + id + ", " + label + ", " + x + ", " + y + ", " + control + "]";
        }
    }
    
    public static class ContainerEntry {
        public String displayName;
        public String labelFormat;
        public int rows;
        public int columns;
        public List /*<ContainerEntry>*/entries;
        public String toString() {
            return "ContainerEntry[" + displayName + ", " + rows + ", " + columns + ", " + entries + "]";
        }
    }
}
