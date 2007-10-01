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

package org.netbeans.modules.clazz;

import java.beans.*;

import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.cookies.FilterCookie;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.src.*;
import org.openide.src.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

/** The implementation of hierarchy nodes factory for the class loader.
*
* @author Petr Hamernik, Dafe Simonek
*/
final class ClassElementNodeFactory extends DefaultFactory {

    private final FactoryGetterNode FACTORY_GETTER_NODE = new FactoryGetterNode();

    /** Default instance of this factory. */
    private static DefaultFactory instance;

    /** Array of the actions for element nodes */
    private static SystemAction[] defaultActions;

    /** Create nodes for tree */
    private boolean tree = false;

    /** Creates new factory. */
    public ClassElementNodeFactory() {
        super(false);
    }

    /** If true generate nodes for tree.
    */
    public void setGenerateForTree (boolean tree) {
        this.tree = tree;
    }

    /** Returns true if generate nodes for tree.
    * @returns true if generate nodes for tree.
    */
    public boolean getGenerateForTree () {
        return tree;
    }

    /** Returns the node asociated with specified element.
    * @return ElementNode
    */
    public Node createMethodNode(final MethodElement element) {
        MethodElementNode n = new MethodElementNode(element, false);
        n.setDefaultAction(SystemAction.get(PropertiesAction.class));
        n.setActions(getDefaultActions());
        return n;
    }

    /** Returns the node asociated with specified element.
    * @return ElementNode
    */
    public Node createConstructorNode(ConstructorElement element) {
        ConstructorElementNode n = new ConstructorElementNode(element, false);
        n.setDefaultAction(SystemAction.get(PropertiesAction.class));
        n.setActions(getDefaultActions());
        return n;
    }

    /** Returns the node asociated with specified element.
    * @return ElementNode
    */
    public Node createFieldNode(FieldElement element) {
        FieldElementNode n = new FieldElementNode(element, false);
        n.setDefaultAction(SystemAction.get(PropertiesAction.class));
        n.setActions(getDefaultActions());
        return n;
    }

    /** Returns the node asociated with specified element.
    * @return ElementNode
    */
    public Node createClassNode (final ClassElement element) {
        if ( element == null ) {
            return FACTORY_GETTER_NODE;
        }
        if (tree) {
            ClassChildren ch = new ClassChildren(ClassDataObject.getBrowserFactory(), element);
            ClassElementNode n = new ClassElementNode(element, ch, false) {
                {
                    getCookieSet().add((FilterCookie)getChildren());
                }
            };

            n.setElementFormat(new ElementFormat (
                                   NbBundle.getBundle (ClassElementNodeFactory.class).getString("CTL_Class_name_format")
                               ));

            // filter out inner classes
            ClassElementFilter cel = new ClassElementFilter ();
            cel.setOrder (new int[] {
                              ClassElementFilter.CONSTRUCTOR + ClassElementFilter.METHOD,
                              ClassElementFilter.FIELD,
                          });
            ch.setFilter (cel);
            n.setActions(getDefaultActions());
            n.setIconBase (element.isInterface () ?
                           "org/netbeans/modules/clazz/resources/interfaceBr" : // NOI18N
                           "org/netbeans/modules/clazz/resources/classBr" // NOI18N
                          );
            return n;
        }
        else {
            Children ch = createClassChildren(element, ClassDataObject.getExplorerFactory() );
            ClassElementNode n = new ClassElementNode(element, ch, false);
            n.setActions(getDefaultActions());
            return n;
        }
    }

    /** Convenience method for obtaining default actions of nodes */
    SystemAction[] getDefaultActions () {
        if (defaultActions == null) {
            defaultActions = new SystemAction[] {
                                 SystemAction.get(ToolsAction.class),
                                 SystemAction.get(PropertiesAction.class),
                             };
        }
        return defaultActions;
    }

    /** Returns instance of this element node factory */
    static DefaultFactory getInstance () {
        if (instance == null)
            instance = new ClassElementNodeFactory();
        return instance;
    }


    /** This is an unusuall use of Node and FilterCookie */

    private class FactoryGetterNode extends AbstractNode implements FilterCookie {

        FactoryGetterNode( ) {
            super ( Children.LEAF );
        }

        public synchronized Node.Cookie getCookie( Class clazz ) {
            if ( clazz == FilterFactory.class )
                return this;
            else
                return super.getCookie( clazz );
        }

        public Class getFilterClass() {
            return null;
        }

        public void setFilter( Object filter ) {}

        public Object getFilter( ) {
            if ( tree )
                return ClassDataObject.getBrowserFactory();
            else
                return ClassDataObject.getExplorerFactory();
        }

    }

}
