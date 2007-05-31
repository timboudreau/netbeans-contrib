/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
