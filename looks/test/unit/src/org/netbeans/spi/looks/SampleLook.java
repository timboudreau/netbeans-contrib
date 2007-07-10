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

package org.netbeans.spi.looks;

import java.beans.*;
import java.util.List;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.Collection;
import javax.swing.Action;
import java.awt.Component;

import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.Lookup;
import org.netbeans.spi.looks.*;

/** Class used for testing. It takes the values from the SampleRepObject.
 *
 *
 * @author Petr Hrebejk
 */
public class SampleLook extends Look {
            

    private SampleListener listener = new SampleListener();
    
    private GoldenValue goldenValues[] = null;

    public SampleLook( String name ) {
        super( name );
    }

    public SampleLook( String name, GoldenValue[] goldenValues ) {
        this( name );
        this.goldenValues = goldenValues;
    }
        
    // Methods for the look itself ---------------------------------------------

    /** The human presentable name of the look.
     * @return human presentable name
     */
    public String getDisplayName() {
        return getName() + " - DisplayName";
    }
    
    public void attachTo(Object representedObject) {
        
        Lookup lookup = null;
        
        if ( representedObject instanceof SampleRepObject ) {
            // Remember that attachTo was called
            ((SampleRepObject)representedObject).attach();
         
            
            ((SampleRepObject)representedObject).addPropertyChangeListener( listener );
        }
        else if ( goldenValues != null ) {
            lookup = (Lookup)GoldenValue.get( ProxyLook.GET_LOOKUP_ITEMS, goldenValues );
        }
        
    }
    
    public void detachFrom( Object representedObject ) {
        if ( representedObject instanceof SampleRepObject ) {
            // Remember that attachTo was called
            ((SampleRepObject)representedObject).detach(); // Mark that detach was called
            ((SampleRepObject)representedObject).removePropertyChangeListener( listener ); // Remove the listener
        }
    }
    
    // Methods for FUNCTIONALITY EXTENSIONS ------------------------------------
    
    public Collection getLookupItems(Object representedObject, Lookup oldEnv ) {
        if ( goldenValues != null ) {
            return (Collection)GoldenValue.get( ProxyLook.GET_LOOKUP_ITEMS, goldenValues );
        }
        else {
            return (Collection)((SampleRepObject)representedObject).getValue( ProxyLook.GET_LOOKUP_ITEMS );
        }            
    }
    
    
    // Methods for STYLE -------------------------------------------------------
    
    public String getDisplayName(Object representedObject, Lookup env ) {
        return (String)get( ProxyLook.GET_DISPLAY_NAME, representedObject );
    }
    
    public String getName(Object representedObject, Lookup env ) {
        return (String)get( ProxyLook.GET_NAME, representedObject );
    }


    public void rename(Object representedObject, String newName, Lookup env ) {
        if ( representedObject instanceof SampleRepObject ) {
            ((SampleRepObject)representedObject).setName();
        }
    }
    
    public String getShortDescription(Object representedObject, Lookup env ) {
        return (String)get( ProxyLook.GET_SHORT_DESCRIPTION, representedObject );
    }
    
    public java.awt.Image getIcon(Object representedObject, int type, Lookup env ) {
        return (java.awt.Image)get( ProxyLook.GET_ICON, representedObject );
    }
    
    public java.awt.Image getOpenedIcon(Object representedObject, int type, Lookup env ) {
        return (java.awt.Image)get( ProxyLook.GET_OPENED_ICON, representedObject );
    }
    
    public HelpCtx getHelpCtx(Object representedObject, Lookup env ) {
        return (HelpCtx)get( ProxyLook.GET_HELP_CTX, representedObject );
    }
    
    // Methods for CHILDREN ----------------------------------------------------
    
    public List getChildObjects(Object representedObject, Lookup env ) {
        return (List)get( ProxyLook.GET_CHILD_OBJECTS, representedObject );
    }
    
    public boolean isLeaf(Object representedObject, Lookup env ) {
        return get( ProxyLook.GET_CHILD_OBJECTS, representedObject ) == null;
    }
        
    // Methods for ACTIONS & NEW TYPES -----------------------------------------
    
    public NewType[] getNewTypes(Object representedObject, Lookup env ) {
        return (NewType[])get( ProxyLook.GET_NEW_TYPES, representedObject );
    }
    
    public Action[] getActions(Object representedObject, Lookup env ) {
        return (Action[])get( ProxyLook.GET_ACTIONS, representedObject );
    }
    
