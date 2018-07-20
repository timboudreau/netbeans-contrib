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
package org.netbeans.modules.perspective.views;

/**
 * Represent TopComponent State In Perspective (ex: Mode ID,Open or not) 
 * @author Anuradha G
 */
public class View implements  Comparable<View> {

    private String topcomponentID;
    private int index;
    private boolean open;



    public View(String topcomponentID, int index, boolean open) {
        this.topcomponentID = topcomponentID;
        this.index = index;
        this.open = open;
    }



    /**
     * Return TopComponent ID
     * @return TopComponent ID
     */
    public String getTopcomponentID() {
        return topcomponentID;
    }

    /**
     * Set TopComponent ID
     * @param topcomponentID TopComponent ID 
     */
    public void setTopcomponentID(String topcomponentID) {
        this.topcomponentID = topcomponentID;
    }

    /**
     * Return TopComponent Opened or not
     * @return opened
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Set TopComponent Opened or not
     * @param open 
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final View other = (View) obj;
        if ((this.topcomponentID == null || !this.topcomponentID.equals(other.topcomponentID))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.topcomponentID != null ? this.topcomponentID.hashCode() : 0);
        return hash;
    }

    public int compareTo(View o) {
        if (o == null)
            return 1;
        if (index == o.index)
            return 0;
        if (index > o.index)
            return 1;

        return -1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
}
