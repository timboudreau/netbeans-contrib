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

package org.netbeans.api.nodes2looks;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.beans.BeanInfo;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Action;
import org.netbeans.modules.looks.LookListener;
import org.netbeans.modules.looks.LookEvent;
import org.netbeans.spi.looks.Look;
import org.netbeans.spi.looks.LookSelector;
import org.netbeans.spi.looks.Looks;
import org.netbeans.spi.looks.Selectors;
import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.DefaultHandle;
import org.openide.nodes.Node;
import org.openide.cookies.InstanceCookie;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.AbstractLookup;

/**
 * Node that represents an object using Looks.
 *
 * @author Petr Hrebejk
 */
final class LookNode extends Node implements Node.Cookie, InstanceCookie.Of {

    /** Use this constant for returning empty NewTypes. */
    private static final NewType[] NO_NEW_TYPES = {};

    /** Use this constant for returning empty PasteTypes. */
    private static final PasteType[] NO_PASTE_TYPES = {};

    /** Use this constant for returning empty PropertySets. */
    private static final Node.PropertySet[] NO_PROPERTY_SETS = {};
    
    /** Use this constant for returning empty Actions. */
    private static final Action[] NO_ACTIONS = {};

    /** Name of the default icon 16x16 */
    private static final String DEFAULT_ICON_16_NAME =
        "org/netbeans/modules/looks/resources/defaultNode.gif"; // NOI18N

    /** Name of the default icon 32x32 */
    private static final String DEFAULT_ICON_32_NAME =
        "org/netbeans/modules/looks/resources/defaultNode32.gif"; //NOI18N

    // LookDescriptor for this node. Describes the Look used on this
    // node and the LookSelector used for this node's children
    // private Look look;

    // LookSelector for this node
    // private LookSelector lookSelector;

    // Lookup provider
    private LookNodeLookupProvider lookupProvider;

    // Currently used firer
    private FirerImpl firer;

    // Persistence cache
    private Cache cache;

    /** Creates new LookNode with The {@link Selectors#defaultTypes() Looks.defaultTypes()}
     * will be used for this node.
     * as associated look.
     * @see Look#attachTo(java.lang.Object)
     * @param representedObject The object which the node will represent.
     */
    public LookNode( Object representedObject ) {
        this (representedObject, null, null );
    }

    /** Creates new LookNode.
     * @see Look#attachTo(java.lang.Object)
     * @param representedObject The object which the node will represent.
     * @param look Explicit look which will be set on the node.
     */
    public LookNode (Object representedObject, Look look ) {
        this ( representedObject, look, null );
    }

    /** Creates new LookNode.
     * @see Look#attachTo(java.lang.Object)
     * @param representedObject The object which the node will represent.
     * @param look Explicit look which will be set on the node.
     */
    public LookNode (Object representedObject, Look look, LookSelector lookSelector ) {
        this ( representedObject, look, lookSelector, null );
    }

    /** Creates new LookNode.
     * @see Look#attachTo(java.lang.Object)
     * @param representedObject The object which the node will represent.
     * @param look Explicit look which will be set on the node.
     * @param handle Node.Handle which will take care of the persistence.
     */
    public LookNode(Object representedObject, Look look, LookSelector lookSelector, Node.Handle handle ) {
        this ( create( representedObject, look, lookSelector ) );
        
        if (handle != null) {
            this.cache = new Cache(handle);
        }
        else {
            this.cache = new Cache();
        }
    }
    
    /** Constructor used by LookChildren. 
     */
    LookNode( Object representedObject, Look look, LookSelector lookSelector, Cache cache ) {
        this ( create( representedObject, look, lookSelector ) );
        this.cache = cache;
    }


    /** Private constructor
     * @param params
     *      representedObject,  // [0]
     *      children,           // [1]
     *      look,               // [2]
     *      lookSelector,       // [3]
     *      lookup,             // [4]
     *      placeholderListener,// [5]
     */
    private LookNode( Object[] params ) {
        super( (Children)params[1], (Lookup)params[4]  );
        ((LookNodeLookupProvider)params[4]).setLookNode( this );
        this.lookupProvider = (LookNodeLookupProvider)params[4];
        this.firer = new FirerImpl( this, (LookSelector)params[3], (Look)params[2], params[0] );                
        org.netbeans.modules.looks.Accessor.DEFAULT.changeLookListener( getLook(), getRepresentedObject(), (LookListener)params[5], firer ); // Change the placehoder-listener to real listener
        org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener( getLookSelector(), firer); // Attach firer to listen on selector        
    }

