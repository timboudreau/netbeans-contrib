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

package org.netbeans.modules.tasklist.usertasks.util;

import java.util.EventObject;

/**
 * An event for changes in an ObjectList
 *
 * @author tl
 */
public class ObjectListEvent extends EventObject {
    /** a range of items was removed */
    public static final int EVENT_REMOVED = 0;

    /** new items added */
    public static final int EVENT_ADDED = 1;

    /** the objects changed completely */
    public static final int EVENT_STRUCTURE_CHANGED = 2;

    /** objects were just reordered. Not removed and not added. */
    public static final int EVENT_REORDERED = 3;

    private int op;
    private int indices[];
    private Object objects[];

    /**
     * Constructor
     *
     * @param source source for this event. Typically an ObjectList
     * @param indices indices of the changed values. May be null if
     *   op == EVENT_REORDERED.
     * @param objects changed values. May be null if
     *   op == EVENT_REORDERED.
     * @param op Operation. One of the EVENT_* constants from this class
     */
    public ObjectListEvent(Object source, int op, int[] indices, 
            Object[] objects) {
        super(source);

        assert op == EVENT_ADDED || op == EVENT_REMOVED ||
            op == EVENT_REORDERED || op == EVENT_STRUCTURE_CHANGED : 
            "Wrong operation"; // NOI18N

        assert indices != null || op == EVENT_REORDERED;
        assert objects != null || op == EVENT_REORDERED;

        this.op = op;
        this.indices = indices;
        this.objects = objects;
    }

    /**
     * Returns the type of the event
     *
     * @return one of the EVENT_* constants from this class
     */
    public int getType() {
        return op;
    }

    /**
     * Returns changed objects
     * 
     * @return array of changed objects. May be null if
     *   op == EVENT_REORDERED
     */
    public Object[] getObjects() {
        return objects;
    }

    /**
     * Returns indices of changed objects
     * 
     * @return indices. May be null if op == EVENT_REORDERED
     */
    public int[] getIndices() {
        return indices;
    }
}
