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

package org.netbeans.modules.j2ee.sun.ws7.nodes;

public class AttributeInfo {
    private String name;
    private String type;
    private String description;
    private boolean readable;
    private boolean writable;
    private boolean bool;

    public AttributeInfo(String name, String type, String description,
                         boolean readable, boolean writable,
                         boolean bool) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.readable = readable;
        this.writable = writable;
        this.bool = bool;
    }
    
    /**
     * Gets the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the value of name
     *
     * @param argName Value to assign to this.name
     */
    public void setName(String argName){
        this.name = argName;
    }

    /**
     * Gets the value of type
     *
     * @return the value of type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Sets the value of type
     *
     * @param argType Value to assign to this.type
     */
    public void setType(String argType){
        this.type = argType;
    }

    /**
     * Gets the value of description
     *
     * @return the value of description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the value of description
     *
     * @param argDescription Value to assign to this.description
     */
    public void setDescription(String argDescription){
        this.description = argDescription;
    }

    /**
     * Gets the value of readable
     *
     * @return the value of readable
     */
    public boolean isReadable() {
        return this.readable;
    }

    /**
     * Sets the value of readable
     *
     * @param argReadable Value to assign to this.readable
     */
    public void setReadable(boolean argReadable){
        this.readable = argReadable;
    }

    /**
     * Gets the value of writable
     *
     * @return the value of writable
     */
    public boolean isWritable() {
        return this.writable;
    }

    /**
     * Sets the value of writable
     *
     * @param argWritable Value to assign to this.writable
     */
    public void setWritable(boolean argWritable){
        this.writable = argWritable;
    }

    /**
     * Gets the value of bool
     *
     * @return the value of bool
     */
    public boolean isBool() {
        return this.bool;
    }

    /**
     * Sets the value of bool
     *
     * @param argBool Value to assign to this.bool
     */
    public void setBool(boolean argBool){
        this.bool = argBool;
    }
}
