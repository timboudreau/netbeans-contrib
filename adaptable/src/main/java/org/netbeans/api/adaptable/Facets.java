package org.netbeans.api.adaptable;

import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.List;
import javax.swing.Action;

/** Definition of general facets like display name, identity, etc.
 *
 * @author Jaroslav Tulach
 * @since 0.3
 */
public final class Facets {
    
    /** Creates a new instance of Facet */
    private Facets() {
    }


    /** Adaptable interface providing name of an object.
     */
    public interface Identity {
        /** The name of the object.
         */
        public String getId();
    }

    /** Ability to rename an object.
     *
     */
    public interface Rename {
        /** Asks the object to rename itself. This interface is usually connected
         * with {@link Identity} one so rename to {@link Identity#getId} shall be
         * no-op operation. Also a rename can mean a change to the identity, so
         * that the {@link Identity#getId} can change its value.
         *
         * @param newName the new name for the object
         * @exception throws something, if the operation fails. It is suggested
         *   that the thrown exception has meaningful localized message as it
         *   may be necessary to show the message to user
         */
        public void rename(String newName) throws Exception;
    }


    /** Adaptable interface providing human readable short description
     * (one line) of the object.
     */
    public interface ShortDescription {
        /** The name of the object.
         */
        public String getShortDescription();
    }

    /** Adaptable interface providing HTML version of human readable
     *  name of an object.
     */
    public interface HtmlDisplayName {
        /** The name of the object.
         */
        public String getHtmlDisplayName();
    }

    /** Associates a delete operation with an object.
     */
    public interface Delete {
        /** Deletes the object.
         *
         * @return transferable with drag of this object
         * @exception Exception an exception showing a failure. It is expected
         *    that its getLocalizedMessage will be meaningul
         */
        public void delete() throws Exception;
    }

    /** Adaptable interface providing human readable name of an object.
     */
    public interface DisplayName {
        /** The display name of the object.
         */
        public String getDisplayName();
    }

    /** Called when one wants to drag the object.
     */
    public interface Drag {
        /** Creates a transferable representing the drag of this object.
         *
         * @return transferable with drag of this object
         * @exception IOException if something goes wrong when creating the transferable
         */
        public Transferable drag() throws IOException;
    }

    /** Creates a copy of the object in a sense of Ctrl-C by creating
     * a transfrable that can then be put into clipboard and pasted anywhere.
     */
    public interface Copy {
        /** Creates a transferable representing copy of this object.
         *
         * @return transferable with copy of this object
         * @exception IOException if something goes wrong when creating the transferable
         */
        public Transferable copy() throws IOException;
    }

    /** Does a cut of the object in a sense of Ctrl-X by creating
     * a transfrable that can then be put into clipboard and pasted anywhere.
     */
    public interface Cut {
        /** Creates a transferable representing cut of this object. Whether
         * the object is destoryed during the time of the cut or later during
         * paste is undefined and depends on the characterics of the modified
         * object.
         *
         * @return transferable with cut of this object
         * @exception IOException if something goes wrong when creating the transferable
         */
        public Transferable cut() throws IOException;
    }

    /** Associates certain user actions with the object.
     */
    public interface ActionProvider {
        /** Getter for the default action that shall be invoked on the
         * object when a default action activation gesture is made.
         *
         * @return an action or <code>null</code>
         */
        public Action getPreferredAction();

        /** An array of actions that make sence on the object. Can
         * be used to contruct a popup menu.
         *
         * @return array of supported actions or <code>null</code>
         */
        public Action[] getActions();
    }

    /** Gives an object a capability to have children
     */
    public interface SubHierarchy {
        /** Computes the list of objects that are supposed to be displayed
         * as children of this object. The list can be lazy - e.g. important
         * is to compute its size, its children can be computed later when
         * the list is asked.
         *
         * @return unmodifiable list of child objects
         */
        public List<? extends Object> getChildren();
    }

}
