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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import org.netbeans.modules.looks.RegistryBridge;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/** Static factory class for different kinds of useful Looks. For looks
 * which can be constructed using <em>module layer</em>
 * <code>XMLFileSystem</code>s, a sample of such XML definition is given
 * in the Javadoc.
 *
 * @author  Petr Hrebejk, Jaroslav Tulach
 */
public abstract class Looks {

    // Shared attribute names
    static final String CONTEXT="context";
    static final String LOOK_SELECTOR = "lookSelector"; // NOI18N

    // Look Attribute names
    private static final String LOOK = "look"; // NOI18N
    private static final String DELEGATE = "delegateLook"; // NOI18N
    private static final String ALL_METHODS = "ALL_METHODS"; // NOI18N
    private static final String NO_METHODS = "NO_METHODS"; // NOI18N

    /* The only bean look in the system */
    private static final Look BEAN_LOOK = new org.netbeans.modules.looks.BeanLook( "JavaBeans" ); // NOI18N

    /* The only LookSwitcherLook in the system */
    // private static final Look LOOK_SWITCHER_LOOK = new org.netbeans.modules.looks.LookSwitcherLook();

    /* Map of FilterLook attributes */
    private static final HashMap mapFilterMethods = new HashMap (16);

    /** No instances */
    private Looks() {
    }

    // Methods to be used from XML layers --------------------------------------

    /** A method to be used from XML layers
     */
    static final Look composite(FileObject fo) throws IOException {
        
        org.netbeans.modules.looks.RegistryBridge registryBridge = RegistryBridge.getDefault( fo );
        String contextName = readStringAttribute (fo, CONTEXT);
        
               
        return new org.netbeans.modules.looks.CompositeLook ( 
                    fo.getPath(), 
                    new LookSelector( org.netbeans.modules.looks.SelectorImplFactory.context( registryBridge, contextName ) ) );
    }

    /** A method to be used from XML layers
     */
    /*
    static final Look lookSwitcherLook( FileObject fo ) {
        return LOOK_SWITCHER_LOOK;
    }
    */

    /** A method to be used from XML layers
     */
    static final Look filter(FileObject fo) throws IOException {
        if (mapFilterMethods.isEmpty ())
            initFilterMethods ();

        Look delegate = readLookAttribute (fo, DELEGATE);

        // proccess mask
        // 1. default value is ALL_METHODS
        // 2. read ALL_METHODS and NO_METHODS attributes, set the base level of masking
        // 3. read a attribute for each method and cut down or enlarge the mask
        long mask = Look.ALL_METHODS;
        Boolean helpBool = null;
        long helpLong;
        helpBool = readBooleanAttribute (fo, ALL_METHODS);
        mask = helpBool == null || helpBool.booleanValue () ? Look.ALL_METHODS : Look.NO_METHODS;

        helpBool = readBooleanAttribute (fo, NO_METHODS);
        mask = helpBool != null && helpBool.booleanValue () ? Look.NO_METHODS : mask;

        String attr;
        for (Enumeration attrs = fo.getAttributes (); attrs.hasMoreElements (); ) {
            attr = (String)attrs.nextElement ();
            if (mapFilterMethods.containsKey (attr)) {
                helpBool = readBooleanAttribute (fo, attr);
                helpLong = ((Long)mapFilterMethods.get (attr)).longValue ();
                mask = helpBool == null ? mask : (helpBool.booleanValue () ?
                    // mask
                    mask | helpLong :
                    // unmask
                    mask & ~helpLong
                    );
            }
        }

        return new org.netbeans.modules.looks.FilterLook ( fo.getName(), delegate, mask);
    }