    /** Computes imporatant parameters of the look node
     * Used in LookNode constructors and in setLookDescriptor method
     */
    private static Object[] create( Object representedObject,
                                    Look look,
                                    LookSelector lookSelector ) {

        if (lookSelector == null) {
            // If lookSelector is not specified use the default one
            ErrorManager.getDefault ().log ("LookSelector is null, DefaultSelector is used."); // NOI18N
            lookSelector = Selectors.defaultTypes ();
        }
        
        LookListener placehoderListener = new PlaceholderLookListener();
        
        if ( look != null ) {
            Exception e = tryAttach( look, representedObject, placehoderListener );
            if ( e != null ) {
                ErrorManager.getDefault().notify( ErrorManager.INFORMATIONAL, e );
                look = null; 
            }
        }
        else {
            look = findFirstLook( lookSelector, representedObject, placehoderListener );
        }

            
        if ( look == null ) {
            // Either the exploicitly set look did not accept the object or
            // we did not find any suitable look in the LookSelector.
            // We have to use default beans look.
            Exception e = tryAttach( Looks.bean(), representedObject, placehoderListener );            
            if ( e != null ) {
                // Thats really bad the beanLook does not accept this object
                throw new IllegalStateException( "BeanLook has to accept all objects " + representedObject );
            }
            else {
                look = Looks.bean();
            }
        }    
        
        
        // Lookup
        Lookup lookup = new LookNodeLookupProvider (); 

        // Children
        Children children = look.isLeaf( representedObject, lookup ) ? Children.LEAF : new LookChildren();

        // Create and fill the results array
        Object results[] = new Object[] {
            representedObject,  // [0]
            children,           // [1]
            look,               // [2]
            lookSelector,       // [3]
            lookup,             // [4]
            placehoderListener  // [5]
        };

        return results;
    }


    // General methods ---------------------------------------------------------

    /** Gets the LookSelector for this node. LookSelector is used for determining
     * looks for the node's subnodes. This methods is useful when using
     * UI for switching looks.
     * @return The LookSelector this node currently uses.
     */
    public final LookSelector getLookSelector() {
        return firer.lookSelector;
    }

    /** Returns represented object which this LookNode represents. This is the
     * same object which can be accessed by calling
     * @return The object represented by this node.
     */
    public synchronized Object getRepresentedObject() {
        return firer.getRepresentedObject();
    }

    private final LookNodeLookupProvider getLookupProvider() {
        return lookupProvider;
    }

    /** Sets the actual LookDescriptor for this node. After changing the
     * LookDescriptor several property
     * changes are fired:
     * <CODE>PROP_COOKIE, PROP_NAME, PROP_NAME, PROP_DISPLAY_NAME, PROP_ICON,
     * PROP_OPENED_ICON</CODE> and children are refreshed.
     * @param look New LookDescriptor of the node.
     */
    public void setLook( Look look ) {

        
        
        
        // PENDING what to do when the selected look does not accept the object        
        synchronized ( this ) {
            
            LookListener placeholder = null;
        
            if ( look == null ) {
                // This means that we have to find new look in the selector
                placeholder = new PlaceholderLookListener();
                look = findFirstLook( getLookSelector(), getRepresentedObject(), placeholder );
                if ( look == getLook() ) {
                    org.netbeans.modules.looks.Accessor.DEFAULT.removeLookListener( getLook(), getRepresentedObject(), placeholder );
                    return; // No need to do anything
                }
            }
            
            Object representedObject = getRepresentedObject();
            FirerImpl oldFirer = firer;
            LookSelector oldLookSelector = getLookSelector();
            Look oldLook = getLook();
                        
            oldFirer.look = null;              // Notify the old firer that i was
            oldFirer.lookSelector = null;      // explicitly removed from listeners lists
            oldFirer.representedObject = null; // and look was detached
            org.netbeans.modules.looks.Accessor.DEFAULT.removeSelectorListener( oldLookSelector, oldFirer );
            org.netbeans.modules.looks.Accessor.DEFAULT.removeLookListener( oldLook, representedObject, oldFirer );

            firer = new FirerImpl ( this, oldLookSelector, look, representedObject ); // Create new firer
            
            this.lookupProvider.setDirty(); // Reset lookup on this node
            
            if ( placeholder == null ) {
                org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( getLook(), getRepresentedObject(), firer ); // Attach the firer to listen on look
            }
            else {
                org.netbeans.modules.looks.Accessor.DEFAULT.changeLookListener( getLook(), getRepresentedObject(), placeholder, firer ); // Attach the firer to listen on look
            }
            org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener ( getLookSelector(), firer);
        }

        // store the descriptor into the cache for pesistsnce
        getCache().store( this );

        // fire possible change notifications
        fireCookieChange();
        refreshChildren(true);
        fireNameChange( null, null );
        fireDisplayNameChange( null, null );
        fireIconChange();
        fireOpenedIconChange();
    }

