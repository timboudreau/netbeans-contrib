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
