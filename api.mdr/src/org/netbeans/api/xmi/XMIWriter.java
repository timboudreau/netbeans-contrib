/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