    /** A method to be used from XML layers
     */
    static final Look childrenSelectorProvider(FileObject fo) throws IOException {

        Look look = null;
        String lookName = readStringAttribute (fo, LOOK);

        // lookup delegate look
        Object delegateObject = RegistryBridge.getDefault( fo ).resolve(lookName);

        if ((delegateObject != null) && (delegateObject instanceof Look)) {
            look = (Look)delegateObject;
        }    
        if (look == null) {
            ErrorManager.getDefault ().notify (new Exception ("Look not found at " + look)); // NOI18N
            return null;
        }

        LookSelector lookSelector = null;
        String selectorName = readStringAttribute (fo, LOOK_SELECTOR);
        
        // lookup delegate look
        delegateObject = RegistryBridge.getDefault( fo ).resolve (selectorName);
        if ((delegateObject != null) && (delegateObject instanceof LookSelector)) {
            lookSelector = (LookSelector)delegateObject;
        }
        
        if (lookSelector == null) {
            ErrorManager.getDefault ().notify (new Exception ("LookSelector not found at " + lookSelector)); // NOI18N
            return null;
        }

        return new org.netbeans.modules.looks.ChildrenSelectorProvider( fo.getName(), look, lookSelector );
    }


    // public factory methods --------------------------------------------------

    /** Look that presents an object as a JavaBean. Uses java.beans.Introspector
     * to get the properties and delegates to the object all other methods.
     * <P>
     * To create this look from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourLook.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.Look" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Looks.bean" /&gt;
     * &lt;/file&gt;
     * </pre>
     *
     * @see org.openide.nodes.BeanNode BeanNode
     * @return Look which represents objects as JavaBeans
     */
    public static final Look bean() {
        return BEAN_LOOK;
    }

    /** Look that filters the features of look which delegates. The filtered
     * features are specified by mask.
     *
     * <P>
     * To create this filter look from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourLook.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.Look" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Looks.filter" /&gt;
     *   &lt;!--The path to declaration of delegate look.--&gt;
     *   &lt;attr name="delegateLook" stringvalue="Looks/MyLayer/MyLooks/FooLook" /&gt;
     *   &lt;!--Optionally! The mask can be specified in mask attribute. The default mask is ALL_METHODS.--&gt;
     *   &lt;attr name="ALL_METHODS" boolvalue="false" /&gt;
     *   &lt;!--Optionally! Mask specification of a methods individually, can enlarge the mask in detail.--&gt;
     *   &lt;attr name="GET_NAME" boolvalue="false" /&gt;
     *   &lt;attr name="GET_ICON" boolvalue="true" /&gt;
     *   ...
     * &lt;/file&gt;
     * </pre>
     * The <code>&lt;attr name="delegateLook" /&gt;</code> attribute can directly return
     * an instance of your look (instead of specifying its path). So one can replace
     * that line with:
     * <PRE>
     *   &lt;attr name="delegateLook" newvalue="yourpkg.YourLook" /&gt;
     * </PRE>
     * or if one wants to use <code>methodvalue</code>:
     * <PRE>
     *   &lt;attr name="delegateLook" methodvalue="yourpkg.YourFactoryClass.yourMethod" /&gt;
     * </PRE>
     * under the condition that there is a YourFactoryClass in yourpkg that contains following
     * method:
     * <PRE>
     * package yourpkg;
     * public final class YourFactoryClass {
     *   public static Look yourMethod () {
     *     Look l = ...;
     *     return l;
     *   }
     * }
     * </PRE>
     *
     *
     * @see org.netbeans.spi.looks.ProxyLook ProxyLook for values for method masking
     * @param delegate The look the FilterLook will delegate to
     * @param mask Binary mask where 1 means that the method call will be
     *        forwarded to the delegate look. 0 means that the delegation
     *        will be suppressed
     * @return new filter look with given mask and delegate
     */
    public static final Look filter( String name, Look delegate, long mask ) {
        return new org.netbeans.modules.looks.FilterLook( name, delegate, mask );
    }

