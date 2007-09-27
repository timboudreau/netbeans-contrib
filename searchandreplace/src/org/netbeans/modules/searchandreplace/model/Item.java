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
