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

/** Configuration class for objects taking XMI as input (e.g. {@link XMIReader}).
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIInputConfig {
    /** Sets reference resolver to be used for resolving hrefs when reading XMI.
     * XMIReader/Consumer should call {@link XMIReferenceResolver#register} for each
     * object deserialized from XMI that has an xmi id associated with it.
     * At the end of the XMI document, the XMI reader/consumer should call
     * {@link XMIReferenceResolver#resolve} passing all of unresolved 
     * external references (hrefs).
     * For immutable configurations this method throws 
     * <code>UnsupportedOperationException</code>.
     * @param resolver Resolver to be used.
     */
    public abstract void setReferenceResolver(XMIReferenceResolver resolver);
    
    /** Returns a reference resolver to be used.
     * This method should never return <code>null</code> for a configuration
     * associated with an XMIReader/Consumer. Otherwise if 
     * <code>null</CODE> is returned, the default reference resolver
     * will be used.
     * @return Reference resolver to be used or <code>null</code>.
     */    
    public abstract XMIReferenceResolver getReferenceResolver();
}
