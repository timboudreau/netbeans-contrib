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
package org.netbeans.modules.loaderswitcher;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;


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
final class ObjectType extends JPanel
implements DataLoader.RecognizedFiles, java.beans.PropertyChangeListener,
ExplorerManager.Provider {
    /** dd we are included in */
    private DialogDescriptor dd;
    /** associated explorer manager */
    private ExplorerManager em = new ExplorerManager();


    private static Logger LOG = Logger.getLogger(ObjectType.class.getName());
    
    /** Creates the components to allow choice of a loader for given
     * object.
     * @param obj the object to choose data for
     */
    private ObjectType(DataObject obj) {
        setLayout(new BorderLayout());

        DataLoader[] arr = findPossibleLoaders (obj, this);
        
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
        
        add (BorderLayout.CENTER, new ListView ());
        
        getExplorerManager ().addPropertyChangeListener(this);
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
            java.awt.Dialog d = org.openide.DialogDisplayer.getDefault ().createDialog (dd);


            d.setVisible(true);

            if (dd.getValue () == DialogDescriptor.OK_OPTION) {
                Node n = reg.getExplorerManager ().getSelectedNodes ()[0];
                org.openide.cookies.InstanceCookie ic = (org.openide.cookies.InstanceCookie)n.getCookie (
                    org.openide.cookies.InstanceCookie.class
                );

                DataLoader pref = (DataLoader)ic.instanceCreate ();
                LOG.fine("pref: " + pref);
                convertTo(obj, pref);
                return;
            }
            if (dd.getValue () == def) {
                // clear prefered loader
                DataLoaderPool.setPreferredLoader (obj.getPrimaryFile (), null);
                obj.setValid (false);
                return;
            }
        } catch (ClassNotFoundException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
        } catch (java.io.IOException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
        } catch (java.beans.PropertyVetoException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
        }
    }

    static void convertTo(final DataObject obj, final DataLoader pref) throws DataObjectNotFoundException, IOException, PropertyVetoException {
        DataLoaderPool.setPreferredLoader (
            obj.getPrimaryFile (), pref
        );
        obj.setValid (false);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("obj: " + obj.isValid ());
            LOG.fine("new: " + DataObject.find (obj.getPrimaryFile()));
        }
    }

    /** Computes the list of DataLoaders that are able to recognize given data object.
     * @param obj the object to check
     * @return list of loaders (first is the current that recognize the object)
     */
    static DataLoader[] findPossibleLoaders (DataObject obj, DataLoader.RecognizedFiles rec) {
        DataLoaderPool pool = DataLoaderPool.getDefault ();
        
        ArrayList recognize = new ArrayList ();
        recognize.add (obj.getLoader ());
        
        DataLoader l = null;
        Enumeration en = pool.allLoaders ();
        while (en.hasMoreElements ()) {
            l = (DataLoader)en.nextElement ();
            try {
                DataObject newobj = l.findDataObject (obj.getPrimaryFile(), rec);
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

    public ExplorerManager getExplorerManager() {
        return em;
    }
    
}
