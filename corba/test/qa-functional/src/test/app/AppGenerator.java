/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package test.app;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.cookies.EditorCookie;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.openide.nodes.Node;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import java.util.StringTokenizer;
import org.openide.loaders.DataFolder;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.IDLNode;
import javax.swing.text.BadLocationException;
import org.openide.text.NbDocument;
import org.openide.loaders.DataObjectNotFoundException;
import org.netbeans.modules.corba.IDLNodeCookie;
import org.netbeans.modules.corba.IDLCompilerSupport;
import org.openide.compiler.CompilerJob;
import org.openide.compiler.Compiler;
import org.openide.cookies.CompilerCookie;
import org.openide.execution.ExecutorTask;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import junit.framework.AssertionFailedError;
import org.netbeans.junit.AssertionFailedErrorException;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.Repository;
import util.Environment;

public class AppGenerator {
    
    PrintStream out, info;
    String shortcut;
    boolean tie;
    boolean poa;
    String outdir;
    CORBASupportSettings css;
    DataObject clientTemplate;
    DataObject serverTemplate;
    FileObject foDst;
    DataFolder daoDst;
    Node nodeDst;
    IDLDataObject daoIDL;
    Node nodeIDL;
    String workdir;
    
    DataObject daoCNS;
    DataObject daoSNS;
    DataObject daoCFI;
    DataObject daoSFI;
    DataObject daoCIO;
    DataObject daoSIO;
    
    public AppGenerator(PrintStream _out, PrintStream _info, String _workdir) {
        out = _out;
        info = _info;
        workdir = _workdir;
    }
    
    public void setStreams (PrintStream _out, PrintStream _info) {
        out = _out;
        info = _info;
    }
    
    public void init(String _shortcut, boolean _tie, boolean _poa, String _outdir) {
        shortcut = _shortcut;
        tie = _tie;
        poa = _poa;
        outdir = _outdir;
        Environment.loadORBEnvironment(shortcut);
        css = (CORBASupportSettings) CORBASupportSettings.findObject(CORBASupportSettings.class, true);
        css.getActiveSetting().setGeneration(ORBSettingsBundle.GEN_NOTHING);
        css.getActiveSetting().setSkeletons(tie ? ORBSettingsBundle.TIE : ORBSettingsBundle.INHER);
        clientTemplate = getTemplate("CORBA/ClientMain.java");
        serverTemplate = getTemplate("CORBA/ServerMain.java");
        foDst = Environment.findFileObject(outdir);
        try {
            daoDst = (DataFolder) DataObject.find(foDst);
            nodeDst = daoDst.getNodeDelegate();
            DataObject idlTemplate = getTemplate("CORBA/Empty.idl");
            daoIDL = (IDLDataObject) idlTemplate.createFromTemplate(daoDst, "HelloWorld");
            nodeIDL = (IDLNode) daoIDL.getNodeDelegate();
            final StyledDocument sdIDL = ((EditorCookie) daoIDL.getCookie(EditorCookie.class)).openDocument();
            NbDocument.runAtomicAsUser(sdIDL, new Runnable() { public void run() { try {
                sdIDL.remove(0, sdIDL.getLength ());
                sdIDL.insertString(0, "\ninterface HelloWorld {\n\tstring hello (in string user);\n};\n", null);
            } catch (BadLocationException e) { throw new AssertionFailedErrorException(e); } }});
            EditorCookie ec = (EditorCookie) daoIDL.getCookie(EditorCookie.class);
            ec.saveDocument();
            ec.close ();
        } catch (Exception e) {
            throw new AssertionFailedErrorException (e);
        }
    }
    
    public DataObject doCNS() {
        return daoCNS = doClient("Naming Service", "CNS", new String[] {
            "replaceAll", "/*interface_name*/", "HelloWorld",
            "addBefore", "// add your client code here", "System.out.println(srv.hello (\"Naming Service\"));\n",
            "addBefore", "// resolve names with the Naming Service", "String[] client_name_hierarchy = new String [] {\"NS_Name\", \"NS_Kind\", \"ServerName\", \"ServerKind\"};\n",
        });
    }
    
