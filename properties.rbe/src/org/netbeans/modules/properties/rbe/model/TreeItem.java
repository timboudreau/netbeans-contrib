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
 * Contributor(s): Denis Stepanov
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.properties.rbe.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Tree item
 * @author Denis Stepanov <denis.stepanov at gmail.com>
 */
public class TreeItem<T extends Comparable<T>> implements VisitableTree<TreeItem<T>>, Comparable<TreeItem<T>> {

    protected T value;
    protected TreeItem<T> parent;
    protected SortedSet<TreeItem<T>> children;
    /** Property change support */
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    /** The property change event names */
    public static final String PROPERTY_VALUE = "PROPERTY_VALUE";
    public static final String PROPERTY_ROOT = "PROPERTY_ROOT";
    public static final String PROPERTY_CHILDREN = "PROPERTY_CHILDREN";

    public TreeItem(T value) {
        this.value = value;
        children = new TreeSet<TreeItem<T>>();
    }

    public T getValue() {
        return value;
    }

    protected void setValue(T value) {
        this.value = value;
        firePropertyChangeEvent(PROPERTY_VALUE, null, null);
    }

    public TreeItem<T> getParent() {
        return parent;
    }

    public void setParent(TreeItem<T> parent) {
        this.parent = parent;
        firePropertyChangeEvent(PROPERTY_ROOT, null, null);
    }

    public void addChild(TreeItem<T> child) {
        children.add(child);
        child.setParent(this);
        firePropertyChangeEvent(PROPERTY_CHILDREN, null, null);
    }

    public void removeChild(TreeItem<T> child) {
        children.remove(child);
        firePropertyChangeEvent(PROPERTY_CHILDREN, null, null);
    }

    public SortedSet<TreeItem<T>> getChildren() {
        return Collections.unmodifiableSortedSet(children);
    }

    protected void setChildren(SortedSet<TreeItem<T>> children) {
        this.children = children;
        firePropertyChangeEvent(PROPERTY_CHILDREN, null, null);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public int getHeight() {
        return getParent() == null ? 0 : getParent().getHeight() + 1;
    }

    public void accept(TreeVisitor<TreeItem<T>> visitor) {
        if (!visitor.isDone()) {
            visitor.preVisit(this);
            for (TreeItem<T> tree : children) {
                tree.accept(visitor);
            }
            visitor.postVisit(this);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

    public int compareTo(TreeItem<T> o) {
        return value.compareTo(o.value);
    }
}
