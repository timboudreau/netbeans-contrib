/*
 * NodeFacets.java
 *
 * Created on 2. duben 2006, 13:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.api.adnode;

import java.awt.datatransfer.Transferable;
import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.modules.adnode.AdaptableNodes;
import org.openide.nodes.Node;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/** An API to deal with Nodes and their Facets.
 *
 * @author Jaroslav Tulach
 */
public class NodeFacets {
    
    /** Creates a new instance of NodeFacets */
    private NodeFacets() {
    }

    /** Factory method that creates a node for given {@link Adaptor} and
     * a represented object.
     *
     * @param adaptor the adaptor that provides Facets.
     * @param obj the object to create the node for
     * @return node that follows suggestions provided by the adaptor
     */
    public static Node create(Adaptor adaptor, Object obj) {
        return AdaptableNodes.create(adaptor, obj);
    }


    /** Identifies a capability (in terms of {@link Adaptable} interface
     * that defines one can show a wizard.
     */
    public interface Customizable {
        /** Creates a customizer for this object.
         *
         * @return a UI customizer for this object
         */
        public java.awt.Component getCustomizer();
    }

    /** Describes ability of an object to accept a drop of a drag.
     */
    public interface Drop {
        /** Given the input arguments, creates a possible paste type
         * that describes what will happen if user really drops the transferable
         * on this object.
         *
         * @param t transferable to paste
         * @param action the drag'n'drop action to do DnDConstants.ACTION_MOVE, ACTION_COPY, ACTION_LINK
         * @param index index between children the drop occured at or -1 if not specified
         * @return null if the transferable cannot be accepted or the paste type
         *    to execute when the drop occures
         */
        public PasteType getDropType(Transferable t, int action, int index);
    }

    /** Describes ability of an object to create new objects inside itself.
     */
    public interface NewTypes {
        /** Creates the array of possible new ways how to create something new
         * in this object.
         *
         * @return the array of new types
         */
        public NewType[] getNewTypes();
    }

    /** Describes ability of an object to accept a transferable, possibly in
     * many different ways.
     */
    public interface PasteTypes {
        /** Creates the array of possible ways how to paste given transfreable
         * into this object.
         *
         * @param t transferable to paste
         * @return the array of paste types
         */
        public PasteType[] getPasteTypes(Transferable t);
    }

    /** The source of capability to produce a set of properties.
     * Useful for objects that would like to present themselves in
     * property sheet.
     */
    public interface SetOfProperties {
        /** Creates the set of properties associated with this object.
         * @return the array of property sets
         */
        public Node.PropertySet[] getPropertySets();
    }
}