    public DataObject doSNS() {
        if (!poa) {
            if (!tie) {
                return daoSNS = doServer("Naming Service", "SNS", new String[] {
                    "replaceAll", "/*servant_class*/", "HelloWorldImpl",
                    "replaceAll", "/*servant_variable*/", "hi",
                    "addBefore", "// create and bind Naming Contexts", "String[] hierarchy_of_contexts = new String [] {\"NS_Name\", \"NS_Kind\"};\nString[] name_of_server = new String [] {\"ServerName\", \"ServerKind\"};\n",
                    "replaceAll", "nc.bind(", "nc.rebind(",
                });
            } else {
                return daoSNS = doServer("Naming Service", "SNS", new String[] {
                    "replaceAll", "new /*servant_class*/()", "new /*servant_class*/ (new HelloWorldImpl ())",
                    "replaceAll", "/*servant_class*/", css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix (),
                    "replaceAll", "/*servant_variable*/", "hi",
                    "addBefore", "// create and bind Naming Contexts", "String[] hierarchy_of_contexts = new String [] {\"NS_Name\", \"NS_Kind\"};\nString[] name_of_server = new String [] {\"ServerName\", \"ServerKind\"};\n",
                    "replaceAll", "nc.bind(", "nc.rebind(",
                });
            }
        } else {
            if (!tie) {
                return daoSNS = doServer("Naming Service", "SNS", new String[] {
                    "replaceAll", "/*servant_variable*/", "hi",
                    "addAfter", "// add your creating of object implementation here", "\nHelloWorldImpl hi = new HelloWorldImpl ();\nbyte[] ID1 = poa.activate_object(hi);\n",
                    "addBefore", "// create and bind Naming Contexts", "String[] hierarchy_of_contexts = new String [] {\"NS_Name\", \"NS_Kind\"};\nString[] name_of_server = new String [] {\"ServerName\", \"ServerKind\"};\n",
                    "replaceAll", "nc.bind(", "nc.rebind(",
                });
            } else {
                return daoSNS = doServer("Naming Service", "SNS", new String[] {
                    "replaceAll", "/*servant_variable*/", "hi",
                    "addAfter", "// add your creating of object implementation here", "\n" + css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix () + " hi = new " + css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix () + " (new HelloWorldImpl ());\nbyte[] ID1 = poa.activate_object(hi);\n",
                    "addBefore", "// create and bind Naming Contexts", "String[] hierarchy_of_contexts = new String [] {\"NS_Name\", \"NS_Kind\"};\nString[] name_of_server = new String [] {\"ServerName\", \"ServerKind\"};\n",
                    "replaceAll", "nc.bind(", "nc.rebind(",
                });
            }
        }
    }
    
    public DataObject doCFI() {
        return daoCFI = doClient("IOR from file", "CFI", new String[] {
            "replaceAll", "/*interface_name*/", "HelloWorld",
            "addBefore", "// add your client code here", "\nSystem.out.println(srv.hello (\"IOR from/to file\"));\n",
            "replaceAll", "<file_name>", workdir + "/ior.ior",
        });
    }
    
