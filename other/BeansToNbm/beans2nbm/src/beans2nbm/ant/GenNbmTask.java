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
package beans2nbm.ant;

import beans2nbm.Main;
import beans2nbm.gen.JarInfo;
import beans2nbm.gen.JarInfo.ScanObserver;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.netbeans.modules.wizard.InstructionsPanel;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;

/**
 *
 * @author Tim Boudreau
 */
public class GenNbmTask extends Task {
    private Map map = new HashMap();
    public GenNbmTask() {
    }
    
    public void execute() throws BuildException {
        String[] keys = new String[] {
        "destFolder",
        "destFileName",
        "description",
        "libversion",
        "homepage",
        "codeName",
        "jarFileName",
        "author",
        "displayName",
        "license",
        "javaVersion",
        };
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            check (key);
        }
        
        String codeName = getCodeName();
        if (codeName.indexOf(".") < 0) {
            throw new BuildException ("Code name should be a unique" +
                    " java package name, e.g. com.foo.mypackage");
        }
        
        if (getSourceJar() == null) {
            log("Source JAR/ZIP not specified.  It is not" +
                    " a requirement, but it helps people to debug.  Please" +
                    " consider including one.");
        }
        if (getDocsJar() == null) {
            log("Javadoc JAR/ZIP not specified.  It is not" +
                    " a requirement, but it helps use components.  Please" +
                    " consider including one.");
        }
        JarInfo info = (JarInfo) map.get("jarInfo");
        boolean infoCreated = info == null;
        if (info == null) {
            info = new JarInfo ((String) map.get("jarFileName"));
        }
        if (infoCreated) {
            O o = new O();
            info.scanImmediate(o);
            if (o.failMsg != null) {
                throw new BuildException (o.failMsg);
            }
        } else {
            log ("Using supplied list of JavaBeans;  JAR Manifest ignored");
        }
        List l = info.getBeans();
        log ("Beans: " + l);
        if (l.isEmpty()) {
            log ("JAR contains no Java-Bean: sections in its manifest." +
                    "  No components will be added to the component" +
                    "palette;  only the library will be added to the " +
                    "list of registered libraries in NetBeans.");
        } else {
            map.put("jarInfo", info);
        }
        String[] missing = info.getBeansWithoutEntries();
        if (missing.length > 0) {
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < missing.length; i++) {
                b.append (missing[i]);
                if (i != missing.length - 1) {
                    b.append (", ");
                }
            }
            throw new BuildException ("The following bean entries were declared " +
                    "but are not actually present in " + map.get("jarFileName") 
                    + ": " + b);
        }
        Main.BackgroundBuilder builder = new Main.BackgroundBuilder ();
        R r = new R();
        builder.start(map, r);
        if (r.failMsg != null) {
            throw new BuildException (r.failMsg);
        }
    }
    
    public void addText(String txt) {
        //whitespace -- ignore
    }
    
    public void addBean (String path) {
        path = path.trim();
        log("addBean string " + path);
        JarInfo info = (JarInfo) map.get("jarInfo");
        if (info == null) {
            info = new JarInfo((String) map.get("jarFileName"));
            info.setDontScan (true);
            map.put ("jarInfo", info);
        }
        List l = info.getBeans();
        if (!l.contains(path)) {
            info.getBeans().add (path);
        }
    }
    
    private void check (String key) throws BuildException {
        if (!map.containsKey(key)) {
            throw new BuildException (key + " not specified");
        }
    }
    
    class O implements ScanObserver {
        public void start() {
            log("Scanning JAR for JavaBeans");
        }

        public void progress(int progress) {
        }

        String failMsg;
        public void fail(String msg) {
            failMsg = msg;
            synchronized (this) {
                notifyAll();
            }
        }

        public void done() {
            log("Completed scan, found ");
        }
    }
    
    class R implements ResultProgressHandle {
        public void setProgress(int currentStep, int totalSteps) {
            
        }

        public void setProgress(String description, int currentStep, int totalSteps) {
        }

        public void setBusy(String description) {
        }

        public void finished(Object result) {
            synchronized (this) {
                notifyAll();
            }
            while (result instanceof Summary) {
                result = ((Summary) result).getResult();
            }
            log("Created " + result);
        }

        String failMsg;
        public void failed(String message, boolean canNavigateBack) {
            Thread.dumpStack();
            failMsg = message;
            synchronized (this) {
                notifyAll();
            }
        }

        public void addProgressComponents(InstructionsPanel panel) {
        }

        public boolean isRunning() {
            //unused - no gui
            return true;
        }
    }
    
    public void setDestFolder  (File val) { 
        if (!val.exists()) {
            File base = super.getOwningTarget().getProject().getBaseDir();
            val = new File (base, val.getPath());
        }
        map.put ("destFolder", val == null ? null : val.getAbsolutePath()); 
    }

    public void setDocsJar  (File val) { 
        if (!val.exists()) {
            File base = super.getOwningTarget().getProject().getBaseDir();
            val = new File (base, val.getPath());
        }
        map.put ("docsJar", val == null ? null : val.getAbsolutePath()); 
    }
    
    public void setSourceJar  (File val) {
        if (!val.exists()) {
            File base = super.getOwningTarget().getProject().getBaseDir();
            val = new File (base, val.getPath());
        }        
        map.put ("sourceJar", val == null ? null : val.getAbsolutePath()); 
    }
    
    public void setDestFileName  (String val) { map.put ("destFileName", val); }
    public void setDescription  (String val) { map.put ("description", val); }
    public void setVersion  (String val) { map.put ("libversion", val); }
    public void setHomepage  (String val) { map.put ("homepage", val); }
    public void setCodeName  (String val) { map.put ("codeName", val); }
    
    public void setJarFileName  (File val) { 
        if (!val.exists()) {
            File base = super.getOwningTarget().getProject().getBaseDir();
            val = new File (base, val.getPath());
        }        
        map.put ("jarFileName", val.getAbsolutePath()); 
    }
    
    public void setAuthor  (String val) { map.put ("author", val); }
    public void setDisplayName  (String val) { map.put ("displayName", val); }
    public void setLicense  (String val) {
        InputStream stream = GenNbmTask.class.getResourceAsStream("/beans2nbm/ui/resources/" + val.toLowerCase() + ".txt");
        if (stream != null) {
            try {
                val = read (stream);
            } catch (IOException ioe) {
                log (ioe, 0);
            }
        } else {
            File f = new File (val);
            if (!f.exists()) {
                f = new File (getProject().getBaseDir(), f.getPath());
                if (!f.exists()) {
                    f = null;
                }
            }
            if (f != null) {
                try {
                InputStream fis = new BufferedInputStream (
                        new FileInputStream(f));
                    try {
                        val = read (fis);
                    } catch (IOException ioe) {
                        log (ioe, 0);
                    } finally {
                        fis.close();
                    }
                } catch (IOException ioe) {
                    log (ioe, 0);
                }
            }
        }
        map.put ("license", val); 
    }
    public void setMinJDK  (String val) { map.put ("javaVersion", val); }
    public File getDestFolder  () { 
        String s = (String) map.get  ("destFolder");
        return s == null ? null : new File(s); 
    }
    public String getDestFileName  () { return (String) map.get  ("destFileName"); }
    public String getDescription  () { return (String) map.get  ("description"); }
    public String getVersion  () { return (String) map.get  ("libversion"); }
    public String getHomepage  () { return (String) map.get  ("homepage"); }
    public String getCodeName  () { return (String) map.get  ("codeName"); }
    public String getJarFileName  () { return (String) map.get  ("jarFileName"); }
    public String getAuthor  () { return (String) map.get  ("author"); }
    public File getDocsJar  () { 
        String s = (String) map.get  ("docsJar");
        return s == null ? null : new File(s); 
    }
    public File getSourceJar  () { 
        String s = (String) map.get  ("sourceJar");
        return s == null ? null : new File(s); 
    }
    public String getDisplayName  () { return (String) map.get  ("displayName"); }
    public String getLicense  () { return (String) map.get  ("license"); }
    public String getMinJDK  () { return (String) map.get  ("javaVersion"); }
    
    public static String read(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream (1024);
        final byte[] BUFFER = new byte[4096];
        int len;

        try {
            for (;;) {
                len = is.read(BUFFER);

                if (len == -1) {
                    break;
                }

                os.write(BUFFER, 0, len);
            }
            byte[] b = os.toByteArray();
            return new String (b, "UTF-8");
        } finally {
            is.close();
        }
    }

    public Bean createBean() {
        return new Bean(this);
    }
    
    public static class Bean {
        public String text;
        private final GenNbmTask task;
        Bean (GenNbmTask task) {
            this.task = task;
        }
        public void addText (String text) {
            this.text = text;
            task.addBean (text);
        }
    }
}