    /** Utility method which gets the actual look from current LookDescriptor.
     * @return Look set on this Node
     */
    public synchronized Look getLook() {
        return firer.look;
    }

    /** Gets handle for the LookNode.
     * @return the handle, or <code>null</code> if this node is not persistable
     */
    public Node.Handle getHandle() {
        if ( cache!= null ) {
            return cache;
        }
        else {
            return DefaultHandle.createHandle (this);
        }
    }

    /** Returns a node representing the same object as the original node. Looks
     * passed in constructor and in setLook method are copied as well.
     *
     * @return LookNode representing the same object.
     */
    public Node cloneNode () {
        return new LookNode( getRepresentedObject(), getLook(), getLookSelector(), cache );
    }

    // Additional methods for LookNode -----------------------------------------


    final synchronized LookSelector getLookSelectorForChildren() {
        Lookup lookup = getLookup();
        LookSelector chLs = (LookSelector)lookup.lookup( LookSelector.class );
        return chLs == null ? getLookSelector() : chLs;
    }

    /** Used to get Firer of this LookNode
     */
    final synchronized FirerImpl getFirer (){
        return firer;
    }

    /** Refreshes children on the node */
    void refreshChildren(boolean brutal) {

        Children current = getChildren();
        boolean isLeaf = getLook().isLeaf( getRepresentedObject(), getLookup() );

        if ( isLeaf ) {
            if ( current == Children.LEAF ) {
                return;
            }
            else {
                setChildren( Children.LEAF );
                return;
            }
        }

        if ( current == Children.LEAF ) {
            setChildren( new LookChildren() );
        }
        else {
            ((LookChildren)current).refreshChildren( brutal );
        }
    }

    Cache getCache() {
        return cache;
    }
    
    void setCache( Cache cache ) {
        this.cache = cache;
    }
    
    private static Look findFirstLook( LookSelector ls, Object ro, LookListener listener ) {

        Enumeration descriptors = ls.getLooks( ro );

        while (descriptors.hasMoreElements ()) {
            Look look = (Look)descriptors.nextElement ();
            
            Exception e = tryAttach( look, ro, listener );
            if ( e == null ) {
                return look;
            }
            
            ErrorManager.getDefault().notify( ErrorManager.INFORMATIONAL, e );                        
        }
        
        return null;        
    }
    
    private static Exception tryAttach( Look look, Object representedObject, LookListener listener ) {
        try {
            org.netbeans.modules.looks.Accessor.DEFAULT.addLookListener( look, representedObject, listener );
            return null;
        }
        catch ( ClassCastException e ) {
            return e;
        }
        catch ( IllegalArgumentException e ) {
            return e;
        }
    }

    private Lookup getLookupNoInit() {
        return lookupProvider;
    }
    
    // Methods for CHILDREN ----------------------------------------------------

    final List getChildObjects() {
        return getLook().getChildObjects( getRepresentedObject(), getLookupNoInit()  );
    }


    // Methods for STYLE -------------------------------------------------------

    /** Determines displayName by querying the Look.
     * @return DisplayName provided by the Look or result
     * of {@link #getName()}.
     */
    public String getDisplayName() {
        String  displayName = getLook().getDisplayName( getRepresentedObject(), getLookupNoInit() );
        return displayName == null ? getName() : displayName;
    }

    /** Empty method, setting <CODE>displayName</CODE> on the
     * <CODE>LookNode</CODE> has no effect. The <CODE>displayName</CODE> should
     * be determined by associated <CODE>Look</CODE>.
     * @param name Parameter is ignored.
     */
    public void setDisplayName( String name ) {
    }

