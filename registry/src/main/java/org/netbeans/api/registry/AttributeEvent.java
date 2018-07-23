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

package org.netbeans.api.registry;


/** An event from context indicating that attribute was added or
 * removed. The {@link #getType} can be used to distinguish type of the change.
 *
 * @author  David Konecny
 */
public final class AttributeEvent extends ContextEvent {

    /** This event type is for added attribute. */
    public static final int ATTRIBUTE_ADDED = 1;

    /** This event type is for removed attribute. */
    public static final int ATTRIBUTE_REMOVED = 2;

    /** This event type is for modified attribute. */
    public static final int ATTRIBUTE_MODIFIED = 3;
    
    private String attributeName;
    private String bindingName;
    private int type;
    
    AttributeEvent(Context source, String bindingName, String attributeName, int type) {
        super(source);
        this.attributeName = attributeName;
        this.bindingName = bindingName;
        this.type = type;
    }

    /**
     * Binding name which attribute has changed or null if context
     * attribute was changed.
     *
     * @return binding name or null for context attribute
     */
    public String getBindingName() {
        return bindingName;
    }

    /**
     * Attribute name.  It can be null what means
     * that concrete source of the change was not clear and that
     * client should reexamine all attributes.
     *
     * @return attribute name; can be null
     */
    public String getAttributeName() {
        return attributeName;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return "AttributeEvent: [bindingName="+bindingName+", attributeName="+attributeName+", type="+type+"] " + super.toString(); // NOI18N
    }
}
