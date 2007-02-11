/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
    
    public void fire (Diff <T> diff);
}