    public DataObject doSFI() {
        if (!poa) {
            if (!tie) {
                return daoSFI = doServer("IOR to file", "SFI", new String[] {
                    "replaceAll", "/*servant_class*/", "HelloWorldImpl",
                    "replaceAll", "/*servant_variable*/", "hi",
                    "replaceAll", "<file_name>", workdir + "/ior.ior",
                });
            } else {
                return daoSFI = doServer("IOR to file", "SFI", new String[] {
                    "replaceAll", "new /*servant_class*/()", "new /*servant_class*/ (new HelloWorldImpl ())",
                    "replaceAll", "/*servant_class*/", css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix (),
                    "replaceAll", "/*servant_variable*/", "hi",
                    "replaceAll", "<file_name>", workdir + "/ior.ior",
                });
            }
        } else {
            if (!tie) {
                return daoSFI = doServer("IOR to file", "SFI", new String[] {
                    "addAfter", "// add your creating of object implementation here", "\nHelloWorldImpl hi = new HelloWorldImpl ();\nbyte[] ID1 = poa.activate_object(hi);\n",
                    "replaceAll", "/*servant_variable*/", "hi",
                    "replaceAll", "<file_name>", workdir + "/ior.ior",
                });
            } else {
                return daoSFI = doServer("IOR to file", "SFI", new String[] {
                    "addAfter", "// add your creating of object implementation here", "\n" + css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix () + " hi = new " + css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix () + " (new HelloWorldImpl ());\nbyte[] ID1 = poa.activate_object(hi);\n",
                    "replaceAll", "/*servant_variable*/", "hi",
                    "replaceAll", "<file_name>", workdir + "/ior.ior",
                });
            }
        }
    }
    
    public DataObject doCIO() {
        return daoCIO = doClient("IOR from standard input", "CIO", new String[] {
            "replaceAll", "/*interface_name*/", "HelloWorld",
            "addBefore", "// add your client code here", "\nSystem.out.println(srv.hello (\"IOR from/to stdio\"));\n",
        });
    }
    
    public DataObject doSIO() {
        if (!poa) {
            if (!tie) {
                return daoSIO = doServer("IOR to standard output", "SIO", new String[] {
                    "replaceAll", "/*servant_class*/", "HelloWorldImpl",
                    "replaceAll", "/*servant_variable*/", "hi",
                });
            } else {
                return daoSIO = doServer("IOR to standard output", "SIO", new String[] {
                    "replaceAll", "new /*servant_class*/()", "new /*servant_class*/ (new HelloWorldImpl ())",
                    "replaceAll", "/*servant_class*/", css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix (),
                    "replaceAll", "/*servant_variable*/", "hi",
                });
            }
        } else {
            if (!tie) {
                return daoSIO = doServer("IOR to standard output", "SIO", new String[] {
                    "addAfter", "// add your creating of object implementation here", "\nHelloWorldImpl hi = new HelloWorldImpl ();\nbyte[] ID1 = poa.activate_object(hi);\n",
                    "replaceAll", "/*servant_variable*/", "hi",
                });
            } else {
                return daoSIO = doServer("IOR to standard output", "SIO", new String[] {
                    "addAfter", "// add your creating of object implementation here", "\n" + css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix () + " hi = new " + css.getActiveSetting().getTieClassPrefix () + "HelloWorld" + css.getActiveSetting().getTieClassPostfix () + " (new HelloWorldImpl ());\nbyte[] ID1 = poa.activate_object(hi);\n",
                    "replaceAll", "/*servant_variable*/", "hi",
                });
            }
        }
    }
    
    public DataObject doClient(String binding, String name, final String[] front) {
        css.getActiveSetting().setClientBindingFromString(binding);
        try {
            DataObject dao = clientTemplate.createFromTemplate(daoDst, name);
//            System.out.println(dao.isValid());
            sleep (2);
            doModifySource(dao, front);
            return dao;
        } catch (Exception e) {
            throw new AssertionFailedErrorException(e);
        }
    }
    
    public DataObject doServer(String binding, String name, final String[] front) {
        css.getActiveSetting().setServerBindingFromString(binding);
        try {
            DataObject dao = serverTemplate.createFromTemplate(daoDst, name);
//            System.out.println(dao.isValid());
            sleep (2);
            doModifySource(dao, front);
            return dao;
        } catch (Exception e) {
            throw new AssertionFailedErrorException(e);
        }
    }
    
