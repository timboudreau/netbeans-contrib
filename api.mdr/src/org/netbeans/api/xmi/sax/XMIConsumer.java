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

import org.netbeans.api.xmi.XMIInputConfig;
import org.xml.sax.ContentHandler;
import javax.jmi.reflect.RefPackage;

/** XMI Content Handler.
 *
 * @author Martin Matula
 * @author Holger Krug
 * @author Brian Smith
 */
public abstract class XMIConsumer implements ContentHandler {
    /** Sets target extent.
     * @param extent Target package extent.
     */
    public abstract void setExtent(RefPackage extent);
    
    /** Returns the target package extent.
     * @return Target package extent.
     */
    public abstract RefPackage getExtent();
    
    /** Returns configuration object of this XMIConsumer. Any changes to the returned
     * object will have immediate effect on the XMIConsumer's configuration.
     */
    public abstract XMIInputConfig getConfiguration();
}
