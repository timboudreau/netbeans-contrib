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

package org.netbeans.modules.jackpot;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.*;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.api.java.classpath.*;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import java.beans.*;
import java.net.URL;
import java.util.*;

/**
 * ClassPath implementation which returns the paths of all open projects.  
 */
class RefactoringClassPath implements ClassPathImplementation {

    private Project[] projects;
    private List<? extends PathResourceImplementation> resourceCache;
    private String type;

    public synchronized static ClassPath getSourcePath(Project[] projects) {
        return ClassPathFactory.createClassPath(
	    new RefactoringClassPath(projects, ClassPath.SOURCE));
    }
    
    public synchronized static ClassPath getCompilePath(Project[] projects) {
	return ClassPathFactory.createClassPath(
	    new RefactoringClassPath(projects, ClassPath.COMPILE));
    }
    
    public synchronized static ClassPath getBootClassPath(Project[] projects) {
        return ClassPathFactory.createClassPath(
	    new RefactoringClassPath(projects, ClassPath.BOOT));
    }
    
    private RefactoringClassPath(Project[] projects, String type) {
	this.projects = projects;
	this.type = type;
    }

    // ClassPathImplementation methods
    public synchronized List<? extends PathResourceImplementation> getResources() {
        if (this.resourceCache == null) {
            this.resourceCache = Collections.unmodifiableList(this.createResources());
        }
        return this.resourceCache;
    }

    // Empty methods, since no property changes are fired
    public void addPropertyChangeListener(PropertyChangeListener listener) {}
    public void removePropertyChangeListener(PropertyChangeListener listener) {}
    
    // implementation methods
    private List<? extends PathResourceImplementation> createResources () {
        List<PathResourceImplementation> result = new ArrayList<PathResourceImplementation> ();
        Set<URL> covered = new HashSet<URL>();
	Set<ClassPath> sources = new HashSet<ClassPath>();
	Set<ClassPath> compile = new HashSet<ClassPath>();
        Set<ClassPath> boot = new HashSet<ClassPath>();
	for (int i = 0; i < projects.length; i++) {
	    Project p = projects[i];
	    Sources src = ProjectUtils.getSources(p);
	    SourceGroup[] groups = src.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
	    for(int j = 0; j < groups.length; j++) {
                FileObject rootFolder = groups[j].getRootFolder();
		ClassPath cp = ClassPath.getClassPath(rootFolder, ClassPath.SOURCE);
		sources.add(cp);
		cp = ClassPath.getClassPath(rootFolder, ClassPath.COMPILE);
		compile.add(cp);
		cp = ClassPath.getClassPath(rootFolder, ClassPath.BOOT);
		boot.add(cp);
	    }
	}
    
	if (type.equals(ClassPath.SOURCE)) 
	    for (Iterator<ClassPath> it = sources.iterator(); it.hasNext();) {
		ClassPath cp = it.next ();
		for (Iterator<ClassPath.Entry> et = cp.entries().iterator(); et.hasNext();) {
		    ClassPath.Entry entry = et.next();
		    URL url = entry.getURL();
		    assert url != null : "ClassPath.Entry.getURL() returned null"; //NOI18N
		    if (covered.add (url))
			result.add (ClassPathSupport.createResource(url));
		}
	    }
	else if (type.equals(ClassPath.COMPILE)) {
	    addResources(compile,covered,result);
	}
        else
            addResources(boot, covered, result);
        return result;
    }

    private void addResources (Set<ClassPath> classPaths, Set<URL> coveredResources, List<PathResourceImplementation> addTo) {
        for (Iterator<ClassPath> it = classPaths.iterator(); it.hasNext();) {
            ClassPath cp = it.next ();
            for (Iterator<ClassPath.Entry> et = cp.entries().iterator(); et.hasNext();) {
                ClassPath.Entry entry = et.next();
                URL url = entry.getURL();
                assert url != null : "ClassPath.Entry.getURL() returned null"; //NOI18N
                if (!isCovered (coveredResources, url)) {
                    addTo.add (ClassPathSupport.createResource(url));
                    coveredResources.add(url);
                }
            }
        }
    }

    private static boolean isCovered (Set<URL> coveredResources, URL url) {
        if (coveredResources.contains(url))
            return true;
        FileObject[] fos = SourceForBinaryQuery.findSourceRoots(url).getRoots();
        assert fos != null : "SourceForBinaryQuery.findSourceRoot() returned null."; // NOI18N
        for (int i=0; i< fos.length; i++) {
            try {
                if (coveredResources.contains(fos[i].getURL())) {
                    return true;
                }
            } catch (FileStateInvalidException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return false;
    }
}
