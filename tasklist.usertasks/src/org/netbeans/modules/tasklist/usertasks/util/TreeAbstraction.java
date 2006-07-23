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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.util;

/**
 * A Tree.
 *
 * T is the type for nodes.
 *
 * @author tl
 */
public interface TreeAbstraction<T> {
    /**
     * Returns the root of the tree.
     *
     * @return root node
     */
    public T getRoot();
    
    /**
     * Returns the number of children of a node.
     *
     * @param obj a node
     * @return number of children.
     */
    public int getChildCount(T obj);
    
    /**
     * Returns a child of a node.
     *
     * @param obj a node
     * @param index index of the child
     */
    public T getChild(T obj, int index);
}
