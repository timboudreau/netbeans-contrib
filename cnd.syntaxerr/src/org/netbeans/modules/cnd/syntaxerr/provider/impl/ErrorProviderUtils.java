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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.cnd.syntaxerr.provider.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;

/**
 *
 * @author vk155633
 */
public class ErrorProviderUtils {

    public static String findInPath(String baseName) {
        String path = System.getenv("PATH"); // NOI18N
        for (StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator); tokenizer.hasMoreTokens();) {
            String pathElement = tokenizer.nextToken();
            File file = new File(pathElement, baseName);
            if( file.exists() ) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
   
    public static File createTmpDir(File base, String prefix) {
	int counter = new Random().nextInt(4096);
	File file = null;
	do {
	    file = new File(base, prefix + "_" +  counter);
	} while( file.exists() );
	file.mkdirs();
	file.deleteOnExit();
	return file;
    }
    
    public static void WriteDocument(BaseDocument doc, File file) throws IOException, BadLocationException {
	File dir = file.getParentFile();
	if( ! dir.exists() ) {
	    dir.mkdirs();
	}
	FileWriter writer = new FileWriter(file);
	doc.write(writer, 0, doc.getLength());
	writer.write(System.getProperty("line.separator"));
	writer.close();	
    }
    
    public static PrintWriter createPrintWriter(File file) throws IOException {
	File dir = file.getParentFile();
	if( ! dir.exists() ) {
	    dir.mkdirs();
	}
	return new PrintWriter(new  FileWriter(file));
    }
}
