/*
*/
package org.netbeans.modules.javanavigators;
import java.util.Comparator;
import java.util.List;
import javax.swing.ListModel;
import org.netbeans.misc.diff.Diff;

/**
 * A sortable ListModel with a generic type, and some collections support
 *
 * @author Tim Boudreau
 */
public interface GenerifiedListModel <T> extends ListModel {
    /**
     * Equivalent of getElementAt()
     * @return an object at this index in the model
     */
    public T get (int index);
    /**
     * Get a read-only, typed java.util.List of the contents of this
     * ListModel
     * @return a List of the full contents of the model
     */
    public List <T> getContents();
    /**
     * Set a Comparator to sort the contents.  If this method alters ordering
     * of the list model, the model must fire the appropriate events
     *
     * @param comparator A comparator that can compare objects in the model
     */
    public void setComparator (Comparator <T> comparator);
    /**
     * Get the comparator, if any
     * @return The Comparator being used to sort the contents.
     */
    public Comparator <T> getComparator();
    
    public void setContents (List <T> list, boolean replace);
    
    public void fire (Diff diff);
}
