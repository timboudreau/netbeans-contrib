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
import org.openide.filesystems.FileObject;
import org.netbeans.modules.looks.SelectorImplFactory;

/** Static factory class providing useful look selectors. For looks which
 * can be constructed using <em>module layer</em>
 * <code>XMLFileSystem</code>s, a sample of such XML definition is given
 * in their Javadoc.
 *
 * @author  Petr Hrebejk
 */
public abstract class Selectors {

    /** no instances */
    private Selectors() {}

    // Names of attributes
    private static final String DECORATING_LOOK = "decoratingLook"; // NOI18N
    private static final String CONTEXT = Looks.CONTEXT;
    private static final String LOOK_SELECTOR = Looks.LOOK_SELECTOR;
    // Default values
    private static final String DEFAULT_CONTEXT = "Looks/Types/"; // NOI18N
    // private static final String AS_LAST = "asLast"; // NOI18N
    // private static final String EXCLUDABLE = "excludable"; // NOI18N

    /* The only default selector in the system */
    private static LookSelector DEFAULT_SELECTOR;
    
    // Metheods to be used from XML layers -------------------------------------

    /** A method to be used from XML layers
     */
    static final LookSelector namespaceTypes(FileObject fo) throws IOException {
        String contextPrefix = Looks.readStringAttribute (fo, CONTEXT);
        if (contextPrefix == null)
            contextPrefix = DEFAULT_CONTEXT;

        org.netbeans.modules.looks.RegistryBridge rb = 
            org.netbeans.modules.looks.RegistryBridge.getDefault( fo );
        
        return new LookSelector( SelectorImplFactory.namespaceTypes( rb, contextPrefix ) ); 
    }

    /** A method to be used from XML layers
     */
    static final LookSelector decorator(FileObject fo) throws IOException {
        return createDecoratorSelector (fo);
    }

    // Methods to be used from code --------------------------------------------

    /** Creates a LookSelector based on LookProvider which will never change
     * it's content.
     * @see LookProvider
     * @param provider The concrete implementation of finding the Looks
     * @return LookSelector based on given LookProvider
     */
    public static synchronized final LookSelector selector( LookProvider provider ) {
        return new LookSelector( SelectorImplFactory.provider( provider ) );
    }

    /** Creates a LookSelector which may change it's content.
     * @see ChangeableLookProvider
     * @param provider The concrete implementation of finding the Looks and
     *        firing changes of the LookSelector content
     * @return LookSelector based on given LookProvider
     */
    public static synchronized final LookSelector selector( ChangeableLookProvider provider ) {
        return new LookSelector( SelectorImplFactory.changeableProvider( provider ) );
    }

    /** Utility method, creates a LookSelector which will allways return
     *  the look put into this method as a parameter.
     *
     * @param delegate The Look to be returned from the selector.
     * @return LookSelector returning Enumeration which contains given look.
     */
    public static final LookSelector singleton( Look delegate ) {
        return new LookSelector( SelectorImplFactory.singleton( delegate ) );
    }

    /** Utility method, creates a LookSelector which will allways return
     *  the looks put into this method as a parameter.
     *
     * @param delegates Array of Looks to be returned from the selector.
     * @return LookSelector returning Enumeration which contains given looks.
     */
    public static final LookSelector array( Look delegates[] ) {
        return new LookSelector( SelectorImplFactory.array( delegates ) );
    }

    /** Utility method, creates a LookSelector which selects the first only Look from another
     * LookSelector which it delegates to.
     *
     * @param delegate LookSelector from which the first Look only will be returned
     * @return LookSelector returning Enumeration which contains the first Look selected by
     * delegate selector for given represented object
     */
    public static final LookSelector first( LookSelector delegate ) {
        return new LookSelector( SelectorImplFactory.first( delegate ) );
    }

    /** NamespaceSelector which searches for a look in default namespace
     * "Looks/Types/".
     *
     * <P>
     * To create this namespace selector from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourNamaspaceSelector.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.LookSelector" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Selectors.defaultTypes" /&gt;
     * &lt;/file&gt;
     * </pre>
     * @see #namespaceTypes(String)
     * @return default namespace selector searching for looks in "Looks/Types"
     *         on system filesystem.
     */
    public static synchronized final LookSelector defaultTypes() {
        if (DEFAULT_SELECTOR == null) {
            DEFAULT_SELECTOR = new LookSelector( SelectorImplFactory.namespaceTypes( DEFAULT_CONTEXT ) );
        }
        return DEFAULT_SELECTOR;
    }

