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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.hints.scripting;

import com.sun.script.java.JavaCompiler;
import com.sun.script.java.MemoryClassLoader;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.modules.java.hints.spi.TreeRule;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jan Lahoda
 */
public class UberHint extends AbstractHint {

    static UberHint INSTANCE;
    
    public UberHint() {
        super(true, false, HintSeverity.WARNING);
        
        updateAllHints();
        INSTANCE = this;
    }

    final Map<Kind, List<TreeRule>> hints = new  EnumMap<Kind, List<TreeRule>>(Kind.class);
    
    @Override
    public String getDescription() {
        return "Uber Hint";
    }

    public Set<Kind> getTreeKinds() {
        return EnumSet.allOf(Kind.class);
    }

    public List<ErrorDescription> run(CompilationInfo compilationInfo, TreePath treePath) {
        List<TreeRule> rules = hints.get(treePath.getLeaf().getKind());
        
        if (rules == null) {
            return null;
        }
        
        List<ErrorDescription> result = new  LinkedList<ErrorDescription>();
        
        for (TreeRule tr : rules) {
            List<ErrorDescription> r = tr.run(compilationInfo, treePath);
            
            if (r != null) {
                result.addAll(r);
            }
        }
        
        return result;
    }

    public String getId() {
        return UberHint.class.getName();
    }

    public String getDisplayName() {
        return "Uber Hint";
    }

    public void cancel() {
    }

    private FileChangeListener l = new FileChangeListener() {
        public void fileFolderCreated(FileEvent fe) {}
        public void fileDataCreated(FileEvent fe) {
            updateAllHints();
        }
        public void fileChanged(FileEvent fe) {
            updateAllHints();
        }
        public void fileDeleted(FileEvent fe) {
            updateAllHints();
        }
        public void fileRenamed(FileRenameEvent fe) {
            updateAllHints();
        }
        public void fileAttributeChanged(FileAttributeEvent fe) {}
    };
    
    private void updateAllHints() {
        worker.schedule(50);
    }
    
    private final RequestProcessor.Task worker = new RequestProcessor(UberHint.class.getName()).create(new Runnable() {
        public void run() {
            doUpdateAllHints();
        }
    });
    
    private void doUpdateAllHints() {
        synchronized (hints) {
            hints.clear();
            
            FileObject hintsFolder = Utilities.getFolder();
            
            hintsFolder.removeFileChangeListener(l);
            hintsFolder.addFileChangeListener(l);
            
            JavaCompiler compiler = new JavaCompiler();
            Map<String, byte[]> bytecode = new  HashMap<String, byte[]>();
            
            for (FileObject c : hintsFolder.getChildren()) {
                c.removeFileChangeListener(l);
                c.addFileChangeListener(l);
                
                if ("text/x-java".equals(FileUtil.getMIMEType(c))) {
                    try {
                        String data = Utilities.copyFileToString(c);
                        
                        Map<String, byte[]> compiled = compiler.compile(c.getNameExt(), data, new OutputStreamWriter(System.err), null, computeCPAsString());
                        
                        if (compiled != null) {
                            bytecode.putAll(compiled);
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            
            MemoryClassLoader mcl = new MemoryClassLoader(bytecode, null, UberHint.class.getClassLoader());
            
            Logger.getLogger("TIMER").log(Level.FINE, "UberHint.MemoryClassLoader", mcl);
            
            try {
                for (Class c : mcl.loadAll()) {
                    Logger.getLogger("TIMER").log(Level.FINE, "UberHint.Class", c);
                    if (TreeRule.class.isAssignableFrom(c)) {
                        try {
                            TreeRule rule = TreeRule.class.cast(c.newInstance());
                            
                            for (Kind k : rule.getTreeKinds()) {
                                List<TreeRule> rules = hints.get(k);
                                
                                if (rules == null) {
                                    hints.put(k, rules = new  LinkedList<TreeRule>());
                                }
                                
                                rules.add(rule);
                            }
                        } catch (InstantiationException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (IllegalAccessException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private final static String computeCPAsString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (URL u : Utilities.computeCP()) {
            if (!first) {
                sb.append(':');
            }
            File f = FileUtil.archiveOrDirForURL(u);

            if (f != null) {
                sb.append(f.getAbsolutePath());
                first = false;
            }
        }
        
        return sb.toString();
    }
}
