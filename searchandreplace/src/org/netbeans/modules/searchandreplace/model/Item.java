/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.searchandreplace.model;

import java.awt.EventQueue;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

/**
 * Represents one match in a text search over a file.  Note that this class has
 * a strong threading model - some methods may be called in EQ, some may not.
 *
 * @author Tim Boudreau
 */
public abstract class Item {
    private final File file;

    private boolean replaced;
    private final long timestamp;

    private boolean shouldReplace = true;
    Item (File file) {
        this.file = file;
        timestamp = file == null ? -1 : file.lastModified();
    }

    long getTimestamp() {
        return timestamp;
    }

    /** Get the name (not the path) of the file */
    public String getName() {
        return getFile().getName();
    }

    /** Get the description - file path or problem description */
    public String getDescription() {
        return getFile().getParent();
    }

    /** Get a Point representing the start and end positions of one match in
     * the file.  May be called in EQ.
     * @return a Point, x=start y=end
     */
    public abstract Point getLocation();

    /**
     * Determine if this item has already written its content to the backing
     * store.  May be called in EQ.
     *
     * @return true if replace() has already been successfully called
     */
    public final boolean isReplaced() {
        return replaced;
    }

    /**
     * Set a flag noting this item has had replace() successfully called on it.
     *  May be called in EQ.
     * @param val whether it's been replaced or not
     */
    void setReplaced (boolean val) {
        if (replaced && !val) {
            throw new IllegalStateException ("Cannot go from replaced to not " +
                    "replaced");
        }
        this.replaced = val;
    }

    /**
     * Set a flag noting that replace() should be invoked on this item or that
     * the user has not excluded it.  May be called in EQ.
     */
    public void setShouldReplace (boolean replace) {
        this.shouldReplace = replace;
    }

    /**
     * Get a flag indicating if the user has excluded this item from having
     * replace() called on it.  May be called in EQ.
     */
    public boolean isShouldReplace() {
        return shouldReplace;
    }

    /**
     * Get the file this item occurs in.  May be called in EQ.
     */
    public File getFile() {
        return file;
    }

    /**
     * Invoke this item, causing it to replace its matching text with the
     * originating search's replacement text.  May NOT be called in EQ.
     * <p>
     * Note that for multiple items representing one file
     * (OneFileItem.InFileItem), they need to be called in tail first order.
     * @see ItemComparator
     */
    public abstract void replace() throws IOException;

    /**
     * Determine if the last call to checkValid() threw an exception or not.
     * If not valid, replace() should not be invoked on this item.  May be
     * called in EQ.
     */
    public abstract boolean isValid();

    /**
     * Asynchronously read the backing file and get it content.  May NOT be
     * called from EQ.
     */
    abstract String getText() throws IOException;

    /**
     * Synchronously check if this item still is valid (i.e. the file has not
     * changed from underneath it or been deleted, etc.).  May NOT be called
     * in EQ.
     */
    public void checkValid() throws IOException {
        assert !EventQueue.isDispatchThread();
    }

    /**
     * Include or exclude all sibling items over the same file from
     * replacement.  May be called in EQ.
     */
    public void setEntireFileShouldReplace (boolean val) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determine if setEntireFileShouldReplace() has been called.  Note that
     * a true response does *not* mean all items in the file *will* be
     * replaced, but only that items in that file have not been *excluded*
     * from replacement (i.e. the user shift-clicked).
     */
    public boolean isEntireFileShouldReplace() {
        return false;
    }

    public final int hashCode() {
        return getClass().hashCode() * getFile().hashCode() * getLocation().hashCode();
    }

    public final boolean equals (Object o) {
        if (o.getClass() == getClass()) {
            Item i = (Item) o;
            return ((i.getLocation() == null && getLocation() == null) ||
                    i.getLocation() != null && i.getLocation().equals(getLocation()))
                    && i.getFile().equals(getFile());
        } else {
            return false;
        }
    }
}