    /** Determines name by querying the Look.
     * @return Name provided by the Look or <CODE>null</CODE>.
     */
    public String getName() {
        return getLook().getName( getRepresentedObject(), getLookupNoInit() );
    }

    /** Invoking this method on LookNode invokes method {@link Look#rename}
     * @see org.netbeans.spi.looks.Look#rename(Object, String, Lookup)
     * @param name The new name to be set.
     */
    public void setName( String name ) {
        try {
            getLook().rename( getRepresentedObject(), name, getLookupNoInit() );
        }
        catch ( IOException ex ) {
            RuntimeException e = new IllegalArgumentException();
            ErrorManager.getDefault().annotate (e, ex);
            throw e;

        }
    }

    /** Empty method, setting <CODE>shortDescription</CODE> on the
     * <CODE>LookNode</CODE> has no effect. The <CODE>shortDescription</CODE>
     * should be determined by associated <CODE>Look</CODE>.
     * @param shortDescription Parameter is ignored.
     */
    public void setShortDescription( String shortDescription ) {
    }

    /** Determines shortDescription by querying the Look.
     * @return Name provided by the Look or result of {@link #getDisplayName()}.
     */
    public String getShortDescription() {
        String shortDescription = getLook().getShortDescription( getRepresentedObject(), getLookupNoInit() );
        return shortDescription == null ? getDisplayName() : shortDescription;
    }

    /** Determines icon for closed state by querying the Look.
     * @param type Icon type constant from {@link java.beans.BeanInfo}
     * @return Icon provided by the Look
     */
    public Image getIcon( int type ) {
        Image image = getLook().getIcon( getRepresentedObject(), type, getLookupNoInit() );

        if ( image == null ) {
            if ( type == BeanInfo.ICON_COLOR_32x32 || type == BeanInfo.ICON_MONO_32x32 ) {
                return Utilities.loadImage( DEFAULT_ICON_32_NAME );
            }
            return Utilities.loadImage( DEFAULT_ICON_16_NAME );
        }
        return image;
    }

    /** Determines icon for opened state by querying the Look.
     * @param type Icon type constant from {@link java.beans.BeanInfo}
     * @return Icon provided by the Look or the result of
     * {@link #getIcon(int)}.
     */
    public Image getOpenedIcon( final int type ) {
        Image image = getLook().getOpenedIcon( getRepresentedObject(), type, getLookupNoInit() );
        return image == null ? getIcon( type ) : image;
    }

    /** Determines HelpCtx for opened state by querying the Look.
     * @return HelpCtx provided by the Look.
     */
    public HelpCtx getHelpCtx () {
        return getLook().getHelpCtx( getRepresentedObject(), getLookupNoInit() );
    }

    // Methods for ACTIONS & NEW TYPES -----------------------------------------

    /** Determines NewTypes by querying the Look.
     * @return NewTypes provided by the Look.
     */
    public NewType[] getNewTypes() {
        NewType arr[] = getLook().getNewTypes( getRepresentedObject(), getLookupNoInit() );
        return arr == null ? NO_NEW_TYPES : arr;
    }

    /** Get the set of actions associated with this node by asking the look
     * @see org.openide.nodes.Node#getActions()
     * @return system actions appropriate to the node
     */
    public SystemAction[] getActions() {
        return toSA (getActions ( false ));
    }

    /** Implementation of the getActions command with the expected signature
     * of a method that will be used in future
     *
     * @return the actions supported by this node
     */
    public Action[] getActions ( boolean context ) {

        Action systemActions[];

        if ( context ) {
            systemActions = getLook().getContextActions( getRepresentedObject(), getLookupNoInit() );
        }
        else {
            systemActions = getLook().getActions( getRepresentedObject(), getLookupNoInit() );
        }

        return systemActions == null ? NO_ACTIONS : systemActions;

    }

    /** Queries the look for  special set of actions for situations when this
     * node is displayed as a context.
     * @see org.openide.nodes.Node#getContextActions()
     * @return actions for a context. In the default implementation, same as {@link #getActions}.
     */
    public SystemAction [] getContextActions() {
        return toSA (getActions ( true ));
    }

    /** Determines the default action for this node by asking the look.
     * @see org.openide.nodes.Node#getDefaultAction()
     * @return default action, or <code>null</code> if there should be none
     */
    public SystemAction getDefaultAction() {
        Action a = getPreferredAction();
        return a instanceof SystemAction ? (SystemAction)a : null;
    }

