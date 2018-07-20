package test.synchro;

import java.io.PrintWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import org.openide.filesystems.FileObject;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.options.SystemOption;
import org.netbeans.modules.corba.IDLDataObject;
import org.netbeans.modules.corba.IDLNodeCookie;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
import org.netbeans.modules.corba.settings.ORBSettingsBundle;
import javax.swing.JDialog;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import java.util.Comparator;
import java.util.Arrays;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.junit.NbTestCase;
import org.openide.cookies.SourceCookie;
import org.openide.src.ClassElement;
import org.openide.src.ConstructorElement;
import org.openide.src.FieldElement;
import org.openide.src.Identifier;
import org.openide.src.InitializerElement;
import org.openide.src.MethodElement;
import org.openide.src.SourceElement;
import util.Environment;
import util.Helper;

public class Main extends NbTestCase {
    
    public Main(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite test = new org.netbeans.junit.NbTestSuite();
        test.addTest(new Main("testSynchro_a1"));
        test.addTest(new Main("testSynchro_a2"));
        test.addTest(new Main("testSynchro_a3"));
        test.addTest(new Main("testSynchro_a4"));
        test.addTest(new Main("testSynchro_a5"));
        test.addTest(new Main("testSynchro_a6"));
        test.addTest(new Main("testSynchro_a7"));
        test.addTest(new Main("testSynchro_a8"));
        test.addTest(new Main("testSynchro_a9"));
        test.addTest(new Main("testSynchro_b1"));
        test.addTest(new Main("testSynchro_b2"));
        test.addTest(new Main("testSynchro_b3"));
        test.addTest(new Main("testSynchro_b4"));
        test.addTest(new Main("testSynchro_b5"));
        test.addTest(new Main("testSynchro_b6"));
        test.addTest(new Main("testSynchro_b7"));
        test.addTest(new Main("testSynchro_b8"));
        return test;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    String requiredFolder = "data/synchro/";
    CORBASupportSettings css;
    PrintStream out;
    PrintStream info;
    
    public void runTest (String[] commands) {
        int index = 0;
        int syncdialogs = 0;
        while (index < commands.length) {
            String command = commands[index];
            index ++;
            String file = commands[index];
            index ++;
            out.println ("Processing: Command: " + command + " File: " + file);
            if ("GEN".equals (command)) {
                FileObject fo = Environment.findFileObject (file);
                if (fo != null) {
                    DataObject dao;
                    try {
                        dao = DataObject.find (fo);
                    } catch (Exception e) {
                        out.println ("ERROR: Cannot get DataObject from FileObject: " + file);
                        e.printStackTrace (out);
                        throw new AssertionFailedErrorException (e);
                    }
                    if (dao != null  &&  dao instanceof IDLDataObject) {
                        IDLDataObject idl = (IDLDataObject) dao;
                        ((IDLNodeCookie) idl.getCookie (IDLNodeCookie.class)).GenerateImpl (idl);
                        int timeout = 20;
                        while (timeout > 0) {
                            Helper.sleep (500);
                            try {
                                java.lang.reflect.Field f = IDLDataObject.class.getDeclaredField ("_M_generation");
                                f.setAccessible (true);
                                if (!f.getBoolean (idl))
                                    break;
                            } catch (Exception e) {
                                out.println ("ERROR: NetBeans internal: GEN: Cannot work with _M_generation field: File: " + file);
                                e.printStackTrace (out);
                            }
                            timeout --;
                        }
//                            if (syncdialogs <= 0)
//                                  Helper.sleep (500);
                        out.println ("GEN: Synchronizing: Count: " + syncdialogs + " File: " + file);
                        for (int sd = 0; sd < syncdialogs; sd ++) {
                            JDialog dialog = JDialogOperator.waitJDialog ("Confirm Changes", true, true);
                            if (dialog != null) {
                                new JButtonOperator (JButtonOperator.waitJButton (dialog, "Process All", true, true)).push ();
                                Helper.sleep (1000);
                            } else {
                                out.println ("ERROR: GEN: No Confirm Changes dialog is shown: Index: " + sd + " File: " + file);
                                break;
                            }
                        }
                        out.println ("Done");
                    } else
                        out.println ("ERROR: GEN: " + file + " has no data object or is not idl data object");
                } else
                    out.println ("ERROR: GEN: " + file + " not found");
                Helper.sleep (3000);
            } else if ("DUMP".equals (command)) {
                Helper.sleep (1000);
                FileObject fo = Environment.findFileObject (file);
                if (fo != null) {
                    DataObject dao;
                    try {
                        dao = DataObject.find (fo);
                    } catch (Exception e) {
                        out.println ("ERROR: Cannot get DataObject from FileObject: " + file);
                        e.printStackTrace (out);
                        throw new AssertionFailedErrorException (e);
                    }
                    if (dao != null) {
                        SaveCookie sc = ((SaveCookie) dao.getCookie (SaveCookie.class));
                        if (sc != null) {
                            out.println ("DUMP: Saving: File: " + file);
                            try {
                                sc.save ();
                            } catch (Exception e) {
                                out.println ("ERROR: DUMP: File: " + file + " Exception: " + e.getMessage ());
                                e.printStackTrace(out);
//                                    setenv.FileLockShow.main(new String[0]);
                            }
                        }
                        SourceCookie src = (SourceCookie) dao.getCookie(SourceCookie.class);
                        out.println ("DUMP: File: " + file);
                        out.println ("---- beginning of file ----");
                        out.println (dumpFile(dao));
                        out.println ("---- end of file ----");
                        out.println ("Done");
/*                        InputStream is = null;
                        try {
                            is = fo.getInputStream ();
                            BufferedReader br = new BufferedReader (new InputStreamReader (is));
                            out.println ("DUMP: File: " + file);
                            out.println ("---- beginning of file ----");
                            for (;;) {
                                String str = br.readLine ();
                                if (str == null)
                                    break;
                                out.println (str);
                            }
                            out.println ("---- end of file ----");
                            out.println ("Done");
                        } catch (Exception e) {
                            out.println ("ERROR: DUMP: File: " + file + " Exception: " + e.getMessage ());
                            out.printException (e);
//                                setenv.FileLockShow.main(new String[0]);
                        } finally {
                            if (is != null) try { is.close (); } catch (Exception e) {}
                        }*/
                    } else
                        out.println ("ERROR: DUMP: Cannot get data object: File: " + file);
                } else
                    out.println ("ERROR: DUMP: " + file + " not found");
            } else if ("SETORB".equals (command)) {
                Environment.loadORBEnvironment(file);
                out.println ("Done");
            } else if ("SETGEN".equals (command)) {
                if ("NOTHING".equals (file))
                    css.getActiveSetting ().setGeneration (ORBSettingsBundle.GEN_NOTHING);
                else if ("EXCEPTION".equals (file))
                    css.getActiveSetting ().setGeneration (ORBSettingsBundle.GEN_EXCEPTION);
                else if ("NULL".equals (file))
                    css.getActiveSetting ().setGeneration (ORBSettingsBundle.GEN_RETURN_NULL);
                else
                    assertTrue ("Internal error: Uknown Generation Type: " + file, false);
                out.println ("Done");
            } else if ("SETDEL".equals (command)) {
                if ("NONE".equals (file))
                    css.getActiveSetting ().setDelegation (ORBSettingsBundle.DELEGATION_NONE);
                else if ("STATIC".equals (file))
                    css.getActiveSetting ().setDelegation (ORBSettingsBundle.DELEGATION_STATIC);
                else if ("VIRTUAL".equals (file))
                    css.getActiveSetting ().setDelegation (ORBSettingsBundle.DELEGATION_VIRTUAL);
                else
                    assertTrue ("Internal error: Uknown Delegation Type: " + file, false);
                out.println ("Done");
            } else if ("SETSKEL".equals (command)) {
                if ("INHER".equals (file))
                    css.getActiveSetting ().setSkeletons (ORBSettingsBundle.INHER);
                else if ("TIE".equals (file))
                    css.getActiveSetting ().setSkeletons (ORBSettingsBundle.TIE);
                else
                    assertTrue ("Internal error: Uknown Delegation Type: " + file, false);
                out.println ("Done");
            } else if ("SETSYNCCOUNT".equals (command)) {
                try {
                    syncdialogs = Integer.parseInt(file);
                } catch (NumberFormatException e) {
                    syncdialogs = 0;
                    info.println("WARNING: SETSYNCCOUNT: Invalid count format: Count: " + file);
                }
            } else
                assertTrue ("Internal error: Unknown command: Command: " + command, false);
        }
    }
    
    public void resolveFlags (ArrayList out, String flags, boolean first) {
        int left, right;
        if (first) {
            left = 0;
            right = flags.indexOf ('|');
            if (right < 0)
                right = flags.length ();
        } else {
            left = flags.indexOf ('|') + 1;
            if (left <= 0)
                left = flags.length ();
            right = flags.length ();
        }
        if (first  &&  right - left != 3)
            info.println ("WARNING: First generation has not specified all flags");
        for (int a = left; a < right; a ++) {
            switch (flags.charAt (a)) {
                case 'I':
                    out.add ("SETSKEL");
                    out.add ("INHER");
                    break;
                case 'T':
                    out.add ("SETSKEL");
                    out.add ("TIE");
                    break;
                case 'N':
                    out.add ("SETDEL");
                    out.add ("NONE");
                    break;
                case 'S':
                    out.add ("SETDEL");
                    out.add ("STATIC");
                    break;
                case 'V':
                    out.add ("SETDEL");
                    out.add ("VIRTUAL");
                    break;
                case 'O':
                    out.add ("SETGEN");
                    out.add ("NOTHING");
                    break;
                case 'X':
                    out.add ("SETGEN");
                    out.add ("EXCEPTION");
                    break;
                case 'U':
                    out.add ("SETGEN");
                    out.add ("NULL");
                    break;
            }
        }
    }
    
    public void testSynchro (String[] input) {
        String[] commands = new String[input.length + 2];
        commands[0] = "SETORB";
        commands[1] = "OB4X";
        System.arraycopy(input, 0, commands, 2, input.length);
        css = (CORBASupportSettings) SystemOption.findObject (CORBASupportSettings.class, true);
        assertNotNull ("DELEG: NetBeans internal error: Cannot find CORBA support settings", css);
        out = getRef ();
        info = getLog ();
        
        ArrayList cmds = new ArrayList ();
        int a = 0;
        while (a < commands.length) {
            String com = commands[a];
            a ++;
            if ("TEST".equals (com)) {
                String pkg = commands[a];
                if (pkg.length () > 0)
                    pkg += '/';
                a ++;
                String idl = commands[a];
                a ++;
                String flags = commands[a];
                a ++;
                String sync = commands[a];
                a ++;
                ArrayList ar = new ArrayList ();
                while (!"#".equals (commands[a])) {
                    ar.add (commands[a]);
                    a ++;
                }
                a ++;
                resolveFlags (cmds, flags, true);
                cmds.add ("SETSYNCCOUNT");
                cmds.add ("0");
                cmds.add ("GEN");
                cmds.add (requiredFolder + pkg + idl + ".idl");
                for (int b = 0; b < ar.size (); b ++) {
                    cmds.add ("DUMP");
                    cmds.add (requiredFolder + pkg + ((String) ar.get (b)) + "Impl.java");
                }
                resolveFlags (cmds, flags, false);
                cmds.add ("SETSYNCCOUNT");
                cmds.add (sync);
                cmds.add ("GEN");
                cmds.add (requiredFolder + pkg + idl + "_1.idl");
                for (int b = 0; b < ar.size (); b ++) {
                    cmds.add ("DUMP");
                    cmds.add (requiredFolder + pkg + ((String) ar.get (b)) + "Impl.java");
                }
            } else {
                cmds.add (com);
                cmds.add (commands[a]);
                a ++;
            }
        }
        commands = new String[cmds.size ()];
        for (a = 0; a < cmds.size (); a ++)
            commands[a] = (String) cmds.get (a);
        runTest (commands);
        compareReferenceFiles();
    }

    public String dumpFile (DataObject dao) {
        SourceCookie sc = (SourceCookie) dao.getCookie(SourceCookie.class);
        SourceElement se = sc.getSource();
        ClassElement[] cea = se.getClasses();
        if (cea == null)
            return "";
        ClassElement[] newcea = new ClassElement[cea.length];
        for (int a = 0; a < cea.length; a ++)
            newcea[a] = (ClassElement) cea[a].clone();
        newcea = sortClasses (newcea);
        String str = "";
        for (int a = 0; a < newcea.length; a ++)
            str += newcea[a].toString ();
        return str;
    }
    
    public ClassElement[] sortClasses (ClassElement[] cea) {
        Arrays.sort (cea, new Comparator () {
            public int compare (Object o1, Object o2) {
                return ((ClassElement) o1).getName().getName ().compareTo(((ClassElement) o2).getName ().getName());
            }
        });
        for (int a = 0; a < cea.length; a ++) {
            ClassElement ce = cea[a];
            try {
                ce.setInterfaces(sortInterfaces (ce.getInterfaces()));
                ce.setFields(sortFields (ce.getFields()));
                ce.setInitializers(sortInitializers (ce.getInitializers()));
                ce.setConstructors(sortConstructors (ce.getConstructors()));
                ce.setMethods(sortMethods (ce.getMethods()));
                ce.setClasses(sortClasses (ce.getClasses()));
            } catch (Exception e) {
                out.println ("ERROR: Exception while normalizing class: ClassElement: " + ce.getName () + " | " + ce.getSignature());
                e.printStackTrace (out);
                throw new AssertionFailedErrorException (e);
            }
        }
        return cea;
    }
    
    public static Identifier[] sortInterfaces(Identifier[] ar) {
        Arrays.sort (ar, new Comparator () {
            public int compare (Object o1, Object o2) {
                return ((Identifier) o1).getName().compareTo(((Identifier) o2).getName());
            }
        });
        return ar;
    }

    public static FieldElement[] sortFields(FieldElement[] ar) {
        Arrays.sort (ar, new Comparator () {
            public int compare (Object o1, Object o2) {
                return ((FieldElement) o1).getName().getName().compareTo(((FieldElement) o2).getName().getName());
            }
        });
        return ar;
    }

    public static InitializerElement[] sortInitializers(InitializerElement[] ar) {
        Arrays.sort (ar, new Comparator () {
            public int compare (Object o1, Object o2) {
                InitializerElement s1 = (InitializerElement) o1;
                InitializerElement s2 = (InitializerElement) o2;
                if (s1.isStatic () == s2.isStatic ())
                    return 0;
                return (s1.isStatic ()) ? -1 : 1;
            }
        });
        return ar;
    }

    public static ConstructorElement[] sortConstructors(ConstructorElement[] ar) {
        Arrays.sort (ar, new Comparator () {
            public int compare (Object o1, Object o2) {
                return ((ConstructorElement) o1).getName().getName().compareTo(((ConstructorElement) o2).getName().getName());
            }
        });
        return ar;
    }

    public static MethodElement[] sortMethods(MethodElement[] ar) {
        Arrays.sort (ar, new Comparator () {
            public int compare (Object o1, Object o2) {
                return ((MethodElement) o1).getName().getName().compareTo(((MethodElement) o2).getName().getName());
            }
        });
        return ar;
    }

    public void testSynchro_a1 () {
        testSynchro(new String[] {"TEST", "a1", "Empty", "INO|TVX", "1", "A/C", "B", "#"});
    }
    
    public void testSynchro_a2 () {
        testSynchro(new String[] {"TEST", "a2", "calc4", "ISX|TVU", "1", "Calc", "#"});
    }
    
    public void testSynchro_a3 () {
        testSynchro(new String[] {"TEST", "a3", "Empty", "ISX", "2", "A", "B", "C", "D", "#"});
    }
    
    public void testSynchro_a4 () {
        testSynchro(new String[] {"TEST", "a4", "Empty", "IVX", "2", "A", "B", "C", "D", "#"});
    }
    
    public void testSynchro_a5 () {
        testSynchro(new String[] {"TEST", "a5", "Empty", "IVX|ISX", "0", "A", "C", "F", "#"});
    }
    
    public void testSynchro_a6 () {
        testSynchro(new String[] {"TEST", "a6", "Empty", "ISX|IVX", "0", "A", "C", "F", "#"});
    }
    
    public void testSynchro_a7 () {
        testSynchro(new String[] {"TEST", "a7", "Simple", "ISX", "2", "D", "E", "F", "FValueFactory", "X", "#"});
    }
    
    public void testSynchro_a8 () {
        testSynchro(new String[] {"TEST", "a8", "Empty", "ISX", "3", "A", "B", "BValueFactory", "C", "D", "E", "#"});
    }
    
    public void testSynchro_a9 () {
        testSynchro(new String[] {"TEST", "a9", "Empty", "ISX", "0", "IO", "#"});
    }
    
    public void testSynchro_b1 () {
        testSynchro(new String[] {"TEST", "b1", "Empty", "ISX|IVX", "0", "A", "C", "H", "I", "#"});
    }
    
    public void testSynchro_b2 () {
        testSynchro(new String[] {"TEST", "b2", "Empty", "ISX|IVX", "0", "H", "I", "#"});
    }
    
    public void testSynchro_b3 () {
        testSynchro(new String[] {"TEST", "b3", "Empty", "IVX|IVX", "0", "H", "I", "#"});
    }
    
    public void testSynchro_b4 () {
        testSynchro(new String[] {"TEST", "b4", "Empty", "ISX|ISX", "0", "H", "I", "#"});
    }
    
    public void testSynchro_b5 () {
        testSynchro(new String[] {"TEST", "b5", "Empty", "IVX|ISX", "0", "A", "B", "C", "D", "E", "#"});
    }
    
    public void testSynchro_b6 () {
        testSynchro(new String[] {"TEST", "b6", "Empty", "IVX|ISX", "0", "C", "D", "E", "#"});
    }
    
    public void testSynchro_b7 () {
        testSynchro(new String[] {"TEST", "b7", "Empty", "ISX|ISX", "0", "C", "D", "E", "#"});
    }
    
    public void testSynchro_b8 () {
        testSynchro(new String[] {"TEST", "b8", "Empty", "ISX|ISX", "2", "A", "B", "#"});
    }
    
}
