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

package org.netbeans.modules.clazz;

import java.beans.*;

import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.cookies.ElementCookie;
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
                           "/org/netbeans/modules/clazz/resources/interfaceBr" : // NOI18N
                           "/org/netbeans/modules/clazz/resources/classBr" // NOI18N
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

/*
* Log
*  12   src-jtulach1.11        1/13/00  David Simonek   i18n
*  11   src-jtulach1.10        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  10   src-jtulach1.9         7/9/99   Petr Hrebejk    Factory chaining fixed
*  9    src-jtulach1.8         7/8/99   Jan Jancura     Special icons for Object 
*       Br.
*  8    src-jtulach1.7         6/28/99  Petr Hamernik   new hierarchy under 
*       ClassChildren
*  7    src-jtulach1.6         6/9/99   Ian Formanek    ToolsAction
*  6    src-jtulach1.5         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  5    src-jtulach1.4         5/16/99  Jaroslav Tulach New hiearchy.
*  4    src-jtulach1.3         4/2/99   Jan Jancura     ObjectBrowser support II.
*  3    src-jtulach1.2         4/1/99   Ian Formanek    Rollback to make it 
*       compilable
*  2    src-jtulach1.1         4/1/99   Jan Jancura     Object browser support
*  1    src-jtulach1.0         3/26/99  David Simonek   
* $
*/