    /** Look that is composite of the delegates specified by a given context or
     * composite of array of looks. Composite means that all attributes of the
     * node are determined by asking all sublooks for the value. If the attribute
     * only allows one value (e.g. name or icon) first not <CODE>null</CODE> or
     * not <CODE>false</CODE> value is chosen. If the attribute is multivalued
     * (e.g. child nodes or properties) the result is set produced by merging
     * results from all sublooks.
     *
     * <P>
     * To create this composite look from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourNamespaceSelector.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.Look" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Looks.composite" /&gt;
     *   &lt;attr name="context" stringvalue="Looks/Types/" /&gt;
     * &lt;/file&gt;
     * </pre>
     * <BR>
     * The context attribute should point to some Context (usualy a folder on
     * system filesystem) which will contain all sublooks of the look. Content
     * of the folder is equivalent of the delegates parameter. 
     *
     * @see org.netbeans.spi.looks.ProxyLook ProxyLook for more info about looks
     *      composition.
     * @param name Name of the composite look.
     * @param delegates Sublooks of the look.
     * @return new look composed from sublooks
     */
    public static final Look composite( String name, Look[] delegates) {
        return new org.netbeans.modules.looks.CompositeLook( name, delegates );
    }

    /*  Look that serves for switching the looks on LookNodes. The main feature
     * of this look is adding the LookNode into it's own lookup. This means
     * that looking up a LookNode in the Lookup returned from
     * {@link org.netbeans.spi.looks.Look.NodeSubstitute#getLookup() Look.NodeSubstitute#getLookup()}
     * will return LookNode which is the NodeSubstitute associated with. This
     * gives you the opportunity to call
     * {@link LookNode#getLook() getLook()},
     * {@link LookNode#getLookSelector() getLookSelector()} and
     * {@link LookNode#setLook(org.netbeans.spi.looks.Look) setLook(Look)}
     * to determine set of available looks and to set new look.
     * <P>
     * This look also provides an default action and default property sheet
     * which allows for changing looks. In case you want to implement your
     * own UI for changing looks consider wrapping this look into a filter look
     * which will switch of properties and actions.
     * <P>
     * Standard usage of lookSwitcherLook is to use it in decorator
     * on root node of your browser.
     * <P>
     * To create this look from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourLook.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.Look" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Looks.lookSwitcherLook" /&gt;
     * &lt;/file&gt;
     * </pre>
     * @see #filter(String,Look,long)
     * @see #decorator(org.netbeans.spi.looks.LookSelector, org.netbeans.spi.looks.Look, boolean)
     * @return Look which adds LookNode into NodeSubstitute's lookup and
     *         which provides action and property for switching looks.
     */

     // !Uncoment javadoc also
     /*
      public static final Look lookSwitcher() {
        return LOOK_SWITCHER_LOOK;
      }
     */

    public static final Look childrenSelectorProvider( String name, Look look, LookSelector selector ) {
        return new org.netbeans.modules.looks.ChildrenSelectorProvider( name, look, selector );
    }
    

    // Package private helper methods used from Selectors class ----------------

    static Boolean readBooleanAttribute(FileObject fo, String attribute) throws IOException {
        Object value = fo.getAttribute (attribute);
        if (value == null)
            return null;
        if (value instanceof Boolean)
            return (Boolean)value;
        else
            throw new IOException ("Attribute " + attribute + " is not Boolean but: " + value); // NOI18N
    }

    static String readStringAttribute(FileObject fo, String attribute) throws IOException {
        Object value = fo.getAttribute (attribute);
        if (value == null)
            return null;
        if (value instanceof String)
            return (String)value;
        else
            throw new IOException ("Attribute " + attribute + " is not String but: " + value); // NOI18N
    }


    static Object readLookOrSelectorAttribute (FileObject fo, String attribute, boolean look) throws IOException {
        Object value = fo.getAttribute (attribute);
        if (look) {
            if (value instanceof Look) {
                return value;
            }
        } else {
            if (value instanceof LookSelector) {
                return value;
            }
        }

        if (!(value instanceof String)) {
            throw new IOException ("Attribute " + attribute + " is not String but: " + value); // NOI18N
        }

        // lookup delegate
        Object delegate = RegistryBridge.getDefault( fo ).resolve ((String)value);
        if (look) {
            if (delegate instanceof Look) {
                return delegate;
            }
        } 
        else {
            if (delegate instanceof LookSelector) {
                return delegate;
            }
        }
        
        // The delegate is either null or has a wrong class
        
        IOException newEx = new IOException (
            "Look/LookSelector " + value + " not found. Attribute " + attribute + " on " + fo); // NOI18N      

        throw newEx;
    }

