/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
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
