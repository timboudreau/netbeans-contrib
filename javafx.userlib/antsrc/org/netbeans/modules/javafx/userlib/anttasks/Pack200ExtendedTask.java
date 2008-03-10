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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author alex
 */
public class Pack200ExtendedTask extends Pack200Task {
    
    private boolean doRepack = false;

    public Pack200ExtendedTask() {
        super();
    }
    private List<FileSet> filesets = new LinkedList<FileSet>();
    public void addFileSet(FileSet fs) {
        filesets.add(fs);
    }
    
    /**
     * Sets the repack option, ie the jar will be packed and repacked.
     */

    @Override
    public void setRepack(boolean value) {
	doRepack = value;
        super.setRepack(doRepack);
    }

    @Override
    protected void pack() {
        //Map<Set<String>, List<File>> signersMap = new HashMap();
        List<File> files2pack = new ArrayList<File>();
        //List<File> alreadyPacked = new ArrayList<File>();

        Iterator it = filesets.iterator();
        while (it.hasNext()) {
            FileSet fs = (FileSet) it.next();
            File dir = fs.getDir(getProject());
            if (!dir.exists()) {
                continue;
            }
            System.out.println("Processing FileSet: " + fs);
            log("Processing FileSet: " + fs, Project.MSG_VERBOSE);
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            File basedir = ds.getBasedir();
            String[] files = ds.getIncludedFiles();
            for (String f : files) {
                File fl = new File(basedir, f);
                files2pack.add(fl);
            }
        }
        //System.out.println("Files to be packed: "  + files2pack.toString());
        log("Files to be packed: "  + files2pack.toString(), Project.MSG_VERBOSE);
        
        if (files2pack.size()>0) {
            for(Iterator<File> iter = files2pack.iterator();iter.hasNext();) {
                File f = iter.next();
                //System.out.println("Packing file: " + f);
                log("Packing file: " + f, Project.MSG_VERBOSE);
                source = f;
                if (doRepack) {
                    zipFile = f;
                } else {
                    zipFile = new File(f.toString()+".pack.gz");
                }
                super.pack();
            }
        }
    }
}