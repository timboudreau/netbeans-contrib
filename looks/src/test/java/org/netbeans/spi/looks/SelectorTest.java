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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.netbeans.api.nodes2looks.Nodes;
import org.netbeans.api.nodes2looks.TestUtil;
import org.netbeans.junit.NbTestCase;
import org.openide.nodes.Node;
import org.openide.util.Enumerations;
import org.openide.util.Lookup;

/** Various tests for selectors on nodes, including decoration
 *
 * @author Petr Hrebejk. Jiri Rechtacek
 */
public class SelectorTest extends NbTestCase {
    
    public SelectorTest(String name) {
        super(name);
    }
    
    /** Tests whether all nodes inherit the selector through the 
     * hirearchy
     */
    public void testSimpleSelector() {
        
        LookSelector rootSelector = Selectors.selector( new RootProvider( null ) );
        Node root = Nodes.node ( "ROOT", null, rootSelector);
        
        Node[] nodes = root.getChildren ().getNodes ();
        
        // children
        assertEquals("Root has two childner.", 2, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, TestUtil.getLookSelector ( root ));
        Node children[] = root.getChildren().getNodes();
                
        // looks
        assertEquals( "Look of child is a instance of LookForRepObj1", Looks.bean().getClass(), TestUtil.getLook( children[0] ).getClass() );
        assertEquals( "Look of child is a instance of LookForRepObj2", Looks.bean().getClass(), TestUtil.getLook ( children[1] ).getClass() );
        
        // selectors
        assertTrue ("Selector of child 1 ", rootSelector == TestUtil.getLookSelector ( children[0] ) );
        assertTrue ("Selector of child 2 ", rootSelector == TestUtil.getLookSelector( children[1] ) );
    }
    
    /** Tests that the Selector for children changes when provided in lookup
     */    
    public void testChangeSimpleSelector() {
        LookSelector restrictedSelector = Selectors.selector( new RestrictedProvider() );
        LookSelector rootSelector = Selectors.selector( new RootProvider ( restrictedSelector ) );        
        Node root = Nodes.node ( "ROOT", null, rootSelector);
        
        Node[] nodes = root.getChildren ().getNodes ();
        
        // children
        assertEquals("Root has two childner.", 2, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, TestUtil.getLookSelector ( root ));
        Node children[] = root.getChildren().getNodes();
        
        // looks
        assertEquals( "Look of child is a instance of LookForRepObj1", LookForRepObj1.class, TestUtil.getLook( children[0] ).getClass() );
        assertEquals( "Look of child is a instance of LookForRepObj2", LookForRepObj2.class, TestUtil.getLook ( children[1] ).getClass() );
        
        // selectors
        assertTrue ("Selector of child 1 ", restrictedSelector == TestUtil.getLookSelector ( children[0] ) );
        assertTrue ("Selector of child 2 ", restrictedSelector == TestUtil.getLookSelector( children[1] ) );
    }
    
    /*
    public void testChangeDecoratorSelectorExcludable () {
        
        Look rootLook = new RootLook ();
        // isDecorator = true;
        // excludable = true;
        LookSelector rootSelector = new ProxySelector (new RootSelector ());
        Node root = Looks.node (null, rootLook, rootSelector);
        Node[] nodes = root.getChildren ().getNodes ();
        // children
        assertEquals("Root has two childner.", 2, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, TestUtil.getLookSelector ( root ));
        Node n1 = root.getChildren ().findChild ("LookForRepObj1");
        Node n2 = root.getChildren ().findChild ("LookForRepObj2");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof org.netbeans.modules.looks.LookNode);
        assertTrue ("Child is a instance of LookNode", n2 instanceof org.netbeans.modules.looks.LookNode);
        // looks
        assertEquals ("Look of child is a instance of LookForRepObj1", "Composite[Decorated[LookForRepObj1", TestUtil.getLook ( n1 ).getName ());
        assertEquals ("Look of child is a instance of LookForRepObj2", "Composite[Decorated[LookForRepObj2", TestUtil.getLook ( n2 ).getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+ TestUtil.getLookSelector ( n1 ), TestUtil.getLookSelector ( n1 ) instanceof DecoratorSelector);
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+ TestUtil.getLookSelector ( n2 ), TestUtil.getLookSelector ( n2 ) instanceof DecoratorSelector);

        // second level
        nodes = nodes[0].getChildren ().getNodes ();
        // children
        assertEquals("Child has one child.", 1, nodes.length);
        n1 = root.getChildren ().findChild ("LookForRepObj1");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof org.netbeans.modules.looks.LookNode);
        // looks - one times decorated
        assertEquals ("Look of child is a instance of LookForRepObj1", "Composite[Decorated[LookForRepObj1", TestUtil.getLook ( n1 ).getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+ TestUtil.getLookSelector ( n1 ), TestUtil.getLookSelector ( n1 ) instanceof DecoratorSelector);
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+ TestUtil.getLookSelector ( n2 ), TestUtil.getLookSelector ( n2 ) instanceof DecoratorSelector);
        
    }
     */
    
