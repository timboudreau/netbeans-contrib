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
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.modules.looks.LookListener;
import org.netbeans.modules.looks.LookEvent;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Enumerations;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/** This is base class for delegating to other looks. All methods are
 * enumerated by numeric constants and by overriding
 * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object)}
 * method subclasses can easily decide which methods and where to delegate.
 * <P>
 * It is not suggested to switch the looks that is being delegated to
 * during different calls, because it can also result in calls to a look
 * that has not have been attached by calling attachTo.
 * <p>It is also not suggested to base the delegation on some changeable attribute
 * of the representedObject.
 * In such cases developer are encouraged to write new LookSelector and pass
 * it as parameter into the constructor of the ProxyLook.
 * <P>
 * It is possible to change the represented object for a look
 * those are being delegated to (see method
 * {@link #delegateObject} )
 * <P>
 * The default implementation of all methods share similar logic. If 
 * single value is returned from the method e.g. String or boolean all sublooks are 
 * consulted until one them returns diffrent value than null of false the 
 * value is returned from the ProxyLook (See Look for neutral values
 * definition). If none of the sublooks returns non-neutral value then null or
 * false is returned. In case of mutilvalued attributes e.g. Collections or
 * Arrays the ProxyLook takes care of merging the values.
 *
 * @author Jaroslav Tulach
 */
public abstract class ProxyLook extends Look {

    private final LookSelector content;

    private ProxyLookEventTranslator eventTranslator = new ProxyLookEventTranslator();

    /** Creates new instance of look does no work.
     * @param content LookSelector containing Looks the ProxyLook should
     *        delegate to.
     */
    public ProxyLook( String name, LookSelector content ) {
        super( name );
        this.content = content;
        // Start listening on the selector
        // Pending this should be elewhere and it also should be deregistered
        org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener( content, eventTranslator );
        
    }

    /** A heart of this class - method which can decide where to delegate
     * particular call.
     *
     * <P>
     * The default implementation ignores all parameters and just returns true,
     * which means that by default all methods are delegated.<BR>
     * Subclasses might override this method with implementation that bases its
     * decision on different criteria.
     * @param method One of the constants defined here that identifies the method
     *        we want to delegate to
     * @param look The look we want to delegate to
     * @param representedObject The Look.NodeSubstitute of the node.
     * @return <CODE>true</CODE> if method identified by the constant should be
     *         delegated to given Look with given substitute.
     *
     */
    protected boolean delegateTo( long method, Look look, Object representedObject ) {
        return true;
    }

    private Enumeration delegateTo (final long method, final Object representedObject ) {
        return Enumerations.filter(content.getLooks(delegateObject(representedObject)), new Enumerations.Processor() {
            public Object process(Object object, Collection ignore) {
                if (!(object instanceof Look)) {
                    return null;
                } else {
                    return delegateTo(method, (Look) object, representedObject) ? object : null;
                }
            }
        });
    }



    /** Controls whether we delegate to all looks just the first one that returns
     * a value.
     * <p>Useful for methods that return an array of objects. By default all sublooks
     * are consulted and the result is a merge of all values returned by
     * the sublooks. Overriding this method may disallow merging and force
     * mutlivalued attributes of the node (e.g. properties, children, etc,) to
     * behave like singlevalued attributes (e.g. name or icon). It means that
     * the first sublook will "win" and will provide the values.
     * @param method One of the constants defined here that identifies the method
     *        we want to delegate to
     * @param representedObject Represented object the look should work with.
     * @return <CODE>true</CODE> if the merge should happen <CODE>false</CODE> otherwise
     */
    protected boolean delegateAll (long method, Object representedObject) {
        return true;
    }

    /**
     * Permits changing the represented object when delegating to sublooks.
     * <P>
     * The default implementation just returns the same object, but
     * subclasses might use this method to change the represented object
     * for the look they delegate to.
     * <P>
     * When overriding this object make sure you implement the 
     * {@link #undelegateObject} method correctly.
     * @param representedObject Represented object which should be translated
     * @return The represented object for the sub-look (not <CODE>null</CODE>)
     */
    protected Object delegateObject (Object representedObject) {
        return representedObject;
    }
    
