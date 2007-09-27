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
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import org.netbeans.spi.looks.Look;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/**
 * Utility Class which allows to use LookNodes on existing Nodes.
 * The class assumes that represented object is a Node. If there is
 * Node.Interior representing other object than a ndoe the look delegates
 * to {DefaultLook#INSTANCE} i.e. returns neutral values.<BR>
 * Recomended usage of this class is to be used as delegatee.
 * <CODE>
 * public class MyNodeLook extends AcceptorLook {
 *
 *     public MyNodeLook() {
 *         super( new NodeProxyLook(), MyNode.Class );
 *         // set mask or do other initializations
 *     }
 * }
 * </CODE>
 *
 * @author Petr Hrebejk
 */
final class NodeProxyLook extends Look {

    private static final Lookup.Template TEMPLATE_ALL =
        new Lookup.Template( Object.class );

    private NodeEventTranslator eventTranslator = new NodeEventTranslator();

    /** Creates new NodeProxySupport */
    public NodeProxyLook( String name ) {
        super( name );
        // nodeListener = new NodeEventTranslator( this );
    }

    // Methods of look itself --------------------------------------------------

    /** Specifies the node to which the Look delegates. The default
     * implementation returns <CODE>substitute.getRepresentedObject()</CODE>
     * casted to Node in case the represented object is a Node in other cases
     * it returns null. If you override this method you can provide delegation
     * nodes for different types of object. (E.g. by calling
     * dataObject.getNodeDelegate() ).
     */
    protected Node getNodeDelegate( Object representedObject ) {

        if ( representedObject instanceof Node ) {
            return (Node)representedObject;
        }
        else {
            return null;
        }
    }

    // General methods ---------------------------------------------------------

    public void attachTo(Object representedObject) {

        if ( representedObject instanceof Node ) {
            Node dn = (Node)representedObject;
            // Register listener to the node to pass events forward
            dn.addNodeListener( eventTranslator );
            dn.addPropertyChangeListener( eventTranslator.propertyChangeListener );
        }
        else {
            throw new IllegalArgumentException( "Represented object has to be a node" );
        }
    }


    public void detachFrom( Object representedObject ) {
        if ( representedObject instanceof Node ) {
            Node dn = (Node)representedObject;
            // Register listener to the node to pass events forward
            dn.removeNodeListener( eventTranslator );
            dn.removePropertyChangeListener( eventTranslator.propertyChangeListener );
        }
    }

    // Methods for FUNCTIONALITY EXTENSIONS ------------------------------------

    public Collection getLookupItems(Object representedObject, Lookup oldEnv ) {
        if ( representedObject instanceof Node ) {
            Node dn = (Node)representedObject;
            Lookup lookup = dn.getLookup();
            Lookup.Result result = lookup.lookup( TEMPLATE_ALL );
            return result.allItems();
        }
        else {
            throw new IllegalArgumentException( "Represented object has to be a node: " + representedObject );
        }
    }

    // Methods for STYLE -------------------------------------------------------

