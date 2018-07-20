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

package test.genimpl;

import java.io.PrintStream;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import org.netbeans.modules.java.settings.JavaSynchronizationSettings;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.corba.IDLCompilerSupport;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.IDLNodeCookie;
import org.openide.compiler.Compiler;
import org.openide.compiler.CompilerJob;
import org.openide.cookies.CompilerCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.compiler.Compiler;

import util.Environment;
import util.ExceptionDialogCloser;

public class Main extends NbTestCase {
    
    public Main(String name) {
        super(name);
    }
/*    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testGenImpl"));
        return test;
    }
*/
    CORBASupportSettings css = null;
    JavaSynchronizationSettings jss = null;
    ExceptionDialogCloser edc = null;
    
    PrintStream out;
    PrintStream info;
    
    public void testGenImpl(String _orb, boolean _tie, String _package) {
        out = getRef ();
        info = getLog ();
        boolean javaSynchronization = true;
        try {
            jss = (JavaSynchronizationSettings) JavaSynchronizationSettings.findObject (JavaSynchronizationSettings.class, true);
            assertNotNull ("Cannot find JavaSynchronizationSettings", jss);
            javaSynchronization = jss.isEnabled();
            jss.setEnabled(false);
            css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
            assertNotNull ("Cannot find CORBASupportSettings", css);
            edc = new ExceptionDialogCloser();
            edc.setOut (info);
            edc.play();
            runTestCore(_orb, _tie, _package);
            edc.pause();
        } finally {
            jss.setEnabled(javaSynchronization);
            if (edc != null) {
                edc.cancel();
                edc = null;
            }
        }
    }
    
    public void runTestCore(String orb, boolean tie, String _package) {
        Environment.loadORBEnvironment(orb);

        String gen = css.getActiveSetting().getGeneration();
        String tiestr = css.getActiveSetting().getSkeletons();
//        boolean guarded = css.getActiveSetting().getUseGuardedBlocks();

        css.getActiveSetting().setGeneration(ORBSettingsBundle.GEN_EXCEPTION);
        css.getActiveSetting().setSkeletons(tie ? ORBSettingsBundle.TIE : ORBSettingsBundle.INHER);
//        css.getActiveSetting().setUseGuardedBlocks(false);

        FileObject fo = Environment.findFileObject(_package);
        assertNotNull("Cannot find " + _package + " folder in repository!", fo);
        findIDL(fo, _package + " : ");

//        css.getActiveSetting().setUseGuardedBlocks(guarded);
        css.getActiveSetting().setSkeletons(tiestr);
        css.getActiveSetting().setGeneration(gen);
    }
    
    public void findIDL(FileObject fo, String path) {
        fo.refresh();
        FileObject[] afo = fo.getChildren();
        for (int b = 0; b < afo.length - 1; b ++) {
            boolean changed = false;
            for (int a = 0; a < afo.length - 1; a ++) {
                if (afo[a].getName().compareTo(afo[a+1].getName()) > 0) {
                    FileObject chfo = afo[a];
                    afo[a] = afo[a+1];
                    afo[a+1] = chfo;
                    changed = true;
                }
            }
            if (!changed)
                break;
        }
        for (int a = 0; a < afo.length; a ++) {
            if (afo [a] == null)
                continue;
            if (afo [a].isFolder()) {
                findIDL(afo [a], path + "/" + afo [a].getName());
/*                if (afo [a].getChildren ().length == 0) {
                    try {
                        FileLock lock = afo [a].lock ();
                        try {
                            afo [a].delete (lock);
                        } catch (IOException e) {
                            info.printException (e, "IOException: File name: " + path + " - " + afo[a].getName ());
                        } finally {
                            lock.releaseLock ();
                        }
                    } catch (IOException e) {
                        info.printException (e, "Cannot remove: " + path + " - " + afo [a].getName () + " directory");
                    }
                }*/
            } else if (afo [a].hasExt("idl")) {
                IDLDataObject idl = null;
                try {
                    idl = (IDLDataObject) DataObject.find(afo [a]);
                    testIDL(fo, idl, path + "/" + afo [a].getName());
                } catch (DataObjectNotFoundException e) {
                    info.println ("Data object not found: File name: " + path + " - " + afo[a].getName());
                    e.printStackTrace(info);
                }
/*                if (idl != null) {
                    boolean b = false;
                    try {
                        b = getOpenImpl (idl);
                        testIDL(fo, idl, path + "/" + afo [a].getName());
                    } finally {
                        setOpenImpl (idl, b);
                    }
                }*/
            }
        }
    }
    
