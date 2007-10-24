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
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.modules.apisupport.project.NbModuleProject;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Jaroslav Tulach <jtulach@netbeans.org>
 */
final class FindBugsTaskScanner extends PushTaskScanner {
    private static final RequestProcessor RP = new RequestProcessor("Hudson FindBugs for NetBeans"); // NOI18N
    
    private final URL root;
    
    
    FindBugsTaskScanner(URL root) {
        super(
            NbBundle.getMessage(FindBugsTaskScanner.class, "MSG_NbFindBugs"),
            "description", 
            "huh"
        );
        this.root = root;
    }
    
    public static FindBugsTaskScanner create() throws MalformedURLException {
        URL root = new URL("http://deadlock.netbeans.org/hudson/job/FindBugs/lastSuccessfulBuild/artifact/nbbuild/build/findbugs/"); // NOI18N
        return new FindBugsTaskScanner(root);
    }

    
    @Override
    public void setScope(TaskScanningScope scope, Callback callback) {
        ParseRequest req = new ParseRequest();
        if (scope == null) {
            req.callback = callback;
            RP.post(req);
            return;
        }
        
        if (cnb(scope.getLookup(), req)) {
            req.callback = callback;
            req.scanner = this;
            RP.post(req);
        }
    }

    
    
    final void parse(String cnb, FileObject project, Map<FileObject,List<Task>> cummulate) 
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
    
    private static boolean cnb(Lookup where, ParseRequest req) {
        NbModuleProject nbmp = where.lookup(NbModuleProject.class);
        if (nbmp == null) {
            return false;
        }
        req.cnb = nbmp.getCodeNameBase();
        req.projectRoot = nbmp.getProjectDirectory();
        return true;
    }
    
    //
    // end of apisupport/project tricks
    //

    private static final class ParseRequest implements Runnable {
        FindBugsTaskScanner scanner;
        String cnb;
        FileObject projectRoot;
        Callback callback;

        public void run() {
            if (cnb == null) {
                if (callback != null) {
                    callback.clearAllTasks();
                }
                return;
            }
            
            callback.started();
            Map<FileObject, List<Task>> map = new HashMap<FileObject, List<Task>>();
            try {
                scanner.parse(cnb, projectRoot, map);
                for (Map.Entry<FileObject, List<Task>> entry : map.entrySet()) {
                    callback.setTasks(entry.getKey(), entry.getValue());
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ParserConfigurationException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                callback.finished();
            }
        }
    }
    
    private static final class Parse extends DefaultHandler {
        private final FileObject project;
        private final Map<FileObject,List<Task>> cummulate;
        
        private String type;
        private int priority;
        private String category;
        private Stack<String> currentTag;
        
        public Parse(FileObject project, Map<FileObject,List<Task>> cummulate) {
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
                FileObject src = project.getFileObject("src/" + attributes.getValue("sourcepath"));
                if (src != null) {
                    Task t = Task.create(src, "warning", Msgs.getLocalizedMessage(type), line);
                    List<Task> arr = cummulate.get(src);
                    if (arr == null) {
                        arr = new ArrayList<Task>();
                        cummulate.put(src, arr);
                    }
                    arr.add(t);
                }
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
    
    private static final class Msgs extends DefaultHandler {
        private static Reference<Map<String,String>> map;
        static {
            map = new SoftReference<Map<String, String>>(null);
        }
        

        
        public static String getLocalizedMessage(String msg) {
            Map<String,String> m = map.get();
            if (m == null) {
                m = new HashMap<String, String>();
                
                InputStream is = Msgs.class.getResourceAsStream("messages.xml");
                try {
                    SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
                    Msgs p = new Msgs(m);
                    sax.parse(is, p);
                } catch (SAXException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ParserConfigurationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                map = new SoftReference<Map<String, String>>(m);
            }
            
            
            String r = m.get(msg);
            return r != null ? r : msg;
        }
        
        
        private String type;
        private String descrShort;
        private StringBuilder text;
        private Map<String,String> push;

        private Msgs(Map<String,String> map) {
            this.push = map;
        }
        
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("BugPattern".equals(name)) {
                type = attributes.getValue("type");
                return;
            }
            text = new StringBuilder();
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if ("BugPattern".equals(name)) {
                type = null;
                return;
            }
            if ("ShortDescription".equals(name) && type != null) {
                push.put(type, text.toString());
                return;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (text != null) {
                text.append(ch, start, length);
            }
        }
    } // end of Msgs
}