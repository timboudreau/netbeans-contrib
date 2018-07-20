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
/*
 * Change.java
 *
 * Created on September 18, 2004, 6:42 PM
 */

package org.netbeans.misc.diff;

import javax.swing.event.*;

/**
 * Immutable class representing a single transformation to a data range in a list indicating the addition, removal or
 * modification of a range of indices.
 *
 * @author Tim Boudreau
 * @see Diff
 */
public interface Change {
    /**
     * Insertion type.  For convenience, this is the same value as ListDataEvent.INTERVAL_ADDED.
     */
    public static final int INSERT = ListDataEvent.INTERVAL_ADDED;
    /**
* Deletion type.  For convenience, this is the same value as ListDataEvent.INTERVAL_REMOVED.
     */
    public static final int DELETE = ListDataEvent.INTERVAL_REMOVED;
    /**
     * Change type.  For convenience, this is the same value as ListDataEvent.CONTENTS_CHANGED.
     */
    public static final int CHANGE = ListDataEvent.CONTENTS_CHANGED;

    /**
     * Get the change type
     * @return the type of change
     */
    int getType ();

    /**
     * Get the start index
     *
     * @return the first affected index in the list
     */
    int getStart ();

    /**
     * Get the end index (inclusive)
     *
     * @return the last affected index in the list
     */
    int getEnd ();
}