    /*
    public void testChangeDecoratorSelectorNonExcludable () {
        Look rootLook = new RootLook ();
        isDecorator = true;
        excludable = false;
        LookSelector rootSelector = new ProxySelector (new RootSelector ());
        Node root = Looks.node (null, rootLook, rootSelector);
        Node[] nodes = root.getChildren ().getNodes ();
        // children
        assertEquals("Root has two childner.", 2, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, TestUtil.getLookSelector ( root ));
        Node n1 = root.getChildren ().findChild ("LookForRepObj1");
        Node n2 = root.getChildren ().findChild ("LookForRepObj2");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof org.netbeans.modules.looks.LookNode);
        assertTrue ("Child is a instance of LookNode", n2 instanceof org.netbeans.modules.looks.LookNode);
        // looks - two times decorated
        assertEquals ("Look of child is a instance of LookForRepObj1", "Composite[Decorated[Composite[Decorated[LookForRepObj1", TestUtil.getLook ( n1 ).getName ());
        assertEquals ("Look of child is a instance of LookForRepObj2", "Composite[Decorated[Composite[Decorated[LookForRepObj2", TestUtil.getLook ( n2 ).getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+ TestUtil.getLookSelector ( n1 ), TestUtil.getLookSelector ( n1 ) instanceof DecoratorSelector);

        // second level
        nodes = nodes[0].getChildren ().getNodes ();
        // children
        assertEquals("Child has one child.", 1, nodes.length);
        n1 = root.getChildren ().findChild ("LookForRepObj1");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof org.netbeans.modules.looks.LookNode);
        // looks
        assertEquals ("Look of child of child is a instance of LookForRepObj1", "Composite[Decorated[Composite[Decorated[LookForRepObj1", TestUtil.getLook ( n1 ).getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+ TestUtil.getLookSelector ( n1 ), TestUtil.getLookSelector ( n1 ) instanceof DecoratorSelector);      
    }
     */
    