    static Look readLookAttribute (FileObject fo, String attribute) throws IOException {
        return (Look)readLookOrSelectorAttribute (fo, attribute, true);
    }


    static private final void initFilterMethods () {
        mapFilterMethods.put ("GET_LOOKUP_ITEMS", new Long (Look.GET_LOOKUP_ITEMS)); // NOI18N
        mapFilterMethods.put ("GET_NAME", new Long (Look.GET_NAME)); // NOI18N
        mapFilterMethods.put ("RENAME", new Long (Look.RENAME)); // NOI18N
        mapFilterMethods.put ("GET_DISPLAY_NAME", new Long (Look.GET_DISPLAY_NAME)); // NOI18N
        mapFilterMethods.put ("GET_SHORT_DESCRIPTION", new Long (Look.GET_SHORT_DESCRIPTION)); // NOI18N
        mapFilterMethods.put ("GET_ICON", new Long (Look.GET_ICON)); // NOI18N
        mapFilterMethods.put ("GET_OPENED_ICON", new Long (Look.GET_OPENED_ICON)); // NOI18N
        mapFilterMethods.put ("GET_HELP_CTX", new Long (Look.GET_HELP_CTX)); // NOI18N
        mapFilterMethods.put ("GET_CHILD_OBJECTS", new Long (Look.GET_CHILD_OBJECTS)); // NOI18N
        mapFilterMethods.put ("GET_NEW_TYPES", new Long (Look.GET_NEW_TYPES)); // NOI18N
        mapFilterMethods.put ("GET_ACTIONS", new Long (Look.GET_ACTIONS)); // NOI18N
        mapFilterMethods.put ("GET_CONTEXT_ACTIONS", new Long (Look.GET_CONTEXT_ACTIONS)); // NOI18N
        mapFilterMethods.put ("GET_DEFAULT_ACTION", new Long (Look.GET_DEFAULT_ACTION)); // NOI18N
        mapFilterMethods.put ("GET_PROPERTY_SETS", new Long (Look.GET_PROPERTY_SETS)); // NOI18N
        mapFilterMethods.put ("GET_CUSTOMIZER", new Long (Look.GET_CUSTOMIZER)); // NOI18N
        mapFilterMethods.put ("CAN_RENAME", new Long (Look.CAN_RENAME)); // NOI18N
        mapFilterMethods.put ("CAN_DESTROY", new Long (Look.CAN_DESTROY)); // NOI18N
        mapFilterMethods.put ("CAN_COPY", new Long (Look.CAN_COPY)); // NOI18N
        mapFilterMethods.put ("CAN_CUT", new Long (Look.CAN_CUT)); // NOI18N
        mapFilterMethods.put ("GET_PASTE_TYPES", new Long (Look.GET_PASTE_TYPES)); // NOI18N
        mapFilterMethods.put ("GET_DROP_TYPE", new Long (Look.GET_DROP_TYPE)); // NOI18N
        mapFilterMethods.put ("CLIPBOARD_COPY", new Long (Look.CLIPBOARD_COPY)); // NOI18N
        mapFilterMethods.put ("CLIPBOARD_CUT", new Long (Look.CLIPBOARD_CUT)); // NOI18N
        mapFilterMethods.put ("DRAG", new Long (Look.DRAG)); // NOI18N
        mapFilterMethods.put ("DESTROY", new Long (Look.DESTROY)); // NOI18N
        mapFilterMethods.put ("HAS_CUSTOMIZER", new Long (Look.HAS_CUSTOMIZER)); // NOI18N
        // mapFilterMethods.put ("ALL_METHODS", new Long (Look.ALL_METHODS)); // NOI18N
        // mapFilterMethods.put ("NO_METHODS", new Long (Look.NO_METHODS)); // NOI18N
    }

}
