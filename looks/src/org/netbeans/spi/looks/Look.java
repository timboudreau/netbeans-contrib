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

package org.netbeans.spi.looks;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.modules.looks.LookListener;
import org.netbeans.modules.looks.LookEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/**
 * Base class for all Looks. All methods in the class provide
 * neutral behavior (i.e. do nothing, rerurn null or false).<BR>
 * Extending this class in order to implement
 * a Look requires overriding all methods which should provide some
 * functionality. You may also consider subclassing
 * the adaptor class {@link org.netbeans.spi.looks.DefaultLook
 * DefaultLook class}, which provides basic implementations for
 * finding Icons and Actions.
 * <P>
 * Most methods take a Lookup as parameter. This lookup represents environement
 * associated with given represented object. This environement usually
 * contains data from the {@link #getLookupItems} method. However if more
 * looks cooperate on the same represented object it should contain union
 * of items provided by all cooperating looks.
 *
 * @author Petr Hrebejk
 * @see org.netbeans.spi.looks.DefaultLook
 */
public abstract class Look<T> extends Object {
    
    /** Internal mask for property change */
    private static final long PROPERTY_CHANGE = -1;
    
    /** Mask for enabling (unmasking) all methods */
    public static final long ALL_METHODS = Long.MAX_VALUE;
    /** Mask for disabling (masking) all methods */
    public static final long NO_METHODS = 0;    
    
    
    /** Mask for the method {@link org.netbeans.spi.looks.Look#destroy}. */
    public static final long DESTROY = 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#rename}. */
    public static final long RENAME = DESTROY << 1;    
    /** Mask for firing Cookie events and providing lookup items */
    public static final long GET_LOOKUP_ITEMS = RENAME << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getDisplayName}. */
    public static final long GET_DISPLAY_NAME = GET_LOOKUP_ITEMS << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getName}. */
    public static final long GET_NAME = GET_DISPLAY_NAME << 1;    
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getShortDescription}. */
    public static final long GET_SHORT_DESCRIPTION = GET_NAME << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getIcon}. */
    public static final long GET_ICON = GET_SHORT_DESCRIPTION << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getOpenedIcon}. */
    public static final long GET_OPENED_ICON = GET_ICON << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getHelpCtx}. */
    public static final long GET_HELP_CTX = GET_OPENED_ICON << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getChildObjects}. */
    public static final long GET_CHILD_OBJECTS = GET_HELP_CTX << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getNewTypes}. */
    public static final long GET_NEW_TYPES = GET_CHILD_OBJECTS << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getActions}. */
    public static final long GET_ACTIONS = GET_NEW_TYPES << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getContextActions}. */
    public static final long GET_CONTEXT_ACTIONS = GET_ACTIONS << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getDefaultAction}. */
    public static final long GET_DEFAULT_ACTION = GET_CONTEXT_ACTIONS << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getPropertySets}. */
    public static final long GET_PROPERTY_SETS = GET_DEFAULT_ACTION << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getCustomizer}. */
    public static final long GET_CUSTOMIZER = GET_PROPERTY_SETS << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#canRename}. */
    public static final long CAN_RENAME = GET_CUSTOMIZER << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#canDestroy}. */
    public static final long CAN_DESTROY = CAN_RENAME << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#canCopy}. */
    public static final long CAN_COPY = CAN_DESTROY << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#canCut}. */
    public static final long CAN_CUT = CAN_COPY << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getPasteTypes}. */
    public static final long GET_PASTE_TYPES = CAN_CUT << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#getDropType}. */
    public static final long GET_DROP_TYPE = GET_PASTE_TYPES << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#clipboardCopy}. */
    public static final long CLIPBOARD_COPY = GET_DROP_TYPE << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#clipboardCut}. */
    public static final long CLIPBOARD_CUT = CLIPBOARD_COPY << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#drag}. */
    public static final long DRAG = CLIPBOARD_CUT << 1;
    /** Mask for the method {@link org.netbeans.spi.looks.Look#hasCustomizer}. */
    public static final long HAS_CUSTOMIZER = DRAG << 1;
    
    
    private ListenerCache<T> listenerCache;

    // Programmatic name of the look

    private String name;

    static {
        // initialize module private access to this package
        org.netbeans.modules.looks.Accessor.DEFAULT = new AccessorImpl ();
    }

    // Constructors ------------------------------------------------------------

    /** Creates new instance of look does no work.
     * @param name the name to assign to the look.
     */
    protected Look( String name ) {
        this.name = name;
    }

    // Identification methods --------------------------------------------------

    /** Returns name of the look. This name should identify the look.
     * @return Name of the look.
     */
    public final String getName() {
        return name;
    }

    /** The human presentable name of the look. Default implementation
     * calls getName();
     * @return human presentable name
     */
    public String getDisplayName() {
        return getName();
    }

    public String toString() {
        return getName();
    }

    // General methods ---------------------------------------------------------

    /**
     * Overriding this method permits registering listeners on represented objects.
     * <p>
     * If given instance is used in many places (e.g. multiple views are using 
     * the look for representing the given object or when the look is used as a sublook
     * in multiple composite looks) <code>attachTo</code> will only be called once per
     * represented object.
     * <P>
     * Implementors may not wait for any other threads at it may be potentially called
     * from internal lock.
     *  
     * @param representedObject Represented object the look should work with.
     * @throws ClassCastException When the represented object is unacceptable
     *         for the look due to wrong class
     * @throws IllegalArgumentException When the represented object is
     *          unacceptable for other reason than class cast.
     *
     */
    protected void attachTo( T representedObject ) {
    }


    /**
     * This method is called when listening on represeted object is no
     * longer needed. Make sure to deregister all listeners registered
     * in {@link #attachTo} method.<BR>
     * If given instance is used in many places (e.g. more views are using 
     * the look for representing given object or when the look is used as a sublook
     * in more composite look) the detachFrom method will only be called once per
     * reprsented object.
     * <P>
     * Implementors may not wait for any other threads at it may be potentially called
     * from internal lock.
     * @param representedObject Represented object to detach from.
     */
    protected void detachFrom( T representedObject ) {
    }

    // Methods for FUNCTIONALITY EXTENSIONS ------------------------------------

    /** Allowes for adding new object into the object's environement passed
     * to other methods as the env parameter.
     * @param representedObject Parameter is ignored.
     * @param oldEnv Content of previous environement when called after a change
     *        of environemnt or is empty.
     * @return <CODE>null</CODE>
     */
    public java.util.Collection getLookupItems( T representedObject, Lookup oldEnv ) {
        return null;
    }

    // Methods for STYLE -------------------------------------------------------

    /** Gets the programmatic name of the object. This name shouldn't be
     * localized.<BR>
     * Notice that the env parameter may be empty in some cases (e.g. when
     * call to this methods is performed during serialization)
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Programmatic name of the object.
     */
    public String getName( T representedObject, Lookup env ) {
        return null;
    }

    /** Gets localized name of the object.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Localized name of the object.
     */
    public String getDisplayName( T representedObject, Lookup env ) {
        return null;
    }

    /** This method is called when the user renames the object.
     * @param representedObject Represented object to be renamed.
     * @param newName The new name set by the user.
     * @param env Environment for the represented object.
     */
    public void rename( T representedObject, String newName, Lookup env ) throws IOException {
    }

    /** Gets short description for given object. The short description is usually
     * visualized as a tooltip, but may have another forms as well.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return A localized short description of the object.
     */
    public String getShortDescription( T representedObject, Lookup env ) {
        return null;
    }

    /** Find an icon for this object (in the closed state).
     * @param representedObject Represented object the look should work with.
     * @param type Constant from {@link java.beans.BeanInfo}
     * @param env Environment for the represented object.
     * @return Icon to use to represent the object in the closed state.
     */
    public java.awt.Image getIcon( T representedObject, int type, Lookup env ) {
        return null;
    }



    /** Find an icon for this object (in the open state).
     * This icon is used when the object may have children and is expanded.
     * @param representedObject Represented object the look should work with.
     * @param type Constant from {@link java.beans.BeanInfo}
     * @param env Environment for the represented object.
     * @return Icon to use to represent the object in the open state.
     */
    public java.awt.Image getOpenedIcon( T representedObject, int type, Lookup env ) {
        return null;
    }

    /** Get context help associated with this object.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return The context help object (could be <code>null</code> or
     *         {@link org.openide.util.HelpCtx#DEFAULT_HELP})
     */
    public HelpCtx getHelpCtx( T representedObject, Lookup env ) {
        return null;
    }

    // Methods for CHILDREN ----------------------------------------------------

    /** Gets objects which are children of the represented object in the
     * hierarchy represented by this Look.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return List of objects which should be represented as node children of
     *         the node, can return <code>null</code>.
     */
    public List<?> getChildObjects( T representedObject, Lookup env ) {
        return null;
    }

    /** Decides whether given object should be a leaf.
     * I.e. if the visual
     * representation of the object should be expandable.
     * <P>
     * <B>Notice :</B> Implementation of
     * this method must be consistent with implementation of
     * {@link #getChildObjects}.
     * Notice that you can switch the form LEAF to nonLEAF node and vice versa
     * by calling {@link #fireChange} with parameter {@link #GET_CHILD_OBJECTS},
     * which in turn will call
     * <CODE>isLeaf(...)</CODE> and eventually <CODE>getChildObjects(...)</CODE>
     * where you can return new value.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return <CODE>true</CODE> if the object should be unexpandable
     *         <CODE>false</CODE> otherwise.
     */
    public boolean isLeaf( T representedObject, Lookup env ) {
        return false;
    }


    // Methods for ACTIONS & NEW TYPES -----------------------------------------

    /** Get the new types that user can create from given object.
     * For example, a a Java package will permit classes to be added.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Array of new type operations that are allowed,
     *     can return <code>null</code> that is equivalent to empty array
     */
    public NewType[] getNewTypes( T representedObject, Lookup env ) {
        return null;
    }

    /** Get the set of actions associated with the object.
     * This may be used for in constructing popup menus etc.
     * <P>
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Array of the Actions applicable to the node or <CODE>null</CODE>
     *         if actions in {@link org.openide.nodes.NodeOp#getDefaultActions()} should be used.
     */
    public Action[] getActions( T representedObject, Lookup env ) {
        return null;
    }


    /** Get a special set of actions for situations when this object is displayed
     * as a context. (E.g. right clicking in the empty area of a tree)
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Actions for a context.
     */
    public Action[] getContextActions( T representedObject, Lookup env ) {
        return null;
    }

    /** Get the default action for this object.
     * This action can but need not be one from the list returned
     * from {@link #getActions}.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Default action, or <code>null</code> if there should be none.
     */
    public Action getDefaultAction( T representedObject, Lookup env ) {
        return null;
    }

    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------

    /** Get the list of property sets for object. E.g. typically there
     * may be one for normal Bean properties, one for expert
     * properties, and one for hidden properties.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Property sets for the object, can return <code>null</code> that
     *         is equivalent to empty array
     */
    public Node.PropertySet[] getPropertySets( T representedObject, Lookup env ) {
        return null;
    }

    /** Check whether the customizer for the represented object is available.
     * If so, the method
     * getCustomizer should return non-null value.
     *
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return true if the customizer is available, false otherwise
     */
    public boolean hasCustomizer( T representedObject, Lookup env ) {
        return false;
    }

    /** Get the customizer for represented object if available.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return The component, or <CODE>null</CODE> if there is no customizer.
     */
    public Component getCustomizer( T representedObject, Lookup env ) {
        return null;
    }


    // Methods for CLIPBOARD OPERATIONS ----------------------------------------

    /** Test whether this object can be renamed.
     * If true, {@link #rename} will be called when the user changes the name
     * of the node.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return <code>true</code> if the node object be renamed.
     */
    public boolean canRename( T representedObject, Lookup env ) {
        return false;
    }

    /** Test whether this object can be deleted.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return <CODE>True</CODE> if can be deleted.
     */
    public boolean canDestroy( T representedObject, Lookup env ) {
        return false;
    }

    /** Test whether this object permits copying.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return <code>True</code> if so.
     */
    public boolean canCopy( T representedObject, Lookup env ) {
        return false;
    }

    /** Test whether this object permits cutting.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return <code>True</code> if so.
     */
    public boolean canCut( T representedObject, Lookup env ) {
        return false;
    }

    /** Determine which paste operations are allowed when a given
     * transferable is in the clipboard. For example, a
     * Java package will permit classes to be pasted into it.
     * @param representedObject Represented object the look should work with.
     * @param t The transferable in the clipboard.
     * @param env Environment for the represented object.
     * @return Array of operations that are allowed, can return <code>null</code>
     *         that is equivalent to empty array
     */
    public PasteType[] getPasteTypes( T representedObject, Transferable t, Lookup env ) {
        return null;
    }

    /** Determine if there is a paste operation that can be performed
     * on provided transferable. Used by drag'n'drop code to check
     * whether the drop is possible.
     * @param representedObject Represented object the look should work with.
     * @param t The transferable.
     * @param action The drag'n'drop action to do DnDConstants.ACTION_MOVE,
     *        ACTION_COPY, ACTION_LINK.
     * @param index Index between children the drop occured at or -1 if not specified.
     * @param env Environment for the represented object.
     * @return <CODE>Null</CODE> if the transferable cannot be accepted or the paste type
     *         to execute when the drop occurs.
     */
    public PasteType getDropType( T representedObject, Transferable t, int action, int index, Lookup env ) {
        return null;
    }

    /** Called when a object is to be copied to the clipboard.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return The transferable object representing the content of the clipboard.
     * @throws java.io.IOException When the copy cannot be performed.
     */
    public Transferable clipboardCopy( T representedObject, Lookup env ) throws IOException {
        return null;
    }

    /** Called when the object is to be cut to the clipboard.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return The transferable object representing the content of the clipboard.
     * @throws java.io.IOException When the copy cannot be performed.
     */
    public Transferable clipboardCut( T representedObject, Lookup env ) throws IOException {
        return null;
    }

    /** Called when a drag is started with this object.
     * The object can attach a transfer listener to ExTransferable and
     * will be then notified about progress of the drag (accept/reject).
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @return Transferable to represent this object during a drag.
     * @throws java.io.IOException If a drag cannot be started.
     */
    public Transferable drag( T representedObject, Lookup env ) throws IOException {
        return null;
    }

    /** Called when object was destroyed.
     * @param representedObject Represented object the look should work with.
     * @param env Environment for the represented object.
     * @throws java.io.IOException If reflecting the destroy action in underlying data
     *         fails for some reason.
     */
    public void destroy( T representedObject, Lookup env ) throws IOException {
    }

    // Event firing ------------------------------------------------------------
    
    /** Notifies all listeners attached to the representedObject that
     * result(s) of some method(s) changed. The mask patameter contains
     * bit mask of the methods.
     *
     * @param representedObject the object that has changed
     * @param mask Bit mask of methods which's result changed. See {@link Look}
     *        for the constants
     * 
     */
    protected final void fireChange( T representedObject, long mask ) {
        fireUniversal( mask, representedObject, null );
    }
    
    /** Notifies all listeners attached to the representedObject that
     * a property in the PropertySets changed it's value.
     *
     * @param representedObject the object that has changed
     * @param propertyName Name of the property
     */
    protected final void firePropertyChange( T representedObject, String propertyName ) {
        fireUniversal( PROPERTY_CHANGE, representedObject, propertyName );
    }

    // Registering look listeners ----------------------------------------------
    
    synchronized void addLookListener( T representedObject, LookListener listener ) {
                
        if ( representedObject != null && 
            ( listenerCache == null ||  
              listenerCache.getListenersCount( representedObject ) == 0 ) ) {
            attachTo( representedObject );
        }
                
        if ( listenerCache == null ) {
            listenerCache = new ListenerCache<T>();
        }            
        
        listenerCache.addListener( representedObject, listener );           

    }
    
    synchronized void changeLookListener( T representedObject, LookListener oldListener, LookListener newListener ) {
        if ( listenerCache != null ) {            
            listenerCache.changeListener( representedObject, oldListener, newListener );
        }    
    }

    synchronized void removeLookListener( T representedObject, LookListener listener ) {
                
        if ( representedObject != null && listenerCache != null &&
             listenerCache.getListenersCount( representedObject ) == 1 ) {        
            detachFrom( representedObject );
        }
                
        if ( listenerCache != null /* && listener != null */ ) {
            listenerCache.removeListener( representedObject, listener );
        }        
    }

    // Package private methods -------------------------------------------------
    
    /** Gets all objects which have some object registred. Used from look and
     * to fire on all objects and from ProxyLook to fire when selector change
     * occurs. It returns copy of the cache.
     */
    synchronized T[] getAllObjects() {
        Collection<T> allObjects = listenerCache.getAllObjects();
        @SuppressWarnings("unchecked")
        T[] objects = (T[]) allObjects.toArray();
        return objects;
    }
    
    // Private methods ---------------------------------------------------------

    private void fireUniversal( long mask, T representedObject, String propertyName ) {
        if ( listenerCache == null ) {
            return;
        }
        
        if ( representedObject == null ) { // Fire on all objects
            T[] objects = getAllObjects();
            if ( objects == null ) {
                return;
            }
            for( int i = 0; i < objects.length; i++ ) {
                fireUniversal( mask, objects[i], name );
            }
        }
        else {                             // Fire on one object 
            LookEvent evt = mask == PROPERTY_CHANGE ? 
                new LookEvent( representedObject, propertyName ) :
                new LookEvent( representedObject, mask );   
             
            List listeners;    
            synchronized( this ) {
                listeners = listenerCache.getListeners( representedObject );
            }
            for( int i = 0; i < listeners.size(); i++ ) {
                if ( mask == PROPERTY_CHANGE ) {                    
                    ((LookListener)listeners.get( i )).propertyChange( evt );
                }
                else {
                    ((LookListener)listeners.get( i )).change( evt );
                }                
            }
        }
                                                 
    }
    
    
    // Innerclasses ------------------------------------------------------------

    private static final Object PLACE_HOLDER = new Object();
    
    // Cache of listeners
    private class ListenerCache<T> {

        private Set<LookListener> allObjectListeners; // Listeners which should listen to all objects
        private Map<T,/*Reference<LookListener>|List<Reference<LookListener>>*/Object> obj2l;

        void addListener( T object, LookListener listener ) {

            if ( object == null ) {         // Listener which wants to know about all objects
                
                if ( allObjectListeners == null ) {
                    allObjectListeners = new HashSet<LookListener>();
                }
                if ( listener != null ) {
                    allObjectListeners.add( listener );
                }
            }
            else { // Listener registered to particular object
                
                if ( obj2l == null ) {
                    obj2l = new IdentityHashMap<T,Object>();
                }
                                
                Object l = obj2l.get( object );

                if ( l == null ) {  // There is nothing in the cache
                    obj2l.put( object, listener == null ? PLACE_HOLDER : listener ); // just add
            }
                else { // Something already registered
                    if ( l instanceof LookListener || l == PLACE_HOLDER ) { // One listener
                        if (l == listener) {
                            return;
                        }
                        List ll = new ArrayList( 2 );  // PENDING make this to array in order to save some memory                        
                        ll.add( l );
                        ll.add( listener == null ? PLACE_HOLDER : listener );
                        obj2l.put( object, ll );
                    }
                    else { // There are already many listeners
                        List list = (List)l;
                        if (list.contains(listener)) {
                            return;
                        }
                        list.add( listener == null ? PLACE_HOLDER : listener );
                    }
                }
            }
        }

        void removeListener( T object, LookListener listener ) {

            if ( object == null ) {
                if ( allObjectListeners != null ) {
                    allObjectListeners.remove( listener );
                }
                return;
            }

            if ( obj2l == null ) {
                return;
            }

            Object l = obj2l.get( object );

            if ( l == PLACE_HOLDER || l instanceof LookListener ) { // Removing the listener from map
                obj2l.remove( object );
            }
            else if ( l != null ) {  // Removing the listener from list
                List ll = (List)l;
                ll.remove( listener == null ? PLACE_HOLDER : listener );
                if ( ll.size() == 1 ) {
                    obj2l.put( object, ll.get( 0 ) ); // Remove list put last listener instead
                }
            }

        }
        
        void changeListener( T object, LookListener oldListener, LookListener newListener ) {

            if ( object == null ) {
                if ( allObjectListeners != null ) {
                    allObjectListeners.remove( oldListener );
                    allObjectListeners.add( newListener );
                }
                return;
            }

            if ( obj2l == null ) {
                return;
            }

            Object l = obj2l.get( object );

            if ( l == oldListener ) { 
                obj2l.put( object, newListener == null ? PLACE_HOLDER : newListener ); // Change the listener
            }
            else {  // Removing the listener from list
                List ll = (List)l;
                if ( ll.remove( oldListener ) ) {
                    ll.add( newListener == null ? PLACE_HOLDER : newListener );
                }
            }

        }

        List getListeners( Object object ) {

            if ( object == null ) {
                throw new IllegalStateException( "Reperesented object is null" );
            }

            List<LookListener> result = new ArrayList<LookListener>( 4 );

            if ( allObjectListeners != null ) {
                result.addAll( allObjectListeners );
            }

            if ( obj2l != null ) {
                Object l = obj2l.get( object );

                if ( l == null ) {
//                    System.err.println( "LOOK : " + Look.this );
//                    System.err.println( "OBJ  : " + object );
                }
                else if ( l instanceof LookListener ) {
                    result.add((LookListener) l);
                }
                else if ( l == PLACE_HOLDER ) {
                    // Cant fire on placeholder
                }
                else {
                    // We need to filter out PLACE_HOLDERs
                    for( Iterator it = ((List)l).iterator(); it.hasNext(); ) {
                        Object listener = it.next();
                        if ( listener != PLACE_HOLDER ) {
                            result.add((LookListener) listener);
                        }
                    }
                }
            }

            return result;
        }
        
        /*@ !! does not count listeners on all objects */        
        int getListenersCount( Object object ) {
            
            if ( object == null ) {
                throw new IllegalStateException( "Reperesented object is null" );
            }
            
            if ( obj2l != null ) {
                Object l = obj2l.get( object );

                if ( l == null ) {
                    return 0;
                }
                else if ( l == PLACE_HOLDER || l instanceof LookListener ) {
                    return 1;
                }
                else {
                    return ((List)l).size();
                }
            }
            
            return 0;
            
        }
        
        Collection<T> getAllObjects() {
            if ( obj2l == null ) {
                return null;
            }
            else {
                return obj2l.keySet();
            }
        }

    }



}