    public final StyledDocument doModifySource(DataObject dao, final String[] fr) {
        try {
            final StyledDocument sd = ((EditorCookie) dao.getCookie(EditorCookie.class)).openDocument();
            NbDocument.runAtomicAsUser(sd, new Runnable() { public void run() { try {
                int i = 0;
                while (i < fr.length) {
                    if ("insertLine".equals(fr[i])) {
                        i ++;
                        int l; try { l = Integer.parseInt(fr[i]); } catch (Exception e) { e.printStackTrace(); l = 0; }
                        i ++;
                        int o; try { o = Integer.parseInt(fr[i]); } catch (Exception e) { e.printStackTrace(); o = 0; }
                        i ++;
                        sd.insertString(NbDocument.findLineOffset(sd, l) + o, fr[i], null);
                        i ++;
                    } else if ("replaceString".equals(fr[i])) {
                        i ++;
                        int l; try { l = Integer.parseInt(fr[i]); } catch (Exception e) { e.printStackTrace(); l = 0; }
                        i ++;
                        int o; try { o = Integer.parseInt(fr[i]); } catch (Exception e) { e.printStackTrace(); o = 0; }
                        i ++;
                        int s; try { s = Integer.parseInt(fr[i]); } catch (Exception e) { e.printStackTrace(); s = 0; }
                        i ++;
                        sd.remove(NbDocument.findLineOffset(sd, l) + o, s);
                        sd.insertString(NbDocument.findLineOffset(sd, l) + o, fr[i], null);
                        i ++;
                    } else if ("replaceAll".equals(fr[i])) {
                        i ++;
                        String fs = fr[i];
                        i ++;
                        String rs = fr[i];
                        i ++;
                        boolean repeat = true;
                        while (repeat) {
                            int pos = sd.getText(0, sd.getLength()).indexOf(fs);
                            if (pos >= 0) {
                                sd.remove(pos, fs.length());
                                sd.insertString(pos, rs, null);
                            } else
                                repeat = false;
                        }
                    } else if ("addBefore".equals(fr[i])) {
                        i ++;
                        String fs = fr[i];
                        i ++;
                        String as = fr[i];
                        i ++;
                        boolean repeat = true;
                        int pos = sd.getText(0, sd.getLength()).indexOf(fs);
                        if (pos >= 0)
                            sd.insertString(pos, as, null);
                    } else if ("addAfter".equals(fr[i])) {
                        i ++;
                        String fs = fr[i];
                        i ++;
                        String as = fr[i];
                        i ++;
                        boolean repeat = true;
                        int pos = sd.getText(0, sd.getLength()).indexOf(fs);
                        if (pos >= 0)
                            sd.insertString(pos + fs.length(), as, null);
                    } else {
                        info.println("Unrecognized word: " + fr[i]);
                        break;
                    }
                }
            } catch (Exception e) { throw new AssertionFailedErrorException(e); } }});
            saveDocument (dao);
            //Helper.printJavaFile (ref, (EditorSupport) dao.getCookie (EditorCookie.class));
            return sd;
        } catch (Exception e) {
            throw new AssertionFailedErrorException(e);
        }
    }
    
    public void saveDocument (DataObject dao) {
        int a = 0;
        for (;;) {
            try {
                ((EditorCookie) dao.getCookie(EditorCookie.class)).saveDocument();
                return;
            } catch (IOException e) {
                if (++ a > 10)
                    throw new AssertionFailedErrorException (e);
            }
            sleep ();
        }
    }
    
    public static DataObject getTemplate(String name) {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().findResource("Templates/" + name);
        if (fo == null)
            throw new AssertionFailedError ("Template not found: Template: " + name);
        try {
            return DataObject.find (fo);
        } catch (DataObjectNotFoundException e) {
            throw new AssertionFailedErrorException ("Template DataObject not found: Template: " + name, e);
        }
    }
    