    /*
    public void testChangeContextSelectorExcludable () {
        try {
        Look rootLook = new RootLook ();
        isDecorator = false;
        isStatic = true;
        excludable = true;
        LookSelector rootSelector = new ProxySelector (new RootSelector ());
        LookNode root = new LookNode (null, rootLook, rootSelector);
        Node[] nodes = root.getChildren ().getNodes ();
        // children
        assertEquals("Root has two childner.", 2, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, root.getLookSelector ());
        Node n1 = root.getChildren ().findChild ("LookForRepObj1");
        Node n2 = root.getChildren ().findChild ("LookForRepObj2");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof LookNode);
        assertTrue ("Child is a instance of LookNode", n2 instanceof LookNode);
        // looks
        assertEquals ("Look of child is a instance of LookForRepObj1", "Composite[Decorated[LookForRepObj1", ((LookNode)n1).getLook ().getName ());
        assertEquals ("Look of child is a instance of LookForRepObj2", "Composite[Decorated[LookForRepObj2", ((LookNode)n2).getLook ().getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+((LookNode)n1).getLookSelector ().getName (), ((LookNode)n1).getLookSelector () instanceof DecoratorSelector);
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+((LookNode)n2).getLookSelector ().getName (), ((LookNode)n2).getLookSelector () instanceof DecoratorSelector);
        // second level
        nodes = nodes[0].getChildren ().getNodes ();
        // children
        assertEquals("Root has one child.", 1, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, root.getLookSelector ());
        n1 = root.getChildren ().findChild ("LookForRepObj1");
        n2 = root.getChildren ().findChild ("LookForRepObj2");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof LookNode);
        assertTrue ("Child is a instance of LookNode", n2 instanceof LookNode);
        // looks - one times decorated
        assertEquals ("Look of child is a instance of LookForRepObj1", "Composite[Decorated[LookForRepObj1", ((LookNode)n1).getLook ().getName ());
        assertEquals ("Look of child is a instance of LookForRepObj2", "Composite[Decorated[LookForRepObj2", ((LookNode)n2).getLook ().getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+((LookNode)n1).getLookSelector ().getName (), ((LookNode)n1).getLookSelector () instanceof DecoratorSelector);
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+((LookNode)n2).getLookSelector ().getName (), ((LookNode)n2).getLookSelector () instanceof DecoratorSelector);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public void testChangeContextSelectorNonExcludable () {
        try {
        Look rootLook = new RootLook ();
        isDecorator = false;
        isStatic = true;
        excludable = false;
        LookSelector rootSelector = new ProxySelector (new RootSelector ());
        LookNode root = new LookNode (null, rootLook, rootSelector);
        Node[] nodes = root.getChildren ().getNodes ();
        // children
        assertEquals("Root has two childner.", 2, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, root.getLookSelector ());
        Node n1 = root.getChildren ().findChild ("LookForRepObj1");
        Node n2 = root.getChildren ().findChild ("LookForRepObj2");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof LookNode);
        assertTrue ("Child is a instance of LookNode", n2 instanceof LookNode);
        // looks - two times decorated
        assertEquals ("Look of child is a instance of LookForRepObj1", "Composite[Decorated[Composite[Decorated[LookForRepObj1", ((LookNode)n1).getLook ().getName ());
        assertEquals ("Look of child is a instance of LookForRepObj2", "Composite[Decorated[Composite[Decorated[LookForRepObj2", ((LookNode)n2).getLook ().getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+((LookNode)n1).getLookSelector ().getName (), ((LookNode)n1).getLookSelector () instanceof DecoratorSelector);

        // second level
        nodes = nodes[0].getChildren ().getNodes ();
        // children
        assertEquals("Root has one child.", 1, nodes.length);
        assertEquals("Root LookNode has RootSelector.", rootSelector, root.getLookSelector ());
        n1 = root.getChildren ().findChild ("LookForRepObj1");
        // look nodes
        assertTrue ("Child is a instance of LookNode", n1 instanceof LookNode);
        // looks
        assertEquals ("Look of child of child is a instance of LookForRepObj1", "Composite[Decorated[Composite[Decorated[LookForRepObj1", ((LookNode)n1).getLook ().getName ());
        // selectors
        assertTrue ("Selector of child is a instance of DecoratorSelector, was "+((LookNode)n1).getLookSelector ().getName (), ((LookNode)n1).getLookSelector () instanceof DecoratorSelector);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
     */
    
    // Selectors for testing ---------------------------------------------------
    
    public static class RootProvider implements  LookProvider {
    
        private Look stringLook;
        private Look r1Look;
        private Look r2Look;
        
        public RootProvider( LookSelector chidrenSelector ) {
            if ( chidrenSelector == null ) {
                stringLook = new RootLook( null );
                r1Look = Looks.bean();
                r2Look = Looks.bean();
            }
            else {
                stringLook = new RootLook( chidrenSelector );
                r1Look = LookForRepObj1.INSTANCE;    
                r2Look = LookForRepObj2.INSTANCE;
            }
        }

        public Enumeration getLooksForObject(Object representedObject) {
            if (representedObject instanceof RepObj1) {
                return Enumerations.singleton(r1Look);
            }
            else if ( representedObject instanceof RepObj2 ) {
                return Enumerations.singleton(r2Look);
            }
            else if ( representedObject instanceof String ) {
                return Enumerations.singleton(stringLook);
            }
            else {
                return Enumerations.singleton(Looks.bean());
            }
        }
        
    }
    
    public class RestrictedProvider implements  LookProvider {
        
        public RestrictedProvider() {
        }
        
        public Enumeration getLooksForObject( Object representedObject ) {
            if ( representedObject instanceof  RepObj1 ) {
                return Enumerations.singleton(LookForRepObj1.INSTANCE);
            }
            else if ( representedObject instanceof RepObj2 ) {                
                return Enumerations.singleton(LookForRepObj2.INSTANCE);
            }
            else
                throw new RuntimeException ("Wrong type of represented object, was "+representedObject);
        }
                
    }
    
    // proxy selector
    
