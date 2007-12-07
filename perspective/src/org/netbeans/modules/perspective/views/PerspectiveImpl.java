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


/**
 * PerspectiveImpl.java
 *
 */

package org.netbeans.modules.perspective.views;

import org.netbeans.modules.perspective.PerspectiveListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.perspective.Perspective;

/**
 *
 * @author Anuradha G
 */
public  class PerspectiveImpl implements Perspective, Comparable<PerspectiveImpl> {

    private String name;
    private String alias;
    private String description;
    private int index;
    private Set<PerspectiveMode> perspectiveModes = new LinkedHashSet<PerspectiveMode>();
    private List<PerspectiveListener> perspectiveListener = new ArrayList<PerspectiveListener>();
    private String imagePath = "org/netbeans/modules/perspective/resources/custom.png";

    /**
     * Create PerspectiveImpl with name and display name
     * @param name PerspectiveImpl name
     * @param alias PerspectiveImpl display name
     */
    public PerspectiveImpl(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public void clear() {
        perspectiveModes.clear();
    }

    /**
     * Return Display name of the perspective
     * @return PerspectiveImpl display name
     */
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    /**
     * Return name of the PerspectiveImpl
     * @return perspective name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPerspectiveMode(PerspectiveMode mode){
        perspectiveModes.add(mode);
    }
    
    public void removePerspectiveMode(PerspectiveMode mode){
        perspectiveModes.remove(mode);
    }

    public Set<PerspectiveMode> getPerspectiveModes() {
        return Collections.unmodifiableSet(perspectiveModes);
    }

    /**
     * Return index of the PerspectiveImpl
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set Index of the PerspectiveImpl
     * @param index Index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Return Image path
     * @return Image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Set Image Path
     * @param imagePath
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Add {@link org.netbeans.modules.perspective.views.PerspetiveListner}
     * to PerspectiveImpl
     * @param listner
     */
    public void addPerspectiveListener(PerspectiveListener listner) {
        perspectiveListener.add(listner);
    }

    /**
     * Remove {@link org.netbeans.modules.perspective.views.PerspectiveListener}
     * from PerspectiveImpl
     * @param listner
     */
    public void removePerspectiveListener(PerspectiveListener listner) {
        perspectiveListener.remove(listner);
    }

    /**
     * Return List of {@link org.netbeans.modules.perspective.views.PerspectiveListener}s
     * @return List of Perspectives
     */
    public List<PerspectiveListener> getPerspectiveListeners() {
        return Collections.unmodifiableList(perspectiveListener);
    }

    /**
     * Remove all {@link org.netbeans.modules.perspective.views.PerspetiveListner}s
     */
    public void removePerspectiveListeners() {
        perspectiveListener.clear();
    }

    public int compareTo(PerspectiveImpl o) {
        if (o.index == index) {
            return 0;
        }
        if (o.index < index) {
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return alias;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PerspectiveImpl other = (PerspectiveImpl) obj;
        if (this.name == null || !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}