    public void testIDL(FileObject parent, IDLDataObject data, String path) {
        //        out.println ("=================================");
        out.print(path + " ... ");
        info.println(path + " ... ");
        DataFolder folder;
        try {
            folder = (DataFolder) DataObject.find(parent);
        } catch (DataObjectNotFoundException e) {
            out.println("ERROR: No parent dir");
            info.println("ERROR: No parent dir");
            out.println ("Cannot find owner directory: File name: " + path);
            e.printStackTrace (out);
            return;
        }

        if (data.getStatus () == IDLDataObject.STATUS_NOT_PARSED)
            data.startParsing ();
        for (int a = 0; a < 10; a ++) {
            if (data.getStatus () == IDLDataObject.STATUS_ERROR  ||  data.getStatus () == IDLDataObject.STATUS_OK)
                break;
            try { Thread.currentThread ().sleep (1000); } catch (Exception e) {}
        }
        if (data.getStatus () != IDLDataObject.STATUS_OK) {
            out.println("ERROR: Parse error: Status=" + data.getStatus ());
            info.println("ERROR: Parse error: Status=" + data.getStatus ());
            return;
        }

        ((IDLNodeCookie) data.getCookie(IDLNodeCookie.class)).GenerateImpl(data);
        int timeout;
        timeout = 60;
        while (timeout > 0) {
            try { Thread.currentThread().sleep(1000); } catch (Exception e) {}
            try {
                java.lang.reflect.Field f = IDLDataObject.class.getDeclaredField("_M_generation");
                f.setAccessible(true);
                if (!f.getBoolean(data))
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            timeout --;
        }
        if (timeout <= 0) {
            out.println("FAILED: TIMEOUT");
            info.println("FAILED: TIMEOUT");
            return;
        }
        timeout = 5;
        while (timeout > 0) {
            try { Thread.currentThread().sleep(1000); } catch (Exception e) {}
            refreshRecursive (data.getPrimaryFile().getParent ());
            if (data.hasGeneratedImplementation() > 0)
                break;
            info.println("No generated impls time step");
            timeout --;
        }
        if (timeout <= 0) {
            out.println("FAILED: No generated impls");
            info.println("FAILED: No generated impls");
            return;
        }
        
        IDLCompilerSupport.Compile compile = (IDLCompilerSupport.Compile) data.getCookie(IDLCompilerSupport.Compile.class);
        CompilerJob cj = new CompilerJob(Compiler.DEPTH_ZERO);
        compile.addToJob(cj, Compiler.DEPTH_ZERO);
        cj.start().waitFinished();
        refreshRecursive (data.getPrimaryFile().getParent ());
        CompilerCookie.Build cookie = (CompilerCookie.Build) folder.getCookie(CompilerCookie.Build.class);
        CompilerJob cj2 = new CompilerJob(Compiler.DEPTH_INFINITE);
        cookie.addToJob(cj2, Compiler.DEPTH_INFINITE);
        if (!cj2.start().isSuccessful()) {
            out.println("FAILED: Compiler error");
            info.println("FAILED: Compiler error");
        } else {
            out.println("OK");
            info.println("OK");
        }
    }
    
    public static void refreshRecursive (FileObject fo) {
        fo.refresh ();
        FileObject[] afo = fo.getChildren ();
        for (int a = 0; a < afo.length; a ++)
            if (afo[a].isFolder())
                refreshRecursive (afo[a]);
    }
/*    
    public boolean getOpenImpl (IDLDataObject idl) {
        try {
            Field r = idl.getClass ().getDeclaredField("_M_run_testsuite");
            r.setAccessible(true);
            return r.getBoolean(idl);
        } catch (Exception e) {
            info.printException (e);
            return false;
        }
    }
    
    public void setOpenImpl (IDLDataObject idl, boolean val) {
        try {
            Field r = idl.getClass ().getDeclaredField("_M_run_testsuite");
            r.setAccessible(true);
            r.setBoolean(idl, val);
        } catch (Exception e) {
            info.printException (e);
        }
    }
*/   
/*
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
*/
}