    /** Performs reverse transformation from delegated object to original
     * represented object.
     * <P>
     * The default implementation simply retruns the delegate.
     * <P>
     * This method has to be implemented correctly when the 
     * {@link #delegateObject} method is overridden.
     * @param delegate The delegate
     * @return Original represented object
     */
    protected Object undelegateObject( Object delegate ) {
        return delegate;
    }

    // General methods ---------------------------------------------------------


    private static Look extractLook (Enumeration en) {
        Object obj = en.nextElement();
        return obj instanceof Look ? (Look)obj : null;
    }

        
    // Methods for FUNCTIONALITY EXTENSIONS ------------------------------------

    public Collection getLookupItems(Object representedObject, Lookup oldEnv ) {
        Enumeration delegates = delegateTo (GET_LOOKUP_ITEMS, representedObject );

        if (delegates == null) {
            return null;
        }

        boolean merge = delegateAll (GET_LOOKUP_ITEMS, representedObject );

        Collection lookupItems = null;

        // Create list of subarrays
        for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            Collection data = look.getLookupItems (delegateObject( representedObject ), oldEnv );
            if (data == null || data.size() == 0 ) {
                continue;
            }

            if (!merge) {
                // we are not merging and need keys just by one look
                return data;
            }

            // add all those objects into array
            if ( lookupItems == null ) {
                lookupItems = new LinkedList( data );
            } else {
                lookupItems.addAll( data );
            }
        }

