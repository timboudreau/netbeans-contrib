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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.hudsonfindbugs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
public class FindBugsTaskScanner extends FileTaskScanner {
    FindBugsTaskScanner() {
        super("displayName", "description", "huh");
    }
    
    public static FindBugsTaskScanner create() {
        return new FindBugsTaskScanner();
    }

    public List<? extends Task> scan(FileObject resource) {
        ArrayList<Task> res = new ArrayList<Task>();
        Project p = FileOwnerQuery.getOwner(resource);
        String cnb = cnb(p);
        if (cnb != null) {
            
        }
        
        return res;
    }

    public void attach(Callback callback) {
        // no listening, no need for callbacks
    }
    
    
    private static void parse(String cnb) throws MalformedURLException {
        URL root = new URL("http://deadlock.netbeans.org/hudson/job/FindBugs/lastSuccessfulBuild/artifact/nbbuild/build/findbugs/"); // NOI18N
        URL errors = new URL(root, cnb.replace('.', '-') + ".xml");
        
    }

    //
    // apisupport/project related tricks
    //
    
    private static String cnb(Project p) {
        NbModuleProject nbmp = p.getLookup().lookup(NbModuleProject.class);
        if (nbmp == null) {
            return null;
        }
        return nbmp.getCodeNameBase();
    }
}
