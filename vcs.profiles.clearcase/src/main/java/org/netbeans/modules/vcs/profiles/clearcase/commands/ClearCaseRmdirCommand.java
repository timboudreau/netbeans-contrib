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

package org.netbeans.modules.vcs.profiles.clearcase.commands;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.openide.filesystems.*;
import org.openide.cookies.CloseCookie;
import org.openide.loaders.*;
import java.util.*;
import java.io.*;

/**
 * This command is called to remove a ClearCase directory.
 *
 * @author  Peter Liu
 */
public class ClearCaseRmdirCommand extends ClearCaseAbstractCommand {

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

	protected boolean process() {
		// This prevents the comment dialog to appear twice.
		vars.put("COMMENT", vars.get("REASON"));  //NOI18N
		boolean status = executeCommand(args[0]);

		// Close all open files.
		closeOpenFiles();		
		return status;

    }

	private void closeOpenFiles() {
		String rmp = fileSystem.getRelativeMountPoint();
		String root = (String) vars.get("ROOTDIR");   //NOI18N
		String mpaths = (String) vars.get("MPATHS");  //NOI18N
		StringTokenizer tokenizer = new StringTokenizer(mpaths);
	   
		while (tokenizer.hasMoreTokens()) {
			String path = tokenizer.nextToken();
			File folder = new File(root+File.separator+path);
			if (!folder.exists()) {
				if (rmp != null) 
					path = path.substring(rmp.length()+1);

				path = path.replace('\\', '/');

				Enumeration files = fileSystem.getExistingFiles();
				while (files.hasMoreElements()) {
					FileObject f = (FileObject) files.nextElement();
					if (!f.isFolder()) {
						if (f.toString().startsWith(path)) {
							try {
								DataObject dobj = DataObject.find(f);
								CloseCookie cookie = (CloseCookie) dobj.getCookie(CloseCookie.class);
								if (cookie != null) 
									cookie.close();
							} catch (DataObjectNotFoundException ex) {
								//ignore
							}
						}
					}
				}
			}
		}
	}
}
