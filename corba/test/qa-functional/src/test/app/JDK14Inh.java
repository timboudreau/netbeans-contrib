package test.app;

import java.io.IOException;
import java.io.PrintStream;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestCase;
import org.openide.loaders.DataObject;
import test.app.AppGenerator;
import util.Environment;
import util.NameService;

public class JDK14Inh extends NbTestCase {
    
    public JDK14Inh(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new JDK14Inh("testJDK14Inh_Create"));
        test.addTest(new JDK14Inh("testJDK14Inh_RunNS"));
        test.addTest(new JDK14Inh("testJDK14Inh_RunFI"));
        test.addTest(new JDK14Inh("testJDK14Inh_RunIO"));
        return test;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    static AppGenerator app = null;
    static boolean done = false;
    
    public void testJDK14Inh_Create() {
        String work;
        try {
            work = Environment.replaceWinSeparator (getWorkDirPath());
        } catch (IOException e) {
            throw new AssertionFailedErrorException (e);
        }
        app = new AppGenerator (getRef (), getLog (), work);
        app.init ("JDK14", false, true, "data/app/jdk14inh/");
        DataObject daoCNS = app.doCNS ();
        app.doModifySource(daoCNS, new String[] {
            "addBefore", "ORB orb = ORB.init(args, null);", "args = new String[] { \"-ORBInitialPort\", \"1052\" };\n",
        });
        app.dumpFile (daoCNS);
        DataObject daoSNS = app.doSNS ();
        app.doModifySource(daoSNS, new String[] {
            "addBefore", "ORB orb = ORB.init(args, null);", "args = new String[] { \"-ORBInitialPort\", \"1052\" };\n",
        });
        app.dumpFile (daoSNS);
        app.dumpFile (app.doCFI ());
        app.dumpFile (app.doSFI ());
        app.dumpFile (app.doCIO ());
        app.dumpFile (app.doSIO ());
        app.doGenerateAndCompile();
        done = true;
    }
    
    public void testJDK14Inh_RunNS () {
        if (!done)
            return;
        NameService ns = new NameService (getRef ());
        ns.start (1052);
        app.setStreams (getRef (), getLog ());
        app.runNS ();
        ns.stop ();
        compareReferenceFiles();
    }
    
    public void testJDK14Inh_RunFI () {
        if (!done)
            return;
        app.setStreams (getRef (), getLog ());
        app.runFI ();
        compareReferenceFiles();
    }
    
    public void testJDK14Inh_RunIO () {
        if (!done)
            return;
        app.setStreams (getRef (), getLog ());
        app.runIO ();
        compareReferenceFiles();
    }
    
}