    public Action[] getContextActions(Object representedObject, Lookup env ) {
        return (Action[])get( ProxyLook.GET_CONTEXT_ACTIONS, representedObject );
    }
    
    public Action getDefaultAction(Object representedObject, Lookup env ) {
        return (Action)get( ProxyLook.GET_DEFAULT_ACTION, representedObject );
    }
    
    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------
    
    public Node.PropertySet[] getPropertySets(Object representedObject, Lookup env ) {
        return (Node.PropertySet[])get( ProxyLook.GET_PROPERTY_SETS, representedObject );
    }
    
    public Component getCustomizer(Object representedObject, Lookup env ) {
        return (java.awt.Component)get( ProxyLook.GET_CUSTOMIZER, representedObject );
    }
    
    public boolean hasCustomizer(Object representedObject, Lookup env ) {
        return ((Boolean)get( ProxyLook.HAS_CUSTOMIZER, representedObject )).booleanValue();
    }
    
    // Methods for CLIPBOARD OPERATIONS ----------------------------------------
     
    public boolean canRename(Object representedObject, Lookup env ) {
        return ((Boolean)get( ProxyLook.CAN_RENAME, representedObject )).booleanValue();
    }
    
    public boolean canDestroy(Object representedObject, Lookup env ) {
        return ((Boolean)get( ProxyLook.CAN_DESTROY, representedObject )).booleanValue();
    }
    
    public boolean canCopy(Object representedObject, Lookup env ) {
        return ((Boolean)get( ProxyLook.CAN_COPY, representedObject )).booleanValue();
    }
    
    public boolean canCut(Object representedObject, Lookup env ) {
        return ((Boolean)get( ProxyLook.CAN_CUT, representedObject )).booleanValue();
    }
    
    public PasteType[] getPasteTypes(Object representedObject, Transferable t, Lookup env ) {
        return (PasteType[])get( ProxyLook.GET_PASTE_TYPES, representedObject );
    }
    
    public PasteType getDropType(Object representedObject, Transferable t, int action, int index, Lookup env ) {
        return (PasteType)get( ProxyLook.GET_DROP_TYPE, representedObject );
    }
    
    public Transferable clipboardCopy(Object representedObject, Lookup env ) throws IOException {
        return (Transferable)get( ProxyLook.CLIPBOARD_COPY, representedObject );
    }
    
    public Transferable clipboardCut(Object representedObject, Lookup env ) throws IOException {
        return (Transferable)get( ProxyLook.CLIPBOARD_CUT, representedObject );
    }
    
    public Transferable drag(Object representedObject, Lookup env ) throws IOException {
        return (Transferable)get( ProxyLook.DRAG, representedObject );
    }
          
    public void destroy(Object representedObject, Lookup env ) throws IOException {
        if ( representedObject instanceof SampleRepObject ) {
            ((SampleRepObject)representedObject).destroy();
        }
    }
        
    // Private/protected helper methods ----------------------------------------
    
    /** Returns the value by key. Most of the keys are those used in ProxyLook
     * to mark the methods, but there are some additional to test attachTo(),
     * rename() and destroy().
     */    
    protected Object get( long key, Object representedObject ) {
        
        if ( goldenValues != null ) {
            return GoldenValue.get( key, goldenValues );
        }
        
        /*
        if ( substitute instanceof Look.NodeSubstitute ) {
            substitute = ((Look.NodeSubstitute)substitute).getRepresentedObject();
        }
        */
        if ( representedObject instanceof SampleRepObject ) {
            return ((SampleRepObject)representedObject).getValue( key );
        }
        else {
            return null;
        }
    }                
        
    // Innerclasses ------------------------------------------------------------
    
    public class SampleListener implements PropertyChangeListener {
            
        // Kind of strange translation of events

        public void propertyChange(PropertyChangeEvent evt) {
            
            try {
                long ev = Long.parseLong( evt.getPropertyName() );

                fireChange( evt.getSource(), ev );                
            }
            catch ( NumberFormatException e ) {
                if ( SampleRepObject.DESTROY.equals( evt.getPropertyName() ) ) {
                    fireChange( evt.getSource(), Look.DESTROY );
                }
                else {
                    firePropertyChange( evt.getSource(), evt.getPropertyName() );
                }
            }
        }
        
    }
    
    
}