    /** Gets the preferred action for this node by asking the look.
     * This action can but need not to be one from the action array returned
     * from {@link #getActions(boolean)}.
     * In case it is, the popup menu created from those actions
     * is encouraged to highlight the preferred action.
     * Override in subclasses accordingly.
     *
     * @return the preferred action instance of <code>null</code> if there is none.
     * @since 3.29 */
    public Action getPreferredAction() {
        return getLook().getDefaultAction( getRepresentedObject(), getLookupNoInit() );
    }

    /** A convertor that takes array of Actions and return array of SystemActions
     * @param arr array of Action
     * @return array of SystemActions
     */
    private static SystemAction[] toSA (Action[] arr) {
        SystemAction[] sa = new SystemAction[arr.length];

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof SystemAction) {
                sa[i] = (SystemAction)arr[i];
            }
        }

        return sa;
    }


    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------


    /** Asks the look for the list of property sets for this node
     * @see org.openide.nodes.Node#getPropertySets()
     * @return the property sets
     */
    public Node.PropertySet[] getPropertySets() {

        Node.PropertySet sets[] = getLook().getPropertySets( getRepresentedObject(), getLookupNoInit() );
        return sets == null ? NO_PROPERTY_SETS : sets;

    }

    /** Gets the customizer component by consulting the look.
     * @return the component, or <CODE>null</CODE> if there is no customizer
     */
    public Component getCustomizer() {
        return getLook().getCustomizer( getRepresentedObject(), getLookupNoInit() );
    }

    /** Asks look whether there is a customizer for this node. If true,
    * the customizer can be obtained via {@link #getCustomizer}. Result is
    * @return <CODE>true</CODE> if there is a customizer
    */
    public boolean hasCustomizer () {
        return getLook().hasCustomizer( getRepresentedObject(), getLookupNoInit() );
    }

    // Methods for CLIPBOARD OPERATIONS ----------------------------------------

    /** Asks the look whether this node can be renamed.
    * If true, one can use {@link #getName} to obtain the current name and
    * {@link #setName} to change it.
    * @return <code>true</code> if the node can be renamed
    */
    public boolean canRename() {
        return getLook().canRename( getRepresentedObject(), getLookupNoInit() );
    }

    /** Asks the look whether this node can be deleted.
     * @return <CODE>true</CODE> if the node can be deleted.
     */
    public boolean canDestroy() {
        return getLook().canDestroy( getRepresentedObject(), getLookupNoInit() );
    }

    /** Asks the look whether this node permits copying.
     * @return <code>true</code> if the node permits copying
     */
    public boolean canCopy() {
        return getLook().canCopy( getRepresentedObject(), getLookupNoInit() );
    }

    /** Asks the look whether this node permits cutting.
     * @return <code>true</code> if the node permits cutting
     */
    public boolean canCut() {
        return getLook().canCut( getRepresentedObject(), getLookupNoInit() );
    }

    /** Consults the look to determine which paste operations are allowed when
     * a given transferable is in the clipboard.
     * @see org.openide.nodes.Node#getPasteTypes(java.awt.datatransfer.Transferable)
     * @param t the transferable in the clipboard
     * @return array of operations that are allowed
     */
    public PasteType[] getPasteTypes( Transferable t) {
        PasteType arr[] = getLook().getPasteTypes( getRepresentedObject(), t, getLookupNoInit() );
        return arr != null ? arr : NO_PASTE_TYPES;
    }

    /** Consults the look to determine if there is a paste operation that can
     * be performed on provided transferable. Used by drag'n'drop code to check
     * whether the drop is possible.
     * @param t the transferable
     * @param action the drag'n'drop action to do DnDConstants.ACTION_MOVE, ACTION_COPY, ACTION_LINK
     * @param index index between children the drop occurred at or -1 if not specified
     * @return null if the transferable cannot be accepted or the paste type
     *    to execute when the drop occurs
     */
    public PasteType getDropType( Transferable t, int action, int index) {
        return getLook().getDropType( getRepresentedObject(), t, action, index, getLookupNoInit() );
    }

    /** Called when a node is to be copied to the clipboard. Look is responsible
     * for handling the operation.
     * @return the transferable object representing the content of the clipboard
     * @exception IOException when the copy cannot be performed
     */
    public Transferable clipboardCopy() throws IOException {
        return getLook().clipboardCopy( getRepresentedObject(), getLookupNoInit() );
    }

    /** Called when a node is to be cut to the clipboard. Look is responsible for
     * handling the operation.
     * @return the transferable object representing the content of the clipboard
     * @exception IOException when the cut cannot be performed
     */
    public Transferable clipboardCut() throws IOException {
        return getLook().clipboardCut( getRepresentedObject(), getLookupNoInit() );
    }

    /** Called when a drag is started with this node. Look is responsible
     * for handling the operation.<BR>
     * The node can attach a transfer listener to ExTransferable and
     * will be then notified about progress of the drag (accept/reject).
     * @return transferable to represent this node during a drag
     * @exception IOException if a drag cannot be started
     */
    public Transferable drag() throws IOException {
        return getLook().drag( getRepresentedObject(), getLookupNoInit() );
    }

    /** Call to this method removes the node from its parent and deletes it.
     * The look is responsible for reflecting the destroy action in underlying
     * data and for doing necessary cleanups.
     * @exception IOException if something fails
     */
    public void destroy () throws IOException {
        getLook().destroy( getRepresentedObject(), getLookupNoInit() );
    }

    // Implementation of InstanceCooke.Of --------------------------------------
    
    public Class instanceClass() throws java.io.IOException, ClassNotFoundException {
        return getRepresentedObject().getClass();
    }
    
    public Object instanceCreate() throws java.io.IOException, ClassNotFoundException {
        return getRepresentedObject();
    }
    
    public String instanceName() {
        return getRepresentedObject().getClass().getName();
    }
    
    public boolean instanceOf( Class type ) {
        return type.isInstance( getRepresentedObject() );
    }
    
    public String toString() {
        if (getRepresentedObject() != null) {
            return super.toString();
        } else {
            return "LookNode<" + getLook().getName() + ";DEAD>"; // NOI18N
        }
    }
    
    // Firer innerclass -----------------------------------------------------

    /** Class passed to the Look methods as parameter. Each LookNode contains
     * exactly one instance of this class. Metods of the class allow access
     * to the properties and methods od the node needed for the Look.
     */
    static final class FirerImpl extends WeakReference
        implements Runnable, org.netbeans.modules.looks.SelectorListener, LookListener {

        /** Represented object of the LookNode */
        private Object representedObject;

        private LookSelector lookSelector;
        
        // LookDescriptor the node. Describes the Look used on this
        // node and the LookSelector used for this node's children
        private Look look;

        /** The constructor of the interior is private to prevent other classes
         * than LookNode and Look from firing events on the node
         */
        FirerImpl( LookNode lookNode,
                   LookSelector lookSelector,
                   Look look,
                   Object representedObject ) {
            super( lookNode, Utilities.activeReferenceQueue() );
            this.representedObject = representedObject;
            this.look = look;
            this.lookSelector = lookSelector;
        }
        
        public LookNode getLookNode () {
            return (LookNode)get();
        }

        /** Returns the object represented by the node
         * @return Object represented by the node this interior belongs to.
         */
        public Object getRepresentedObject() {
            return representedObject;
        }

        // Implementation of LookListener --------------------------------------

        public void change( LookEvent evt ) {
           
            long mask = evt.getMask();
            LookNode l = getLookNode();

            if ( l != null ) {
                
                if ( ( mask & Look.GET_NAME ) > 0 ) {                 
                    l.fireNameChange( null, null );
                }               
                if ( ( mask & Look.GET_DISPLAY_NAME ) > 0 ) {
                    l.fireDisplayNameChange( null, null );
                }
                if ( ( mask & Look.GET_ICON ) > 0 ) {
                    l.fireIconChange();
                }
                if ( ( mask & Look.DESTROY ) > 0 ) {
                    l.fireNodeDestroyed();
                }
                if ( ( mask & Look.GET_OPENED_ICON ) > 0 ) {
                    l.fireOpenedIconChange();
                }
                if ( ( mask & Look.GET_PROPERTY_SETS ) > 0 ) {
                    l.firePropertySetsChange( null, null );
                }
                if ( ( mask & Look.GET_CHILD_OBJECTS ) > 0 ) {
                    l.refreshChildren(false);
                }    
                if ( ( mask & Look.GET_SHORT_DESCRIPTION ) > 0 ) {
                    l.fireShortDescriptionChange( null, null );
                }
                if ( ( mask & Look.GET_LOOKUP_ITEMS ) > 0 ) {
                    l.getLookupProvider().setDirty();
                }            
            }
        }

        /** Notification of property change on given object
         */
        public void propertyChange( LookEvent evt ) {
            LookNode l = getLookNode();
            if (l != null) {
                l.firePropertyChange( evt.getPropertyName(), null, null );
            }
        }
 
        // Implementation of Runnable ------------------------------------------

        public void run () {
            
            if ( lookSelector != null ) {
                org.netbeans.modules.looks.Accessor.DEFAULT.removeSelectorListener(lookSelector, this);
            }
            if ( look != null ) {
                org.netbeans.modules.looks.Accessor.DEFAULT.removeLookListener( look, representedObject, this );
            }
            
            // Help GC:
            representedObject = null;
          
        }

        // Implementation of SelectorListener ----------------------------------

        public void contentsChanged(org.netbeans.modules.looks.SelectorEvent evt) {
            LookNode ln = getLookNode();

            if ( evt.affectsObject(representedObject) && ln != null ) {
                ln.setLook( null );
            }
        }
    } // end of FirerImpl

    private static final class LookNodeLookupProvider extends org.openide.util.lookup.AbstractLookup {
        private LookNode lookNode;
        
        protected void setLookNode (LookNode l) {
            lookNode = l;
        }
        
        protected void initialize() {
            setDirty ();
        }

        // Implementation of Lookup.Provider -----------------------------------

        public void setDirty() {
            if ( lookNode == null ) {
                throw new IllegalStateException( "getLookupCalled before attached node" );
            }

            // We have to create new lookup - in order to fire changes properly
            
            Look look = lookNode.getLook();
            Collection lookupItems = look.getLookupItems( lookNode.getRepresentedObject(), this );

            List pairs = new ArrayList( lookupItems == null ? 1 : 1 + lookupItems.size() );
            pairs.add( ItemPair.wrap ( ( new NodeLookupItem( lookNode ) ) ) );
            
            
            if ( lookupItems != null ) {
                for (Iterator it = lookupItems.iterator(); it.hasNext(); ) {
                    pairs.add( ItemPair.wrap ((Lookup.Item)it.next()) );
                }                
            }

            setPairs( pairs );
        }
    } // end of LookNodeLookupProvider

    /** Pair that wraps an item */
    private static final class ItemPair extends AbstractLookup.Pair {
        
        private AbstractLookup.Item item;
        
        private ItemPair (org.openide.util.lookup.AbstractLookup.Item i) {
            this.item = i;
        }

        public static AbstractLookup.Pair wrap (Lookup.Item item) {
            if (false/*XXX see #13779; doesn't work: item instanceof AbstractLookup.Pair*/) {
                return (AbstractLookup.Pair)item;
            } else {
                return new ItemPair (item);
            }
        }

        protected boolean creatorOf(Object obj) {
            return item.getInstance() == obj;
        }

        public String getDisplayName() {
            return item.getDisplayName ();
        }

        public String getId() {
            return item.getId ();
        }

        public Object getInstance() {
            return item.getInstance ();
        }

        public Class getType() {
            return item.getType ();
        }

        protected boolean instanceOf(Class c) {
            return c.isAssignableFrom (getType ());
        }

        public boolean equals (Object o) {
            if (o instanceof ItemPair) {
                ItemPair p = (ItemPair)o;
                return item.equals (p.item);
            }
            return false;
        }

        public int hashCode () {
            return item.hashCode ();
        }
    } // end of ItemPair
    
    private static class NodeLookupItem extends Lookup.Item {
        private LookNode ln;
        
        public NodeLookupItem( LookNode ln ) {
            this.ln = ln;
        }
                
        public String getDisplayName() {
            return getId();
        }
        
        public String getId() {
            return ln.toString();
        }
        
        public Object getInstance() {
            return ln;
        }
        
        public Class getType() {
            return ln.getClass();
        }
        
    }
    
    // Serves as a placeholder for look listener, does nothing
    private static class PlaceholderLookListener implements LookListener {
        
        public void change(LookEvent evt) {}
        
        public void propertyChange(LookEvent evt) {}
                
    }
    
}