    /*
    public class ProxySelector extends LookSelector {
        
        private LookSelector delegate;
        
        public ProxySelector (LookSelector delegate) {
            this.delegate = delegate;
        }
        
        public Enumeration getLooks(Object representedObject) {
            if (isDecorator)
                return Collections.enumeration (Arrays.asList (new Object[] { Selectors.decorator (delegate, new DecoratorLook () ) }));
            // else if (isStatic)
            //    return Collections.enumeration (Arrays.asList (new Object[] { Looks.contextSelector (delegate, new DecoratorLook (), true, excludable) }));
            else
                return Collections.enumeration (Arrays.asList (new Object[] { delegate }));
        }
        
    }
    */
    
    
    // selector for represented object 1
    
    public class ProviderForRepObj1 implements  LookProvider {
        private String name = "SelectorForRepObj1";
        
        public ProviderForRepObj1 () {
        }
        
        public Enumeration getLooksForObject(Object representedObject) {
            if (representedObject instanceof RepObj1)
                return Collections.enumeration (Arrays.asList (new Object[] { new LookForRepObj1 () }));
            else
                throw new RuntimeException ("Wrong type of represented object, was "+representedObject);
        }
        
    }
    
    // selector for represented object 2
    
    public class ProviderForRepObj2 implements LookProvider {
        private String name = "SelectorForRepObj2";
        
        public ProviderForRepObj2 () {
        }
        
        public Enumeration getLooksForObject(Object representedObject) {
            if (representedObject instanceof RepObj2)
                return Collections.enumeration (Arrays.asList (new Object[] { new LookForRepObj2 () }));
            else
                throw new RuntimeException ("Wrong type of represented object, was "+representedObject);
        }
                
    }
    
    // Looks for testing -------------------------------------------------------
    
    private static class RootLook extends Look {
                
        private LookSelector childrenSelector;
        
        public RootLook ( LookSelector childrenSelector ) {
            super( "RootLook" );
            this.childrenSelector = childrenSelector;
        }
                
        public String getName( Object representedObject, Lookup env ) {
            return getName();
        }

        public boolean isLeaf ( Object representedObject, Lookup env ) {
            return false;
        }

        public List getChildObjects( Object representedObject, Lookup env ) {
            ArrayList objects = new ArrayList ();
            objects.add (new RepObj1 ("RepObj1"));
            objects.add (new RepObj2 ("RepObj2"));
            return objects;            
        }
        
        public Collection getLookupItems( Object representedObject, Lookup env ) {
            if ( childrenSelector != null ) {
                ArrayList items = new ArrayList();
                items.add( new GoldenValue.TestLookupItem( childrenSelector ) );
                return items;
            }
            else {
                return null;
            }
        }
        
    }

    private static class LookForRepObj1 extends Look {
        
        public static Look INSTANCE = new LookForRepObj1();
        
        public LookForRepObj1 () {
            super( "LookForRepObj1" );
        }
        
        public String getName( Object representedObject, Lookup env ) {
            return getName();
        }

        public boolean isLeaf( Object representedObject, Lookup env ) {
            return false;
        }
        
        public List getChildObjects(  Object representedObject, Lookup env ) {
            ArrayList objects = new ArrayList ();
            objects.add (new RepObj1 ("RepObj1"));
            return objects;
        }
    }

    private static class LookForRepObj2 extends Look {
        
        public static Look INSTANCE = new LookForRepObj2();
        
        public LookForRepObj2 () {
            super( "LookForRepObj2" );
        }
        
        public String getDisplayName() {
            return getName();
        }
        
        public String getName( Object representedObject, Lookup env ) {
            return getName();
        }

        public boolean isLeaf( Object representedObject, Lookup env ) {
            return false;
        }
        
        public List getChildObjects( Object representedObject, Lookup env ) {
            ArrayList objects = new ArrayList ();
            objects.add (new RepObj2 ("RepObj2"));
            return objects;
        }
    }

    private static class DecoratorLook extends Look {
        
        
        public DecoratorLook () {
            super( "DecoratorLook" );
        }
        
        
        public String getName(  Object representedObject, Lookup env  ) {
            return getName();
        }
        
        public boolean isLeaf (  Object representedObject, Lookup env ) {
            return true;
        }
    }

    // Represented objects for testing -----------------------------------------
    
    public static class RepObj1 extends Object {
        
        private String name;
        
        public RepObj1 (String name) {
            this.name = name;
        }
        
        public String toString () {
            return "Represented object "+name;
        }
    }

    public static class RepObj2 extends Object {
        private String name;
        
        public RepObj2 (String name) {
            this.name = name;
        }
        
        public String toString () {
            return "Represented object "+name;
        }
    }
     
     
}
