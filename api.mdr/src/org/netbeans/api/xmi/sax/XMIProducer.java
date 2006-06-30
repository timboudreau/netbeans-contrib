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
package org.netbeans.api.xmi.sax;

import org.netbeans.api.xmi.XMIOutputConfig;
import org.xml.sax.XMLReader;
import java.util.Collection;
import javax.jmi.reflect.RefPackage;

/** Implementation of XMLReader used for producing XMI documents.
 *
 * @author Martin Matula
 * @author Holger Krug
 * @author Brian Smith
 */
public abstract class XMIProducer implements XMLReader {
    /** Sets collection of objects to be serialized to XMI.
     * @param objects Collection of RefObjects to be serialized to XMI.
     */
    public abstract void setSource(Collection objects);
    
    /** Sets source package extent to be serialized to XMI.
     * @param extent Extent to be serialized.
     */
    public abstract void setSource(RefPackage extent);
    
    /** Returns source objects to be serialized to XMI.
     * @return Collection of RefObjects or RefPackage to be serialized to XMI.
     */
    public abstract Object getSource();
    
    /** Sets version of XMI to be produced.
     * Default value is "1.2".
     * @param xmiVersion Version of XMI.
     */
    public abstract void setXmiVersion(String xmiVersion);
    
    /** Returns version of XMI to be produced.
     * @return XMI version.
     */
    public abstract String getXmiVersion();
    
    /** Returns configuration object of this XMIProducer. Any changes to the returned
     * object will have immediate effect on the XMIProducer's configuration.
     */
    public abstract XMIOutputConfig getConfiguration();
}