    public String getDisplayName( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
            return dn.getDisplayName();
        }
    }

    public String getName( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
            return dn.getName();
        }
    }

    public void rename( Object representedObject, String newName, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn != null) {
            dn.setName( newName );
        }
    }

    public String getShortDescription ( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
            return dn.getShortDescription();
        }
    }

    public java.awt.Image getIcon( Object representedObject, int type, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
            return dn.getIcon( type );
        }
    }

    public java.awt.Image getOpenedIcon( Object representedObject, int type, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
            return dn.getOpenedIcon( type );
        }
    }

    public HelpCtx getHelpCtx( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
            return dn.getHelpCtx();
        }
    }

    // Methods for CHILDREN ----------------------------------------------------

    public List getChildObjects( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return null;
        } else {
           return Arrays.asList( dn.getChildren().getNodes() );
        }
    }

    public boolean isLeaf( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if (dn == null) {
            return true;
        } else {
           return dn.isLeaf();
        }
    }

    // Methods for ACTIONS & NEW TYPES -----------------------------------------

    public NewType[] getNewTypes( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getNewTypes();
        }
    }

    public Action[] getActions(Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getActions(false);
        }
    }

    public Action[] getContextActions(Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getActions(true);
        }
    }

    public Action getDefaultAction(Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getDefaultAction();
        }
    }

    // Methods for PROPERTIES AND CUSTOMIZER -----------------------------------

    public Node.PropertySet[] getPropertySets( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getPropertySets();
        }
    }

    public Component getCustomizer(Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null || !dn.hasCustomizer() ) {
            return null;
        }
        else {
            return dn.getCustomizer();
        }
    }

    public boolean hasCustomizer( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        return dn != null && dn.hasCustomizer ();
    }

    // Methods for CLIPBOARD OPERATIONS ----------------------------------------

    public boolean canRename( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return false;
        }
        else {
            return dn.canRename();
        }
    }

    public boolean canDestroy( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return false;
        }
        else {
            return dn.canDestroy();
        }
    }

    public boolean canCopy( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return false;
        }
        else {
            return dn.canCopy();
        }
    }

    public boolean canCut( Object representedObject, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return false;
        }
        else {
            return dn.canCut();
        }
    }

    public PasteType[] getPasteTypes( Object representedObject, Transferable t, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getPasteTypes( t );
        }
    }

    public PasteType getDropType( Object representedObject, Transferable t, int action, int index, Lookup env ) {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.getDropType( t, action, index );
        }
    }

    public Transferable clipboardCopy( Object representedObject, Lookup env ) throws IOException {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.clipboardCopy();
        }
    }

    public Transferable clipboardCut( Object representedObject, Lookup env ) throws IOException {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.clipboardCut();
        }
    }

    public Transferable drag( Object representedObject, Lookup env ) throws IOException {
        Node dn = getNodeDelegate( representedObject );
        if ( dn == null ) {
            return null;
        }
        else {
            return dn.drag();
        }
    }

    public void destroy( Object representedObject, Lookup env ) throws IOException {
        Node dn = getNodeDelegate( representedObject );
        if ( dn != null ) {
            dn.destroy();
        }
    }

    /** The human presentable name of the look.
     * @return human presentable name
     */
    public String getDisplayName() {
        return NbBundle.getMessage (NodeProxyLook.class, "LAB_NodeProxyLook"); // NOI18N
    }


    // Innerclasses ------------------------------------------------------------

    private class NodeEventTranslator implements NodeListener {

        private NodePropertyChangeTranslator propertyChangeListener = new NodePropertyChangeTranslator();

        /*
        public void unregister( Object representedObject ) {
            ((Node)representedObject).removeNodeListener( this );
            ((Node)representedObject).removePropertyChangeListener( propertyChangeListener );
        }
        */

        public void childrenAdded( NodeMemberEvent e ) {
            fireChange( e.getSource(), Look.GET_CHILD_OBJECTS );
        }

        public void childrenRemoved( NodeMemberEvent e ) {
            fireChange( e.getSource(), Look.GET_CHILD_OBJECTS );
        }

        public void childrenReordered( NodeReorderEvent e ) {
            fireChange( e.getSource(), Look.GET_CHILD_OBJECTS );
        }

        public void nodeDestroyed( NodeEvent e ) {
            fireChange( e.getSource(), Look.DESTROY );
        }

        public void propertyChange( PropertyChangeEvent e ) {

            if ( Node.PROP_NAME.equals( e.getPropertyName() ) ) {
                fireChange( e.getSource(),  Look.GET_NAME );
            }
            else if ( Node.PROP_DISPLAY_NAME.equals( e.getPropertyName() ) ) {
                fireChange(  e.getSource(), Look.GET_DISPLAY_NAME );
            }
            else if ( Node.PROP_SHORT_DESCRIPTION.equals( e.getPropertyName() ) ) {
                fireChange(  e.getSource(), Look.GET_SHORT_DESCRIPTION );
            }
            else if ( Node.PROP_ICON.equals( e.getPropertyName() ) ) {
                fireChange( e.getSource(), Look.GET_ICON );
            }
            else if ( Node.PROP_OPENED_ICON.equals( e.getPropertyName() ) ) {
                fireChange( e.getSource(), Look.GET_OPENED_ICON );
            }
            // else if ( Node.PROP_PARENT_NODE.equals( e.getPropertyName() ) ) {
            //    property
            // }
            else if ( Node.PROP_PROPERTY_SETS.equals( e.getPropertyName() ) ) {
                fireChange(  e.getSource(), Look.GET_PROPERTY_SETS );
            }
            else if ( Node.PROP_LEAF.equals( e.getPropertyName() ) ) {
                fireChange( e.getSource(), GET_CHILD_OBJECTS );
            }
            else if ( Node.PROP_COOKIE.equals( e.getPropertyName() ) ) {
                fireChange( e.getSource(), GET_LOOKUP_ITEMS );
            }
        }

        private class NodePropertyChangeTranslator implements PropertyChangeListener {


            public void propertyChange( PropertyChangeEvent e ) {
                firePropertyChange(  e.getSource(), e.getPropertyName() );
            }
        }
    }

}
