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
 * Software is Nokia. Portions Copyright 1997-2006 Nokia. All Rights Reserved.
 */
package org.netbeans.api.propertiestool;

import org.openide.nodes.Node;

/**
 * Interface for modules that want to get finer control over process
 * of saving property changes using the Properties Tool.
 * Instances of objects implementing this interface should be used
 * as cookies (lookup objects) of nodes displayed by the Properties Tool.
 *
 * When more nodes are selected the batch updates can be coalesced
 * from more nodes. See method coalesce(BatchUpdate) for details.
 *
 * @author David Strupl
 */
public interface BatchUpdate {
    /**
     * Invoked by the Properties tool after the user hit Save button and
     * the tool starts batch update of the property values.
     */
    public void startSaving();
    
    /**
     * For each changed property of the target node this method is invoked
     * to propagate the new value to the property. The call to startSaving()
     * must precede calls to this method.
     * <p> If this cookie is not present the tool calls equivalent of
     * <code>property.setValue(value);</code> while property and value
     * are the parameters to this method.
     */
    public void savePropertyValue(Node.Property property, Object value);
    
    /**
     * Concludes the save operation. After finishSaving no more calls to
     * savePropertyValue occur as part of this batch operation.
     */
    public void finishSaving();
    
    /**
     * The infrastructure can decide that more save events will be
     * part of one batch update (typically when more nodes are edited
     * at once). 
     * <OL>The call sequence can be as follows (batchUpdateXXX being
     * instances of BatchUpdate):
     *      <LI>batchUpdate1.startSaving();</LI>
     *      <LI>batchUpdate1.savePropertyValue(...);</LI>
     *      <LI>batchUpdate1.coalesce(batchUpdate2);</LI>
     *      <LI>batchUpdate2.savePropertyValue(...);</LI>
     *      <LI>batchUpdate2.coalesce(batchUpdate3);</LI>
     *      <LI>...</LI>
     *      <LI>batchUpdateN.savePropertyValue(...);</LI>
     *      <LI>batchUpdateN.finishSaving();</LI>
     * </OL>
     * The return value of this method indicates whether our instance
     * is willing to continue by the next BatchUpdate instance. If it returns
     * false the infrastructure can try to call another coalesce(...)
     * with different argument or finishSaving() to conclude the current session.
     * If it returns true the control is handed over to the next instance
     * and finishSaving() will <em>not</em> be called on this instance
     * but on the next one.
     */
    public boolean coalesce(BatchUpdate next);
}
