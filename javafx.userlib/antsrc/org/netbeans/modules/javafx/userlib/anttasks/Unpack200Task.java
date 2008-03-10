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

package org.netbeans.modules.javafx.userlib.anttasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import java.util.*;
import java.io.*;
import java.util.zip.*;
import java.util.jar.*;
import java.util.jar.Pack200.*;


/**
 * An optional ant Task which emulates the Command Line Interface unpack200(1).
 * @version %W% %E%
 * @author Kumar Srinivasan
 */
public class Unpack200Task extends Unpack {

    enum FileType { unknown, gzip, pack200, zip };

    private SortedMap <String, String> propMap;
    private Pack200.Unpacker unpkr;

    public Unpack200Task() {
	unpkr = Pack200.newUnpacker();
	propMap = unpkr.properties();
    }

    // Needed by the super class
    protected String getDefaultExtension() {
	return ".jar";
    }

    public void setVerbose(String value) {
	propMap.put(Pack200Task.COM_PREFIX + "verbose",value);
    }

    private FileType getMagic(File in) throws IOException {
	DataInputStream is = new DataInputStream(new FileInputStream(in));
	int i = is.readInt();
	is.close();
	if ( (i & 0xffffff00) == 0x1f8b0800) {
	    return FileType.gzip;
	} else if ( i == 0xcafed00d) {
	    return FileType.pack200;
	} else if ( i == 0x504b0304) {
	    return FileType.zip;
	} else {
	    return FileType.unknown; 
	}
    }
	
    protected void extract() {
	System.out.println("Unpacking with Unpack200");
	System.out.println("Source File :" + source);
	System.out.println("Dest.  File :" + dest);

	try { 
	    FileInputStream fis = new FileInputStream(source);

	    InputStream is = (FileType.gzip == getMagic(source))
		? new BufferedInputStream(new GZIPInputStream(fis))
		: new BufferedInputStream(fis);

	    FileOutputStream fos = new FileOutputStream(dest);
	    JarOutputStream jout = new JarOutputStream(
					new BufferedOutputStream(fos));
	    
	    unpkr.unpack(is, jout);
            is.close();
  	    jout.close();

	} catch (IOException ioe) {
	    throw new BuildException("Error in unpack200");	
        }

    }

}
