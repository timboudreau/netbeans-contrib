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