        return lookupItems;
    }

    // Methods for STYLE -------------------------------------------------------

    /** Gets the first display name provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First display name returned by some of the sublooks or <CODE>null</CODE>
     */
    public String getDisplayName(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_DISPLAY_NAME, representedObject );
        if (delegates != null) {
            for ( int i[] = { 0 }; delegates.hasMoreElements(); i[0]++ ) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    String h = ((Look)delegate).getDisplayName (delegateObject( representedObject ), env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first name provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First name returned by some of the sublooks or <CODE>null</CODE>
     */
    public String getName(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_NAME, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    String h = ((Look)delegate).getName (delegateObject( representedObject ), env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }


    /** Notifies all sublooks that the object was renamed. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param newName New name of the object.
     * @param env Environement for the represented object.
     */
    public void rename(Object representedObject, String newName, Lookup env ) throws IOException {
        Enumeration delegates = delegateTo (RENAME, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    ((Look)delegate).rename (delegateObject( representedObject ), newName, env );
                }
            }
        }
    }

    /** Gets the first short description provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First short description returned by some of the sublooks or <CODE>null</CODE>
     */
    public String getShortDescription(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_SHORT_DESCRIPTION, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    String h = ((Look)delegate).getShortDescription (delegateObject( representedObject ), env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first icon provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param type Icon type constant from {@link java.beans.BeanInfo}
     * @param env Environement for the represented object.
     * @return First icon returned by some of the sublooks or <CODE>null</CODE>
     */
    public java.awt.Image getIcon(Object representedObject, int type, Lookup env) {
        Enumeration delegates = delegateTo (GET_ICON, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    java.awt.Image h = ((Look)delegate).getIcon (delegateObject( representedObject ), type, env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first icon for opened state provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param type Icon type constant from {@link java.beans.BeanInfo}
     * @param env Environement for the represented object.
     * @return First icon for opened state returned by some of the sublooks or <CODE>null</CODE>
     */
    public java.awt.Image getOpenedIcon(Object representedObject, int type, Lookup env) {
        Enumeration delegates = delegateTo (GET_OPENED_ICON, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    java.awt.Image h = ((Look)delegate).getOpenedIcon (delegateObject( representedObject ), type, env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first help context provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First help context returned by some of the sublooks or <CODE>null</CODE>
     */
    public HelpCtx getHelpCtx(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_HELP_CTX, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    HelpCtx h = ((Look)delegate).getHelpCtx (delegateObject( representedObject ), env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    // Methods for CHILDREN ----------------------------------------------------

    /** Creates list of child objects of given object.
     * Consults the sublooks to get the resulting list
     * of child objects.<BR>
     * The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.<BR>
     * By default the resulting set is a merge of all values returned by
     * sublooks. However the merging behavior can be modified by overriding the
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return (Merged) list of the child objects
     */
    public List getChildObjects(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_CHILD_OBJECTS, representedObject );

        if (delegates == null) {
            return null;
        }

        boolean merge = delegateAll (GET_CHILD_OBJECTS, representedObject );

        List children = null;

        // Create list of subarrays
        for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            List data = look.getChildObjects (delegateObject( representedObject ), env );
            if (data == null || data.size() == 0 ) {
                continue;
            }

            if (!merge) {
                // we are not merging and need keys just by one look
                return data;
            }

            // add all those objects into array
            if ( children == null ) {
                children = new ArrayList( data );
            } else {
                children.addAll( data );
            }
        }

        return children;
    }

     /** Determines if the object should be expandable by consulting the set of sublooks.
     *  The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * <P>
     * Notice that filtering of this method is driven by the same constant as
     * by the {@link #getChildObjects}
     * getChildObjects( substitute )} method i.e.
     * {@link #GET_CHILD_OBJECTS GET_CHILD_OBJECTS}.
       * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.

     * @return <CODE>true</CODE> if at least one of the sublooks returns <CODE>true</CODE>,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean isLeaf(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_CHILD_OBJECTS, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    if ( !((Look)delegate).isLeaf (delegateObject( representedObject ), env )) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Methods for ACTIONS & NEW TYPES -----------------------------------------

    /** Creates an array of NewTypes for given node consulting the set of
     * sublooks.
     * The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.<BR>
     * By default the resulting set is a merge of all values returned by
     * sublooks. However the merging behavior can be modified by overriding the
     * {@link #delegateAll} method.<BR>
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return (Merged) array of the NewTypes
     */
    public NewType[] getNewTypes(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_NEW_TYPES, representedObject );

        if (delegates == null) {
            return null;
        }

        boolean merge = delegateAll (GET_NEW_TYPES, representedObject );

        Object arrays = null;

        // Create list of subarrays
        for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            NewType[] data = look.getNewTypes (delegateObject( representedObject ), env );
            if (data == null || data.length == 0 ) {
                continue;
            }

            if (!merge) {
                // we are not merging and need keys just by one look
                return data;
            }

            // add all those objects into array
            if (arrays == null) {
                arrays = data;
            } else {
                ArrayList l;
                if (arrays instanceof Object[]) {
                    // arrays contains Object[] convert to ArrayList
                    Object[] arr = (Object[])arrays;
                    l = new ArrayList (arr.length * 2);
                    l.addAll (Arrays.asList (arr));
                    arrays = l;
                } else {
                    l = (ArrayList)arrays;
                }
                l.addAll (Arrays.asList (data));
            }
        }

        if (arrays == null) {
            // Return if there is nothing to merge
            return null;
        }

        return arrays instanceof NewType[] ? (NewType[])arrays : (NewType[])((ArrayList)arrays).toArray (new NewType[0]);
    }

    /** Crates an array of actions for the node by consulting the set of sublooks.
     * The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.<BR>
     * By default the resulting set is a merge of all values returned by
     * sublooks. However the merging behavior can be modified by overriding the
     * {@link #delegateAll} method.<BR>
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return (Merged) array of actions
     */
    public Action[] getActions(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_ACTIONS, representedObject );

        if (delegates == null) {
            return null;
        }

        boolean merge = delegateAll (GET_ACTIONS, representedObject);

        Object arrays = null;

        // Create list of subarrays
        for (int i[] = { 0 };delegates.hasMoreElements(); i[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            Action[] data = look.getActions (delegateObject( representedObject ), env );
            if (data == null || data.length == 0 ) {
                continue;
            }

            if (!merge) {
                // we are not merging and need keys just by one look
                return data;
            }

            // add all those objects into array
            if (arrays == null) {
                arrays = data;
            } else {
                ArrayList l;
                if (arrays instanceof Object[]) {
                    // arrays contains Object[] convert to ArrayList
                    Object[] arr = (Object[])arrays;
                    l = new ArrayList (arr.length * 2);
                    l.addAll (Arrays.asList (arr));
                    arrays = l;
                } else {
                    l = (ArrayList)arrays;
                }
                l.addAll (Arrays.asList (data));
            }
        }

        if (arrays == null) {
            // Return if there is nothing to merge
            return null;
        }

        return arrays instanceof Action[] ? (Action[])arrays : (Action[])((ArrayList)arrays).toArray (new Action[0]);
    }

    /** Crates an array of context actions for the node by consulting the set of sublooks.
     * The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.<BR>
     * By default the resulting set is a merge of all values returned by
     * sublooks. However the merging behavior can be modified by overriding the
     * {@link #delegateAll} method.<BR>
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return (Merged) array of context actions
     */
    public Action[] getContextActions(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_CONTEXT_ACTIONS, representedObject );

        if (delegates == null) {
            return null;
        }

        boolean merge = delegateAll (GET_CONTEXT_ACTIONS, representedObject );

        Object arrays = null;

        // Create list of subarrays
        for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            Action[] data = look.getContextActions (delegateObject( representedObject ), env );
            if (data == null || data.length == 0 ) {
                continue;
            }

            if (!merge) {
                // we are not merging and need keys just by one look
                return data;
            }

            // add all those objects into array
            if (arrays == null) {
                arrays = data;
            } else {
                ArrayList l;
                if (arrays instanceof Object[]) {
                    // arrays contains Object[] convert to ArrayList
                    Object[] arr = (Object[])arrays;
                    l = new ArrayList (arr.length * 2);
                    l.addAll (Arrays.asList (arr));
                    arrays = l;
                } else {
                    l = (ArrayList)arrays;
                }
                l.addAll (Arrays.asList (data));
            }
        }

        if (arrays == null) {
            // Return if there is nothing to merge
            return null;
        }

        return arrays instanceof Action[] ? (Action[])arrays : (Action[])((ArrayList)arrays).toArray (new Action[0]);
    }

    /** Gets the first default action provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First default action returned by some of the sublooks or <CODE>null</CODE>
     */
    public Action getDefaultAction(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_DEFAULT_ACTION, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    Action h = ((Look)delegate).getDefaultAction (delegateObject( representedObject ), env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------

    /** Creates and array of PropertySets by consulting sublooks. The resulting array
     * (if merging is enabled) contains all sets returned from the sublooks. If set
     * with equal name is contained in array returned from more than one sublook,
     * then the resulting array will contain one set of given name with all
     * properties from the (sub)sets merged.<BR>
     * The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.<BR>
     * By default the resulting set is a merge of all values returned by
     * sublooks. However the merging behavior can be modified by overriding the
     * {@link #delegateAll} method.<BR>
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return (Merged) array of PropertySets
     */
    public Node.PropertySet[] getPropertySets(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_PROPERTY_SETS, representedObject );

        if (delegates == null) {
            return null;
        }

        delegates = Enumerations.filter(delegates, new Enumerations.Processor() {
            public Object process(Object o, Collection ignore) {
                return o; // We need to get rid of null values
            }
        });

        boolean merge = delegateAll (GET_PROPERTY_SETS, representedObject );

        ArrayList setsList = null;
        HashMap nameMap = null;

        // Create list of property sets and the name map
        for (int index[] = { 0 } ; delegates.hasMoreElements(); index[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            Node.PropertySet[] sets = look.getPropertySets(delegateObject( representedObject ), env );
            if ( sets == null || sets.length == 0 ) {
                continue; // Look does not provide any properties
            }

            if (!merge) {
                // no need to do merging, return the first reasonable value
                return sets;
            }

            if (setsList == null) {
                if ( !delegates.hasMoreElements() ) {
                    // I am the last look in the raw, no need to do merging
                    return sets;
                }

                setsList = new ArrayList ();
                nameMap = new HashMap (37);
            }


            // Merge the property sets. We use sheet sets for
            // more comfortable work with propertySets
            for ( int i = 0; i < sets.length; i++ ) {
                if ( sets[i].getName() == null ) {
                    continue; // Ignore unnamed lists
                }
                Sheet.Set es = (Sheet.Set)nameMap.get( sets[i].getName() );
                if ( es == null ) { //Such sheet does not exist yet
                    es = new Sheet.Set( );
                    es.setName( sets[i].getName() );
                    es.setDisplayName( sets[i].getDisplayName() );
                    es.setShortDescription( sets[i].getShortDescription() );
                    es.put( sets[i].getProperties() );
                    setsList.add( es );
                    nameMap.put( sets[i].getName(), es );
                }
                else { // Sheet exists => merge properties
                    Node.Property[] props = sets[i].getProperties();
                    if ( props == null || props.length == 0 ) {
                        continue;
                    }
                    else {
                        es.put( sets[i].getProperties() );
                    }
                }
            }
        }

        if ( setsList == null || setsList.size() == 0 ) {
            return null;
        }
        else {
            Node.PropertySet[] result = new Node.PropertySet[ setsList.size() ];
            setsList.toArray( result );
            return result;
        }
    }

    /** Gets the first customizer component provided by any sublook. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First customizer component returned by some of the sublooks or <CODE>null</CODE>
     */
    public Component getCustomizer(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (GET_CUSTOMIZER, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    Component h = ((Look)delegate).getCustomizer (delegateObject( representedObject ), env );
                    if (h != null) {
                        return h;
                    }
                }
            }
        }
        return null;
    }

    /** Determines if the node has customize element by consulting the set of sublooks.
     *  The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return <CODE>true</CODE> if at least one of the sublooks returns <CODE>true</CODE>,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean hasCustomizer(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (HAS_CUSTOMIZER, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    if ( ((Look)delegate).hasCustomizer (delegateObject( representedObject ), env )) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    // Methods for CLIPBOARD OPERATIONS ----------------------------------------

    /** Tests whether the node permits renaming by consulting the set of sublooks.
     *  The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return <CODE>true</CODE> if at least one of the sublooks returns <CODE>true</CODE>,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean canRename(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (CAN_RENAME, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    boolean b = ((Look)delegate).canRename (delegateObject( representedObject ), env );
                    if (b) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Tests whether the node permits the destroy operation by consulting the set of sublooks.
     *  The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return <CODE>true</CODE> if at least one of the sublooks returns <CODE>true</CODE>,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean canDestroy(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (CAN_DESTROY, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    boolean b = ((Look)delegate).canDestroy (delegateObject( representedObject ), env );
                    if (b) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Tests whether the node permits the copy operation by consulting the set of sublooks.
     *  The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return <CODE>true</CODE> if at least one of the sublooks returns <CODE>true</CODE>,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean canCopy(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (CAN_COPY, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    boolean b = ((Look)delegate).canCopy (delegateObject( representedObject ), env );
                    if (b) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Tests whether the node permits the cut operation by consulting the set of sublooks.
     *  The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return <CODE>true</CODE> if at least one of the sublooks returns <CODE>true</CODE>,
     *         <CODE>false</CODE> otherwise.
     */
    public boolean canCut(Object representedObject, Lookup env ) {
        Enumeration delegates = delegateTo (CAN_CUT, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    boolean b = ((Look)delegate).canCut (delegateObject( representedObject ), env );
                    if (b) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Crates an array of allowed PasteTypes by consulting the set of sublooks.
     * The set of consulted sublooks is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.<BR>
     * By default the resulting set is a merge of all values returned by
     * sublooks. However the merging behavior can be modified by overriding the
     * {@link #delegateAll} method.<BR>
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @param t The transferable
     * @return (Merged) array of PasteTypes
     */
    public PasteType[] getPasteTypes(Object representedObject, Transferable t, Lookup env ) {
        Enumeration delegates = delegateTo (GET_PASTE_TYPES, representedObject );

        if (delegates == null) {
            return null;
        }

        boolean merge = delegateAll (GET_PASTE_TYPES, representedObject );

        Object arrays = null;

        // Create list of subarrays
        for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
            Look delegate = extractLook (delegates);
            if (delegate == null) {
                continue;
            }

            Look look = (Look)delegate;

            PasteType[] data = look.getPasteTypes (delegateObject( representedObject ), t, env );
            if (data == null || data.length == 0 ) {
                continue;
            }

            if (!merge) {
                // we are not merging and need keys just by one look
                return data;
            }

            // add all those objects into array
            if (arrays == null) {
                arrays = data;
            } else {
                ArrayList l;
                if (arrays instanceof Object[]) {
                    // arrays contains Object[] convert to ArrayList
                    Object[] arr = (Object[])arrays;
                    l = new ArrayList (arr.length * 2);
                    l.addAll (Arrays.asList (arr));
                    arrays = l;
                } else {
                    l = (ArrayList)arrays;
                }
                l.addAll (Arrays.asList (data));
            }
        }

        if (arrays == null) {
            // Return if there is nothing to merge
            return null;
        }

        return arrays instanceof PasteType[] ? (PasteType[])arrays : (PasteType[])((ArrayList)arrays).toArray (new PasteType[0]);
    }

    /** Gets the first PasteType from any sublook, which can provide the
     * paste operation. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @see org.netbeans.spi.looks.Look#getDropType
     * @param representedObject Represented object the look should work with.
     * @param t the transferable
     * @param action the drag'n'drop action to do DnDConstants.ACTION_MOVE, ACTION_COPY, ACTION_LINK
     * @param index index between children the drop occurred at or -1 if not specified
     * @param env Environement for the represented object.
     * @return First PasteType returned by some of the sublooks or <CODE>null</CODE>
     */
    public PasteType getDropType(Object representedObject, Transferable t, int action, int index, Lookup env ) {
        Enumeration delegates = delegateTo (GET_DROP_TYPE, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    PasteType b = ((Look)delegate).getDropType (delegateObject( representedObject ), t, action, index, env );
                    if (b != null) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first transferable from any sublook which can provide the copy
     * operation. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @return First transferable returned by some of the sublooks or <CODE>null</CODE>
     * @param env Environement for the represented object.
     * @throws IOException If any of the sublooks throws the exception
     */
    public Transferable clipboardCopy(Object representedObject, Lookup env ) throws IOException {
        Enumeration delegates = delegateTo (CLIPBOARD_COPY, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    Transferable b = ((Look)delegate).clipboardCopy (delegateObject( representedObject ), env );
                    if (b != null) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first transferable from any sublook which can provide the cut
     * operation. Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First transferable returned by some of the sublooks or <CODE>null</CODE>
     * @throws IOException If any of the sublooks throws the exception
     */
    public Transferable clipboardCut(Object representedObject, Lookup env ) throws IOException {
        Enumeration delegates = delegateTo (CLIPBOARD_CUT, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    Transferable b = ((Look)delegate).clipboardCut (delegateObject( representedObject ), env );
                    if (b != null) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    /** Gets the first transferable from any sublook that can provide the drag operation.
     * Set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @return First transferable returned by some of the sublooks or <CODE>null</CODE>
     * @throws IOException If any of the sublooks throws the exception
     */
    public Transferable drag(Object representedObject, Lookup env ) throws IOException {
        Enumeration delegates = delegateTo (DRAG, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    Transferable b = ((Look)delegate).drag (delegateObject( representedObject ), env );
                    if (b != null) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    /** Calls the destroy method on all sublooks. The set of sublooks
     * is determined by the selector associated with this look and the
     * {@link #delegateTo(long, org.netbeans.spi.looks.Look, java.lang.Object) filtering method }.
     * @param representedObject Represented object the look should work with.
     * @param env Environement for the represented object.
     * @throws IOException If any of the sublooks throws the exception
     */
    public void destroy(Object representedObject, Lookup env ) throws IOException {
        Enumeration delegates = delegateTo (DESTROY, representedObject );
        if (delegates != null) {
            for (int i[] = { 0 }; delegates.hasMoreElements(); i[0]++) {
                Look delegate = extractLook (delegates);
                if (delegate != null) {
                    ((Look)delegate).destroy (delegateObject( representedObject ), env );
                }
            }
        }
    }

    // Package private methods -------------------------------------------------
    
    
    void addLookListener( Object representedObject, LookListener listener ) {
    
        Object dObject = null;
        Enumeration delegates = null;
        
        if ( representedObject != null ) {
            dObject = delegateObject( representedObject );
            delegates = content.getLooks( dObject );
        }
        
        synchronized ( this ) {
            super.addLookListener( representedObject, listener );

            // Start listening on the selector
            // org.netbeans.modules.looks.Accessor.DEFAULT.addSelectorListener( content, eventTranslator );

            if (representedObject != null) {
                // Allow sublooks to attach to representedObject

                List dList = new ArrayList();
                if (delegates != null) {
                    for ( int i[] = { 0 }; delegates.hasMoreElements(); i[0]++ ) {
                        Look delegate = extractLook (delegates);
                        if (delegate != null) {
                            RuntimeException ex = null;
                            try {   // Sublooks may throw an exception
                                ((Look)delegate).addLookListener( dObject, null );
                                ((Look)delegate).addLookListener( null, eventTranslator );
                                dList.add( delegate );
                            }
                            catch ( ClassCastException e ) {
                                ex = e;
                            }
                            catch ( IllegalArgumentException e ) {
                                ex = e;
                            }
                            if ( ex != null ) { // Exception thrown
                                // We need to detach and remove listeners
                                for( Iterator it = dList.iterator(); it.hasNext();  ) {
                                    Look l = (Look)it.next();
                                    l.removeLookListener( dObject, null );
                                    l.removeLookListener( null, eventTranslator );
                                }
                                // and rethrow the exception
                                throw ex;
                            }
                        }
                    }
                }
            }
        }
    }
           
    void removeLookListener( Object representedObject, LookListener listener ) {
    
        Object dObject = null;
        Enumeration delegates = null;
        
        if ( representedObject != null ) {
            dObject = delegateObject( representedObject );
            delegates = content.getLooks( dObject );
        }
        
        synchronized ( this ) {
            if (representedObject != null) {
                // Tell sublooks that they should stop listening
                if (delegates != null) {
                    for ( int i[] = { 0 }; delegates.hasMoreElements(); i[0]++ ) {
                        Look delegate = extractLook (delegates);
                        if (delegate != null) {
                            delegate.removeLookListener( dObject, null );
                        }
                    }
                }
            }
        
            // Stop listening on the selector
            // org.netbeans.modules.looks.Accessor.DEFAULT.removeSelectorListener( content, eventTranslator );

            super.removeLookListener( representedObject, listener );
        }
    }
    

    // Innerclasses ------------------------------------------------------------

    /** Class which listens on other looks an simply resends the events.
     */
    private class ProxyLookEventTranslator implements LookListener, org.netbeans.modules.looks.SelectorListener {

        public void change( LookEvent evt ) {
            fireChange( undelegateObject( evt.getSource() ), evt.getMask() );
        }
        public void propertyChange( LookEvent evt ) {
            firePropertyChange( undelegateObject( evt.getSource() ), evt.getPropertyName() );
        }
        
        // Look selector implementation ----------------------------------------
        
        public void contentsChanged( org.netbeans.modules.looks.SelectorEvent evt ) {
            
            // Prepare the mask
            long mask = Look.ALL_METHODS;
            mask &= ~( Look.DESTROY | Look.RENAME );
            
            Object objects[] = getAllObjects(); // Read the content of the cache
            if ( objects == null ) {
                return;  // Nothing to do
            }
            
            for( int i = 0; i < objects.length; i++ ) {
                Object dObject = delegateObject( objects[i] );
                Collection removedLooks = evt.getRemovedLooks( objects[i] );
                if ( !removedLooks.isEmpty() ) {
                    // Stop listening on the remnoved looks
                    for( Iterator it = removedLooks.iterator(); it.hasNext(); ) {
                        ((Look)it.next()).removeLookListener( dObject, null );
                    }
                }
                
                Collection addedLooks = evt.getAddedLooks( objects[i] );
                if ( !addedLooks.isEmpty() ) {
                    // Start listening on the new looks
                    for( Iterator it = addedLooks.iterator(); it.hasNext(); ) {
                        ((Look)it.next()).addLookListener( dObject, null );
                    }
                }
            }
            
            
            fireChange( null, mask );
            firePropertyChange( null, null );
        }
        
    }

}
