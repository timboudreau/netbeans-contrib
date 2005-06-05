/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.profiles;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import junit.framework.*;
import org.netbeans.junit.*;


import org.openide.filesystems.*;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

/**
 *
 * @author Jaroslav Tulach
 */
public class ProfilesTest extends NbTestCase {
    private FileSystem fs;
    
    public ProfilesTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        fs = Repository.getDefault ().getDefaultFileSystem ();
        
        assertEquals ("We are really using the core filesystem", "org.netbeans.core.startup.layers.SystemFileSystem", fs.getClass ().getName ());
        
        java.util.Iterator it = Lookup.getDefault ().lookup (new Lookup.Template (ModuleInfo.class)).allInstances().iterator();
        boolean ok = false;
        while (it.hasNext ()) {
            ModuleInfo i = (ModuleInfo)it.next ();
            if (i.getCodeName ().equals ("org.netbeans.modules.profiles.test")) {
                ok = i.isEnabled ();
                break;
            }
        }
        if (!ok) {
            fail ("The test module is supposed to be found");
        }
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ProfilesTest.class);
        
        return suite;
    }

    public void testGenerateProfile() throws Exception {
        clearWorkDir();

        File f = getWorkDir();
        LocalFileSystem lfs = new LocalFileSystem ();
        lfs.setRootDirectory(f);
        
        FileObject root = FileUtil.createFolder(lfs.getRoot(), "original");
        
        FileObject x = FileUtil.createData (root, "down/bellow/X.instance");
        FileObject y = FileUtil.createData (root, "down/Y.settings");
        java.io.OutputStream os = y.getOutputStream(y.lock());
        os.write ("Ahoj".getBytes());
        os.close();
        FileObject z = FileUtil.createData (root, "down/attr/Z.xml");
        z.setAttribute("IntAttr", new Integer (20));
        z.setAttribute("StringAttr", "Kuk");
        
        HashSet all = new HashSet() {
            public boolean contains (Object o) {
                return true;
            }
        };
        FileObject profil = Profiles.generateProfile(
            FileUtil.createFolder (lfs.getRoot(), "result"), "Sample", 
            root, all
        );
        
        XMLFileSystem fs = new XMLFileSystem (profil.getURL());
        
        assertDirectories ("Generated profile is exactly the same", fs.getRoot(), root);
    }
    
    public void testOverridesOfLayersReallyWork () throws Exception {
        FileObject our = fs.findResource ("TestModule/sample.txt");
        assertNotNull ("We defined the file in our layer", our);
        assertEquals ("It contains Ahoj", 4, our.getSize ());
        assertContent ("It contains Ahoj", "Ahoj", our);
        
        class L extends FileChangeAdapter {
            public int cnt;
            
            public void fileChanged (FileEvent ev) {
                cnt++;
            }
        }
        L listener = new L ();
        our.addFileChangeListener (listener);
        
        Profiles.activateProfile (ProfilesTest.class.getResource ("override-layer.xml"));
        
        FileObject nf = fs.findResource ("TestModule/sample.txt");
        assertEquals ("The file stays the same", our, nf);
        assertContent ("It contains new content", "Ahoj Man.", our);
        assertEquals ("one changes in content", 1, listener.cnt);
        
        // deactivate the profile
        Profiles.activateProfile ((java.net.URL)null);
        FileObject ourAgain = fs.findResource ("TestModule/sample.txt");
        assertEquals ("Still the same", nf, ourAgain);
        assertContent ("It contains Ahoj", "Ahoj", ourAgain);
        assertEquals ("second changes in content", 1, listener.cnt);
        
        
    }

    public static void assertDirectories (String msg, FileObject f1, FileObject f2) throws java.io.IOException {
        assertEquals (msg + " both are the same", f1.isData(), f2.isData());
        
        if (f1.isFolder()) {
            FileObject[] arr1 = f1.getChildren();
            FileObject[] arr2 = f2.getChildren();
            
            if (arr1.length != arr2.length) {
                fail (msg + " wrong children\n" + Arrays.asList (arr1) + "\n" + Arrays.asList (arr2));
            }
            
            for (int i = 0; i < arr1.length; i++) {
                assertDirectories (msg, arr1[i], arr2[i]);
            }
        } else {
            byte[] arr1 = new byte[(int)f1.getSize()];
            byte[] arr2 = new byte[(int)f2.getSize()];

            assertEquals (msg + " length is the same for " + f1 + " and " + f2, arr1.length, arr2.length);

            if (arr1.length > 0) {
                int r1 = f1.getInputStream().read(arr1);
                int r2 = f2.getInputStream().read(arr2);

                assertEquals (msg + " read the same", r1, r2);

                for (int i = 0; i < r1; i++) {
                    assertEquals (msg + " arr[" + i + "]", arr1[i], arr2[i]);
                }
            }
        }
        
        Enumeration e = f1.getAttributes();
        HashSet attr = new HashSet ();
        while (e.hasMoreElements()) {
            attr.add (e.nextElement());
        }
        
        e = f2.getAttributes();
        while (e.hasMoreElements()) {
            String s = (String)e.nextElement();
            if (!attr.remove (s)) {
                fail ("File " + f2 + " contains attribute " + s + " but " + f1 + " does not");
            }
            
            Object v1 = f1.getAttribute(s);
            Object v2 = f2.getAttribute(s);
            assertEquals (msg + " Values for " + s + " are the same", v1, v2);
        }
        
        if (!attr.isEmpty()) {
            fail (msg + " These attributes are only at " + f1 + ": " + attr);
        }
    }
    
    private static void assertContent (String msg, String cnt, FileObject fo) throws Exception {
        byte[] arr = new byte[(int)fo.getSize()];

        assertEquals (msg + " length must match ", cnt.length(), arr.length);

        int r1 = fo.getInputStream().read(arr);
        assertEquals (msg + " read enough", arr.length, r1);
        
        assertEquals (msg + " content", cnt, new String (arr));
    }
}
