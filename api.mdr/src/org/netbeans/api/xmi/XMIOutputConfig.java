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
package org.netbeans.api.xmi;

/** Configuration class for objects producing XMI as output (e.g. {@link XMIWriter}).
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIOutputConfig {
    /** Sets a reference provider to be used by XMI writer/producer to generate XMI IDs and
     * determine target document for a given object. If <code>null</code> is passed,
     * the default reference provider will be used.
     * For immutable configurations this method throws
     * <code>UnsupportedOperationException</code>.
     * @param provider Reference provider to be used.
     */
    public abstract void setReferenceProvider(XMIReferenceProvider provider);
    
    /** Returns a reference provider to be used by writer/producer to generate XMI IDs and
     * determine target document for a given object. The method should never return
     * <code>null</code> for a configuration associated with a writer/producer. Otherwise
     * <code>null</code> means that the default reference provider will be used.
     * @return Reference provider to be used or <code>null</code>.
     */
    public abstract XMIReferenceProvider getReferenceProvider();
    
    /** Sets an encoding to be used by XMI writer/producer to generate XMI documents.
     * If <code>null</code> is passed, the default encoding will be used.
     * For immutable configurations this method throws 
     * <code>UnsupportedOperationException</code>.
     * @param encoding to be used.
     */
    public abstract void setEncoding(String encoding);
    
    /** Returns an encoding to be used by writer/producer to generate XMI documents.
     * The method should never return <code>null</code> for a configuration associated 
     * with a writer/producer. Otherwise <code>null</code> means that the default 
     * encoding will be used.
     * @return encoding to be used or <code>null</code>.
     */
    public abstract String getEncoding();
}