    public void doGenerateAndCompile() {
        if (daoIDL.getStatus() == IDLDataObject.STATUS_NOT_PARSED)
            daoIDL.startParsing();
        for (int a = 0; a < 10; a ++) {
            if (daoIDL.getStatus() == IDLDataObject.STATUS_ERROR  ||  daoIDL.getStatus() == IDLDataObject.STATUS_OK)
                break;
            sleep ();
        }
        if (daoIDL.getStatus() != IDLDataObject.STATUS_OK)
            throw new AssertionFailedError ("IDL is not parsed");
        
        ((IDLNodeCookie) daoIDL.getCookie(IDLNodeCookie.class)).GenerateImpl(daoIDL);
        int timeout;
        timeout = 10;
        while (timeout > 0) {
            sleep ();
            try {
                java.lang.reflect.Field f = IDLDataObject.class.getDeclaredField("_M_generation");
                f.setAccessible(true);
                if (!f.getBoolean(daoIDL))
                    break;
            } catch (Exception e) {
                info.println ("Reflection Error");
                e.printStackTrace (info);
            }
            timeout --;
        }
        if (timeout <= 0)
            throw new AssertionFailedError ("Implementation generator timeout");
        timeout = 5;
        while (timeout > 0) {
            sleep ();
            daoIDL.getPrimaryFile().getParent().refresh();
            if (daoIDL.hasGeneratedImplementation() > 0)
                break;
            info.println("No generated impls time step");
            timeout --;
        }
        if (timeout <= 0)
            throw new AssertionFailedError ("Refresh timeout");

        DataObject daoImpl;
        try {
            String implname = "HelloWorld";
            if (tie)
                implname = css.getActiveSetting().getTieImplPrefix() + implname + css.getActiveSetting().getTieImplPostfix();
            else
                implname = css.getActiveSetting().getImplBaseImplPrefix() + implname + css.getActiveSetting().getImplBaseImplPostfix();
            daoImpl = DataObject.find (Environment.findFileObject(outdir + "/" + implname + ".java"));
        } catch (DataObjectNotFoundException e) {
            throw new AssertionFailedErrorException (e);
        }
        final String[] frontImpl = {
            "addAfter", "public String hello(String user) {", "\nreturn \"Hello \" + user;",
        };
        doModifySource (daoImpl, frontImpl);
        
        IDLCompilerSupport.Compile compile = (IDLCompilerSupport.Compile) daoIDL.getCookie(IDLCompilerSupport.Compile.class);
        CompilerJob cj = new CompilerJob(Compiler.DEPTH_ZERO);
        compile.addToJob(cj, Compiler.DEPTH_ZERO);
        cj.start().waitFinished();
        daoIDL.getPrimaryFile().getParent().refresh();
        CompilerCookie.Build cookie;
        try {
            cookie = (CompilerCookie.Build) ((DataFolder) DataObject.find (daoIDL.getPrimaryFile().getParent())).getCookie(CompilerCookie.Build.class);
        } catch (DataObjectNotFoundException e) {
            throw new AssertionFailedErrorException (e);
        }
        CompilerJob cj2 = new CompilerJob(Compiler.DEPTH_INFINITE);
        cookie.addToJob(cj2, Compiler.DEPTH_INFINITE);
        if (!cj2.start().isSuccessful())
            throw new AssertionFailedError ("Compiler Error");
    }
    
    public MyProcessExecutor execute (FileObject fo, int skip, int read, PrintStream output) {
        String run;
        try {
            run = System.getProperty ("java.home") + "/bin/java -classpath " + fo.getFileSystem().getSystemName() + " " + fo.getPackageName('.');
        } catch (FileStateInvalidException e) {
            throw new AssertionFailedErrorException ("FileStateInvalidException while creating command", e);
        }
        MyProcessExecutor proc = new MyProcessExecutor (run, output, info, skip, read);
        proc.start ();
        return proc;
    }
    
    public void runNS () {
        runNS (0, 0, 0, 1);
    }
    
    public void runNS (int skipSNS, int readSNS, int skipCNS, int readCNS) {
        MyProcessExecutor peSNS = null;
        MyProcessExecutor peCNS = null;
        try {
            String str;
            peSNS = execute (daoSNS.getPrimaryFile(), skipSNS, readSNS, null);
            sleep (5);
            peCNS = execute (daoCNS.getPrimaryFile(), skipCNS, readCNS, out);
            sleep ();
            peCNS.join(15000);
        } catch (InterruptedException e) {
            throw new AssertionFailedErrorException ("Running Naming Service - ServerMain was interrupted", e);
        } finally {
            if (peCNS != null)
                peCNS.destroy ();
            if (peSNS != null)
                peSNS.destroy ();
        }
    }
    
