/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.api.xmi;

import java.util.Collection;
import java.io.OutputStream;
import java.io.IOException;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.XmiWriter;

/** Base class for enhanced XMI writers.
 *
 * @author Martin Matula
 * @author Brian Smith
 */
public abstract class XMIWriter implements XmiWriter {
    //----------------
    // public methods
    //----------------
    
    /** Returns configuration object of this XMIWriter. Any changes to the returned
     * object will have immediate effect on the XMIWriter's configuration.
     */
    public abstract XMIOutputConfig getConfiguration();
    
    /** Writes specified objects and a transitive closure of their components to 
     * an XMI document using the specified output stream and URI.
     * @param stream Output stream that should be used for XMI document generation.
     * If <code>null</code>, XMIWriter will try to create a new output stream using
     * the specified URI.
     * @param uri Target URI of the document. When set to <code>null</code>,
     * any XMIReferenceProvider registered will be ignored as XMIWriter is not able
     * to determine whether the returned reference points to the same file.
     * @param objects Collection of objects to be serialized into XMI (objects
     * will be serialized recursively including their components).
     * @param xmiVersion Version of XMI to be used for writing.
     * @throws IOException Error during XMI production.
     */
    public abstract void write(OutputStream stream, String uri, Collection objects, String xmiVersion) throws IOException;

    /** Writes content of the specified package extent to 
     * an XMI document using the specified output stream and URI.
     * @param stream Output stream that should be used for XMI document generation.
     * If <code>null</code>, XMIWriter will try to create a new output stream using
     * the specified URI.
     * @param uri Target URI of the document. When set to <code>null</code>,
     * any XMIReferenceProvider registered will be ignored as XMIWriter is not able
     * to determine whether the returned reference points to the same file.
     * @param extent Package extent to be serialized into XMI.
     * @param xmiVersion Version of XMI to be used for writing.
     * @throws IOException Error during XMI production.
     */
    public abstract void write(OutputStream stream, String uri, RefPackage extent, String xmiVersion) throws IOException;

    
    //-------------------------------------------
    // javax.jmi.xmi.XmiWriter interface methods
    //-------------------------------------------
    
    /** Standard JMI method for writing content of a package extent into an XMI document.
     * @param stream Output stream to be used for writing the XMI document.
     * @param extent Package extent to be serialized into XMI document.
     * @param xmiVersion Version of XMI to be produced.
     * @throws IOException Error occurred during the production of XMI document.
     */    
    public void write(OutputStream stream, RefPackage extent, String xmiVersion) throws IOException {
        write(stream, null, extent, xmiVersion);
    }
    
    /** Standard JMI method for writing collection of objects (and transitive
     * closure of their components) an XMI document.
     * @param stream Output stream to be used for writing the XMI document.
     * @param objects Objects to be serialized into XMI document.
     * @param xmiVersion Version of XMI to be produced.
     * @throws IOException Error occurred during the production of XMI document.
     */    
    public void write(OutputStream stream, Collection objects, String xmiVersion) throws IOException {
        write(stream, null, objects, xmiVersion);
    }
}
