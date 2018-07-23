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
package org.netbeans.api.mdr.events;

import java.util.Collection;
import javax.jmi.reflect.RefObject;

/** MDR event used for representing events related to lifecycle of package extents in a repository
 * (creation and deletion of extents).
 *
 * @author Martin Matula
 */
public class ExtentEvent extends MDRChangeEvent {
    /** Bitmask representing all the events related to extent lifecycle. */
    public static final int EVENTMASK_EXTENT = 0x801FFFF;

    /** Identifier for event type that indicates creation of a new extent in a repository. */
    public static final int EVENT_EXTENT_CREATE = 0x8010001;
    /** Identifier for event type that indicates an extent is to be/was deleted. */
    public static final int EVENT_EXTENT_DELETE = 0x8010002;

    private final String extentName;
    private final RefObject metaObject;
    private final Collection existingExtents;
    private final boolean empty;
    
    /** Creates new ExtentEvent object.
     * @param source Event source (MDRepository in case of extent creation, RefPackage in case of extent deletion).
     * @param type Event type.
     * @param extentName Name of created/deleted extent.
     * @param metaObject Metaobject of created/deleted extent or null.
     * @param existingExtents Immutable collection of existing extents that were provided to be used for clustering or null.
     */    
    public ExtentEvent (Object source, int type, String extentName, RefObject metaObject, Collection existingExtents) {
        this (source,  type, extentName, metaObject, existingExtents, true);
    }
    
    public ExtentEvent(Object source, int type, String extentName, RefObject metaObject, Collection existingExtents, boolean empty) {
        super(source, type);
        this.extentName = extentName;
        this.metaObject = metaObject;
        this.existingExtents = existingExtents;
        this.empty = empty;
    }
    
    /** Returns name of deleted/created extent.
     * @return Name of created/deleted extent.
     */    
    public String getExtentName() {
        return extentName;
    }
    
    /** Returns metaobject of created/deleted extent.
     * @return metaobject of created/deleted extent or null.
     */    
    public RefObject getMetaObject() {
        return metaObject;
    }
    
    /** Returns collection of existing extents provided to be used for package clustering.
     * @return Collection of extents or null.
     */    
    public Collection getExistingExtents() {
        return existingExtents;
    }
    
    /** Returns true in the case that the created package is empty.
     *  The false value means that the extent was created by partition mounting.
     *  @return boolean
     */
    public boolean isEmpty () {
        return this.empty;
    }
}
