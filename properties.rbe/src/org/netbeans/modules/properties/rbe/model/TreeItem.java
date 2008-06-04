/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Denis Stepanov
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

    public TreeItem getParent() {
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
