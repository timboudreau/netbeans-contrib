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

package org.netbeans.modules.vcscore.ui.fsmanager;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.ResourceBundle;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSRegistryEvent;
import org.netbeans.modules.vcscore.registry.FSRegistryListener;

import org.openide.nodes.Children;
import org.openide.nodes.*;
import org.openide.cookies.FilterCookie;
import org.openide.filesystems.*;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataFilter;
import org.openide.util.WeakListener;


/** Implements children. Nodes representing registered filesystems.
 * 
 * @author Richard Gregor
 */
public class VcsChildren extends Children.Keys implements FSRegistryListener, Runnable {    

    private FSRegistry registry;
    
    /** Create pattern children. The children are initilay unfiltered.
     * @param elemrent the atteached class. For this class we recognize the patterns 
     */ 

    public VcsChildren() {
        super();
        debug("VcsChildren Init");
        registry = FSRegistry.getDefault();
        // Add registry listener
        registry.addFSRegistryListener((FSRegistryListener) WeakListener.create(FSRegistryListener.class, this, registry));      
    }

    /** Called when the preparation of nodes is needed
     */
    protected void addNotify() {        
        setKeys (getVcssFileSystems());        
    }

    /** Called when all children are garbage collected */
    protected void removeNotify() {
        setKeys( java.util.Collections.EMPTY_SET );
    }
    
    /** Creates nodes for given key.
     */
    protected Node[] createNodes( final Object key ) {
        debug("create nodes");
        
        FSInfo info = (FSInfo)key; 
        FSInfoBeanNode node = null;
        try{
            node = new FSInfoBeanNode(info);            
        }catch(Exception e){
        }
        return new Node[] { node };
    }
    
    private Collection getVcssFileSystems() {           
        debug("getVcsFileSystems");
        ArrayList vcsFileSystems = new ArrayList();
        FSInfo info[] = registry.getRegistered();         
        for(int i = 0; i < info.length; i++){  
            vcsFileSystems.add(info[i]);
        }
        return vcsFileSystems;
    }

    /** Called when a new filesystem information is added.
     *
     */
    public void fsAdded(FSRegistryEvent ev) {
        resetKeys();
    }
    
    /** Called when a filesystem information is removed.
     *
     */
    public void fsRemoved(FSRegistryEvent ev) {
        resetKeys();
    }
    
    /**
     * Dispatch the refresh of children keys into AWT thread to avoid deadlock.
     */
    private final void resetKeys() {
        javax.swing.SwingUtilities.invokeLater(this);
    }
    

    /**
     * Refresh the children keys. This is implemented as runnable, for
     * convenient dispatch into AWT.
     */
    public void run() {
        setKeys(getVcssFileSystems());
    }
    
    static class FSInfoBeanNode extends BeanNode implements PropertyChangeListener {
        private FSInfo info;
        
        public FSInfoBeanNode(FSInfo info) throws IntrospectionException{
            super(info);
            this.info = info;
            setName(info.getFSRoot().toString());
            setDisplayName(info.getFSRoot().toString());
            info.addPropertyChangeListener(WeakListener.propertyChange(this, info));
        }
        
        /** Finds an icon for this node. The filesystem's icon is returned.
         * @see java.bean.BeanInfo
         * @see org.openide.filesystems.FileSystem#getIcon
         * @param type constants from <CODE>java.bean.BeanInfo</CODE>
         * @return icon to use to represent the bean
         */
        public Image getIcon(int type) {
            Image icon = info.getIcon();
            return icon==null ? super.getIcon(type) : icon;
            /*
            FileSystem fileSystem = info.getFileSystem();
            Class klass = fileSystem.getClass();
            BeanInfo bi = null;
            try {
                bi = org.openide.util.Utilities.getBeanInfo(fileSystem.getClass());
            } catch (IntrospectionException e) {
                return super.getIcon(type);
            }
            Image icon =  bi.getIcon(type);
            return icon==null ? super.getIcon(type) : icon;
             */
        }
        
        public FSInfo getInfo(){
            return info;
        }
        
        /** This method gets called when an info property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *   	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (FSInfo.PROP_ROOT.equals(evt.getPropertyName())) {
                setName(info.getFSRoot().toString());
                setDisplayName(info.getFSRoot().toString());
            }
        }
        
    }
        
            
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("VcsChildren: "+msg);
    }
    
}