    /** Searches for a look in namespace given name of context to search. Name
     * of the look should correspond with a type (class, superclass, interface)
     * of the represented object.
     *
     * <P>
     * To create this namespace selector from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourNamaspaceSelector.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.LookSelector" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Selectors.namespaceTypes" /&gt;
     *   &lt;!--Optionally! The context prefix for the namespace. The default prefix is "Looks/Types/".--&gt;
     *   &lt;attr name="context" stringvalue="Looks/Selectors/MyTypes/" /&gt;
     * &lt;/file&gt;
     * </pre>
     *
     * @param contextPrefix the Context to be searched
     * @return LookSelector searching the given Context by type of the
     *         represented object.
     */
    public static final LookSelector namespaceTypes( String contextPrefix ) {
        return new LookSelector( SelectorImplFactory.namespaceTypes( contextPrefix ) );
    }

    // * &lt;!--Optionally! The additionally features are append at last. The default value is false.--$gt;
    // *   &lt;attr name="asLast" boolvalue="true" /&gt;
    // * @param asLast If true the decoratingLook will be the second look in the
    // *        resulting composite looks. If true the decorating look will
    // *        occupy the first place.
    // * @param excludable If set to true the decoration will stop on next
    // *        change of LookSelector in the nodes hierarchy.
    // The excludable parameter determines
    // * whether decorating should stop on next change of selector in the node
    // * hierarchy. I.e. if the excludable parameter is set to <CODE>true</CODE>
    // * then the decoration will stop as soon as some node in the nodes hierarchy
    // * will heave some other LookSelector set. If parameter is set to <CODE>false</CODE>
    // * then the decoration will be effective throughout the complete node hierarchy.

    /** Decorates looks found by given selector, with a decorating look.
     * Works exactly as the decorator. Resulting looks are composite
     * looks made from the look found by the selector and the decorating look.
     *
     * <P>
     * To create this decorator selector from an XML layer type:
     * <pre>
     * &lt;file name="NameOfYourDecoratorSelector.instance" &gt;
     *   &lt;attr name="instanceClass" stringvalue="org.netbeans.spi.looks.LookSelector" /&gt;
     *   &lt;attr name="instanceCreate" methodvalue="org.netbeans.spi.looks.Selectors.decorator" /&gt;
     *   &lt;!--The path to declaration of look selector searches for the looks.--&gt;
     *   &lt;attr name="lookSelector" stringvalue="Looks/Selectors/MyTypes/" /&gt;
     *   &lt;!--The path to declaration of decorating look.--&gt;
     *   &lt;attr name="decoratingLook" stringvalue="Looks/MyLayer/MyLooks/FooLook" /&gt;
     *   &lt;!--Optionally! The additionally features are append at last. The default value is false.--&gt;
     * &lt;/file&gt;
     * </pre>
     *
     * @param lookSelector Selector used for searching for Looks for given represented
     *        object
     * @param decoratingLook Look which will decorate all the subnodes.
     * @return LookSelector which adds the decorating look to every look provided
     *         in the parameter selector.
     *
     */
    public static final LookSelector decorator( LookSelector lookSelector, Look decoratingLook ) {
        return new LookSelector( SelectorImplFactory.decorator( lookSelector, decoratingLook, true, true ) );        
    }

    /*
    public static final LookSelector decorator( LookSelector selector, Look decoratingLook, boolean asLast, boolean excludable ) {
        return new org.netbeans.modules.looks.DecoratorSelector( selector, decoratingLook, asLast, excludable );
    }
    */

    /** Creates composite selector which merges all selectors given as
     *  parameter and removed duplicities.
     * @param selectors Array of selctors to be merged.
     * @return Selector which merges the selectors
     */
    public static final LookSelector composite( LookSelector[] selectors ) {
        return new LookSelector( SelectorImplFactory.composite( selectors, true ) );        
    }

    // Usefull look selectors may be published later ---------------------------


    static synchronized final LookSelector selector( org.netbeans.modules.looks.NamespaceLookProvider provider, String prefix ) {
        return new LookSelector( SelectorImplFactory.namespaceProvider( provider, prefix ) );
    }


    
    // Private methods ---------------------------------------------------------

    private static LookSelector createDecoratorSelector(FileObject fo) throws IOException {
        Look decorator = Looks.readLookAttribute (fo, DECORATING_LOOK);
        LookSelector selector = readLookSelectorAttribute (fo, LOOK_SELECTOR);

        // proccess parameters
        // 1. read asLast, default value is false
        // 2. read excludable, default value is excludable
        /*
        Boolean helpBool = null;

        helpBool = readBooleanAttribute (fo, AS_LAST);
        boolean asLast = helpBool != null && helpBool.booleanValue () ? true : false;

        helpBool = readBooleanAttribute (fo, EXCLUDABLE);
        boolean excludable = helpBool != null && helpBool.booleanValue () ? true : false;

        return new org.netbeans.modules.looks.DecoratorSelector (selector, decorator, asLast, excludable );
        */
        return new LookSelector( SelectorImplFactory.decorator( selector, decorator, true, true ) );
    }

    private static LookSelector readLookSelectorAttribute (FileObject fo, String attribute) throws IOException {
        return (LookSelector)Looks.readLookOrSelectorAttribute (fo, attribute, false);
    }
}
