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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.spi.tasklist.FileTaskScanner;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
final class FindBugsTaskScanner extends PushTaskScanner {
    private final URL root;
    
    
    FindBugsTaskScanner(URL root) {
        super("displayName", "description", "huh");
        this.root = root;
    }
    
    public static FindBugsTaskScanner create() throws MalformedURLException {
        URL root = new URL("http://deadlock.netbeans.org/hudson/job/FindBugs/lastSuccessfulBuild/artifact/nbbuild/build/findbugs/"); // NOI18N
        return new FindBugsTaskScanner(root);
    }

    
    @Override
    public void setScope(TaskScanningScope scope, Callback callback) {
        String cnb = cnb(scope.getLookup());
        if (cnb != null) {
            callback.started();
//            callback.setTasks(file, tasks);
            callback.finished();
        }
    }

    
    
    final void parse(String cnb, FileObject project, List<Task> cummulate) 
    throws IOException, SAXException, ParserConfigurationException {
        URL errors = new URL(root, cnb.replace('.', '-') + ".xml");
        
        SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
        
        InputStream is = errors.openStream();
        try {
            Parse p = new Parse(project, cummulate);
            sax.parse(is, p);
        } finally {
            is.close();
        }
        
    }

    //
    // apisupport/project related tricks
    //
    
    private static String cnb(Lookup where) {
        NbModuleProject nbmp = where.lookup(NbModuleProject.class);
        if (nbmp == null) {
            return null;
        }
        return nbmp.getCodeNameBase();
    }
    
    //
    // end of apisupport/project tricks
    //
    
    private static final class Parse extends DefaultHandler {
        private final FileObject project;
        private final List<Task> cummulate;

        
        private String type;
        private int priority;
        private String category;
        private Stack<String> currentTag;
        
        public Parse(FileObject project, List<Task> cummulate) {
            this.project = project;
            this.cummulate = cummulate;
            this.currentTag = new Stack<String>();
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            String enclosingTag = currentTag.isEmpty() ? "" : currentTag.peek();
            currentTag.push(name);
            if ("BugInstance".equals(name)) {
                type = attributes.getValue("type");
                priority = Integer.valueOf(attributes.getValue("priority"));
                category = attributes.getValue("category");
                return;
            }
            if (enclosingTag.equals("BugInstance") && "SourceLine".equals(name)) {
                int line = Integer.valueOf(attributes.getValue("start"));
                Task t = Task.create(project, category, type, line);
                cummulate.add(t);
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            String ending = currentTag.pop();
            assert ending.equals(name);
            if ("BugInstance".equals("localName")) {
                type = null;
                priority = -1;
                category = null;
                return;
            }
        }
        
        
    } // end of Parse
}