    public void waitForIORFile (String file) {
        info.println ("Waiting for valid IOR file: File: " + file);
        for (int a = 0; a < 15; a ++) {
            sleep ();
            try {
                BufferedReader b = new BufferedReader (new FileReader (file));
                String str = b.readLine();
                if (str != null  &&  str.startsWith ("IOR:"))
                    return;
                else
                    info.println ("Time: " + a + "  Context is not valid: Context: " + str);
                b.close ();
            } catch (FileNotFoundException e) {
                info.println ("Time: " + a + "  File not found");
            } catch (IOException e) {
                info.println ("Time: " + a + "  IOException");
            }
        }
        throw new AssertionFailedError ("Timeout expired while waiting for valid IOR file");
    }
    
    public void runFI () {
        runFI (0, 0, 0, 1);
    }
    
    public void runFI (int skipSFI, int readSFI, int skipCFI, int readCFI) {
        MyProcessExecutor peSFI = null;
        MyProcessExecutor peCFI = null;
        try {
            peSFI = execute (daoSFI.getPrimaryFile(), skipSFI, readSFI, null);
            waitForIORFile (workdir + "/ior.ior");
            peCFI = execute (daoCFI.getPrimaryFile(), skipCFI, readCFI, out);
            sleep ();
            peCFI.join(15000);
        } catch (InterruptedException e) {
            throw new AssertionFailedErrorException ("Running IOR To File - ServerMain was interrupted", e);
        } finally {
            if (peCFI != null)
                peCFI.destroy ();
            if (peSFI != null)
                peSFI.destroy ();
        }
    }
    
    public String waitForIOR (MyProcessExecutor pe) {
        info.println ("Waiting for valid IOR");
        for (int a = 0; a < 15; a ++) {
            sleep ();
            String line = pe.getFirstLine ();
            if (line != null &&  line.startsWith ("IOR:"))
                return line;
            info.println ("Time: " + a + "  Invalid IOR: IOR: " + line);
        }
        throw new AssertionFailedError ("Timeout expired while waiting for valid IOR");
    }
    
    public void runIO () {
        runIO (0, 2, 0, 1);
    }
    
    public void runIO (int skipSIO, int readSIO, int skipCIO, int readCIO) {
        MyProcessExecutor peSIO = null;
        MyProcessExecutor peCIO = null;
        try {
            peSIO = execute (daoSIO.getPrimaryFile(), skipSIO, readSIO, null);
            String ior = waitForIOR (peSIO);
            peCIO = execute (daoCIO.getPrimaryFile(), skipCIO, readCIO, out);
            peCIO.input (ior);
            sleep ();
            peCIO.join(15000);
        } catch (InterruptedException e) {
            e.printStackTrace (info);
            throw new AssertionFailedErrorException ("Running IOR To StdOut - ServerMain was interrupted", e);
        } finally {
            if (peCIO != null)
                peCIO.destroy ();
            if (peSIO != null)
                peSIO.destroy ();
        }
    }
    
    public void sleep () {
        sleep (1);
    }
    
    public void sleep (int i) {
        try {
            Thread.sleep (i * 1000);
        } catch (Exception e) {
            info.println ("Thread sleep for " + i + " seconds was interrupted");
            e.printStackTrace (info);
        }
    }
    
    public void dumpFile (DataObject dao) {
        EditorCookie ec = (EditorCookie) dao.getCookie (EditorCookie.class);
        StyledDocument sd = ec.getDocument();
        info.println ("================================");
        try {
            info.println (sd.getText(0, sd.getLength()));
        } catch (BadLocationException e) {
            info.println ("Error while getting position");
            e.printStackTrace(info);
        }
    }
    
}
