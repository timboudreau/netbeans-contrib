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
package org.netbeans.modules.loaderswitcher;

import java.util.Enumeration;
import javax.swing.JComponent;


import org.openide.DialogDescriptor;
import org.openide.filesystems.*;
import org.openide.explorer.*;
import org.openide.explorer.view.*;
import org.openide.loaders.*;
import org.openide.nodes.*;
import java.util.ArrayList;

/**
 *
 * @author  Jaroslav Tulach
 */
final class ObjectType extends ExplorerPanel implements DataLoader.RecognizedFiles, java.beans.PropertyChangeListener {
    /** dd we are included in */
    private DialogDescriptor dd;
    
    /** Creates the components to allow choice of a loader for given
     * object.
     * @param obj the object to choose data for
     */
    private ObjectType(DataObject obj) {
        DataLoader[] arr = findPossibleLoaders (obj);
        
        Node[] nodes = new Node[arr.length];
        for (int i = 0; i < arr.length; i++) {
            try {
                nodes[i] = new BeanNode (arr[i]);
            } catch (java.beans.IntrospectionException ex) {
                nodes[i] = Node.EMPTY.cloneNode ();
            }
        }
        
        // all loaders
        Node root = new AbstractNode (new Children.Array ());
        root.getChildren ().add (nodes);
        
        getExplorerManager ().setRootContext (root);
        getExplorerManager ().setExploredContext(root, new Node[] { nodes[0] });
        
        add (java.awt.BorderLayout.CENTER, new ListView ());
        
        getExplorerManager ().addPropertyChangeListener(this);
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        FileObject fo = Repository.getDefault ().findResource (args[0]);
        
        DataObject obj = DataObject.find (fo);
        convert (obj);
    }
        
    /** Does conversion of a data object to new values.
     */
    public static void convert (DataObject obj) {
        try {
            ObjectType reg = new ObjectType (obj);

            javax.swing.JButton def = new javax.swing.JButton (org.openide.util.NbBundle.getMessage(ObjectType.class, "Default"));
            def.setEnabled (DataLoaderPool.getPreferredLoader (obj.getPrimaryFile ()) != null);

            DialogDescriptor dd = new DialogDescriptor (reg, org.openide.util.NbBundle.getMessage(ObjectType.class, "Choose_a_type_of_this_object"));
            reg.dd = dd;

            Object[] options = { DialogDescriptor.OK_OPTION, def, DialogDescriptor.CANCEL_OPTION };
            dd.setOptions (options);
            dd.setClosingOptions (options);
            java.awt.Dialog d = org.openide.TopManager.getDefault ().createDialog (dd);


            d.show ();

            if (dd.getValue () == DialogDescriptor.OK_OPTION) {
                Node n = reg.getExplorerManager ().getSelectedNodes ()[0];
                org.openide.cookies.InstanceCookie ic = (org.openide.cookies.InstanceCookie)n.getCookie (
                    org.openide.cookies.InstanceCookie.class
                );

                DataLoader pref = (DataLoader)ic.instanceCreate ();
                System.out.println("pref: " + pref);

                DataLoaderPool.setPreferredLoader (
                    obj.getPrimaryFile (), pref
                );
                obj.setValid (false);
                System.out.println("obj: " + obj.isValid ());
                System.out.println("new: " + DataObject.find (obj.getPrimaryFile()));
                return;
            }
            if (dd.getValue () == def) {
                // clear prefered loader
                DataLoaderPool.setPreferredLoader (obj.getPrimaryFile (), null);
                obj.setValid (false);
                return;
            }
        } catch (ClassNotFoundException ex) {
            org.openide.TopManager.getDefault ().notifyException(ex);
        } catch (java.io.IOException ex) {
            org.openide.TopManager.getDefault ().notifyException(ex);
        } catch (java.beans.PropertyVetoException ex) {
            org.openide.TopManager.getDefault ().notifyException(ex);
        }
    }

    /** Computes the list of DataLoaders that are able to recognize given data object.
     * @param obj the object to check
     * @return list of loaders (first is the current that recognize the object)
     */
    private DataLoader[] findPossibleLoaders (DataObject obj) {
        DataLoaderPool pool = org.openide.TopManager.getDefault ().getLoaderPool ();
        
        ArrayList recognize = new ArrayList ();
        recognize.add (obj.getLoader ());
        
        DataLoader l = null;
        Enumeration en = pool.allLoaders ();
        while (en.hasMoreElements ()) {
            l = (DataLoader)en.nextElement ();
            try {
                DataObject newobj = l.findDataObject (obj.getPrimaryFile(), this);
                if (newobj == obj) {
                    continue;
                }
                
                if (newobj != null) {
                    throw new IllegalStateException ("Object created for: " + newobj + " for: " + obj); // NOI18N
                }
            } catch (DataObjectExistsException ex) {
                recognize.add (l);
            } catch (java.io.IOException ex) {
                // does not recognize
            }
        }
        
        // the last one is default data loader and it can recognize anything
        if (l != null && l != obj.getLoader()) {
            recognize.add (l);
        }

        return (DataLoader[])recognize.toArray (new DataLoader[0]);
    }
    
    /** Implements DataLoader.RecognizedFiles inteface, but does nothing.
     */
    public void markRecognized(FileObject fo) {
    }    
    
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
        dd.setValid (getExplorerManager ().getSelectedNodes ().length == 1);
    }
    
}
